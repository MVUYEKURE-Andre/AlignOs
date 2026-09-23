package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate50

@Composable
fun CircularAlignmentGauge(
  loggedCount: Int,
  totalPillars: Int = 5,
  size: Dp = 150.dp,
  strokeWidth: Dp = 12.dp,
  modifier: Modifier = Modifier
) {
  val targetProgress = if (totalPillars > 0) loggedCount.toFloat() / totalPillars.toFloat() else 0f
  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "alignment_gauge_progress"
  )

  val gaugeColor = when {
    loggedCount == 5 -> Emerald400
    loggedCount >= 3 -> Indigo400
    else -> Amber400
  }

  val sweepBrush = Brush.sweepGradient(
    colors = listOf(
      Indigo400,
      gaugeColor,
      if (loggedCount == 5) Emerald400 else Amber400,
      Indigo400
    )
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.size(size)
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val stroke = strokeWidth.toPx()
      val arcSize = size.toPx() - stroke

      // Background Track
      drawArc(
        color = Slate800,
        startAngle = 140f,
        sweepAngle = 260f,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
        style = Stroke(width = stroke, cap = StrokeCap.Round)
      )

      // Active Progress Arc
      if (animatedProgress > 0f) {
        drawArc(
          brush = sweepBrush,
          startAngle = 140f,
          sweepAngle = 260f * animatedProgress,
          useCenter = false,
          topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
          size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
          style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
      }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "${(targetProgress * 100).toInt()}%",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          color = Slate50,
          fontSize = 32.sp
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "$loggedCount of $totalPillars Tracked",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Slate400,
          fontWeight = FontWeight.SemiBold
        )
      )
    }
  }
}
