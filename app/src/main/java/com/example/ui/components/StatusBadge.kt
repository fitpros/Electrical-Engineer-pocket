package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssetStatus
import com.example.data.model.CriticalityLevel
import com.example.data.model.FaultSeverity
import com.example.data.model.MaintenanceStatus
import com.example.data.model.TestResultStatus
import com.example.ui.theme.StatusCriticalRed
import com.example.ui.theme.StatusCriticalRedBgDark
import com.example.ui.theme.StatusCriticalRedBgLight
import com.example.ui.theme.StatusInactiveGray
import com.example.ui.theme.StatusInactiveGrayBgDark
import com.example.ui.theme.StatusInactiveGrayBgLight
import com.example.ui.theme.StatusInfoBlue
import com.example.ui.theme.StatusInfoBlueBgDark
import com.example.ui.theme.StatusInfoBlueBgLight
import com.example.ui.theme.StatusPassGreen
import com.example.ui.theme.StatusPassGreenBgDark
import com.example.ui.theme.StatusPassGreenBgLight
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.StatusWarningAmberBgDark
import com.example.ui.theme.StatusWarningAmberBgLight

@Composable
fun StatusBadge(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon()
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AssetStatusBadge(status: AssetStatus, isDark: Boolean = false) {
    when (status) {
        AssetStatus.NORMAL -> StatusBadge(
            text = "Normal / Healthy",
            textColor = StatusPassGreen,
            backgroundColor = if (isDark) StatusPassGreenBgDark else StatusPassGreenBgLight,
            borderColor = StatusPassGreen.copy(alpha = 0.5f),
            icon = { Icon(Icons.Default.CheckCircle, "Normal", tint = StatusPassGreen, modifier = Modifier.size(13.dp)) }
        )
        AssetStatus.WARNING -> StatusBadge(
            text = "Warning / Attention",
            textColor = StatusWarningAmber,
            backgroundColor = if (isDark) StatusWarningAmberBgDark else StatusWarningAmberBgLight,
            borderColor = StatusWarningAmber.copy(alpha = 0.5f),
            icon = { Icon(Icons.Default.Warning, "Warning", tint = StatusWarningAmber, modifier = Modifier.size(13.dp)) }
        )
        AssetStatus.FAULT -> StatusBadge(
            text = "Fault / Critical",
            textColor = StatusCriticalRed,
            backgroundColor = if (isDark) StatusCriticalRedBgDark else StatusCriticalRedBgLight,
            borderColor = StatusCriticalRed.copy(alpha = 0.5f),
            icon = { Icon(Icons.Default.Error, "Fault", tint = StatusCriticalRed, modifier = Modifier.size(13.dp)) }
        )
        AssetStatus.INACTIVE -> StatusBadge(
            text = "Inactive",
            textColor = StatusInactiveGray,
            backgroundColor = if (isDark) StatusInactiveGrayBgDark else StatusInactiveGrayBgLight,
            borderColor = StatusInactiveGray.copy(alpha = 0.5f),
            icon = { Icon(Icons.Default.Info, "Inactive", tint = StatusInactiveGray, modifier = Modifier.size(13.dp)) }
        )
    }
}

@Composable
fun CriticalityBadge(level: CriticalityLevel, isDark: Boolean = false) {
    val (text, color) = when (level) {
        CriticalityLevel.LOW -> "Low Priority" to StatusInfoBlue
        CriticalityLevel.MEDIUM -> "Medium Criticality" to StatusWarningAmber
        CriticalityLevel.HIGH -> "High Criticality" to StatusCriticalRed.copy(alpha = 0.9f)
        CriticalityLevel.CRITICAL -> "MISSION CRITICAL" to StatusCriticalRed
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = if (isDark) 0.25f else 0.12f), RoundedCornerShape(4.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MaintenanceStatusBadge(status: MaintenanceStatus, isDark: Boolean = false) {
    when (status) {
        MaintenanceStatus.DUE_TODAY -> StatusBadge(
            text = "Due Today",
            textColor = StatusWarningAmber,
            backgroundColor = if (isDark) StatusWarningAmberBgDark else StatusWarningAmberBgLight,
            borderColor = StatusWarningAmber.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.Warning, "Due Today", tint = StatusWarningAmber, modifier = Modifier.size(13.dp)) }
        )
        MaintenanceStatus.DUE_THIS_WEEK -> StatusBadge(
            text = "Due This Week",
            textColor = StatusInfoBlue,
            backgroundColor = if (isDark) StatusInfoBlueBgDark else StatusInfoBlueBgLight,
            borderColor = StatusInfoBlue.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.Info, "Due This Week", tint = StatusInfoBlue, modifier = Modifier.size(13.dp)) }
        )
        MaintenanceStatus.OVERDUE -> StatusBadge(
            text = "Overdue",
            textColor = StatusCriticalRed,
            backgroundColor = if (isDark) StatusCriticalRedBgDark else StatusCriticalRedBgLight,
            borderColor = StatusCriticalRed.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.Error, "Overdue", tint = StatusCriticalRed, modifier = Modifier.size(13.dp)) }
        )
        MaintenanceStatus.COMPLETED -> StatusBadge(
            text = "Completed",
            textColor = StatusPassGreen,
            backgroundColor = if (isDark) StatusPassGreenBgDark else StatusPassGreenBgLight,
            borderColor = StatusPassGreen.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.CheckCircle, "Completed", tint = StatusPassGreen, modifier = Modifier.size(13.dp)) }
        )
    }
}

@Composable
fun TestResultBadge(result: TestResultStatus, isDark: Boolean = false) {
    when (result) {
        TestResultStatus.PASS -> StatusBadge(
            text = "PASS / Compliant",
            textColor = StatusPassGreen,
            backgroundColor = if (isDark) StatusPassGreenBgDark else StatusPassGreenBgLight,
            borderColor = StatusPassGreen.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.CheckCircle, "Pass", tint = StatusPassGreen, modifier = Modifier.size(13.dp)) }
        )
        TestResultStatus.ATTENTION -> StatusBadge(
            text = "Attention / Retest",
            textColor = StatusWarningAmber,
            backgroundColor = if (isDark) StatusWarningAmberBgDark else StatusWarningAmberBgLight,
            borderColor = StatusWarningAmber.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.Warning, "Attention", tint = StatusWarningAmber, modifier = Modifier.size(13.dp)) }
        )
        TestResultStatus.FAIL -> StatusBadge(
            text = "FAIL / Out of Tolerance",
            textColor = StatusCriticalRed,
            backgroundColor = if (isDark) StatusCriticalRedBgDark else StatusCriticalRedBgLight,
            borderColor = StatusCriticalRed.copy(alpha = 0.6f),
            icon = { Icon(Icons.Default.Error, "Fail", tint = StatusCriticalRed, modifier = Modifier.size(13.dp)) }
        )
    }
}

@Composable
fun FaultSeverityBadge(severity: FaultSeverity, isDark: Boolean = false) {
    val (text, color) = when (severity) {
        FaultSeverity.CRITICAL_ALARM -> "Critical Alarm" to StatusCriticalRed
        FaultSeverity.PROTECTION_TRIP -> "Protection Trip" to StatusCriticalRed
        FaultSeverity.THERMAL_ANOMALY -> "Thermal Anomaly" to StatusWarningAmber
        FaultSeverity.INSULATION_DEGRADATION -> "Insulation Warning" to StatusWarningAmber
        FaultSeverity.MECHANICAL_STRESS -> "Mechanical Issue" to StatusInfoBlue
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = if (isDark) 0.25f else 0.12f), RoundedCornerShape(4.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
