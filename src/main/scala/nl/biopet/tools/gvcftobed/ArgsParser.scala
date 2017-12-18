package nl.biopet.tools.gvcftobed

import java.io.File

import nl.biopet.utils.tool.AbstractOptParser

class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]('I', "inputVcf") required () maxOccurs 1 valueName "<file>" action {
    (x, c) =>
      c.copy(inputVcf = x)
  } text "Input vcf file"
  opt[File]('O', "outputBed") required () maxOccurs 1 valueName "<file>" action {
    (x, c) =>
      c.copy(outputBed = x)
  } text "Output bed file"
  opt[File]("invertedOutputBed") maxOccurs 1 valueName "<file>" action {
    (x, c) =>
      c.copy(invertedOutputBed = Some(x))
  } text "Output bed file"
  opt[String]('S', "sample") unbounded () maxOccurs 1 valueName "<sample>" action {
    (x, c) =>
      c.copy(sample = Some(x))
  } text "Sample to consider. Will take first sample on alphabetical order by default"
  opt[Int]("minGenomeQuality") unbounded () maxOccurs 1 valueName "<int>" action {
    (x, c) =>
      c.copy(minGenomeQuality = x)
  } text "Minimum genome quality to consider"
}
