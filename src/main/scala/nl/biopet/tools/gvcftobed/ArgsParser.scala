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

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

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
  opt[String]('S', "sample") maxOccurs 1 valueName "<sample>" action { (x, c) =>
    c.copy(sample = Some(x))
  } text "Sample to consider. Will take first sample on alphabetical order by default"
  opt[Int]("minGenomeQuality") maxOccurs 1 valueName "<int>" action { (x, c) =>
    c.copy(minGenomeQuality = x)
  } text "Minimum genome quality to consider"
}
