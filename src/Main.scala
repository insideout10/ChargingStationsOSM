import java.io.PrintWriter
import java.net.URL
import scala.xml.{Node, XML}
import scala.collection._

object Main extends App {

  val url: URL       = new URL("http://overpass-api.de/api/xapi_meta?*[amenity%3Dcharging_station]")
  val nodes          = XML.load(url) \ "node"

  val initFields     = Seq("id", "version", "timestamp", "uid", "user", "changeset", "lat", "lon")
  val ignoreFields   = Set("what_kind_of_plug", "addr:city", "addr:country", "addr:housenumber", "refID",
                       "addr:postcode", "addr:street", "addr:suburb", "fixme", "FIXME", "addr:housename",
                       "addr:floor", "addr:state", "fuel:electricity", "note", "petrol",
                       "rwetempcharging_station:plug", "note:de", "description", "description:de", "start_date",
                       "operator:en", "hamburgenergie", "vattenfall", "survey:date", "wheelchair", "places", "price",
                       "phone", "shop", "network", "accuracy", "layer", "count", "date", "building:levels")
  lazy val writer    = new PrintWriter("charging-stations.tsv")

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

  def printMap(map: Map[String, String], fs: Seq[String]) = fs foreach { f => writer.print(map.getOrElse(f, "") + "\t") }
  def printFields(fs: Seq[String])                        = fs foreach { f => writer.print(f + "\t") }

  val fields = nodes.foldLeft(initFields) { (fields, n) =>
    val nm = (n \ "tag").foldLeft(nodeMap(n)) { (nm, t) => nm + ((t \ "@k").text -> (t \ "@v").text) }
    val fs = fields ++ ((nm.keySet -- ignoreFields) -- fields)

    printMap(nm, fs); writer.print("\n")

    fs
  }

  printFields(fields)
  writer.close()

  println("nodes : " + nodes.length)
  println("fields: " + fields.size)

}
