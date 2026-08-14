package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TimeSeriesPoint
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LineChartCard(
    title: String,
    subtitle: String,
    dataPoints: List<TimeSeriesPoint>,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillGradientColors: List<Color> = listOf(lineColor.copy(alpha = 0.35f), lineColor.copy(alpha = 0.02f)),
    unit: String = "",
    minY: Float? = null,
    maxY: Float? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (dataPoints.isNotEmpty()) {
                    val latest = dataPoints.last().value
                    Text(
                        text = "Latest: $latest $unit",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = lineColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SmoothLineChart(
                dataPoints = dataPoints,
                lineColor = lineColor,
                fillGradientColors = fillGradientColors,
                unit = unit,
                minY = minY,
                maxY = maxY,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        }
    }
}

@Composable
fun SmoothLineChart(
    dataPoints: List<TimeSeriesPoint>,
    lineColor: Color,
    fillGradientColors: List<Color>,
    unit: String,
    minY: Float? = null,
    maxY: Float? = null,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    val values = dataPoints.map { it.value }
    val computedMin = minY ?: ((values.minOrNull() ?: 0f) * 0.9f)
    val computedMax = maxY ?: ((values.maxOrNull() ?: 100f) * 1.1f)
    val range = if (computedMax - computedMin == 0f) 1f else computedMax - computedMin

    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800),
        label = "chartAnimation"
    )

    LaunchedEffect(dataPoints) {
        animationProgress = 1f
    }

    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val surfaceColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val paddingLeft = 40.dp.toPx()
        val paddingBottom = 30.dp.toPx()
        val paddingTop = 10.dp.toPx()
        val paddingRight = 10.dp.toPx()

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        // Draw Horizontal Grid Lines & Y Axis Labels
        val gridLines = 3
        for (i in 0..gridLines) {
            val yValue = computedMin + (range / gridLines) * i
            val yPos = paddingTop + chartHeight - (i.toFloat() / gridLines) * chartHeight
            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, yPos),
                end = Offset(width - paddingRight, yPos),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Generate coordinates for data points
        val stepX = if (dataPoints.size > 1) chartWidth / (dataPoints.size - 1) else chartWidth
        val points = dataPoints.mapIndexed { index, point ->
            val x = paddingLeft + index * stepX
            val normalizedY = (point.value - computedMin) / range
            val y = paddingTop + chartHeight - (normalizedY * chartHeight * animatedProgress)
            Offset(x, y)
        }

        // Draw Area Fill
        if (points.size > 1) {
            val fillPath = Path().apply {
                moveTo(points.first().x, paddingTop + chartHeight)
                lineTo(points.first().x, points.first().y)

                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val control1 = Offset(p1.x + (p2.x - p1.x) / 2, p1.y)
                    val control2 = Offset(p1.x + (p2.x - p1.x) / 2, p2.y)
                    cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                }

                lineTo(points.last().x, paddingTop + chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = fillGradientColors,
                    startY = paddingTop,
                    endY = paddingTop + chartHeight
                )
            )
        }

        // Draw Line
        if (points.size > 1) {
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val control1 = Offset(p1.x + (p2.x - p1.x) / 2, p1.y)
                    val control2 = Offset(p1.x + (p2.x - p1.x) / 2, p2.y)
                    cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                }
            }

            drawPath(
                path = strokePath,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Draw Points & X-Labels
        points.forEachIndexed { index, point ->
            drawCircle(
                color = surfaceColor,
                radius = 5.dp.toPx(),
                center = point
            )
            drawCircle(
                color = lineColor,
                radius = 3.5.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun SimpleBarChart(
    dataPoints: List<TimeSeriesPoint>,
    barColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    val values = dataPoints.map { it.value }
    val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val chartWidth = size.width
                val chartHeight = size.height - 24.dp.toPx()
                val barWidth = (chartWidth / dataPoints.size) * 0.55f
                val barSpacing = chartWidth / dataPoints.size

                dataPoints.forEachIndexed { index, point ->
                    val barHeight = (point.value / maxVal) * chartHeight
                    val left = index * barSpacing + (barSpacing - barWidth) / 2
                    val top = chartHeight - barHeight

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
fun CircularGauge(
    value: Float, // 0 to 100
    label: String,
    valueText: String,
    statusText: String,
    gaugeColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 1000),
        label = "gaugeAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        val trackColor = MaterialTheme.colorScheme.surfaceVariant

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val diameter = Math.min(this.size.width, this.size.height) - strokeWidth
            val topLeft = Offset((this.size.width - diameter) / 2, (this.size.height - diameter) / 2)
            val arcSize = Size(diameter, diameter)

            // Background arc (240 degrees)
            drawArc(
                color = trackColor,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweep = (animatedValue / 100f) * 240f
            drawArc(
                color = gaugeColor,
                startAngle = 150f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = gaugeColor
            )
        }
    }
}
