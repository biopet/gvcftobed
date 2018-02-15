/*
 * Copyright (c) 2014 Biopet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.biopet.tools.gvcftobed

import java.io.File

import htsjdk.variant.vcf.VCFFileReader
import nl.biopet.utils.test.tools.ToolTest
import nl.biopet.utils.ngs.vcf.BiopetGenotype
import org.testng.annotations.Test

import scala.io.Source

class GvcfToBedTest extends ToolTest[Args] {
  def toolCommand: GvcfToBed.type = GvcfToBed

  import GvcfToBed._

  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      GvcfToBed.main(Array())
    }
  }

  val vcf3 = new File(resourcePath("/VCFv3.vcf"))
  val veppedPath: String = resourcePath("/VEP_oneline.vcf")
  val vepped = new File(veppedPath)
  val unvepped = new File(resourcePath("/unvepped.vcf"))

  @Test def testMinQuality(): Unit = {
    val reader = new VCFFileReader(vepped, false)
    val record = reader.iterator().next()

    record.getGenotype("Sample_101").hasMinGenomeQuality(99) shouldBe true

    val reader2 = new VCFFileReader(unvepped, false)
    val record2 = reader2.iterator.next()

    record2.getGenotype("Sample_102").hasMinGenomeQuality(3) shouldBe true
    record2.getGenotype("Sample_102").hasMinGenomeQuality(99) shouldBe false
  }

  @Test
  def testGvcfToBedOutput(): Unit = {
    val tmp = File.createTempFile("gvcf2bedtest", ".bed")
    tmp.deleteOnExit()
    val args: Array[String] = Array("-I",
                                    unvepped.getAbsolutePath,
                                    "-O",
                                    tmp.getAbsolutePath,
                                    "-S",
                                    "Sample_101",
                                    "--minGenomeQuality",
                                    "99")
    main(args)

    Source.fromFile(tmp).getLines().size shouldBe 0

    val tmp2 = File.createTempFile("gvcf2bedtest", ".bed")
    tmp2.deleteOnExit()
    val args2: Array[String] = Array("-I",
                                     unvepped.getAbsolutePath,
                                     "-O",
                                     tmp2.getAbsolutePath,
                                     "-S",
                                     "Sample_102",
                                     "--minGenomeQuality",
                                     "2")
    main(args2)

    Source.fromFile(tmp2).getLines().size shouldBe 1
  }

  @Test
  def testGvcfToBedInvertedOutput(): Unit = {
    val tmp = File.createTempFile("gvcf2bedtest", ".bed")
    val tmpInv = File.createTempFile("gvcf2bedtest", ".bed")
    tmp.deleteOnExit()
    tmpInv.deleteOnExit()
    val args: Array[String] = Array("-I",
                                    unvepped.getAbsolutePath,
                                    "-O",
                                    tmp.getAbsolutePath,
                                    "-S",
                                    "Sample_101",
                                    "--minGenomeQuality",
                                    "99",
                                    "--invertedOutputBed",
                                    tmpInv.getAbsolutePath)
    main(args)

    Source.fromFile(tmpInv).getLines().size shouldBe 1

    val tmp2 = File.createTempFile("gvcf2bedtest", ".bed")
    val tmp2Inv = File.createTempFile("gvcf2bedtest", ".bed")
    tmp2.deleteOnExit()
    tmp2Inv.deleteOnExit()
    val args2: Array[String] = Array("-I",
                                     unvepped.getAbsolutePath,
                                     "-O",
                                     tmp.getAbsolutePath,
                                     "-S",
                                     "Sample_102",
                                     "--minGenomeQuality",
                                     "3",
                                     "--invertedOutputBed",
                                     tmp2Inv.getAbsolutePath)
    main(args2)

    Source.fromFile(tmp2Inv).getLines().size shouldBe 0
  }
}
