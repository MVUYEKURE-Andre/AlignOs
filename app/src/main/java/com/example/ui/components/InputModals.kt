package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DailyMood
import com.example.data.model.SleepQuality
import com.example.data.model.SubtaskItem
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.data.model.TimeCategory
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Rose400
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.DateUtils
import com.example.util.HapticUtils

@Composable
fun AddTransactionDialog(
  initialType: String = "expense",
  selectedDate: String,
  currencySymbol: String = "$",
  onDismiss: () -> Unit,
  onSave: (type: String, category: String, amount: Double, desc: String, date: String) -> Unit
) {
  val context = LocalContext.current
  var txType by remember { mutableStateOf(initialType) }
  var amountText by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  val expenseCategories = listOf("Food", "Rent", "Groceries", "Transport", "Tech", "Leisure", "Health", "Other")
  val incomeCategories = listOf("Stipend", "Salary", "Freelance", "Gift", "Consulting", "Investment")
  var selectedCategory by remember(txType) {
    mutableStateOf(if (txType == "expense") expenseCategories.first() else incomeCategories.first())
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Slate900,
      border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (txType == "expense") "Log Expense" else "Record Income",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_tx_dialog")) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Slate400)
          }
        }

        // Toggle Type
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate850)
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (txType == "expense") Rose400 else Slate850)
              .clickable { txType = "expense" }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Expense",
              color = if (txType == "expense") Slate950 else Slate400,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (txType == "income") Emerald400 else Slate850)
              .clickable { txType = "income" }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Income",
              color = if (txType == "income") Slate950 else Slate400,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount Input
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("Amount ($currencySymbol)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_tx_amount"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (txType == "expense") Rose400 else Emerald400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description Input
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description (e.g. Lunch with team)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_tx_desc"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Emerald400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Category", style = MaterialTheme.typography.labelMedium.copy(color = Slate400))
        Spacer(modifier = Modifier.height(6.dp))

        val currentCategories = if (txType == "expense") expenseCategories else incomeCategories
        FlowChipSelector(
          items = currentCategories,
          selectedItem = selectedCategory,
          onSelect = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val amount = amountText.toDoubleOrNull() ?: 0.0
            if (amount > 0) {
              HapticUtils.trigger(context)
              onSave(txType, selectedCategory, amount, description.ifBlank { selectedCategory }, selectedDate)
              onDismiss()
            }
          },
          enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (txType == "expense") Rose400 else Emerald400,
            contentColor = Slate950
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_save_tx")
        ) {
          Text("Save Transaction", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun AddSleepDialog(
  selectedDate: String,
  onDismiss: () -> Unit,
  onSave: (bedTime: String, wakeTime: String, durationMinutes: Int, quality: String, notes: String, date: String) -> Unit
) {
  val context = LocalContext.current
  var bedTime by remember { mutableStateOf("23:15") }
  var wakeTime by remember { mutableStateOf("07:00") }
  var durationHrsText by remember { mutableStateOf("7.5") }
  var selectedQuality by remember { mutableStateOf(SleepQuality.GOOD) }
  var notes by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Slate900,
      border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Log Overnight Sleep",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_sleep_dialog")) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Slate400)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = bedTime,
            onValueChange = { bedTime = it },
            label = { Text("Bedtime (HH:mm)") },
            modifier = Modifier.weight(1f).testTag("input_bed_time"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Indigo400,
              unfocusedBorderColor = Slate700,
              focusedTextColor = Slate100,
              unfocusedTextColor = Slate100
            )
          )
          OutlinedTextField(
            value = wakeTime,
            onValueChange = { wakeTime = it },
            label = { Text("Wake (HH:mm)") },
            modifier = Modifier.weight(1f).testTag("input_wake_time"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Indigo400,
              unfocusedBorderColor = Slate700,
              focusedTextColor = Slate100,
              unfocusedTextColor = Slate100
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = durationHrsText,
          onValueChange = { durationHrsText = it },
          label = { Text("Duration (Hours)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.fillMaxWidth().testTag("input_sleep_duration"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Recovery & Sleep Quality", style = MaterialTheme.typography.labelMedium.copy(color = Slate400))
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SleepQuality.entries.forEach { quality ->
            val isSelected = quality == selectedQuality
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Indigo400 else Slate850)
                .clickable { selectedQuality = quality }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                quality.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Slate950 else Slate300
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Recovery Notes (optional)") },
          modifier = Modifier.fillMaxWidth().testTag("input_sleep_notes"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val hrs = durationHrsText.toDoubleOrNull() ?: 7.0
            val totalMins = (hrs * 60).toInt()
            HapticUtils.trigger(context)
            onSave(bedTime, wakeTime, totalMins, selectedQuality.name.lowercase(), notes, selectedDate)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Indigo400,
            contentColor = Slate950
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_save_sleep")
        ) {
          Text("Save Sleep Record", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun AddTimeLogDialog(
  selectedDate: String,
  onDismiss: () -> Unit,
  onSave: (activity: String, category: String, durationMinutes: Int, isProductive: Boolean, date: String) -> Unit
) {
  val context = LocalContext.current
  var activity by remember { mutableStateOf("") }
  var durationText by remember { mutableStateOf("45") }
  var selectedCategory by remember { mutableStateOf(TimeCategory.CODING) }
  var isProductive by remember { mutableStateOf(true) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Slate900,
      border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Log Focus Time",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_timer_dialog")) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Slate400)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = activity,
          onValueChange = { activity = it },
          label = { Text("Activity Title (e.g. Kotlin Coroutines)") },
          modifier = Modifier.fillMaxWidth().testTag("input_timer_activity"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Emerald400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = durationText,
          onValueChange = { durationText = it },
          label = { Text("Duration (Minutes)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth().testTag("input_timer_duration"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Emerald400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Category", style = MaterialTheme.typography.labelMedium.copy(color = Slate400))
        Spacer(modifier = Modifier.height(6.dp))

        FlowChipSelector(
          items = TimeCategory.entries.map { it.label },
          selectedItem = selectedCategory.label,
          onSelect = { label ->
            selectedCategory = TimeCategory.entries.find { it.label == label } ?: TimeCategory.CODING
          }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val mins = durationText.toIntOrNull() ?: 30
            if (activity.isNotBlank() && mins > 0) {
              HapticUtils.trigger(context)
              onSave(activity, selectedCategory.name.lowercase(), mins, isProductive, selectedDate)
              onDismiss()
            }
          },
          enabled = activity.isNotBlank(),
          colors = ButtonDefaults.buttonColors(
            containerColor = Emerald400,
            contentColor = Slate950
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_save_timelog")
        ) {
          Text("Save Time Log", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun AddTaskDialog(
  selectedDate: String,
  onDismiss: () -> Unit,
  onSave: (
    title: String,
    category: String,
    priority: String,
    date: String,
    notes: String,
    subtasks: List<SubtaskItem>,
    company: String?,
    role: String?,
    interviewTime: String?,
    prepItems: List<String>?
  ) -> Unit
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(TaskCategory.CAREER) }
  var priority by remember { mutableStateOf(TaskPriority.HIGH) }
  var notes by remember { mutableStateOf("") }
  var newSubtaskText by remember { mutableStateOf("") }
  val subtasks = remember { mutableStateListOf<SubtaskItem>() }

  // Interview specific fields
  var isInterview by remember { mutableStateOf(false) }
  var company by remember { mutableStateOf("") }
  var role by remember { mutableStateOf("") }
  var interviewTime by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Slate900,
      border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Create Priority Task",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_task_dialog")) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Slate400)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task Title") },
          modifier = Modifier.fillMaxWidth().testTag("input_task_title"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Emerald400,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Slate100,
            unfocusedTextColor = Slate100
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Priority", style = MaterialTheme.typography.labelMedium.copy(color = Slate400))
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          TaskPriority.entries.forEach { p ->
            val isSelected = p == priority
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) {
                    when (p) {
                      TaskPriority.HIGH -> Rose400
                      TaskPriority.MEDIUM -> Amber400
                      TaskPriority.LOW -> Emerald400
                    }
                  } else Slate850
                )
                .clickable { priority = p }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                p.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Slate950 else Slate300
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Category", style = MaterialTheme.typography.labelMedium.copy(color = Slate400))
        Spacer(modifier = Modifier.height(6.dp))

        FlowChipSelector(
          items = TaskCategory.entries.map { it.label },
          selectedItem = category.label,
          onSelect = { label ->
            val sel = TaskCategory.entries.find { it.label == label } ?: TaskCategory.CAREER
            category = sel
            if (sel == TaskCategory.INTERVIEW) isInterview = true
          }
        )

        // Interview Drawer if interview
        if (category == TaskCategory.INTERVIEW || isInterview) {
          Spacer(modifier = Modifier.height(14.dp))
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Slate850)
              .border(1.dp, Indigo400.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Text(
              "Interview Preparation Details",
              style = MaterialTheme.typography.labelLarge.copy(color = Indigo400)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = company,
              onValueChange = { company = it },
              label = { Text("Company (e.g. Stripe, Google)") },
              modifier = Modifier.fillMaxWidth().testTag("input_interview_company")
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = role,
              onValueChange = { role = it },
              label = { Text("Job Title / Role") },
              modifier = Modifier.fillMaxWidth().testTag("input_interview_role")
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = interviewTime,
              onValueChange = { interviewTime = it },
              label = { Text("Interview Time (e.g. 14:00)") },
              modifier = Modifier.fillMaxWidth().testTag("input_interview_time")
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subtasks Section
        Text("Subtasks Checklist", style = MaterialTheme.typography.labelMedium.copy(color = Slate400))
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = newSubtaskText,
            onValueChange = { newSubtaskText = it },
            placeholder = { Text("Add subtask item...") },
            modifier = Modifier.weight(1f).testTag("input_subtask")
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = {
              if (newSubtaskText.isNotBlank()) {
                subtasks.add(SubtaskItem(title = newSubtaskText.trim()))
                newSubtaskText = ""
              }
            },
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Emerald400).testTag("btn_add_subtask")
          ) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Slate950)
          }
        }

        subtasks.forEachIndexed { index, subtask ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("• ${subtask.title}", color = Slate300, fontSize = 13.sp)
            IconButton(onClick = { subtasks.removeAt(index) }) {
              Icon(Icons.Filled.Close, contentDescription = "Remove", tint = Slate500, modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes (optional)") },
          modifier = Modifier.fillMaxWidth().testTag("input_task_notes")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (title.isNotBlank()) {
              HapticUtils.trigger(context)
              onSave(
                title.trim(),
                category.name.lowercase(),
                priority.name.lowercase(),
                selectedDate,
                notes,
                subtasks.toList(),
                if (company.isNotBlank()) company else null,
                if (role.isNotBlank()) role else null,
                if (interviewTime.isNotBlank()) interviewTime else null,
                null
              )
              onDismiss()
            }
          },
          enabled = title.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Slate950),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_task")
        ) {
          Text("Create Task", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowChipSelector(
  items: List<String>,
  selectedItem: String,
  onSelect: (String) -> Unit
) {
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    items.forEach { item ->
      val isSelected = item.equals(selectedItem, ignoreCase = true)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) Emerald400 else Slate850)
          .border(1.dp, if (isSelected) Emerald400 else Slate700, RoundedCornerShape(8.dp))
          .clickable { onSelect(item) }
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(
          text = item,
          fontSize = 12.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          color = if (isSelected) Slate950 else Slate300
        )
      }
    }
  }
}
