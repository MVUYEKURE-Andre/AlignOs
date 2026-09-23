package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TimeCategory
import com.example.ui.components.AddTimeLogDialog
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.IndigoBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel
import com.example.util.HapticUtils

@Composable
fun TimerScreen(
  viewModel: AlignViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
  val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
  val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
  val timerCategory by viewModel.timerCategory.collectAsStateWithLifecycle()
  val timerActivity by viewModel.timerActivity.collectAsStateWithLifecycle()
  val todayLogs by viewModel.selectedDateTimeLogs.collectAsStateWithLifecycle()
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

  var showManualEntryDialog by remember { mutableStateOf(false) }

  val minutes = timerSeconds / 60
  val seconds = timerSeconds % 60
  val formattedTime = String.format("%02d:%02d", minutes, seconds)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Focus Timer & Deep Work",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate50
        )
      )
      Text(
        text = "Real-time stopwatch, Pomodoro sessions, and activity tagging",
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
        modifier = Modifier.padding(bottom = 8.dp)
      )
    }

    // Preset Selector Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val presets = listOf(
          Pair("Pomodoro (25m)", 25),
          Pair("Deep Work (50m)", 50),
          Pair("Flow State (90m)", 90),
          Pair("Open Stopwatch", 0)
        )

        presets.forEach { (name, mins) ->
          val isSelected = timerMode == name
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) Indigo400 else Slate900)
              .border(1.dp, if (isSelected) Indigo400 else Slate800, RoundedCornerShape(10.dp))
              .clickable {
                HapticUtils.trigger(context, 20)
                viewModel.setTimerPreset(name, mins)
              }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (mins > 0) "${mins}m" else "Open",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Slate950 else Slate300
            )
          }
        }
      }
    }

    // Circular Timer Gauge & Display
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .border(1.dp, Slate800, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(200.dp)) {
              val stroke = 12.dp.toPx()
              val arcSize = 200.dp.toPx() - stroke

              drawArc(
                color = Slate800,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = stroke)
              )

              drawArc(
                brush = Brush.sweepGradient(listOf(Indigo400, Emerald400, Indigo400)),
                startAngle = -90f,
                sweepAngle = if (isTimerRunning) 270f else 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = formattedTime,
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontSize = 42.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Slate50
                )
              )
              Text(
                text = timerMode,
                style = MaterialTheme.typography.labelMedium.copy(color = Indigo400)
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Controls Row: Reset, Play/Pause, Finish & Log
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                HapticUtils.trigger(context, 30)
                viewModel.resetTimer()
              },
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Slate850)
                .testTag("btn_timer_reset")
            ) {
              Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = Slate300)
            }

            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isTimerRunning) Amber400 else Emerald400)
                .clickable {
                  HapticUtils.trigger(context, 50)
                  if (isTimerRunning) viewModel.pauseTimer() else viewModel.startTimer()
                }
                .testTag("btn_timer_toggle"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isTimerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isTimerRunning) "Pause" else "Start",
                tint = Slate950,
                modifier = Modifier.size(36.dp)
              )
            }

            IconButton(
              onClick = {
                HapticUtils.trigger(context, 50)
                viewModel.finishAndLogTimer()
              },
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(IndigoBg)
                .testTag("btn_timer_finish")
            ) {
              Icon(Icons.Filled.Check, contentDescription = "Log Session", tint = Indigo400)
            }
          }
        }
      }
    }

    // Activity Tagging & Category Linking
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Session Tagging",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Slate100
            )
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = timerActivity,
            onValueChange = { viewModel.setTimerActivity(it) },
            label = { Text("Task / Activity Label") },
            modifier = Modifier.fillMaxWidth().testTag("input_session_label"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Indigo400,
              unfocusedBorderColor = Slate700,
              focusedTextColor = Slate100,
              unfocusedTextColor = Slate100
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text("Category", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            TimeCategory.entries.take(4).forEach { cat ->
              val isSelected = cat == timerCategory
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) Indigo400 else Slate850)
                  .clickable { viewModel.setTimerCategory(cat) }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  cat.label,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) Slate950 else Slate300
                )
              }
            }
          }
        }
      }
    }

    // Manual Entry Button
    item {
      Button(
        onClick = { showManualEntryDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = Slate850, contentColor = Indigo400),
        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_manual_timelog")
      ) {
        Icon(Icons.Filled.Add, contentDescription = "Manual", modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Manual Entry Mode (Completed Away From Phone)", fontSize = 12.sp)
      }
    }

    // Today's Time Logs List
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Logged Sessions Today",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Slate100
          )
        )
        Text(
          text = "${todayLogs.sumOf { it.durationMinutes }} mins total",
          color = Emerald400,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    if (todayLogs.isEmpty()) {
      item {
        Text("No focus sessions logged yet today.", color = Slate500, fontSize = 13.sp)
      }
    } else {
      items(todayLogs, key = { it.id }) { log ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Slate900),
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = log.activity,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = Slate100
                )
              )
              Text(
                text = "${log.category.replaceFirstChar { it.uppercase() }} • ${log.durationMinutes} minutes",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
              )
            }

            IconButton(onClick = { viewModel.deleteTimeLog(log.id) }) {
              Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Slate500)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showManualEntryDialog) {
    AddTimeLogDialog(
      selectedDate = selectedDate,
      onDismiss = { showManualEntryDialog = false },
      onSave = { activity, category, duration, isProductive, date ->
        viewModel.addTimeLog(activity, category, duration, isProductive, date)
      }
    )
  }
}
