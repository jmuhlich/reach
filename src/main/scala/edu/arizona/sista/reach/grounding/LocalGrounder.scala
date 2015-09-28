package edu.arizona.sista.reach.grounding

import edu.arizona.sista.odin._
import edu.arizona.sista.reach._
import edu.arizona.sista.reach.mentions._

/**
  * Class which implements project internal methods to ground entities.
  *   Written by Tom Hicks. 4/6/2015.
  *   Last Modified: Also use manual and generated KBs.
  */
class LocalGrounder extends DarpaFlow {

  /** An exception in case we somehow fail to assign an ID during resolution. */
  case class NoFailSafe(message:String) extends Exception(message)

  /** Project local sequence for resolving entities: check local facade KBs in this order:
    * 1. Proteins
    * 2. Protein Families
    * 3. Small Molecules (metabolites and chemicals)
    * 4. Subcellular Locations
    * 5. AZ Failsafe KB (failsafe: always generates an ID in a non-official, local namespace)
    */
  protected val searchSequence = Seq(

    new StaticProteinKBAccessor,
    new ManualProteinKBAccessor,
    new StaticProteinFamilyKBAccessor,
    new ManualProteinFamilyKBAccessor,

    // Context-relevant accessors
    new CellTypeKBAccessor,
    new InferredCellTypeKBAccessor, // These mentions come from a rule
    new SpeciesKBAccessor,
    new CellLinesKBAccessor,
    new OrganKBAccessor,
    new StaticTissueTypeKBLookup,

    // NB: generated protein families are included in the generated protein KB:
    new GendProteinKBAccessor,

    new StaticChemicalKBAccessor,
    new StaticMetaboliteKBAccessor,
    new ManualChemicalKBAccessor,
    new GendChemicalKBAccessor,

    new StaticCellLocationKBAccessor,
    new ManualCellLocationKBAccessor,
    new GendCellLocationKBAccessor,

    new AzFailsafeKBAccessor
  )


  /** Local implementation of trait: use project specific KBs to ground and augment given mentions. */
  def apply (mentions: Seq[Mention], state: State): Seq[Mention] = mentions map {
    case tm: BioTextBoundMention => resolveAndAugment(tm, state)
    case m => m
  }

  /** Search the KB accessors in sequence, use the first one which resolves the given mention. */
  private def resolveAndAugment(mention: BioMention, state: State): Mention = {
    searchSequence.foreach { kbAccessor =>
      val resInfo = kbAccessor.resolve(mention)
      if (!resInfo.isEmpty) {
        mention.ground(resInfo("namespace"), resInfo("referenceID"))
        return mention
      }
    }
    // we should never get here because our accessors include a failsafe ID assignment
    throw NoFailSafe(s"LocalGrounder failed to assign an ID to ${mention.displayLabel} '${mention.text}' in S${mention.sentence}")
  }

}