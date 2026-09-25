package com.example.calculator

import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class CalculationResult(
    val primaryValue: String,
    val primaryUnit: String,
    val primaryLabel: String,
    val secondaryResults: List<Pair<String, String>>,
    val formulaDisplay: String,
    val calculationSteps: List<String>,
    val engineeringNotes: List<String>,
    val warnings: List<String> = emptyList()
)

object ElectricalCalculators {

    // --- 1. Three-Phase Power & Full Load Current ---
    fun calculateFullLoadCurrent(
        powerKw: Double,
        voltageVolts: Double,
        powerFactor: Double,
        efficiencyPercent: Double = 100.0
    ): CalculationResult {
        val pf = powerFactor.coerceIn(0.1, 1.0)
        val eff = (efficiencyPercent / 100.0).coerceIn(0.5, 1.0)
        val v = voltageVolts.coerceAtLeast(1.0)
        val p = powerKw.coerceAtLeast(0.0)

        // I = P / (sqrt(3) * V * PF * eff)
        val current = (p * 1000.0) / (sqrt(3.0) * v * pf * eff)
        val apparentPowerKva = (sqrt(3.0) * v * current) / 1000.0
        val reactivePowerKvar = apparentPowerKva * sin(acos(pf))

        val steps = listOf(
            "Step 1: Input Real Power P = %.2f kW, Voltage V = %.1f V, PF = %.2f, Efficiency = %.1f%%".format(p, v, pf, efficiencyPercent),
            "Step 2: Formula I = P / [√3 × V × cos(φ) × η]",
            "Step 3: Denominator = 1.732 × %.1f × %.2f × %.3f = %.2f".format(v, pf, eff, sqrt(3.0) * v * pf * eff),
            "Step 4: I = (%.1f × 1000) / %.2f = %.2f Amperes".format(p, sqrt(3.0) * v * pf * eff, current)
        )

        val notes = listOf(
            "Complies with IEC 60034 and IEEE standard guidelines for three-phase balanced industrial systems.",
            "Cable and thermal overload sizing should account for 125% continuous duty rating where applicable (NEC 430.22).",
            "Apparent Power S = %.2f kVA | Reactive Power Q = %.2f kVAR".format(apparentPowerKva, reactivePowerKvar)
        )

        val warnings = mutableListOf<String>()
        if (pf < 0.85) {
            warnings.add("Low Power Factor (%.2f < 0.85). Utility reactive power penalty charges may apply. Consider power factor correction capacitors.".format(pf))
        }

        return CalculationResult(
            primaryValue = "%.2f".format(current),
            primaryUnit = "A",
            primaryLabel = "Full Load Current (FLA)",
            secondaryResults = listOf(
                "Apparent Power (S)" to "%.2f kVA".format(apparentPowerKva),
                "Reactive Power (Q)" to "%.2f kVAR".format(reactivePowerKvar),
                "Active Power (P)" to "%.2f kW".format(p),
                "125% Continuous Cable Sizing" to "%.2f A".format(current * 1.25)
            ),
            formulaDisplay = "I = \\frac{P}{\\sqrt{3} \\times V \\times \\cos\\phi \\times \\eta}",
            calculationSteps = steps,
            engineeringNotes = notes,
            warnings = warnings
        )
    }

    // --- 2. Transformer Sizing, Impedance & Fault Level ---
    fun calculateTransformerFault(
        ratingKva: Double,
        secondaryVoltageVolts: Double,
        impedancePercent: Double
    ): CalculationResult {
        val s = ratingKva.coerceAtLeast(1.0)
        val v2 = secondaryVoltageVolts.coerceAtLeast(1.0)
        val z = impedancePercent.coerceIn(0.5, 30.0)

        val ifl = (s * 1000.0) / (sqrt(3.0) * v2)
        val isc = ifl / (z / 100.0)
        val isckA = isc / 1000.0
        val scMva = (s / 1000.0) / (z / 100.0)
        val iPeak = isc * 2.55 / 1000.0 // IEC 60909 peak factor for typical substation X/R

        val steps = listOf(
            "Step 1: Transformer Base Rating S = %.1f kVA, Secondary Voltage V = %.1f V, Impedance %%Z = %.2f%%".format(s, v2, z),
            "Step 2: Rated Full Load Secondary Current I_fl = S / (√3 × V2)",
            "Step 3: I_fl = (%.1f × 1000) / (1.732 × %.1f) = %.2f A".format(s, v2, ifl),
            "Step 4: Prospective Symmetrical Fault Current I_sc = I_fl / (%%Z / 100)",
            "Step 5: I_sc = %.2f / %.4f = %.2f A (%.2f kA)".format(ifl, z / 100.0, isc, isckA),
            "Step 6: Fault Capacity MVA_sc = MVA_base / (%%Z / 100) = %.2f MVA".format(scMva)
        )

        val notes = listOf(
            "Assumes infinite bus on primary HV side as conservative baseline according to IEC 60076-5.",
            "Peak dynamic short-circuit current calculated using standard peak factor κ = 2.55 (IEC 60909 standard for X/R ratio of 14-20).",
            "Switchgear breaking capacity (Icu / Ics) must strictly exceed %.2f kA symmetrical.".format(isckA)
        )

        val warnings = mutableListOf<String>()
        if (isckA > 50.0) {
            warnings.add("Extremely high prospective fault level (%.2f kA). Verify busbar mechanical bracing and switchgear withstand rating.".format(isckA))
        }

        return CalculationResult(
            primaryValue = "%.2f".format(isckA),
            primaryUnit = "kA",
            primaryLabel = "Symmetrical Short-Circuit Fault Current (I_sc)",
            secondaryResults = listOf(
                "Secondary Full Load Current (I_fl)" to "%.1f A".format(ifl),
                "Peak Dynamic Current (I_peak)" to "%.2f kA".format(iPeak),
                "Short-Circuit Apparent Power" to "%.2f MVA".format(scMva),
                "Impedance (%Z)" to "%.2f%%".format(z)
            ),
            formulaDisplay = "I_{sc} = \\frac{I_{fl}}{\\%Z / 100} = \\frac{S}{\\sqrt{3} \\times V \\times \\%Z}",
            calculationSteps = steps,
            engineeringNotes = notes,
            warnings = warnings
        )
    }

    // --- 3. Voltage Drop & Conductor Length ---
    fun calculateVoltageDrop(
        currentAmps: Double,
        voltageVolts: Double,
        lengthMeters: Double,
        conductorResistancePerKm: Double,
        conductorReactancePerKm: Double,
        powerFactor: Double = 0.85
    ): CalculationResult {
        val i = currentAmps.coerceAtLeast(0.1)
        val v = voltageVolts.coerceAtLeast(1.0)
        val l = lengthMeters.coerceAtLeast(1.0)
        val pf = powerFactor.coerceIn(0.1, 1.0)
        val sinPhi = sin(acos(pf))

        // VD = sqrt(3) * I * (L/1000) * (R*cosPhi + X*sinPhi)
        val effectiveImpedancePerKm = conductorResistancePerKm * pf + conductorReactancePerKm * sinPhi
        val voltDrop = sqrt(3.0) * i * (l / 1000.0) * effectiveImpedancePerKm
        val percentDrop = (voltDrop / v) * 100.0
        val receivingVoltage = v - voltDrop

        val steps = listOf(
            "Step 1: Load Current I = %.1f A, Line Voltage V = %.1f V, Route Length L = %.1f m".format(i, v, l),
            "Step 2: Conductor R = %.4f Ω/km, X = %.4f Ω/km, cos(φ) = %.2f, sin(φ) = %.3f".format(conductorResistancePerKm, conductorReactancePerKm, pf, sinPhi),
            "Step 3: Effective AC Resistance Z_eff = (R·cosφ + X·sinφ) = %.4f Ω/km".format(effectiveImpedancePerKm),
            "Step 4: VD = √3 × I × (L / 1000) × Z_eff",
            "Step 5: VD = 1.732 × %.1f × %.4f × %.4f = %.2f Volts".format(i, l / 1000.0, effectiveImpedancePerKm, voltDrop),
            "Step 6: Percentage Drop = (%.2f / %.1f) × 100 = %.2f%%".format(voltDrop, v, percentDrop)
        )

        val notes = listOf(
            "Standard maximum allowable voltage drop: 3.0% for lighting/branch circuits, 5.0% for general power feeders (IEC 60364-5-52 / NEC 210.19).",
            "Receiving end terminal voltage under full load will be %.1f V.".format(receivingVoltage)
        )

        val warnings = mutableListOf<String>()
        if (percentDrop > 5.0) {
            warnings.add("Voltage drop (%.2f%%) EXCEEDS the recommended 5.0%% limit! Conductor size must be upgraded or run shortened.".format(percentDrop))
        } else if (percentDrop > 3.0) {
            warnings.add("Voltage drop is %.2f%%. Acceptable for general sub-feeders, but exceeds 3%% limit for branch/sensitive equipment circuits.".format(percentDrop))
        }

        return CalculationResult(
            primaryValue = "%.2f".format(percentDrop),
            primaryUnit = "%",
            primaryLabel = "Voltage Drop Percentage",
            secondaryResults = listOf(
                "Absolute Voltage Drop (ΔV)" to "%.2f V".format(voltDrop),
                "Receiving End Voltage" to "%.1f V".format(receivingVoltage),
                "Route Length" to "%.1f m".format(l),
                "Max Allowable Run for 3% Drop" to "%.1f m".format(l * (3.0 / percentDrop))
            ),
            formulaDisplay = "\\Delta V = \\sqrt{3} \\times I \\times \\frac{L}{1000} \\times (R \\cos\\phi + X \\sin\\phi)",
            calculationSteps = steps,
            engineeringNotes = notes,
            warnings = warnings
        )
    }

    // --- 4. Cable Ampacity Derating ---
    fun calculateCableDerating(
        baseAmpacity: Double,
        ambientTempC: Double,
        groupCount: Int,
        isDirectBuried: Boolean
    ): CalculationResult {
        val base = baseAmpacity.coerceAtLeast(1.0)
        // IEC 60364-5-52 Temperature Correction for XLPE 90°C conductor:
        // Kt = sqrt((90 - Tamb) / (90 - 30)) for air, or for 20C for ground
        val refTemp = if (isDirectBuried) 20.0 else 30.0
        val kt = sqrt(((90.0 - ambientTempC.coerceIn(10.0, 75.0)) / (90.0 - refTemp)).coerceAtLeast(0.01))

        // Grouping factor
        val kg = when {
            groupCount <= 1 -> 1.00
            groupCount == 2 -> 0.80
            groupCount == 3 -> 0.70
            groupCount == 4 -> 0.65
            groupCount in 5..6 -> 0.60
            else -> 0.50
        }

        // Soil or tray factor
        val kInstall = if (isDirectBuried) 0.90 else 0.95
        val totalDerating = kt * kg * kInstall
        val deratedAmpacity = base * totalDerating

        val steps = listOf(
            "Step 1: Nominal Base Cable Ampacity I_base = %.1f A".format(base),
            "Step 2: Temperature Correction Factor Kt (for %.1f°C XLPE 90°C) = %.3f".format(ambientTempC, kt),
            "Step 3: Grouping Factor Kg (for %d adjacent circuits) = %.2f".format(groupCount, kg),
            "Step 4: Installation Routing Factor Kinst = %.2f".format(kInstall),
            "Step 5: Total Derating K = Kt × Kg × Kinst = %.3f × %.2f × %.2f = %.3f".format(kt, kg, kInstall, totalDerating),
            "Step 6: Derated Ampacity I_z = %.1f × %.3f = %.1f A".format(base, totalDerating, deratedAmpacity)
        )

        val notes = listOf(
            "Calculation based on IEC 60364-5-52 / IEC 60287 for cross-linked polyethylene (XLPE) insulated cables.",
            "Maximum continuous operating conductor temperature assumed at 90°C.",
            "The protective device setting (In) must satisfy: Ib ≤ In ≤ Iz (where Ib is design load current)."
        )

        return CalculationResult(
            primaryValue = "%.1f".format(deratedAmpacity),
            primaryUnit = "A",
            primaryLabel = "Effective Derated Cable Ampacity (I_z)",
            secondaryResults = listOf(
                "Base Rating (Free Air / Std)" to "%.1f A".format(base),
                "Total Derating Factor" to "%.1f%% (%.3f)".format(totalDerating * 100, totalDerating),
                "Temp Factor (Kt)" to "%.3f".format(kt),
                "Group Factor (Kg)" to "%.2f".format(kg)
            ),
            formulaDisplay = "I_z = I_{base} \\times K_t \\times K_g \\times K_{inst}",
            calculationSteps = steps,
            engineeringNotes = notes,
            warnings = if (totalDerating < 0.6) listOf("Severe derating factor (< 60%). Significant thermal crowding or elevated ambient temperature detected. Consider separating cable trays or increasing conductor cross-section.") else emptyList()
        )
    }

    // --- 5. Protection Relay IEC 60255 Overcurrent Tripping Time ---
    enum class IecCurve(val label: String, val alpha: Double, val beta: Double) {
        NORMAL_INVERSE("IEC Normal / Standard Inverse", 0.14, 0.02),
        VERY_INVERSE("IEC Very Inverse", 13.5, 1.0),
        EXTREMELY_INVERSE("IEC Extremely Inverse", 80.0, 2.0),
        LONG_TIME_INVERSE("IEC Long Time Inverse", 120.0, 1.0)
    }

    fun calculateRelayTripTime(
        curve: IecCurve,
        pickupCurrentIs: Double,
        faultCurrentIf: Double,
        timeMultiplierSetting: Double
    ): CalculationResult {
        val iPickup = pickupCurrentIs.coerceAtLeast(0.1)
        val iFault = faultCurrentIf.coerceAtLeast(0.1)
        val tms = timeMultiplierSetting.coerceIn(0.01, 2.0)

        val psm = iFault / iPickup // Plug Setting Multiplier

        val tripTimeSeconds: Double
        val warnings = mutableListOf<String>()

        if (psm <= 1.0) {
            tripTimeSeconds = Double.POSITIVE_INFINITY
            warnings.add("Fault current is at or below pickup threshold (PSM = %.2f ≤ 1.0). The relay will NOT pick up or trip.".format(psm))
        } else {
            // t = TMS * (alpha / (PSM^beta - 1))
            val denom = psm.pow(curve.beta) - 1.0
            tripTimeSeconds = tms * (curve.alpha / denom)
        }

        val steps = if (psm > 1.0) {
            listOf(
                "Step 1: Selected Curve = %s (α = %.2f, β = %.2f)".format(curve.label, curve.alpha, curve.beta),
                "Step 2: Plug Setting Multiplier PSM = I_fault / I_pickup = %.1f / %.1f = %.2f".format(iFault, iPickup, psm),
                "Step 3: Formula t = TMS × [ α / (PSM^β - 1) ]",
                "Step 4: Denominator = (%.2f^%.2f - 1) = %.4f".format(psm, curve.beta, psm.pow(curve.beta) - 1.0),
                "Step 5: Tripping Time t = %.3f × (%.2f / %.4f) = %.3f seconds (%.0f ms)".format(tms, curve.alpha, psm.pow(curve.beta) - 1.0, tripTimeSeconds, tripTimeSeconds * 1000)
            )
        } else {
            listOf("Fault current %.1f A does not exceed relay setting %.1f A. No trip initiated.".format(iFault, iPickup))
        }

        val notes = listOf(
            "Governed by IEC 60255-151 and BS 142 protection relay standardization.",
            "Standard grading margin (Δt) between upstream and downstream relays should typically be 0.25s - 0.35s to ensure selective tripping.",
            "Account for circuit breaker opening time (typically 35ms - 60ms) and relay overshoot time."
        )

        return CalculationResult(
            primaryValue = if (tripTimeSeconds.isInfinite()) "NO TRIP" else "%.3f".format(tripTimeSeconds),
            primaryUnit = if (tripTimeSeconds.isInfinite()) "" else "s",
            primaryLabel = "Calculated Operating Time",
            secondaryResults = listOf(
                "Plug Setting Multiplier (PSM)" to "%.2f x Is".format(psm),
                "Trip Time in Milliseconds" to if (tripTimeSeconds.isInfinite()) "N/A" else "%.0f ms".format(tripTimeSeconds * 1000),
                "Curve Standard" to curve.label,
                "Time Multiplier (TMS)" to "%.3f".format(tms)
            ),
            formulaDisplay = "t = TMS \\times \\left[ \\frac{%.2f}{(I / I_s)^{%.2f} - 1} \\right]".format(curve.alpha, curve.beta),
            calculationSteps = steps,
            engineeringNotes = notes,
            warnings = warnings
        )
    }

    // --- 6. Motor Starting Current & Method Comparison ---
    enum class StartingMethod(val label: String, val currentRatio: Double, val torqueRatio: Double) {
        DOL("Direct-On-Line (DOL)", 6.5, 1.5),
        STAR_DELTA("Star-Delta (Y-Δ)", 2.2, 0.5),
        SOFT_STARTER("Solid State Soft Starter", 3.2, 0.8),
        VFD("Variable Frequency Drive (VFD)", 1.2, 1.0)
    }

    fun calculateMotorStarting(
        ratedKw: Double,
        ratedVoltage: Double,
        ratedCurrent: Double,
        method: StartingMethod
    ): CalculationResult {
        val in_ = ratedCurrent.coerceAtLeast(1.0)
        val v = ratedVoltage.coerceAtLeast(1.0)
        val startCurrent = in_ * method.currentRatio
        val startKva = (sqrt(3.0) * v * startCurrent) / 1000.0

        val steps = listOf(
            "Step 1: Motor Rated Current I_n = %.1f A at %.0f V".format(in_, v),
            "Step 2: Selected Method = %s with Inrush Multiplier = %.1fx I_n".format(method.label, method.currentRatio),
            "Step 3: Starting Inrush Current I_start = %.1f × %.1f = %.1f Amperes".format(in_, method.currentRatio, startCurrent),
            "Step 4: Starting Apparent Inrush kVA = (√3 × %.0f × %.1f) / 1000 = %.1f kVA".format(v, startCurrent, startKva)
        )

        val notes = listOf(
            "Starting torque for %s is approximately %.0f%% of rated full-load torque.".format(method.label, method.torqueRatio * 100),
            "Breaker magnetic instantaneous trip unit (ANSI 50) must be set above inrush peak (with 1.3x safety margin: %.1f A) to prevent nuisance tripping during start-up.".format(startCurrent * 1.3),
            "For weak grids or captive diesel generators, voltage dip during starting should not exceed 15%."
        )

        return CalculationResult(
            primaryValue = "%.1f".format(startCurrent),
            primaryUnit = "A",
            primaryLabel = "Starting Inrush Current",
            secondaryResults = listOf(
                "Starting Inrush Ratio" to "%.1fx I_n".format(method.currentRatio),
                "Starting Apparent Power" to "%.1f kVA".format(startKva),
                "Recommended Min Breaker Trip" to "%.1f A".format(startCurrent * 1.2),
                "Approximate Starting Torque" to "%.0f%%".format(method.torqueRatio * 100)
            ),
            formulaDisplay = "I_{start} = I_n \\times K_{method}",
            calculationSteps = steps,
            engineeringNotes = notes,
            warnings = if (method == StartingMethod.DOL && in_ > 100.0) listOf("Direct-On-Line start for large motors (> 100A) produces heavy bus voltage dip and thermal stress. Consider Star-Delta, Soft Starter, or VFD.") else emptyList()
        )
    }
}
