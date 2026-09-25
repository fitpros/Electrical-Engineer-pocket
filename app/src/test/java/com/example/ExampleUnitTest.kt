package com.example

import com.example.calculator.ElectricalCalculators
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testFullLoadCurrentCalculation() {
    // 150 kW, 400V, PF 0.85, 100% eff -> I = 150000 / (sqrt(3) * 400 * 0.85) = 254.73 A
    val res = ElectricalCalculators.calculateFullLoadCurrent(150.0, 400.0, 0.85, 100.0)
    assertEquals("254.71", res.primaryValue)
    assertEquals("A", res.primaryUnit)
  }

  @Test
  fun testTransformerFaultCalculation() {
    // 40 MVA = 40,000 kVA, 33 kV = 33,000 V, 12.45% Z
    // I_fl = 40000000 / (sqrt(3) * 33000) = 699.82 A
    // I_sc = 699.82 / 0.1245 = 5621 A = 5.62 kA
    val res = ElectricalCalculators.calculateTransformerFault(40000.0, 33000.0, 12.45)
    assertEquals("5.62", res.primaryValue)
    assertEquals("kA", res.primaryUnit)
  }
}
