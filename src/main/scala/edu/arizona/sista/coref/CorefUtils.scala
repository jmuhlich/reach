package edu.arizona.sista.coref

import edu.arizona.sista.reach.grounding.ReachKBConstants
import edu.arizona.sista.reach.mentions._

import scala.annotation.tailrec
import scala.collection.mutable

/**
 * Utility functions for use with CorefMentions
 * User: danebell
 * Date: 11/10/15
 */
object CorefUtils {
  /**
   * Alternate hash taking antecedents (and antecedents of arguments) into account for proper "distinct"
   */
  def altHash(m: CorefMention): Int = {
    m.hashCode() * 42 * 42 +
      m.arguments.values.flatten.map(a => altHash(a.toCorefMention)).sum * 42 +
      m.antecedents.map(a => altHash(a.asInstanceOf[CorefMention])).sum
  }

  /**
   * Basically the same as "distinct", but using CorefMention's antecedents
   */
  def corefDistinct(ms: Seq[CorefMention]): Seq[CorefMention] = {
    var b: Seq[CorefMention] = Nil
    val seen = mutable.Set[Int]()
    for (x <- ms) {
      val lu = altHash(x)
      if (!seen(lu)) {
        b = b :+ x
        seen += lu
      }
    }
    b
  }

  /**
   * Is the mention generic, e.g. "it", or does it have an argument containing a generic mention,
   * e.g. "It is phophorylated"?
   */
  def genericInside (m: CorefMention): Boolean = {
    @tailrec def genericInsideRec(ms: Seq[CorefMention]): Boolean = {
      if (ms.exists(m => (m matches "Generic_entity") || (m matches "Generic_event"))) true
      else {
        val (tbs, others) = ms.partition(mention => mention.isInstanceOf[CorefTextBoundMention])
        if (others.isEmpty) false
        else genericInsideRec(others.flatMap(_.arguments.values.flatten.map(_.toCorefMention)))
      }
    }
    m.isGeneric || genericInsideRec(Seq(m))
  }

  /**
   * Are the arguments for this mention complete? Phophorylations require a theme, etc.
   */
  def argsComplete(args: Map[String,Seq[CorefMention]], lbls: Seq[String]): Boolean = {
    lbls match {
      case binding if lbls contains "Binding" =>
        args.contains("theme") && args("theme").length == 2
      case simple if lbls contains "SimpleEvent" =>
        args.contains("theme") && args("theme").nonEmpty
      case complex if lbls contains "ComplexEvent" =>
        args.contains("controller") && args.contains("controlled") &&
          args("controller").nonEmpty && args("controlled").nonEmpty
      case _ => true
    }
  }

  /**
    * Do two mentions have groundings that match? E.g. 'H-Ras' (a family) and 'S135' (a site)
    * are not compatible because they don't have the same labels
    *
    * @param a
    * @param b
    */
  def compatibleGrounding(a: CorefMention, b: CorefMention): Boolean = {
    a.isInstanceOf[CorefTextBoundMention] && b.isInstanceOf[CorefTextBoundMention] &&
      a.label == b.label &&
      !a.isGeneric && !b.isGeneric &&
      compatibleContext(a, b) &&
      ((a.isGrounded && a.grounding().get.namespace == ReachKBConstants.DefaultNamespace) ^
        (b.isGrounded && b.grounding().get.namespace == ReachKBConstants.DefaultNamespace))
  }

  /**
    * Do two mentions have contexts that match?
    *
    * @param a
    * @param b
    */
  def compatibleContext(a: CorefMention, b: CorefMention): Boolean = {
    val aContext = a.context.getOrElse(Map[String,Seq[String]]())
    val bContext = b.context.getOrElse(Map[String,Seq[String]]())
    a.label == b.label &&
      aContext.keySet.intersect(bContext.keySet)
        .forall(k => aContext(k).toSet == bContext(k).toSet) // FIXME: Too strict?
  }
}