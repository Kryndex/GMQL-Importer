package it.polimi.genomics.importer.main


import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}
import slick.dbio.Effect.Write
import slick.driver.PostgresDriver.api._
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.meta.MTable


import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Person(id: Int, name: String)

class Persons(tag: Tag) extends Table[Person](tag, "persons") {

  val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
  val name: Rep[String] = column[String]("name")

  override def * : ProvenShape[Person] = (id, name) <> (Person.tupled, Person.unapply)
}

object Main extends App{
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val connectionUrl = "jdbc:postgresql://131.175.120.18/geco-test?user=geco&password=geco78"
  val driver = "org.postgresql.Driver"
  val database = Database.forURL(connectionUrl, driver, keepAliveConnection = true)
  val tables = Await.result(database.run(MTable.getTables), Duration.Inf).toList

  if(!(tables.isEmpty))
    println("non vuoto")

  val persons = TableQuery[Persons]
  var queries = DBIO.seq(
    persons.schema.create
  )

  val setup = database.run(queries)
  Await.result(setup, Duration.Inf)

 /* val setup = DBIO.seq(suppliers.schema.create)
  val setupFuture = database.run(setup)
  Await.result(setupFuture,Duration.Inf)*/

  /*val setup = DBIO.seq(
    // Create the tables, including primary and foreign keys
    //(suppliers.schema ++ coffees.schema).create,
    suppliers.schema.create,
    coffees.schema.create,
    // Insert some suppliers
    suppliers += (101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199"),
    suppliers += ( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460"),
    suppliers += (150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966"),
    // Equivalent SQL code:
    // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

    // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
    coffees ++= Seq(
      ("Colombian",         101, 7.99, 0, 0),
      ("French_Roast",       49, 8.99, 0, 0),
      ("Espresso",          150, 9.99, 0, 0),
      ("Colombian_Decaf",   101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
    // Equivalent SQL code:
    // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
  )*/

}