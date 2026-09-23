package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddSleepDialog
import com.example.ui.components.DateNavigator
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Sky400
import com.example.ui.theme.SkyBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel

@Composable
fun SleepScreen(
  viewModel: AlignViewModel,
  modifier: Modifier = Modifier
) {
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
  val todaySleep by viewModel.selectedDateSleep.collectAsStateWithLifecycle()
  val allSleepRecords by viewModel.allSleepRecords.collectAsStateWithLifecycle()

  var showLogSleepDialog by remember { mutableStateOf(false) }

  val avgSleepMins = if (allSleepRecords.isNotEmpty()) allSleepRecords.map { it.durationMinutes }.average() else 450.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Sleep & Recovery",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          Text(
            text = "Overnight duration, sleep debt & wake consistency",
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
          )
        }

        Button(
          onClick = { showLogSleepDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = Sky400, contentColor = Slate950),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("btn_log_sleep_action")
        ) {
          Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Log Sleep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    item {
      DateNavigator(
        selectedDate = selectedDate,
        onDateChange = { viewModel.setSelectedDate(it) }
      )
    }

    // Today's Sleep Status Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .border(1.dp, Slate800, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Selected Day Sleep Record",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Slate100
              )
            )

            if (todaySleep != null) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SkyBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = todaySleep!!.quality.replaceFirstChar { it.uppercase() },
                  color = Sky400,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          if (todaySleep == null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Slate850)
                .padding(18.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No overnight sleep logged for this date.", color = Slate400, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = { showLogSleepDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = Sky400, contentColor = Slate950)
                ) {
                  Text("Record Sleep")
                }
              }
            }
          } else {
            val sleep = todaySleep!!
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Nightlight, contentDescription = "Bedtime", tint = Indigo400)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Bedtime", color = Slate400, fontSize = 11.sp)
                Text(sleep.bedTime, fontWeight = FontWeight.Bold, color = Slate100, fontSize = 14.sp)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.WbSunny, contentDescription = "Wake", tint = Emerald400)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Wake Time", color = Slate400, fontSize = 11.sp)
                Text(sleep.wakeTime, fontWeight = FontWeight.Bold, color = Slate100, fontSize = 14.sp)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Bedtime, contentDescription = "Duration", tint = Sky400)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Total Sleep", color = Slate400, fontSize = 11.sp)
                Text("${sleep.durationMinutes / 60}h ${sleep.durationMinutes % 60}m", fontWeight = FontWeight.Bold, color = Slate100, fontSize = 14.sp)
              }
            }

            if (sleep.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(12.dp))
              Text("Notes: ${sleep.notes}", color = Slate300, fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Past Sleep History
    item {
      Text(
        text = "Sleep History (${allSleepRecords.size})",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Slate100
        )
      )
    }

    if (allSleepRecords.isEmpty()) {
      item {
        Text("No sleep records logged yet.", color = Slate500, fontSize = 13.sp)
      }
    } else {
      items(allSleepRecords, key = { it.id }) { record ->
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
            Column {
              Text(
                text = "${record.sleepDate} • ${record.durationMinutes / 60}h ${record.durationMinutes % 60}m",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = Slate100
                )
              )
              Text(
                text = "${record.bedTime} → ${record.wakeTime} • Quality: ${record.quality.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
              )
            }

            IconButton(onClick = { viewModel.deleteSleepRecord(record.id) }) {
              Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Slate500, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showLogSleepDialog) {
    AddSleepDialog(
      selectedDate = selectedDate,
      onDismiss = { showLogSleepDialog = false },
      onSave = { bedTime, wakeTime, duration, quality, notes, date ->
        viewModel.saveSleepRecord(bedTime, wakeTime, duration, quality, notes, date)
      }
    )
  }
}
