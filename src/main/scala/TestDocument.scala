import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class TestDocument(name: String, age: Int)

object TestDocument {
  implicit def codec: Codec.AsObject[TestDocument] = deriveCodec[TestDocument]
}
