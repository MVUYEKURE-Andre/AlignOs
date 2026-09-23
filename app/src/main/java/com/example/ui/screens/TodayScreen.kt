package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TaskItemEntity
import com.example.data.model.DailyMood
import com.example.data.model.TaskPriority
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.DateNavigator
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Rose400
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel
import com.example.util.HapticUtils
import com.example.util.JsonUtils

@Composable
fun TodayScreen(
  viewModel: AlignViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
  val userConfig by viewModel.userConfig.collectAsStateWithLifecycle()
  val todayTasks by viewModel.selectedDateTasks.collectAsStateWithLifecycle()
  val todayReflection by viewModel.selectedDateReflection.collectAsStateWithLifecycle()

  var showAddTaskDialog by remember { mutableStateOf(false) }

  // Reflection form state
  var selectedMood by remember { mutableStateOf(DailyMood.GOOD) }
  var energyLevel by remember { mutableIntStateOf(4) }
  var howDayWent by remember { mutableStateOf("") }
  var highlights by remember { mutableStateOf("") }
  var challenges by remember { mutableStateOf("") }
  var showWinsChallengesDrawer by remember { mutableStateOf(false) }
  val checkedHabits = remember { mutableStateMapOf<String, Boolean>() }

  // Populate from existing reflection if any
  LaunchedEffect(todayReflection) {
    todayReflection?.let { ref ->
      selectedMood = DailyMood.fromString(ref.mood)
      energyLevel = ref.energyLevel
      howDayWent = ref.howDayWent
      highlights = ref.highlights
      challenges = ref.challenges
      val map = JsonUtils.parseHabitsChecked(ref.habitsCheckedJson)
      checkedHabits.clear()
      checkedHabits.putAll(map)
    } ?: run {
      selectedMood = DailyMood.GOOD
      energyLevel = 4
      howDayWent = ""
      highlights = ""
      challenges = ""
      checkedHabits.clear()
    }
  }

  val habitsList = remember(userConfig.habitsListJson) {
    JsonUtils.parseHabits(userConfig.habitsListJson)
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & Date Switcher
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Today's Command Center",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate50
        )
      )
      Text(
        text = "Daily priority execution, habits, and evening reflection",
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
        modifier = Modifier.padding(bottom = 12.dp)
      )
      DateNavigator(
        selectedDate = selectedDate,
        onDateChange = { viewModel.setSelectedDate(it) }
      )
    }

    // SECTION 1: Daily Reflection Card
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
              text = "Daily Reflection & Journal",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Slate100
              )
            )

            if (todayReflection != null) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(EmeraldBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "Saved",
                  color = Emerald400,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Mood Selector Pills
          Text(
            text = "Mood Assessment",
            style = MaterialTheme.typography.labelMedium.copy(color = Slate400)
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            DailyMood.entries.forEach { mood ->
              val isSelected = mood == selectedMood
              Column(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Emerald400 else Slate850)
                  .border(1.dp, if (isSelected) Emerald400 else Slate800, RoundedCornerShape(12.dp))
                  .clickable {
                    HapticUtils.trigger(context, 20)
                    selectedMood = mood
                  }
                  .padding(vertical = 8.dp)
                  .testTag("mood_${mood.name.lowercase()}"),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = mood.emoji, fontSize = 20.sp)
                Text(
                  text = mood.label,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) Slate950 else Slate300
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Energy Rating Stars (1-5)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Energy Meter (${energyLevel}/5)",
              style = MaterialTheme.typography.labelMedium.copy(color = Slate400)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              for (star in 1..5) {
                IconButton(
                  onClick = {
                    HapticUtils.trigger(context, 20)
                    energyLevel = star
                  },
                  modifier = Modifier.size(32.dp).testTag("star_energy_$star")
                ) {
                  Icon(
                    imageVector = if (star <= energyLevel) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "$star Stars",
                    tint = if (star <= energyLevel) Amber400 else Slate700,
                    modifier = Modifier.size(22.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // "How Did Your Day Go?" Text Input with local saving
          OutlinedTextField(
            value = howDayWent,
            onValueChange = { howDayWent = it },
            label = { Text("How Did Your Day Go?") },
            placeholder = { Text("Record key breakthroughs, mindset, or realizations...") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_reflection_text"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Emerald400,
              unfocusedBorderColor = Slate700,
              focusedTextColor = Slate100,
              unfocusedTextColor = Slate100
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Collapsible "Wins & Challenges" drawer
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .clickable { showWinsChallengesDrawer = !showWinsChallengesDrawer }
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (showWinsChallengesDrawer) "Hide Wins & Challenges" else "Add Wins & Challenges Drawer",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Indigo400,
                fontWeight = FontWeight.SemiBold
              )
            )
            Icon(
              imageVector = if (showWinsChallengesDrawer) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
              contentDescription = "Toggle Drawer",
              tint = Indigo400,
              modifier = Modifier.size(18.dp)
            )
          }

          AnimatedVisibility(
            visible = showWinsChallengesDrawer,
            enter = expandVertically(),
            exit = shrinkVertically()
          ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
              OutlinedTextField(
                value = highlights,
                onValueChange = { highlights = it },
                label = { Text("Key Wins / Highlights") },
                modifier = Modifier.fillMaxWidth().testTag("input_wins"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Emerald400,
                  unfocusedBorderColor = Slate700,
                  focusedTextColor = Slate100,
                  unfocusedTextColor = Slate100
                )
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedTextField(
                value = challenges,
                onValueChange = { challenges = it },
                label = { Text("Friction / Challenges") },
                modifier = Modifier.fillMaxWidth().testTag("input_challenges"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Amber400,
                  unfocusedBorderColor = Slate700,
                  focusedTextColor = Slate100,
                  unfocusedTextColor = Slate100
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Daily Habits List with Streak Indicator
          Text(
            text = "Daily Habit Alignment",
            style = MaterialTheme.typography.labelMedium.copy(color = Slate400)
          )
          Spacer(modifier = Modifier.height(8.dp))

          habitsList.forEach { habit ->
            val isChecked = checkedHabits[habit] ?: false
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isChecked) EmeraldBg else Slate850)
                .clickable {
                  HapticUtils.trigger(context, 30)
                  checkedHabits[habit] = !isChecked
                }
                .padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = isChecked,
                  onCheckedChange = { checkedHabits[habit] = it },
                  colors = CheckboxDefaults.colors(
                    checkedColor = Emerald400,
                    checkmarkColor = Slate950,
                    uncheckedColor = Slate600
                  )
                )
                Text(
                  text = habit,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isChecked) Slate100 else Slate300,
                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                    textDecoration = if (isChecked) TextDecoration.None else null
                  )
                )
              }

              // Fire streak indicator
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Filled.LocalFireDepartment,
                  contentDescription = "Streak",
                  tint = if (isChecked) Amber400 else Slate700,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = if (isChecked) "Aligned" else "Pending",
                  fontSize = 11.sp,
                  color = if (isChecked) Amber400 else Slate500
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              HapticUtils.trigger(context, 50)
              viewModel.saveDailyReflection(
                date = selectedDate,
                mood = selectedMood,
                energyLevel = energyLevel,
                howDayWent = howDayWent,
                highlights = highlights,
                challenges = challenges,
                habitsChecked = checkedHabits.toMap()
              )
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Emerald400,
              contentColor = Slate950
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("btn_save_daily_reflection")
          ) {
            Text("Save Daily Reflection", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // SECTION 2: Today's Priority Task List
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Priority Tasks",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Slate100
            )
          )
          Text(
            text = "${todayTasks.count { it.status == "completed" }} of ${todayTasks.size} completed",
            style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
          )
        }

        Button(
          onClick = { showAddTaskDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Slate950),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("btn_quick_add_task")
        ) {
          Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    if (todayTasks.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Slate900)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No priority tasks scheduled for this day.", color = Slate400, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { showAddTaskDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Emerald400)
            ) {
              Text("+ Add First Task")
            }
          }
        }
      }
    } else {
      items(todayTasks, key = { it.id }) { task ->
        TaskItemCard(
          task = task,
          onToggleStatus = {
            HapticUtils.trigger(context, 40)
            viewModel.toggleTaskStatus(task)
          },
          onToggleSubtask = { subtaskId ->
            HapticUtils.trigger(context, 20)
            viewModel.toggleSubtask(task, subtaskId)
          },
          onDelete = { viewModel.deleteTask(task.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showAddTaskDialog) {
    AddTaskDialog(
      selectedDate = selectedDate,
      onDismiss = { showAddTaskDialog = false },
      onSave = { title, category, priority, date, notes, subtasks, company, role, interviewTime, prepItems ->
        viewModel.addTask(
          title = title,
          category = category,
          priority = priority,
          date = date,
          notes = notes,
          subtasks = subtasks,
          company = company,
          role = role,
          interviewTime = interviewTime,
          prepItems = prepItems
        )
      }
    )
  }
}

@Composable
fun TaskItemCard(
  task: TaskItemEntity,
  onToggleStatus: () -> Unit,
  onToggleSubtask: (String) -> Unit,
  onDelete: () -> Unit
) {
  val isCompleted = task.status.equals("completed", ignoreCase = true)
  val subtasks = remember(task.subtasksJson) { JsonUtils.parseSubtasks(task.subtasksJson) }
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(
        1.dp,
        if (isCompleted) Slate800 else when (task.priority.lowercase()) {
          "high" -> Emerald400.copy(alpha = 0.5f)
          "medium" -> Emerald400.copy(alpha = 0.3f)
          else -> Slate800
        },
        RoundedCornerShape(14.dp)
      )
      .testTag("task_item_${task.id}"),
    colors = CardDefaults.cardColors(containerColor = Slate900)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Status Checkbox
        IconButton(
          onClick = onToggleStatus,
          modifier = Modifier.size(36.dp).testTag("btn_toggle_task_${task.id}")
        ) {
          Icon(
            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
            contentDescription = "Toggle status",
            tint = if (isCompleted) Emerald400 else Slate400,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = if (isCompleted) Slate500 else Slate100,
              textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
          )

          Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Category Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Slate850)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = task.category.replaceFirstChar { it.uppercase() },
                color = Indigo400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }

            // Priority Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  when (task.priority.lowercase()) {
                    "high" -> Rose400.copy(alpha = 0.2f)
                    "medium" -> Amber400.copy(alpha = 0.2f)
                    else -> EmeraldBg
                  }
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = task.priority.uppercase(),
                color = when (task.priority.lowercase()) {
                  "high" -> Rose400
                  "medium" -> Amber400
                  else -> Emerald400
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (task.interviewCompany != null) {
              Text(
                text = "• ${task.interviewCompany}",
                fontSize = 11.sp,
                color = Amber400,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        if (subtasks.isNotEmpty()) {
          IconButton(onClick = { isExpanded = !isExpanded }) {
            Icon(
              imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
              contentDescription = "Expand subtasks",
              tint = Slate400
            )
          }
        }
      }

      // Expandable Subtasks Checklist
      if (subtasks.isNotEmpty()) {
        AnimatedVisibility(visible = isExpanded) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 44.dp, top = 8.dp)
          ) {
            subtasks.forEach { subtask ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onToggleSubtask(subtask.id) }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (subtask.completed) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                  contentDescription = "Subtask status",
                  tint = if (subtask.completed) Emerald400 else Slate600,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = subtask.title,
                  fontSize = 13.sp,
                  color = if (subtask.completed) Slate500 else Slate300,
                  textDecoration = if (subtask.completed) TextDecoration.LineThrough else null
                )
              }
            }
          }
        }
      }
    }
  }
}
