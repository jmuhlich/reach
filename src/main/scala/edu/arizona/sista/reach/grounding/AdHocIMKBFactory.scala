package edu.arizona.sista.reach.grounding

import scala.io.Source
import edu.arizona.sista.reach.grounding.ReachKBConstants._

/**
  * Factory class for creating and loading an in-memory KB from a mixed-namespaced TSV file.
  *   Written by: Tom Hicks. 1/19/2016.
  *   Last Modified: Remove spurious import.
  */
class AdHocIMKBFactory extends Speciated with ReachKBKeyTransforms {

  /** Main factory method to create, fill, and return an encapsulated knowledge base. */
  def make (kbFilename:String = "", metaInfo:Option[IMKBMetaInfo] = None): InMemoryKB = {
    // all adhoc KBs have species info because human is assumed if species not given:
    val imkb: InMemoryKB = new InMemoryKB(true, metaInfo.getOrElse(new IMKBMetaInfo()))
    if (kbFilename != "")
      loadFromKBDir(imkb, kbFilename)     // load new in-memory KB
    // imkb.imkb.foreach { case (k, entries) =>   // for DEBUGGING
    //   entries.foreach { ent => println(ent.toString()) }} // for DEBUGGING
    return imkb
  }

  /** Additional factory method to workaround meta info option. */
  def make (kbFilename: String, metaInfo: IMKBMetaInfo): InMemoryKB =
    make(kbFilename, Some(metaInfo))


  /**
    * Load this KB from the given 3 or 4-column, tab-separated-value (TSV) text file.
    *   1st column is the text string,
    *   2nd column is the namespace string,
    *   3rd column is the ID string,
    *   4th column is the (optional) species string (assumed human if not given).
    * If filename argument is null or the empty string, skip file loading.
    */
  private def loadFromKBDir (imkb:InMemoryKB, filename:String) = {
    if ((filename != null) && !filename.trim.isEmpty) { // skip loading if filename missing
      val kbResourcePath = ReachKBUtils.makePathInKBDir(filename)
      val source = ReachKBUtils.sourceFromResource(kbResourcePath)
      source.getLines.map(ReachKBUtils.tsvRowToFields(_)).filter(tsvValidateFields(_)).foreach { flds =>
        val species = if (flds.size > 3) flds(3) else Speciated.Human
        imkb.addEntry(makeEntry(flds(0), flds(1), flds(2), species)) // store new entry
      }
      source.close()
    }
  }

  /** Make and return a KB entry from the given fields. */
  private def makeEntry (text:String, namespace:String,
                         refId:String, species:String): KBEntry = {
    val key = makeCanonicalKey(text)        // make canonical storage key
    return new KBEntry(text, key, namespace.toLowerCase, refId, species.toLowerCase)
  }

  /** Check for required fields in one row of the TSV input file. */
  private def tsvValidateFields (fields:Seq[String]): Boolean = {
    if (fields.size < 3) return false       // sanity check
    return fields(0).nonEmpty && fields(1).nonEmpty && fields(2).nonEmpty
  }

}
