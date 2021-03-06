package edu.arizona.sista.reach

import org.scalatest.{Matchers, FlatSpec}

/**
 * Unit tests to ensure Transcription rules are matching correctly
 * User: enoriega
 * Date: 6/01/15
 */

 /*class TestTranscriptionEvents extends FlatSpec with Matchers {

   val sent1 = "expression of NRF2 by Kras"

   sent1 should "contain a transcription event" in {
     val mentions = getBioMentions(sent1)
     hasEventWithArguments("Positive_regulation", List("Kras"), mentions) should be (true)
     hasEventWithArguments("Transcription", List("NRF2"), mentions) should be (true)
   }

   val sent2 = "ErbB3 gene transcription"

   sent2 should "contain a transcription event" in {
     val mentions = getBioMentions(sent2)
     hasEventWithArguments("Transcription", List("ErbB3"), mentions) should be (true)
   }

   val sent3 = "Transcription of Kras"

   sent3 should "contain a transcription event" in {
     val mentions = getBioMentions(sent3)
     hasEventWithArguments("Transcription", List("Kras"), mentions) should be (true)
   }

   val sent4 = "PTEN protein expression was detectable by Western blot in all cell lines."

   sent4 should "contain a transcription event" in {
     val mentions = getBioMentions(sent4)
     hasEventWithArguments("Transcription", List("PTEN"), mentions) should be (true)
   }

   val sent6 = "Indeed, EGFR is overexpressed by Mek in 30%-85% patients with CRC."

   sent6 should "contain a transcription event" in {
     val mentions = getBioMentions(sent6)
     hasEventWithArguments("Positive_regulation", List("Mek"), mentions) should be (true)
     hasEventWithArguments("Transcription", List("EGFR"), mentions) should be (true)
   }

   val sent7 = "We went on to examine the levels of MCL-1 and BIM expressed in several uveal melanoma cell lines"

   sent7 should "contain two transcription events" in {
     val mentions = getBioMentions(sent7)
     hasEventWithArguments("Transcription", List("BIM"), mentions) should be (true)
     hasEventWithArguments("Transcription", List("MCL-1"), mentions) should be (true)
   }

 }*/
