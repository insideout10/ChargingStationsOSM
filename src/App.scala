import java.io.PrintWriter
import java.net.URL
import scala.xml.{Elem, Node, XML}
import scala.collection._

object App {

  def main(args: Array[String]) {

    val url: URL       = new URL("http://overpass-api.de/api/xapi_meta?*[amenity%3Dcharging_station]")
    val nodes          = XML.load(url) \ "node"

    val fields         = mutable.ArrayBuffer[String]("id", "version", "timestamp", "uid", "user", "changeset", "lat", "lon")
    val ignore         = Array[String]("what_kind_of_plug", "addr:city", "addr:country", "addr:housenumber", "refID",
                         "addr:postcode", "addr:street", "addr:suburb", "fixme", "FIXME", "addr:housename",
                         "addr:floor", "addr:state", "fuel:electricity", "note", "petrol",
                         "rwetempcharging_station:plug", "note:de", "description", "description:de", "start_date",
                         "operator:en", "hamburgenergie", "vattenfall", "survey:date", "wheelchair", "places", "price",
                         "phone", "shop", "network", "accuracy", "layer", "count", "date", "building:levels")
    lazy val writer    = new PrintWriter("charging-stations.tsv")

    def nodeMap(n: Node) = mutable.Map(
      "id"        -> (n \ "@id").text,
      "version"   -> (n \ "@version").text,
      "timestamp" -> (n \ "@timestamp").text,
      "uid"       -> (n \ "@uid").text,
      "user"      -> (n \ "@user").text,
      "changeset" -> (n \ "@changeset").text,
      "lat"       -> (n \ "@lat").text,
      "lon"       -> (n \ "@lon").text
    )

    def printMap(map: Map[String, String]) = fields foreach { f => writer.print(map.getOrElse(f, "") + "\t") }
    def printFields()                      = fields foreach { f => writer.print(f + "\t") }

    nodes foreach { n: Node =>
      val nm = nodeMap(n)
      (n \ "tag") foreach { t: Node =>
        val k = (t \ "@k").text
        val v = (t \ "@v").text

        nm += (k -> v)
        if (!ignore.contains(k) && !fields.contains(k)) fields += k
      }
      printMap(nm)
      writer.print("\n")
    }

    printFields()
    writer.close()

    println("nodes : " + nodes.length)
    println("fields: " + fields.length)
  }

}
