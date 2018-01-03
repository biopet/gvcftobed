/*
 * Copyright (c) 2014 Sequencing Analysis Support Core - Leiden University Medical Center
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

import java.io.PrintWriter

import htsjdk.variant.variantcontext.VariantContext
import htsjdk.variant.vcf.VCFFileReader
import nl.biopet.utils.ngs.vcf
import nl.biopet.utils.ngs.intervals.BedRecord
import nl.biopet.utils.tool.ToolCommand

import scala.collection.JavaConversions._

object GvcfToBed extends ToolCommand[Args] {
  def emptyArgs: Args = Args()
  def argsParser = new ArgsParser(this)
  def main(args: Array[String]): Unit = {
    val cmdArgs = cmdArrayToArgs(args)

    logger.info("Start")

    logger.debug("Opening reader")
    val reader = new VCFFileReader(cmdArgs.inputVcf, false)
    logger.debug("Opening writer")
    val writer = new PrintWriter(cmdArgs.outputBed)
    val invertedWriter = cmdArgs.invertedOutputBed.collect {
      case file =>
        logger.debug("Opening inverted writer")
        new PrintWriter(file)
    }

    val sample =
      cmdArgs.sample.getOrElse(reader.getFileHeader.getSampleNamesInOrder.head)

    val it = reader.iterator()
    val firstRecord = it.next()
    var contig = firstRecord.getContig
    var start = firstRecord.getStart
    var end = firstRecord.getEnd
    var pass =
      vcf.hasMinGenomeQuality(firstRecord, sample, cmdArgs.minGenomeQuality)

    def writeResetCachedRecord(newRecord: VariantContext): Unit = {
      writeCachedRecord()
      contig = newRecord.getContig
      start = newRecord.getStart
      end = newRecord.getEnd
      pass =
        vcf.hasMinGenomeQuality(newRecord, sample, cmdArgs.minGenomeQuality)
    }

    def writeCachedRecord(): Unit = {
      if (pass) writer.println(new BedRecord(contig, start - 1, end))
      else
        invertedWriter.foreach(
          _.println(new BedRecord(contig, start - 1, end)))
    }

    var counter = 1
    logger.info("Start")
    for (r <- it) {
      if (contig == r.getContig) {
        val p = vcf.hasMinGenomeQuality(r, sample, cmdArgs.minGenomeQuality)
        if (p != pass || r.getStart > (end + 1)) writeResetCachedRecord(r)
        else end = r.getEnd
      } else writeResetCachedRecord(r)

      counter += 1
      if (counter % 100000 == 0) {
        logger.info(s"Processed $counter records")
      }
    }
    writeCachedRecord()

    logger.info(s"Processed $counter records")

    logger.debug("Closing writer")
    writer.close()
    invertedWriter.foreach { w =>
      logger.debug("Closing inverted writer")
      w.close()
    }
    logger.debug("Closing reader")
    reader.close()

    logger.info("Done")
  }

  def descriptionText: String =
    """
      |This tool makes a bed file with the positions from the input GVCF file.
      |It selects the regions of a certain genome quality treshold.
    """.stripMargin

  def manualText: String =
    s"""
       |$toolName needs an inputVcf and outputs to a bed file.
       |Genome quality threshold can be set by the `--minGenomeQuality` flag.
       |It can optionally output a inverted BED file.
     """.stripMargin

  def exampleText: String =
    s"""
       |To make a bed file from `input.gvcf` by taking sample `F.catus-43`
       |and output to `output.bed`:
       |${example("-I", "input.gvcf", "-O", "output.bed", "-S", "F.catus-43")}
     """.stripMargin
}
