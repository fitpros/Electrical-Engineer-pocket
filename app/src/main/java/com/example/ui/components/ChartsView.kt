package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricalBlue
import com.example.ui.theme.ElectricalBlueCyan
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusWarningAmber

@Composable
fun TransformerLoadingChart(
    modifier: Modifier = Modifier
) {
    // 24-Hour Loading percentage points: 02:00, 06:00, 10:00, 14:00 (peak), 18:00, 22:00
    val hours = listOf("00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "24:00")
    val loadingPoints = listOf(42f, 38f, 65f, 88f, 74f, 58f, 44f) // % of rated 40 MVA

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "132-TR-01 Loading Profile (24h)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Peak: 88.0% (35.2 MVA) at 12:45 | Rating: 40 MVA ONAN",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(ElectricalBlue, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "% Loading", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val paddingLeft = 35f
                    val paddingBottom = 25f
                    val chartW = w - paddingLeft - 10f
                    val chartH = h - paddingBottom - 10f

                    // Grid lines (0%, 25%, 50%, 75%, 100%)
                    for (step in 0..4) {
                        val y = chartH * (1f - step / 4f) + 5f
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.2f),
                            start = Offset(paddingLeft, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // Warning 80% threshold line (dotted or dashed)
                    val warningY = chartH * (1f - 0.80f) + 5f
                    drawLine(
                        color = Color(0xFFD97706).copy(alpha = 0.7f),
                        start = Offset(paddingLeft, warningY),
                        end = Offset(w, warningY),
                        strokeWidth = 1.5f
                    )

                    // Line Path
                    val path = Path()
                    val stepX = chartW / (loadingPoints.size - 1)

                    loadingPoints.forEachIndexed { i, loadPct ->
                        val x = paddingLeft + i * stepX
                        val y = chartH * (1f - (loadPct / 100f)) + 5f
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                        // Data points
                        drawCircle(
                            color = if (loadPct > 80f) Color(0xFFD97706) else Color(0xFF0066CC),
                            radius = 4f,
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF0066CC),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                }
            }

            // Time axis labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                hours.forEach { hour ->
                    Text(
                        text = hour,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MaintenanceSummaryMetrics(
    dueTodayCount: Int,
    dueWeekCount: Int,
    overdueCount: Int,
    completedCount: Int,
    onFilterClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Due Today
        MetricCard(
            title = "Due Today",
            count = dueTodayCount,
            color = StatusWarningAmber,
            modifier = Modifier.weight(1f),
            onClick = { onFilterClick?.invoke("DUE_TODAY") }
        )
        // Due This Week
        MetricCard(
            title = "This Week",
            count = dueWeekCount,
            color = ElectricalBlue,
            modifier = Modifier.weight(1f),
            onClick = { onFilterClick?.invoke("DUE_THIS_WEEK") }
        )
        // Overdue
        MetricCard(
            title = "Overdue",
            count = overdueCount,
            color = StatusCriticalRed,
            modifier = Modifier.weight(1f),
            onClick = { onFilterClick?.invoke("OVERDUE") }
        )
        // Completed
        MetricCard(
            title = "Completed",
            count = completedCount,
            color = StatusPassGreen,
            modifier = Modifier.weight(1f),
            onClick = { onFilterClick?.invoke("COMPLETED") }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SolarProductionCurve(modifier: Modifier = Modifier) {
    // Hourly MW output for 2.5 MW inverter skid (06:00 to 18:00)
    val hours = listOf("06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00")
    val mwOutput = listOf(0.1f, 0.8f, 1.9f, 2.45f, 2.30f, 1.2f, 0.2f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Solar PV Skid Alpha Production",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Peak: 2.45 MW | Daily Yield: 16.8 MWh | Inverter: 98.7% Eff",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF0284C7).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "PV Grid-Tie", fontSize = 10.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val paddingLeft = 30f
                    val chartW = w - paddingLeft - 10f
                    val chartH = h - 20f

                    val path = Path()
                    val stepX = chartW / (mwOutput.size - 1)

                    mwOutput.forEachIndexed { i, mw ->
                        val x = paddingLeft + i * stepX
                        val y = chartH * (1f - (mw / 2.5f)) + 5f
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                        drawCircle(
                            color = Color(0xFF0EA5E9),
                            radius = 3.5f,
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF0284C7),
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                hours.forEach { hour ->
                    Text(
                        text = hour,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
