package com.example.calculator

import kotlin.math.pow

data class RelayDeviceSetting(
    val id: String,
    val name: String,
    val tag: String,
    val curve: ElectricalCalculators.IecCurve,
    val pickupCurrentAmps: Double,
    val timeMultiplier: Double,
    val instantaneousPickupAmps: Double? = null,
    val colorHex: Long = 0xFF0066CC
)

data class CoordinationPoint(
    val currentAmps: Double,
    val upstreamTimeSeconds: Double,
    val downstreamTimeSeconds: Double,
    val marginSeconds: Double
)

enum class CoordinationStatus(val label: String, val isCompliant: Boolean) {
    OPTIMAL("Proper Selective Coordination", true),
    INSUFFICIENT_MARGIN("Miscoordination: Margin < 0.25s", false),
    OVERLAP_TRIP("CRITICAL MISCOORDINATION: Curves Intersect", false),
    EXCESSIVE_DELAY("Excessive Clearance Delay (> 0.45s)", true)
}

data class CoordinationAnalysisResult(
    val status: CoordinationStatus,
    val marginAtMinFault: Double,
    val marginAtMaxFault: Double,
    val upstreamTripTimeAtMaxFault: Double,
    val downstreamTripTimeAtMaxFault: Double,
    val suggestedUpstreamTms: Double,
    val analysisNotes: List<String>,
    val curvePoints: List<Pair<Double, Double>>, // Downstream curve (I, t)
    val upstreamCurvePoints: List<Pair<Double, Double>> // Upstream curve (I, t)
)

object ProtectionCoordinationEngine {

    fun calculateTripTime(
        curve: ElectricalCalculators.IecCurve,
        pickupAmps: Double,
        tms: Double,
        currentAmps: Double,
        instantaneousPickup: Double? = null
    ): Double {
        if (instantaneousPickup != null && currentAmps >= instantaneousPickup) {
            return 0.040 // 40ms typical instantaneous definite-time trip
        }
        val psm = currentAmps / pickupAmps.coerceAtLeast(0.1)
        if (psm <= 1.0) return Double.POSITIVE_INFINITY
        val denom = psm.pow(curve.beta) - 1.0
        if (denom <= 0.0) return Double.POSITIVE_INFINITY
        return tms * (curve.alpha / denom)
    }

    fun analyzeCoordination(
        upstreamRelay: RelayDeviceSetting,
        downstreamRelay: RelayDeviceSetting,
        minFaultCurrentAmps: Double,
        maxFaultCurrentAmps: Double,
        targetGradingMargin: Double = 0.30 // Standard IEEE 242 recommendation
    ): CoordinationAnalysisResult {
        val iMin = minFaultCurrentAmps.coerceAtLeast(100.0)
        val iMax = maxFaultCurrentAmps.coerceAtLeast(iMin + 100.0)

        val tDownMax = calculateTripTime(
            downstreamRelay.curve,
            downstreamRelay.pickupCurrentAmps,
            downstreamRelay.timeMultiplier,
            iMax,
            downstreamRelay.instantaneousPickupAmps
        )

        val tUpMax = calculateTripTime(
            upstreamRelay.curve,
            upstreamRelay.pickupCurrentAmps,
            upstreamRelay.timeMultiplier,
            iMax,
            upstreamRelay.instantaneousPickupAmps
        )

        val marginMax = tUpMax - tDownMax

        val tDownMin = calculateTripTime(
            downstreamRelay.curve,
            downstreamRelay.pickupCurrentAmps,
            downstreamRelay.timeMultiplier,
            iMin,
            downstreamRelay.instantaneousPickupAmps
        )

        val tUpMin = calculateTripTime(
            upstreamRelay.curve,
            upstreamRelay.pickupCurrentAmps,
            upstreamRelay.timeMultiplier,
            iMin,
            upstreamRelay.instantaneousPickupAmps
        )

        val marginMin = tUpMin - tDownMin

        // Generate high resolution curve points for plotting
        val steps = 40
        val startCurrent = (downstreamRelay.pickupCurrentAmps * 1.1).coerceAtLeast(50.0)
        val endCurrent = iMax * 1.2
        val logStart = kotlin.math.ln(startCurrent)
        val logEnd = kotlin.math.ln(endCurrent)
        val logStep = (logEnd - logStart) / steps

        val downPoints = mutableListOf<Pair<Double, Double>>()
        val upPoints = mutableListOf<Pair<Double, Double>>()

        for (step in 0..steps) {
            val i = kotlin.math.exp(logStart + step * logStep)
            val td = calculateTripTime(
                downstreamRelay.curve,
                downstreamRelay.pickupCurrentAmps,
                downstreamRelay.timeMultiplier,
                i,
                downstreamRelay.instantaneousPickupAmps
            )
            val tu = calculateTripTime(
                upstreamRelay.curve,
                upstreamRelay.pickupCurrentAmps,
                upstreamRelay.timeMultiplier,
                i,
                upstreamRelay.instantaneousPickupAmps
            )

            if (!td.isInfinite() && td <= 10.0) downPoints.add(i to td)
            if (!tu.isInfinite() && tu <= 10.0) upPoints.add(i to tu)
        }

        // Determine Status
        val status = when {
            marginMax < 0.0 || marginMin < 0.0 -> CoordinationStatus.OVERLAP_TRIP
            marginMax < 0.25 -> CoordinationStatus.INSUFFICIENT_MARGIN
            marginMax > 0.45 -> CoordinationStatus.EXCESSIVE_DELAY
            else -> CoordinationStatus.OPTIMAL
        }

        // Suggest adjusted Upstream TMS to achieve target margin
        val targetUpstreamTime = tDownMax + targetGradingMargin
        val psmUp = iMax / upstreamRelay.pickupCurrentAmps
        val denomUp = psmUp.pow(upstreamRelay.curve.beta) - 1.0
        val optimalTms = if (denomUp > 0.0) {
            ((targetUpstreamTime * denomUp) / upstreamRelay.curve.alpha).coerceIn(0.05, 1.5)
        } else upstreamRelay.timeMultiplier

        val notes = mutableListOf<String>()
        when (status) {
            CoordinationStatus.OVERLAP_TRIP -> {
                notes.add("CRITICAL MISCOORDINATION: Upstream relay ${upstreamRelay.name} curve intersects or is faster than downstream ${downstreamRelay.name}.")
                notes.add("Risk: Upstream substation breaker will trip simultaneously or before feeder breaker, causing widespread blackout.")
                notes.add("Suggested Adjustment: Increase Upstream TMS from %.2f to %.2f, or increase pickup setting.".format(upstreamRelay.timeMultiplier, optimalTms))
            }
            CoordinationStatus.INSUFFICIENT_MARGIN -> {
                notes.add("INSUFFICIENT GRADING MARGIN (Δt = %.3fs < 0.250s at %.0fA fault).".format(marginMax, iMax))
                notes.add("Standard IEEE 242 requires at least 0.25s grading margin to account for 50ms breaker clearing + 30ms relay overshoot + 50ms CT error.")
                notes.add("Suggested Adjustment: Increase Upstream TMS to %.2f to restore 0.30s selectivity margin.".format(optimalTms))
            }
            CoordinationStatus.OPTIMAL -> {
                notes.add("SELECTIVITY VERIFIED: Grading margin Δt = %.3fs satisfies IEC 60255 and IEEE 242 criteria (0.25s - 0.40s).".format(marginMax))
                notes.add("Downstream relay will reliably clear faults up to %.0fA before upstream relay initiates trip.".format(iMax))
            }
            CoordinationStatus.EXCESSIVE_DELAY -> {
                notes.add("ACCEPTABLE SELECTIVITY BUT EXCESSIVE TIME: Grading margin Δt = %.3fs exceeds 0.45s.".format(marginMax))
                notes.add("Upstream clearing is unnecessarily slow (%.3fs at %.0fA). Increases thermal equipment stress and Arc Flash hazard.".format(tUpMax, iMax))
                notes.add("Suggested Adjustment: Consider lowering Upstream TMS to %.2f.".format(optimalTms))
            }
        }

        return CoordinationAnalysisResult(
            status = status,
            marginAtMinFault = marginMin,
            marginAtMaxFault = marginMax,
            upstreamTripTimeAtMaxFault = tUpMax,
            downstreamTripTimeAtMaxFault = tDownMax,
            suggestedUpstreamTms = optimalTms,
            analysisNotes = notes,
            curvePoints = downPoints,
            upstreamCurvePoints = upPoints
        )
    }
}
