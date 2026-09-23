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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.components.AddTransactionDialog
import com.example.ui.theme.Amber400
import com.example.ui.theme.AmberBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Rose400
import com.example.ui.theme.RoseBg
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
import com.example.util.DateUtils
import com.example.util.HapticUtils

@Composable
fun FinanceScreen(
  viewModel: AlignViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val userConfig by viewModel.userConfig.collectAsStateWithLifecycle()
  val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
  val todayTxs by viewModel.selectedDateTransactions.collectAsStateWithLifecycle()
  val isZeroSpend by viewModel.isZeroSpendSelectedDate.collectAsStateWithLifecycle()
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }
  var selectedCategoryFilter by remember { mutableStateOf("All") }

  // Calculations
  val daysInMonth = DateUtils.daysInCurrentMonth()
  val discretionaryIncome = maxOf(0.0, userConfig.monthlyIncome - userConfig.monthlyFixedCosts - userConfig.monthlySavingsTarget)
  val dailyAllowance = discretionaryIncome / daysInMonth

  val totalIncome = allTransactions.filter { it.type == "income" }.sumOf { it.amount }
  val totalExpenses = allTransactions.filter { it.type == "expense" }.sumOf { it.amount }
  val netBalance = totalIncome - totalExpenses

  val todaySpent = todayTxs.filter { it.type == "expense" }.sumOf { it.amount }
  val isOverBudget = todaySpent > dailyAllowance

  val savingsProgress = if (userConfig.monthlySavingsTarget > 0) {
    (maxOf(0.0, netBalance) / userConfig.monthlySavingsTarget).toFloat().coerceIn(0f, 1f)
  } else 0f

  val filteredTransactions = if (selectedCategoryFilter == "All") {
    allTransactions
  } else {
    allTransactions.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
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
            text = "Financial Discipline",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
          )
          Text(
            text = "Daily stipend, zero-spend tracking & cashflow",
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
          )
        }

        Button(
          onClick = { showAddDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Slate950),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("btn_add_transaction")
        ) {
          Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Log $", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Cash Flow Summary Cards Grid
    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CashflowCard(
          title = "Total Inflow",
          amount = totalIncome,
          icon = Icons.Filled.TrendingUp,
          tint = Emerald400,
          bg = EmeraldBg,
          modifier = Modifier.weight(1f)
        )
        CashflowCard(
          title = "Total Outflow",
          amount = totalExpenses,
          icon = Icons.Filled.TrendingDown,
          tint = Rose400,
          bg = RoseBg,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CashflowCard(
          title = "Net Cash Balance",
          amount = netBalance,
          icon = Icons.Filled.MonetizationOn,
          tint = if (netBalance >= 0) Emerald400 else Rose400,
          bg = Slate850,
          modifier = Modifier.weight(1f)
        )
        Card(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Slate800, RoundedCornerShape(14.dp)),
          colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("Savings Target", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "${(savingsProgress * 100).toInt()}% Met",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Indigo400
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { savingsProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = Indigo400,
              trackColor = Slate800
            )
          }
        }
      }
    }

    // Daily Allowance Calculator Card with Automatic Warnings
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(
            1.dp,
            if (isOverBudget) Rose400.copy(alpha = 0.5f) else Slate800,
            RoundedCornerShape(16.dp)
          ),
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
                text = "Daily Stipend Allowance",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Slate100
                )
              )
              Text(
                text = "(Income - Fixed Costs - Savings) ÷ $daysInMonth Days",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isOverBudget) Rose400.copy(alpha = 0.2f) else EmeraldBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "$${String.format("%.2f", dailyAllowance)}/day",
                color = if (isOverBudget) Rose400 else Emerald400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (isOverBudget) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(RoseBg)
                .border(1.dp, Rose400.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Rose400, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Daily stipend exceeded by $${String.format("%.2f", todaySpent - dailyAllowance)}. Slow down spending today.",
                color = Rose400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // Zero-Spend Day Toggle
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(
            1.dp,
            if (isZeroSpend) Emerald400.copy(alpha = 0.5f) else Slate800,
            RoundedCornerShape(16.dp)
          ),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isZeroSpend) EmeraldBg else Slate850),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Celebration,
                contentDescription = "Zero Spend",
                tint = if (isZeroSpend) Emerald400 else Slate400,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Zero-Spend Day",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Slate100
                )
              )
              Text(
                text = if (isZeroSpend) "Declared: Zero consumer spend today! 🎉" else "Celebrate intentional zero-consumption days",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
              )
            }
          }

          Switch(
            checked = isZeroSpend,
            onCheckedChange = {
              HapticUtils.trigger(context, 40)
              viewModel.toggleZeroSpend(selectedDate)
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Slate950,
              checkedTrackColor = Emerald400,
              uncheckedTrackColor = Slate800
            ),
            modifier = Modifier.testTag("switch_zero_spend")
          )
        }
      }
    }

    // Transactions History & Filter
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Transaction History",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Slate100
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        val categories = listOf("All", "Food", "Study", "Rent", "Tech", "Consulting", "Transport")
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = cat == selectedCategoryFilter
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Emerald400 else Slate850)
                .clickable { selectedCategoryFilter = cat }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = cat,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Slate950 else Slate300
              )
            }
          }
        }
      }
    }

    if (filteredTransactions.isEmpty()) {
      item {
        Text("No transactions found for filter.", color = Slate500, fontSize = 13.sp)
      }
    } else {
      items(filteredTransactions, key = { it.id }) { tx ->
        val isExpense = tx.type == "expense"
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (isExpense) RoseBg else EmeraldBg),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isExpense) Icons.Filled.TrendingDown else Icons.Filled.TrendingUp,
                  contentDescription = tx.type,
                  tint = if (isExpense) Rose400 else Emerald400,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = tx.description,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Slate100
                  )
                )
                Text(
                  text = "${tx.date} • ${tx.category}",
                  style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${if (isExpense) "-" else "+"}$${String.format("%.2f", tx.amount)}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isExpense) Rose400 else Emerald400
                )
              )
              IconButton(onClick = { viewModel.deleteTransaction(tx.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Slate500, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showAddDialog) {
    AddTransactionDialog(
      initialType = "expense",
      selectedDate = selectedDate,
      currencySymbol = "$",
      onDismiss = { showAddDialog = false },
      onSave = { type, category, amount, desc, date ->
        viewModel.addTransaction(type, category, amount, desc, date)
      }
    )
  }
}

@Composable
private fun CashflowCard(
  title: String,
  amount: Double,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: androidx.compose.ui.graphics.Color,
  bg: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, Slate800, RoundedCornerShape(14.dp)),
    colors = CardDefaults.cardColors(containerColor = Slate900)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
        Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "$${String.format("%.2f", amount)}",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Slate50
        )
      )
    }
  }
}
