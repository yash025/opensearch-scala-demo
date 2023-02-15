import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object ExampleApp extends App {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  val config: Config                = ConfigFactory.load()
  val openSearchRepo                = OpenSearchRepo(config)
  val index                         = "opensearch-test-index"

  val ans: Future[List[TestDocument]] = for {
    _             <- openSearchRepo.createIndex(index)
    document       = TestDocument("yashwanth-test", 23)
    _             <- openSearchRepo.addDocument[TestDocument](index, "1", document)
    _              = Thread.sleep(3000) // wait for the document to index
    searchResults <- openSearchRepo.searchDocument[TestDocument](index)
  } yield searchResults

  Await.result(ans, 60.seconds)

  println(ans)
}
