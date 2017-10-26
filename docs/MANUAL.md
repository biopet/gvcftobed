# Manual

## Introduction
This tool makes a bed file with the positions from the input GVCF file.

## Example
To run this tool:
```bash
java -jar GvcfToBed-version.jar -I input.vcf -O output.bed
```

To get help:
```bash
java -jar GvcfToBed-version.jar --help
General Biopet options


Options for GvcfToBed

Usage: GvcfToBed [options]

  -l, --log_level <value>  Level of log information printed. Possible levels: 'debug', 'info', 'warn', 'error'
  -h, --help               Print usage
  -v, --version            Print version
  -I, --inputVcf <file>    Input vcf file
  -O, --outputBed <file>   Output bed file
  --invertedOutputBed <file>
                           Output bed file
  -S, --sample <sample>    Sample to consider. Will take first sample on alphabetical order by default
  --minGenomeQuality <int>
                           Minimum genome quality to consider
```

## Output
A bed file with the positinos from the GVCF file.
