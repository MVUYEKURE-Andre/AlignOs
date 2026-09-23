package com.example.ui.screens

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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircleOutline
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TaskItemEntity
import com.example.data.model.TimeCategory
import com.example.ui.components.AddTaskDialog
import com.example.ui.theme.Amber400
import com.example.ui.theme.AmberBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.IndigoBg
import com.example.ui.theme.Rose400
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel
import com.example.util.DateUtils
import com.example.util.HapticUtils
import com.example.util.JsonUtils

@Composable
fun FutureTasksScreen(
  viewModel: AlignViewModel,
  onLaunchStudySession: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

  var selectedFilter by remember { mutableStateOf("All") }
  var showAddTaskDialog by remember { mutableStateOf(false) }

  val filterOptions = listOf("All", "Next 7 Days", "Interviews", "High Priority", "Completed")

  val filteredTasks = allTasks.filter { task ->
    when (selectedFilter) {
      "Next 7 Days" -> {
        val daysDiff = DateUtils.daysBetween(DateUtils.today(), task.date)
        daysDiff in 0..7
      }
      "Interviews" -> task.category.equals("interview", ignoreCase = true) || task.interviewCompany != null
      "High Priority" -> task.priority.equals("high", ignoreCase = true)
      "Completed" -> task.status.equals("completed", ignoreCase = true)
      else -> true
    }
  }

  val interviewTasks = allTasks.filter {
    (it.category.equals("interview", ignoreCase = true) || it.interviewCompany != null) &&
      !it.status.equals("completed", ignoreCase = true)
  }

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
            text = "Future & Interviews",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          Text(
            text = "Scheduling, interview pipeline & multi-day prep",
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
          )
        }

        Button(
          onClick = { showAddTaskDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = Indigo400, contentColor = Slate950),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("btn_schedule_future_task")
        ) {
          Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Filter Pills
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        filterOptions.forEach { filter ->
          val isSelected = filter == selectedFilter
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) Indigo400 else Slate850)
              .clickable { selectedFilter = filter }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = filter,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Slate950 else Slate300
            )
          }
        }
      }
    }

    // Specialized Interview Preparation Cards (if any)
    if (interviewTasks.isNotEmpty()) {
      item {
        Text(
          text = "Upcoming Interview Pipeline",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Indigo400
          )
        )
      }

      items(interviewTasks, key = { "interview_${it.id}" }) { interview ->
        InterviewPreparationCard(
          task = interview,
          onLaunchStudySession = {
            viewModel.setTimerActivity("Interview Prep: ${interview.interviewCompany ?: interview.title}")
            viewModel.setTimerCategory(TimeCategory.WORK)
            onLaunchStudySession(interview.interviewCompany ?: interview.title)
          },
          onToggleSubtask = { subtaskId ->
            HapticUtils.trigger(context, 20)
            viewModel.toggleSubtask(interview, subtaskId)
          }
        )
      }
    }

    // General Tasks List
    item {
      Text(
        text = "Scheduled Tasks (${filteredTasks.size})",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Slate100
        )
      )
    }

    if (filteredTasks.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Slate900)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No tasks found matching filter.", color = Slate500, fontSize = 13.sp)
        }
      }
    } else {
      items(filteredTasks, key = { it.id }) { task ->
        TaskItemCard(
          task = task,
          onToggleStatus = {
            HapticUtils.trigger(context, 30)
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
fun InterviewPreparationCard(
  task: TaskItemEntity,
  onLaunchStudySession: () -> Unit,
  onToggleSubtask: (String) -> Unit
) {
  val subtasks = remember(task.subtasksJson) { JsonUtils.parseSubtasks(task.subtasksJson) }
  val daysDiff = DateUtils.daysBetween(DateUtils.today(), task.date)
  val countdownBadge = when {
    daysDiff == 0 -> "Today!"
    daysDiff == 1 -> "Tomorrow"
    daysDiff > 1 -> "In $daysDiff days"
    else -> "Past"
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, Indigo400.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
    colors = CardDefaults.cardColors(containerColor = Slate900)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(IndigoBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.BusinessCenter, contentDescription = "Company", tint = Indigo400, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = task.interviewCompany ?: task.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Slate50
              )
            )
            Text(
              text = task.interviewRole ?: "Technical Interview",
              style = MaterialTheme.typography.bodySmall.copy(color = Indigo400)
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (daysDiff <= 1) Rose400.copy(alpha = 0.2f) else IndigoBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = countdownBadge,
            color = if (daysDiff <= 1) Rose400 else Indigo400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (subtasks.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text("Preparation Checklist", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
        Spacer(modifier = Modifier.height(4.dp))

        subtasks.forEach { subtask ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onToggleSubtask(subtask.id) }
              .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (subtask.completed) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
              contentDescription = "Prep status",
              tint = if (subtask.completed) Emerald400 else Slate600,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = subtask.title,
              fontSize = 13.sp,
              color = if (subtask.completed) Slate500 else Slate300
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onLaunchStudySession,
        colors = ButtonDefaults.buttonColors(containerColor = Indigo400, contentColor = Slate950),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("btn_launch_study_session")
      ) {
        Icon(Icons.Filled.PlayArrow, contentDescription = "Launch", modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Launch Interview Study Session", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}
