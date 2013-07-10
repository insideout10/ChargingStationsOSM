import java.io.{PrintWriter, FileWriter}
import java.net.URL
import scala.xml.{Elem, Node, XML}
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

object App {

  def main(args: Array[String]) {

    var nc: Long  = 0

    val url: URL       = new URL("http://open.mapquestapi.com/xapi/api/0.6/node[amenity=charging_station]")
    lazy val xml: Elem = XML.load(url)

    val fields         = ArrayBuffer[String]("id", "version", "timestamp", "uid", "user", "changeset", "lat", "lon")
    lazy val writer    = new PrintWriter("/Users/david/Desktop/charging-stations.tsv")

    def nodeMap(n: Node) = Map(
      "id"        -> (n \ "@id").text,
      "version"   -> (n \ "@version").text,
      "timestamp" -> (n \ "@timestamp").text,
      "uid"       -> (n \ "@uid").text,
      "user"      -> (n \ "@user").text,
      "changeset" -> (n \ "@changeset").text,
      "lat"       -> (n \ "@lat").text,
      "lon"       -> (n \ "@lon").text
    )

    def printMap(map: Map[String, String]) = fields foreach { f: String => writer.print(map.getOrElse(f, "") + "\t") }
    def printFields                        = fields foreach { f: String => writer.print(f + "\t") }

    (xml \ "node") foreach { n: Node =>
      nc += 1
      val nm = nodeMap(n)
      (n \ "tag") foreach { t: Node =>
        val k = (t \ "@k").text
        val v = (t \ "@v").text

        nm += (k -> v)
        if (!fields.contains(k)) fields += k
      }
      printMap(nm)
      writer.print("\n")
    }

    printFields
    writer.close()

    println("nodes : " + nc)
    println("fields: " + fields.length)
  }

}
