import io.circe.Codec
import io.circe.syntax.EncoderOps
import jakarta.json.Json
import jakarta.json.stream.JsonGenerator
import org.opensearch.client.json.{JsonpMapper, JsonpSerializable}

import java.io.StringReader

class CirceToJava[T](value: T)(implicit codec: Codec.AsObject[T]) extends JsonpSerializable {
  override def serialize(generator: JsonGenerator, mapper: JsonpMapper): Unit = {
    val jsonReader    = Json.createReader(new StringReader(value.asJson.toString))
    val messageAsJson = jsonReader.read()
    generator.write(messageAsJson)
  }
}
