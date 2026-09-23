package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddSleepDialog
import com.example.ui.components.AddTimeLogDialog
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.AlignBottomNavBar
import com.example.ui.components.AlignNavTab
import com.example.ui.components.QuickActionBottomSheet
import com.example.ui.components.QuickActionType
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.FutureTasksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IntelligenceReportsScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.ReflectionsHistoryScreen
import com.example.ui.screens.SecondaryDestination
import com.example.ui.screens.SleepScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.AlignOSTheme
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AlignOSTheme {
        AlignOSApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlignOSApp(viewModel: AlignViewModel = viewModel()) {
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val currentUserEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }

  if (!isLoggedIn) {
    AuthScreen(
      onLoginSuccess = { email, name ->
        viewModel.login(email, name)
      },
      defaultEmail = currentUserEmail
    )
    return
  }

  var currentTab by remember { mutableStateOf(AlignNavTab.HOME) }
  var secondaryDestination by remember { mutableStateOf<SecondaryDestination?>(null) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  var showQuickActionSheet by remember { mutableStateOf(false) }

  // Quick Action Dialog states
  var showQuickExpenseDialog by remember { mutableStateOf(false) }
  var showQuickIncomeDialog by remember { mutableStateOf(false) }
  var showQuickSleepDialog by remember { mutableStateOf(false) }
  var showQuickTimerDialog by remember { mutableStateOf(false) }

  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

  // Handle Android Back button when in secondary screen
  BackHandler(enabled = secondaryDestination != null) {
    secondaryDestination = null
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Slate950,
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Slate950,
          titleContentColor = Slate100
        ),
        navigationIcon = {
          if (secondaryDestination != null) {
            IconButton(
              onClick = { secondaryDestination = null },
              modifier = Modifier.testTag("btn_back_to_hub")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Slate100
              )
            }
          }
        },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = when {
                secondaryDestination != null -> when (secondaryDestination!!) {
                  SecondaryDestination.TIMER -> "Focus Timer"
                  SecondaryDestination.SLEEP -> "Sleep & Recovery"
                  SecondaryDestination.FUTURE_TASKS -> "Future & Interviews"
                  SecondaryDestination.REPORTS -> "Monthly Intelligence"
                  SecondaryDestination.REFLECTIONS_ARCHIVE -> "Reflections Archive"
                }
                currentTab == AlignNavTab.HOME -> "AlignOS"
                currentTab == AlignNavTab.TODAY -> "Today's Command"
                currentTab == AlignNavTab.FINANCE -> "Financial Discipline"
                currentTab == AlignNavTab.MENU -> "Life Hub"
                else -> "AlignOS"
              },
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
            )
          }
        },
        actions = {
          Row(
            modifier = Modifier
              .padding(end = 4.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(EmeraldBg)
              .border(1.dp, Emerald400.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.CloudDone,
              contentDescription = "Offline Synced",
              tint = Emerald400,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Offline-First",
              color = Emerald400,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = { showLogoutConfirmDialog = true },
            modifier = Modifier.testTag("btn_topbar_logout")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Sign Out",
              tint = Emerald400,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      )
    },
    bottomBar = {
      AlignBottomNavBar(
        currentTab = currentTab,
        onTabSelected = { tab ->
          secondaryDestination = null
          currentTab = tab
        },
        onQuickActionClick = {
          showQuickActionSheet = true
        }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (secondaryDestination != null) {
        when (secondaryDestination!!) {
          SecondaryDestination.TIMER -> TimerScreen(viewModel = viewModel)
          SecondaryDestination.SLEEP -> SleepScreen(viewModel = viewModel)
          SecondaryDestination.FUTURE_TASKS -> FutureTasksScreen(
            viewModel = viewModel,
            onLaunchStudySession = {
              secondaryDestination = SecondaryDestination.TIMER
            }
          )
          SecondaryDestination.REPORTS -> IntelligenceReportsScreen(viewModel = viewModel)
          SecondaryDestination.REFLECTIONS_ARCHIVE -> ReflectionsHistoryScreen(viewModel = viewModel)
        }
      } else {
        when (currentTab) {
          AlignNavTab.HOME -> HomeScreen(
            viewModel = viewModel,
            onNavigateToTab = { index ->
              when (index) {
                1 -> currentTab = AlignNavTab.TODAY
                2 -> currentTab = AlignNavTab.FINANCE
                else -> currentTab = AlignNavTab.HOME
              }
            },
            onRequestQuickAction = { actionType ->
              when (actionType) {
                QuickActionType.EXPENSE -> showQuickExpenseDialog = true
                QuickActionType.INCOME -> showQuickIncomeDialog = true
                QuickActionType.FOCUS_TIMER -> secondaryDestination = SecondaryDestination.TIMER
                QuickActionType.SLEEP -> showQuickSleepDialog = true
                QuickActionType.REFLECTION -> currentTab = AlignNavTab.TODAY
              }
            }
          )

          AlignNavTab.TODAY -> TodayScreen(viewModel = viewModel)

          AlignNavTab.FINANCE -> FinanceScreen(viewModel = viewModel)

          AlignNavTab.MENU -> MenuScreen(
            viewModel = viewModel,
            onNavigateToSecondary = { dest ->
              secondaryDestination = dest
            },
            onLogoutClick = {
              showLogoutConfirmDialog = true
            }
          )

          AlignNavTab.PLUS -> {
            // Unused as tab since clicking triggers onQuickActionClick
          }
        }
      }
    }
  }

  // Quick Action Modal Bottom Sheet
  if (showQuickActionSheet) {
    QuickActionBottomSheet(
      sheetState = sheetState,
      onDismiss = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
          showQuickActionSheet = false
        }
      },
      onSelectAction = { actionType ->
        scope.launch { sheetState.hide() }.invokeOnCompletion {
          showQuickActionSheet = false
          when (actionType) {
            QuickActionType.EXPENSE -> showQuickExpenseDialog = true
            QuickActionType.INCOME -> showQuickIncomeDialog = true
            QuickActionType.FOCUS_TIMER -> secondaryDestination = SecondaryDestination.TIMER
            QuickActionType.SLEEP -> showQuickSleepDialog = true
            QuickActionType.REFLECTION -> currentTab = AlignNavTab.TODAY
          }
        }
      }
    )
  }

  // Quick Expense Dialog
  if (showQuickExpenseDialog) {
    AddTransactionDialog(
      initialType = "expense",
      selectedDate = selectedDate,
      onDismiss = { showQuickExpenseDialog = false },
      onSave = { type, category, amount, desc, date ->
        viewModel.addTransaction(type, category, amount, desc, date)
      }
    )
  }

  // Quick Income Dialog
  if (showQuickIncomeDialog) {
    AddTransactionDialog(
      initialType = "income",
      selectedDate = selectedDate,
      onDismiss = { showQuickIncomeDialog = false },
      onSave = { type, category, amount, desc, date ->
        viewModel.addTransaction(type, category, amount, desc, date)
      }
    )
  }

  // Quick Sleep Dialog
  if (showQuickSleepDialog) {
    AddSleepDialog(
      selectedDate = selectedDate,
      onDismiss = { showQuickSleepDialog = false },
      onSave = { bedTime, wakeTime, duration, quality, notes, date ->
        viewModel.saveSleepRecord(bedTime, wakeTime, duration, quality, notes, date)
      }
    )
  }

  // Quick Timer Dialog
  if (showQuickTimerDialog) {
    AddTimeLogDialog(
      selectedDate = selectedDate,
      onDismiss = { showQuickTimerDialog = false },
      onSave = { activity, category, duration, isProductive, date ->
        viewModel.addTimeLog(activity, category, duration, isProductive, date)
      }
    )
  }

  // Logout Confirmation Dialog
  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      containerColor = Slate900,
      titleContentColor = Slate100,
      textContentColor = Slate300,
      shape = RoundedCornerShape(18.dp),
      title = {
        Text("Sign Out of AlignOS?", fontWeight = FontWeight.Bold)
      },
      text = {
        Text("Your records remain safely encrypted in your local Room database. You can sign back in anytime.")
      },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirmDialog = false
            viewModel.logout()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Emerald500,
            contentColor = Slate950
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Sign Out", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirmDialog = false }) {
          Text("Cancel", color = Slate400, fontWeight = FontWeight.Medium)
        }
      }
    )
  }
}
