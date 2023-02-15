import com.typesafe.config.Config
import io.circe.{Codec, parser}
import org.opensearch.client.json.JsonData
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.opensearch.core.{IndexRequest, IndexResponse, SearchResponse}
import org.opensearch.client.opensearch.indices.{CreateIndexRequest, CreateIndexResponse}
import org.opensearch.client.transport.aws.{AwsSdk2Transport, AwsSdk2TransportOptions}
import software.amazon.awssdk.auth.credentials.{
  AwsBasicCredentials,
  AwsCredentialsProvider,
  DefaultCredentialsProvider,
  StaticCredentialsProvider
}
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class OpenSearchRepo(host: String,
                     region: Region,
                     accessKey: Option[String],
                     secretKey: Option[String])(implicit ec: ExecutionContext) {
  val httpClient: SdkHttpClient = ApacheHttpClient.builder.build

  val credentials: AwsCredentialsProvider = {
    if (accessKey.getOrElse("").isEmpty || secretKey.getOrElse("").isEmpty)
      DefaultCredentialsProvider.create()
    else
      StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey.get, secretKey.get)
      )
  }

  val openSearchClient: OpenSearchClient = new OpenSearchClient(
    new AwsSdk2Transport(httpClient,
                         host,
                         "aoss",
                         region,
                         AwsSdk2TransportOptions.builder().setCredentials(credentials).build()))

  def createIndex(index: String): Future[CreateIndexResponse] = Future {
    val createIndexRequest = new CreateIndexRequest.Builder()
      .index(index)
      .build()

    openSearchClient.indices().create(createIndexRequest)
  }

  def addDocument[T](index: String, id: String, document: T)(implicit
      codec: Codec.AsObject[T]): Future[IndexResponse] =
    Future {
      val documentRequest =
        IndexRequest.of[CirceToJava[T]](f =>
          f.index(index).id(id).document(new CirceToJava[T](document)))
      openSearchClient.index(documentRequest)
    }

  def searchDocument[T](index: String)(implicit codec: Codec.AsObject[T]): Future[List[T]] =
    Future {
      val searchResponse: SearchResponse[JsonData] =
        openSearchClient.search(s => s.index(index), classOf[JsonData])

      val iterator  = searchResponse.hits().hits().iterator()
      val documents = mutable.ListBuffer.empty[T]
      while (iterator.hasNext) {
        val sourceJsonStr = iterator.next().source().toString

        val document = parser.decode[T](sourceJsonStr) match {
          case Left(_)      => throw new Exception("failed to parse response")
          case Right(value) => value
        }

        documents.append(document)
      }

      documents.toList
    }
}

object OpenSearchRepo {
  def apply(config: Config)(implicit ec: ExecutionContext): OpenSearchRepo = {
    val host: String              = config.getString("openSearch.host")
    val region: Region            = Region.of(config.getString("openSearch.region"))
    val accessKey: Option[String] = Try(config.getString("openSearch.accessKey")).toOption
    val secretKey: Option[String] = Try(config.getString("openSearch.secretKey")).toOption

    new OpenSearchRepo(host, region, accessKey, secretKey)
  }
}
