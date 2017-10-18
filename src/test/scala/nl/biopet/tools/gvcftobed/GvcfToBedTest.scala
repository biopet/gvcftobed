package nl.biopet.tools.gvcftobed

import java.io.File

import htsjdk.variant.vcf.VCFFileReader
import nl.biopet.test.BiopetTest
import nl.biopet.utils.ngs.VcfUtils
import org.testng.annotations.Test

import scala.io.Source

class GvcfToBedTest extends BiopetTest {

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

    VcfUtils.hasMinGenomeQuality(record, "Sample_101", 99) shouldBe true

    val reader2 = new VCFFileReader(unvepped, false)
    val record2 = reader2.iterator.next()

    VcfUtils.hasMinGenomeQuality(record2, "Sample_102", 3) shouldBe true
    VcfUtils.hasMinGenomeQuality(record2, "Sample_102", 99) shouldBe false
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
