package nl.biopet.tools.gvcftobed

import java.io.PrintWriter

import htsjdk.variant.variantcontext.VariantContext
import htsjdk.variant.vcf.VCFFileReader
import nl.biopet.utils.ngs.vcf
import nl.biopet.utils.ngs.intervals.BedRecord
import nl.biopet.utils.tool.ToolCommand

import scala.collection.JavaConversions._

object GvcfToBed extends ToolCommand[Args] {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

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

    val sample = cmdArgs.sample.getOrElse(reader.getFileHeader.getSampleNamesInOrder.head)

    val it = reader.iterator()
    val firstRecord = it.next()
    var contig = firstRecord.getContig
    var start = firstRecord.getStart
    var end = firstRecord.getEnd
    var pass = vcf.hasMinGenomeQuality(firstRecord, sample, cmdArgs.minGenomeQuality)

    def writeResetCachedRecord(newRecord: VariantContext): Unit = {
      writeCachedRecord()
      contig = newRecord.getContig
      start = newRecord.getStart
      end = newRecord.getEnd
      pass = vcf.hasMinGenomeQuality(newRecord, sample, cmdArgs.minGenomeQuality)
    }

    def writeCachedRecord(): Unit = {
      if (pass) writer.println(new BedRecord(contig, start - 1, end))
      else invertedWriter.foreach(_.println(new BedRecord(contig, start - 1, end)))
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
}
