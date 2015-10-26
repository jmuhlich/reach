package edu.arizona.sista.reach.grounding2

/**
  * Trait for species features used in external knowledge bases.
  *   Written by Tom Hicks. 10/23/2015.
  *   Last Modified: Rename species name set.
  */
trait Speciated {

  /** Species name sets. */
  type SpeciesNameSet = Set[String]
  def  SpeciesNameSet(xs:String*): Set[String] = Set(xs: _*)

  /** A set of label strings for humans, found in KBs. */
  val HumanLabels:SpeciesNameSet = SpeciesNameSet("homo sapiens", "human")


  /** Tell whether the given species string is label for humans or not. */
  def isHumanSpecies (species: String): Boolean = {
    if (HumanLabels.contains(species.toLowerCase)) true else false
  }

}


/** Trait Companion Object allows Mixin OR Import pattern. */
object Speciated extends Speciated
