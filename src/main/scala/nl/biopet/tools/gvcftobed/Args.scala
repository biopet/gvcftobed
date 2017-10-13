package nl.biopet.tools.gvcftobed

import java.io.File

case class Args(inputVcf: File = null,
                outputBed: File = null,
                invertedOutputBed: Option[File] = None,
                sample: Option[String] = None,
                minGenomeQuality: Int = 0,
                inverse: Boolean = false)
