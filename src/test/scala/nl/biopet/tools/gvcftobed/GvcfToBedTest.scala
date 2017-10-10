package nl.biopet.tools.gvcftobed

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

object GvcfToBedTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ToolTemplate.main(Array())
    }
  }
}
