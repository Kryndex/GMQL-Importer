package it.polimi.genomics.importer.ModelDatabase.Encode

import exceptions.NoTableNameException
import it.polimi.genomics.importer.ModelDatabase.Encode.Table._
import it.polimi.genomics.importer.ModelDatabase.Utils.Statistics
import it.polimi.genomics.importer.ModelDatabase.{Table, Tables}
import org.apache.log4j.Logger


class EncodeTables(encodeTableId: EncodeTableId) extends Tables{

  protected var _filePath: String = _



  def filePath: String = _filePath
  def filePath_: (filePath: String): Unit = this._filePath = filePath

  val logger: Logger = Logger.getLogger(this.getClass)
/*  val Donors = Value("DONORS")
  val BioSamples = Value("BIOSAMPLES")
  val Replicates = Value("REPLICATES")
  val ExperimentsType = Value("EXPERIMENTSTYPE")
  val Projects = Value("PROJECTS")
  val Containers = Value("CONTAINERS")
  val Cases = Value("CASES")
  val Items = Value("ITEMS")
  val CasesItems = Value("CASESITEMS")
  val ReplicatesItems = Value ("REPLICATESITEMS")*/

  //private val tables = collection.mutable.Map[String, AbstractTable]()
  //EncodeTableEnum.values.foreach(v => tables += v ->EncodeTableEnum.getTable(v))

  this.values.foreach(v => tables += v -> this.getNewTable(v))
  //override def selectTableByName(name: String): Table = tables(EncodeTableEnum.withName(name))
  //override def selectTableByValue(enum: EncodeTableEnum.Value): Table = tables(enum)

  override def selectTableByName(name: String): Table = tables(this.withName(name))
  override def selectTableByValue(enum: this.Value): Table = tables(enum)

  def setPathOnTables(): Unit ={
    getOrderOfInsertion().map(t => this.selectTableByValue(t).filePath_:(this._filePath))
  }

  def getNewTable(value: Value): Table = {
    value match {
      case Donors => return new DonorEncode(encodeTableId, encodeTableId.bioSampleQuantity)
      case BioSamples => return new BioSampleEncode(encodeTableId, encodeTableId.bioSampleQuantity)
      case Replicates => return new ReplicateEncode(encodeTableId)
      case ExperimentsType => return new ExperimentTypeEncode(encodeTableId)
      case Projects => return new ProjectEncode(encodeTableId)
      case Containers => return new ContainerEncode(encodeTableId)
      case Cases => return new CaseEncode(encodeTableId)
      case Items => return new ItemEncode(encodeTableId)
      case CasesItems => return new CaseItemEncode(encodeTableId)
      case ReplicatesItems => return new ReplicateItemEncode(encodeTableId)
      case _ => throw new NoTableNameException(value.toString)
    }
  }

  def insertTables(): Unit ={
    var insert = true
    getOrderOfInsertion().map(t => this.selectTableByValue(t)).foreach(table =>{
      if(table.hasForeignKeys){
        table.foreignKeysTables.map(t => this.selectTableByName(t)).map(t => table.setForeignKeys(t))
      }
      if(table.checkConsistency() == false && insert) {
        insert = false
        logger.warn(s"Primary key of $table doesn't find")
        Statistics.releasedItemNotInserted += 1
      }
      if(insert) {
        table.insertRow()
      }
    }
    )
  }

  def getOrderOfInsertion(): List[Value] ={
    return List(Donors,BioSamples,Replicates,ExperimentsType,Projects,Containers,Cases,Items,CasesItems,ReplicatesItems)
  }
}