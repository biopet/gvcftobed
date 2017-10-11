package nl.biopet.tools.gvcftobed

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class GvcfToBedTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      GvcfToBed.main(Array())
    }
  }
}
