package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ActivityStreamItem
import com.example.ui.components.CircularAlignmentGauge
import com.example.ui.components.DateNavigator
import com.example.ui.components.QuickActionType
import com.example.ui.theme.Amber400
import com.example.ui.theme.AmberBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.IndigoBg
import com.example.ui.theme.Rose400
import com.example.ui.theme.Sky400
import com.example.ui.theme.SkyBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel
import com.example.util.DateUtils
import com.example.util.HapticUtils

@Composable
fun HomeScreen(
  viewModel: AlignViewModel,
  onNavigateToTab: (Int) -> Unit,
  onRequestQuickAction: (QuickActionType) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
  val userConfig by viewModel.userConfig.collectAsStateWithLifecycle()
  val alignment by viewModel.alignmentState.collectAsStateWithLifecycle()
  val recentStream by viewModel.recentStream.collectAsStateWithLifecycle()
  val todayTxs by viewModel.selectedDateTransactions.collectAsStateWithLifecycle()
  val todayTimeLogs by viewModel.selectedDateTimeLogs.collectAsStateWithLifecycle()
  val todaySleep by viewModel.selectedDateSleep.collectAsStateWithLifecycle()

  var selectedSegmentIndex by remember { mutableIntStateOf(0) } // 0: Tracking Alignment, 1: Financials & Life Overview

  // Memoized Daily Allowance Calculation
  val daysInMonth = remember { DateUtils.daysInCurrentMonth() }
  val discretionaryIncome = remember(userConfig.monthlyIncome, userConfig.monthlyFixedCosts, userConfig.monthlySavingsTarget) {
    maxOf(0.0, userConfig.monthlyIncome - userConfig.monthlyFixedCosts - userConfig.monthlySavingsTarget)
  }
  val dailyAllowance = remember(discretionaryIncome, daysInMonth) { discretionaryIncome / daysInMonth }
  val todayExpenses = remember(todayTxs) { todayTxs.filter { it.type == "expense" }.sumOf { it.amount } }
  val stipendRemaining = dailyAllowance - todayExpenses

  // Memoized Deep work progress against 4-hour target (240 min)
  val totalDeepWorkMins = remember(todayTimeLogs) { todayTimeLogs.filter { it.isProductive }.sumOf { it.durationMinutes } }
  val deepWorkProgress = remember(totalDeepWorkMins) { (totalDeepWorkMins.toFloat() / 240f).coerceIn(0f, 1f) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Welcome back, ${userConfig.userName}",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          Text(
            text = DateUtils.formatDate(selectedDate),
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
          )
        }

        // Offline / Online Cloud Sync Status Badge
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(EmeraldBg)
            .border(1.dp, Emerald400.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.CloudDone,
            contentDescription = "Synced",
            tint = Emerald400,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Sync Active",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Emerald400,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
    }

    // Date Switcher
    item {
      DateNavigator(
        selectedDate = selectedDate,
        onDateChange = { viewModel.setSelectedDate(it) }
      )
    }

    // Centerpiece: Tracking Completeness Meter
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .border(1.dp, Slate800, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Tracking Alignment Engine",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Slate100
                )
              )
              Text(
                text = if (alignment.loggedCount == 5) "Full Alignment Achieved! No blind spots." else "${5 - alignment.loggedCount} pillar(s) remaining for today",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (alignment.loggedCount == 5) Emerald400 else Amber400
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (alignment.loggedCount == 5) EmeraldBg else AmberBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${alignment.completenessPercentage}% Logged",
                color = if (alignment.loggedCount == 5) Emerald400 else Amber400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Circular Gauge
          CircularAlignmentGauge(
            loggedCount = alignment.loggedCount,
            totalPillars = 5,
            size = 140.dp,
            strokeWidth = 12.dp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 5 Pillar Mini Status Icons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            PillarBadge(title = "Sleep", isLogged = alignment.sleepLogged, icon = Icons.Filled.Bedtime)
            PillarBadge(title = "Finance", isLogged = alignment.financeLogged || alignment.isZeroSpend, icon = Icons.Filled.MonetizationOn)
            PillarBadge(title = "Focus", isLogged = alignment.focusTimeLogged, icon = Icons.Filled.Timer)
            PillarBadge(title = "Tasks", isLogged = alignment.tasksLogged, icon = Icons.Filled.CheckCircle)
            PillarBadge(title = "Reflect", isLogged = alignment.reflectionLogged, icon = Icons.Filled.Create)
          }
        }
      }
    }

    // Segmented View Toggle
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Slate850)
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selectedSegmentIndex == 0) Emerald400 else Slate850)
            .clickable { selectedSegmentIndex = 0 }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Tracking Alignment",
            color = if (selectedSegmentIndex == 0) Slate950 else Slate400,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selectedSegmentIndex == 1) Indigo400 else Slate850)
            .clickable { selectedSegmentIndex = 1 }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Financials & Overview",
            color = if (selectedSegmentIndex == 1) Slate950 else Slate400,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }

    // TAB 1: Tracking Alignment Tab
    if (selectedSegmentIndex == 0) {
      // 1. Action Required / Missing List
      val missingPillars = mutableListOf<MissingPillarItem>()
      if (!alignment.sleepLogged) {
        missingPillars.add(
          MissingPillarItem(
            name = "Sleep & Recovery",
            actionLabel = "Log Sleep",
            actionType = QuickActionType.SLEEP
          )
        )
      }
      if (!alignment.financeLogged && !alignment.isZeroSpend) {
        missingPillars.add(
          MissingPillarItem(
            name = "Financial Spending",
            actionLabel = "Mark Zero-Spend",
            actionType = QuickActionType.EXPENSE,
            isZeroSpendAction = true
          )
        )
      }
      if (!alignment.focusTimeLogged) {
        missingPillars.add(
          MissingPillarItem(
            name = "Productive Focus Time",
            actionLabel = "Start Focus Timer",
            actionType = QuickActionType.FOCUS_TIMER
          )
        )
      }
      if (!alignment.tasksLogged) {
        missingPillars.add(
          MissingPillarItem(
            name = "Priority Daily Tasks",
            actionLabel = "Add Today's Task",
            customAction = { onNavigateToTab(1) }
          )
        )
      }
      if (!alignment.reflectionLogged) {
        missingPillars.add(
          MissingPillarItem(
            name = "Daily Reflection & Mood",
            actionLabel = "Write Reflection",
            actionType = QuickActionType.REFLECTION
          )
        )
      }

      if (missingPillars.isNotEmpty()) {
        item {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(bottom = 8.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = "Alerts",
                tint = Emerald400,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Action Required / Missing (${missingPillars.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Emerald400
                )
              )
            }

            missingPillars.forEach { missing ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald400.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(14.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Missing: ${missing.name}",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Slate100
                      )
                    )
                    Text(
                      text = "Keep your streak unbroken before bed",
                      style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                  }

                  if (missing.isZeroSpendAction) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      Button(
                        onClick = {
                          HapticUtils.trigger(context)
                          viewModel.toggleZeroSpend(selectedDate)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_action_zero_spend")
                      ) {
                        Text("Zero Spend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                      Button(
                        onClick = { onRequestQuickAction(QuickActionType.EXPENSE) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Slate100),
                        shape = RoundedCornerShape(10.dp)
                      ) {
                        Text("Log $", fontSize = 11.sp)
                      }
                    }
                  } else {
                    Button(
                      onClick = {
                        HapticUtils.trigger(context)
                        if (missing.customAction != null) {
                          missing.customAction.invoke()
                        } else if (missing.actionType != null) {
                          onRequestQuickAction(missing.actionType)
                        }
                      },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald400,
                        contentColor = Slate950
                      ),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.testTag("btn_action_${missing.actionLabel.replace(" ", "_").lowercase()}")
                    ) {
                      Text(
                        text = missing.actionLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // 2. Verified Today List
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Verified Today",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Emerald400
            ),
            modifier = Modifier.padding(bottom = 8.dp)
          )

          val verifiedList = mutableListOf<String>()
          alignment.sleepSummary?.let { verifiedList.add(it) }
          alignment.financeSummary?.let { verifiedList.add(it) }
          alignment.focusSummary?.let { verifiedList.add(it) }
          alignment.taskSummary?.let { verifiedList.add(it) }
          alignment.reflectionSummary?.let { verifiedList.add(it) }

          if (verifiedList.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Slate900)
                .padding(16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No pillars verified yet today. Tap any missing action above to start.",
                color = Slate500,
                fontSize = 13.sp
              )
            }
          } else {
            verifiedList.forEach { verifiedText ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Slate900)
                  .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Check,
                  contentDescription = "Verified",
                  tint = Emerald400,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = verifiedText,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate100,
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
          }
        }
      }

      // 3. Recent Activity Stream Ticker
      item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
          Text(
            text = "Recent Stream",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Slate100
            ),
            modifier = Modifier.padding(bottom = 8.dp)
          )

          if (recentStream.isEmpty()) {
            Text("No recent activity recorded", color = Slate500, fontSize = 13.sp)
          } else {
            recentStream.take(5).forEach { streamItem ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Slate900)
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                      when (streamItem.pillar) {
                        "Finance" -> EmeraldBg
                        "Focus" -> IndigoBg
                        "Sleep" -> SkyBg
                        else -> AmberBg
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (streamItem.pillar) {
                      "Finance" -> Icons.Filled.MonetizationOn
                      "Focus" -> Icons.Filled.Timer
                      "Sleep" -> Icons.Filled.Bedtime
                      "Tasks" -> Icons.Filled.CheckCircle
                      else -> Icons.Filled.Create
                    },
                    contentDescription = streamItem.pillar,
                    tint = when (streamItem.pillar) {
                      "Finance" -> Emerald400
                      "Focus" -> Indigo400
                      "Sleep" -> Sky400
                      else -> Amber400
                    },
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = streamItem.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = Slate100
                    )
                  )
                  Text(
                    text = streamItem.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                  )
                }
              }
            }
          }
        }
      }
    } else {
      // TAB 2: Financials & Life Overview Tab
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Daily Spending Stipend",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Slate100
              )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              Column {
                Text(
                  text = "$${String.format("%.2f", stipendRemaining)}",
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (stipendRemaining >= 0) Emerald400 else Rose400
                  )
                )
                Text(
                  text = "Remaining today (Allowance: $${String.format("%.2f", dailyAllowance)}/day)",
                  style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (todayExpenses > dailyAllowance) Rose400.copy(alpha = 0.2f) else EmeraldBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (todayExpenses > dailyAllowance) "Over Budget" else "Safe Range",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (todayExpenses > dailyAllowance) Rose400 else Emerald400
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spending Bar
            val spendFraction = if (dailyAllowance > 0) (todayExpenses / dailyAllowance).toFloat().coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
              progress = { spendFraction },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = if (spendFraction >= 1f) Rose400 else Emerald400,
              trackColor = Slate800
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Spent: $${String.format("%.2f", todayExpenses)}", color = Slate400, fontSize = 11.sp)
              Text("Cap: $${String.format("%.2f", dailyAllowance)}", color = Slate400, fontSize = 11.sp)
            }
          }
        }
      }

      // Deep Work Focus Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Deep Work Focus",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                  )
                )
                Text(
                  text = "${totalDeepWorkMins / 60}h ${totalDeepWorkMins % 60}m logged of 4h daily target",
                  style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )
              }

              Icon(
                imageVector = Icons.Filled.Speed,
                contentDescription = "Focus",
                tint = Indigo400,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
              progress = { deepWorkProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = Indigo400,
              trackColor = Slate800
            )
          }
        }
      }

      // Sleep Recovery Pill Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Overnight Recovery",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Slate100
                )
              )
              Text(
                text = todaySleep?.let { "${it.durationMinutes / 60}h ${it.durationMinutes % 60}m (${it.quality.replaceFirstChar { c -> c.uppercase() }})" } ?: "No sleep recorded yet",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SkyBg)
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = todaySleep?.quality?.replaceFirstChar { it.uppercase() } ?: "Pending",
                color = Sky400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun PillarBadge(
  title: String,
  isLogged: Boolean,
  icon: ImageVector
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(if (isLogged) EmeraldBg else Slate850)
        .border(1.dp, if (isLogged) Emerald400 else Slate700, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = if (isLogged) Emerald400 else Slate500,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = title,
      fontSize = 10.sp,
      fontWeight = if (isLogged) FontWeight.Bold else FontWeight.Normal,
      color = if (isLogged) Emerald400 else Slate400
    )
  }
}

private data class MissingPillarItem(
  val name: String,
  val actionLabel: String,
  val actionType: QuickActionType? = null,
  val isZeroSpendAction: Boolean = false,
  val customAction: (() -> Unit)? = null
)
