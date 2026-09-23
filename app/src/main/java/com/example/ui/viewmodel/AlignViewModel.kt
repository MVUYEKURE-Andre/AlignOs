package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyReflectionEntity
import com.example.data.local.SleepRecordEntity
import com.example.data.local.TaskItemEntity
import com.example.data.local.TimeLogEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserConfigEntity
import com.example.data.model.ActivityStreamItem
import com.example.data.model.DailyMood
import com.example.data.model.PillarAlignmentState
import com.example.data.model.SubtaskItem
import com.example.data.model.TaskStatus
import com.example.data.model.TimeCategory
import com.example.data.repository.AlignRepository
import com.example.util.DateUtils
import com.example.util.JsonUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AlignViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: AlignRepository

  init {
    val db = AppDatabase.getDatabase(application)
    repository = AlignRepository(db.alignDao())
    viewModelScope.launch {
      try {
        val existingTasks = repository.allTasks.first()
        if (existingTasks.isEmpty()) {
          AppDatabase.seedInitialData(db.alignDao())
        }
      } catch (_: Exception) {}
    }
  }

  // Authentication & Profile State
  private val authPrefs = application.getSharedPreferences("alignos_auth_pref", Context.MODE_PRIVATE)

  private val _isLoggedIn = MutableStateFlow(authPrefs.getBoolean("is_logged_in", true))
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _currentUserEmail = MutableStateFlow(
    authPrefs.getString("user_email", "andremvuyekure@gmail.com") ?: "andremvuyekure@gmail.com"
  )
  val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

  private val _currentUserName = MutableStateFlow(
    authPrefs.getString("user_name", "Andre") ?: "Andre"
  )
  val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

  fun login(email: String, name: String = "Andre") {
    authPrefs.edit()
      .putBoolean("is_logged_in", true)
      .putString("user_email", email)
      .putString("user_name", name)
      .apply()
    _currentUserEmail.value = email
    _currentUserName.value = name
    _isLoggedIn.value = true
  }

  fun logout() {
    authPrefs.edit()
      .putBoolean("is_logged_in", false)
      .apply()
    _isLoggedIn.value = false
  }

  // Selected Date State
  private val _selectedDate = MutableStateFlow(DateUtils.today())
  val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

  fun setSelectedDate(date: String) {
    _selectedDate.value = date
  }

  // User Config
  val userConfig: StateFlow<UserConfigEntity> = repository.userConfig
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserConfigEntity())

  // Transactions
  val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val selectedDateTransactions: StateFlow<List<TransactionEntity>> = _selectedDate
    .flatMapLatest { repository.getTransactionsByDate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Time Logs
  val allTimeLogs: StateFlow<List<TimeLogEntity>> = repository.allTimeLogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val selectedDateTimeLogs: StateFlow<List<TimeLogEntity>> = _selectedDate
    .flatMapLatest { repository.getTimeLogsByDate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Sleep Records
  val allSleepRecords: StateFlow<List<SleepRecordEntity>> = repository.allSleepRecords
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val selectedDateSleep: StateFlow<SleepRecordEntity?> = _selectedDate
    .flatMapLatest { repository.getSleepRecordByDate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Tasks
  val allTasks: StateFlow<List<TaskItemEntity>> = repository.allTasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val selectedDateTasks: StateFlow<List<TaskItemEntity>> = _selectedDate
    .flatMapLatest { repository.getTasksByDate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val interviewTasks: StateFlow<List<TaskItemEntity>> = repository.interviewTasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Daily Reflections
  val allReflections: StateFlow<List<DailyReflectionEntity>> = repository.allReflections
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val selectedDateReflection: StateFlow<DailyReflectionEntity?> = _selectedDate
    .flatMapLatest { repository.getReflectionByDate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Zero Spend
  val allZeroSpendDays: StateFlow<List<com.example.data.local.ZeroSpendEntity>> = repository.allZeroSpendDays
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val isZeroSpendSelectedDate: StateFlow<Boolean> = _selectedDate
    .flatMapLatest { repository.getZeroSpendByDate(it) }
    .combine(selectedDateTransactions) { zeroSpend, txs ->
      zeroSpend != null || (txs.isEmpty() && false)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  // 5-Pillar Alignment State for Selected Date
  val alignmentState: StateFlow<PillarAlignmentState> = combine(
    selectedDateSleep,
    selectedDateTransactions,
    isZeroSpendSelectedDate,
    selectedDateTimeLogs,
    combine(selectedDateTasks, selectedDateReflection) { tasks, ref -> Pair(tasks, ref) }
  ) { sleep, txs, isZeroSpend, timeLogs, tasksAndRef ->
    val (tasks, reflection) = tasksAndRef
    val sleepLogged = sleep != null
    val sleepSummary = sleep?.let {
      val hrs = it.durationMinutes / 60
      val mins = it.durationMinutes % 60
      "Logged ${hrs}h ${mins}m sleep (${it.quality.replaceFirstChar { c -> c.uppercase() }})"
    }

    val financeLogged = txs.isNotEmpty()
    val isZero = isZeroSpend
    val totalSpend = txs.filter { it.type == "expense" }.sumOf { it.amount }
    val financeSummary = when {
      isZero -> "Declared Zero-Spend Day (0.00 spent)"
      financeLogged -> "Tracked ${txs.size} transaction(s) ($${String.format("%.2f", totalSpend)})"
      else -> null
    }

    val productiveMins = timeLogs.filter { it.isProductive }.sumOf { it.durationMinutes }
    val focusLogged = productiveMins > 0 || timeLogs.isNotEmpty()
    val focusSummary = if (focusLogged) {
      val h = productiveMins / 60
      val m = productiveMins % 60
      "Logged ${h}h ${m}m Deep Work"
    } else null

    val completedTasks = tasks.count { it.status.equals("completed", ignoreCase = true) }
    val taskLogged = tasks.isNotEmpty()
    val taskSummary = if (taskLogged) {
      "Completed $completedTasks of ${tasks.size} priority task(s)"
    } else null

    val reflectionLogged = reflection != null
    val reflectionSummary = reflection?.let {
      "Daily reflection written (Mood: ${it.mood.replaceFirstChar { c -> c.uppercase() }}, Energy ${it.energyLevel}/5)"
    }

    PillarAlignmentState(
      sleepLogged = sleepLogged,
      sleepSummary = sleepSummary,
      financeLogged = financeLogged,
      isZeroSpend = isZero,
      financeSummary = financeSummary,
      focusTimeLogged = focusLogged,
      focusSummary = focusSummary,
      tasksLogged = taskLogged,
      taskSummary = taskSummary,
      reflectionLogged = reflectionLogged,
      reflectionSummary = reflectionSummary
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PillarAlignmentState())

  // Live Activity Stream
  val recentStream: StateFlow<List<ActivityStreamItem>> = combine(
    allTransactions,
    allTimeLogs,
    allSleepRecords,
    allTasks,
    allReflections
  ) { txs, times, sleeps, tasks, reflections ->
    val items = mutableListOf<ActivityStreamItem>()

    txs.take(4).forEach {
      val isExpense = it.type == "expense"
      items.add(
        ActivityStreamItem(
          id = "tx_${it.id}",
          title = if (isExpense) "Spent $${String.format("%.2f", it.amount)} on ${it.category}" else "Received $${String.format("%.2f", it.amount)} (${it.category})",
          subtitle = "${it.date} • ${it.description}",
          pillar = "Finance",
          timestamp = it.createdAt,
          iconType = if (isExpense) "expense" else "income"
        )
      )
    }

    times.take(4).forEach {
      items.add(
        ActivityStreamItem(
          id = "time_${it.id}",
          title = "Logged ${it.durationMinutes}m ${it.category.replaceFirstChar { c -> c.uppercase() }}",
          subtitle = "${it.date} • ${it.activity}",
          pillar = "Focus",
          timestamp = it.createdAt,
          iconType = "timer"
        )
      )
    }

    sleeps.take(2).forEach {
      val hrs = it.durationMinutes / 60
      val mins = it.durationMinutes % 60
      items.add(
        ActivityStreamItem(
          id = "sleep_${it.id}",
          title = "Logged ${hrs}h ${mins}m sleep (${it.quality})",
          subtitle = "${it.sleepDate} • ${it.bedTime} to ${it.wakeTime}",
          pillar = "Sleep",
          timestamp = it.createdAt,
          iconType = "sleep"
        )
      )
    }

    tasks.filter { it.status == "completed" }.take(3).forEach {
      items.add(
        ActivityStreamItem(
          id = "task_${it.id}",
          title = "Completed: ${it.title}",
          subtitle = "${it.date} • ${it.category.replaceFirstChar { c -> c.uppercase() }} priority: ${it.priority}",
          pillar = "Tasks",
          timestamp = it.createdAt,
          iconType = "task"
        )
      )
    }

    reflections.take(2).forEach {
      items.add(
        ActivityStreamItem(
          id = "ref_${it.id}",
          title = "Daily Reflection Saved (${it.mood})",
          subtitle = "${it.date} • Energy: ${it.energyLevel}/5",
          pillar = "Reflection",
          timestamp = it.createdAt,
          iconType = "reflection"
        )
      )
    }

    items.sortedByDescending { it.timestamp }.take(10)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Search in Reflections
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _searchScopeAll = MutableStateFlow(true)
  val searchScopeAll: StateFlow<Boolean> = _searchScopeAll.asStateFlow()

  fun setSearchQuery(q: String) {
    _searchQuery.value = q
  }

  fun setSearchScopeAll(all: Boolean) {
    _searchScopeAll.value = all
  }

  val searchResults: StateFlow<List<DailyReflectionEntity>> = combine(
    allReflections,
    searchQuery,
    searchScopeAll
  ) { reflections, query, scopeAll ->
    val currentMonth = DateUtils.currentMonthPrefix()
    val filteredByScope = if (scopeAll) {
      reflections
    } else {
      reflections.filter { it.date.startsWith(currentMonth) }
    }
    if (query.isBlank()) {
      filteredByScope
    } else {
      val lower = query.lowercase()
      filteredByScope.filter {
        it.howDayWent.lowercase().contains(lower) ||
            it.highlights.lowercase().contains(lower) ||
            it.challenges.lowercase().contains(lower) ||
            it.mood.lowercase().contains(lower)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Focus Timer Engine
  private val _timerSeconds = MutableStateFlow(25 * 60)
  val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

  private val _timerMode = MutableStateFlow("Pomodoro (25m)")
  val timerMode: StateFlow<String> = _timerMode.asStateFlow()

  private val _timerCategory = MutableStateFlow(TimeCategory.CODING)
  val timerCategory: StateFlow<TimeCategory> = _timerCategory.asStateFlow()

  private val _timerActivity = MutableStateFlow("Architecture & Deep Work")
  val timerActivity: StateFlow<String> = _timerActivity.asStateFlow()

  private var timerJob: Job? = null
  private var accumulatedElapsedSeconds = 0

  fun setTimerPreset(mode: String, minutes: Int) {
    pauseTimer()
    _timerMode.value = mode
    _timerSeconds.value = minutes * 60
    accumulatedElapsedSeconds = 0
  }

  fun setTimerCategory(cat: TimeCategory) {
    _timerCategory.value = cat
  }

  fun setTimerActivity(activity: String) {
    _timerActivity.value = activity
  }

  fun startTimer() {
    if (_isTimerRunning.value) return
    _isTimerRunning.value = true
    timerJob = viewModelScope.launch {
      while (_isTimerRunning.value) {
        delay(1000)
        if (_timerMode.value.contains("Stopwatch")) {
          _timerSeconds.value += 1
          accumulatedElapsedSeconds += 1
        } else {
          if (_timerSeconds.value > 0) {
            _timerSeconds.value -= 1
            accumulatedElapsedSeconds += 1
          } else {
            _isTimerRunning.value = false
            break
          }
        }
      }
    }
  }

  fun pauseTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
    timerJob = null
  }

  fun resetTimer() {
    pauseTimer()
    val defaultMinutes = when {
      _timerMode.value.contains("50") -> 50
      _timerMode.value.contains("90") -> 90
      _timerMode.value.contains("Stopwatch") -> 0
      else -> 25
    }
    _timerSeconds.value = defaultMinutes * 60
    accumulatedElapsedSeconds = 0
  }

  fun finishAndLogTimer(onLogged: () -> Unit = {}) {
    val durationMins = maxOf(1, accumulatedElapsedSeconds / 60)
    val activity = _timerActivity.value.ifBlank { "Deep Work Session" }
    val category = _timerCategory.value.name.lowercase()

    viewModelScope.launch {
      repository.insertTimeLog(
        TimeLogEntity(
          id = UUID.randomUUID().toString(),
          activity = activity,
          category = category,
          durationMinutes = durationMins,
          isProductive = true,
          date = DateUtils.today()
        )
      )
      resetTimer()
      onLogged()
    }
  }

  // Database Mutations
  fun toggleZeroSpend(date: String) {
    viewModelScope.launch {
      val existing = isZeroSpendSelectedDate.value
      if (existing) {
        repository.removeZeroSpend(date)
      } else {
        repository.markZeroSpend(date)
      }
    }
  }

  fun addTransaction(
    type: String,
    category: String,
    amount: Double,
    description: String,
    date: String
  ) {
    viewModelScope.launch {
      repository.insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = type,
          category = category,
          amount = amount,
          date = date,
          description = description
        )
      )
    }
  }

  fun deleteTransaction(id: String) {
    viewModelScope.launch {
      repository.deleteTransaction(id)
    }
  }

  fun addTimeLog(
    activity: String,
    category: String,
    durationMinutes: Int,
    isProductive: Boolean,
    date: String
  ) {
    viewModelScope.launch {
      repository.insertTimeLog(
        TimeLogEntity(
          id = UUID.randomUUID().toString(),
          activity = activity,
          category = category,
          durationMinutes = durationMinutes,
          isProductive = isProductive,
          date = date
        )
      )
    }
  }

  fun deleteTimeLog(id: String) {
    viewModelScope.launch {
      repository.deleteTimeLog(id)
    }
  }

  fun saveSleepRecord(
    bedTime: String,
    wakeTime: String,
    durationMinutes: Int,
    quality: String,
    notes: String,
    date: String
  ) {
    viewModelScope.launch {
      repository.insertSleepRecord(
        SleepRecordEntity(
          id = UUID.randomUUID().toString(),
          sleepDate = date,
          bedTime = bedTime,
          wakeTime = wakeTime,
          durationMinutes = durationMinutes,
          quality = quality,
          notes = notes
        )
      )
    }
  }

  fun deleteSleepRecord(id: String) {
    viewModelScope.launch {
      repository.deleteSleepRecord(id)
    }
  }

  fun addTask(
    title: String,
    category: String,
    priority: String,
    date: String,
    notes: String,
    subtasks: List<SubtaskItem> = emptyList(),
    company: String? = null,
    role: String? = null,
    interviewTime: String? = null,
    prepItems: List<String>? = null
  ) {
    viewModelScope.launch {
      repository.insertTask(
        TaskItemEntity(
          id = UUID.randomUUID().toString(),
          title = title,
          category = category,
          priority = priority,
          date = date,
          status = "pending",
          notes = notes,
          subtasksJson = JsonUtils.serializeSubtasks(subtasks),
          interviewCompany = company,
          interviewRole = role,
          interviewTime = interviewTime,
          interviewPrepChecklistJson = prepItems?.let { JsonUtils.serializeStringList(it) }
        )
      )
    }
  }

  fun toggleTaskStatus(task: TaskItemEntity) {
    viewModelScope.launch {
      val newStatus = if (task.status == "completed") "pending" else "completed"
      repository.updateTask(task.copy(status = newStatus))
    }
  }

  fun toggleSubtask(task: TaskItemEntity, subtaskId: String) {
    viewModelScope.launch {
      val subtasks = JsonUtils.parseSubtasks(task.subtasksJson).map {
        if (it.id == subtaskId) it.copy(completed = !it.completed) else it
      }
      repository.updateTask(task.copy(subtasksJson = JsonUtils.serializeSubtasks(subtasks)))
    }
  }

  fun moveTaskDate(task: TaskItemEntity, newDate: String) {
    viewModelScope.launch {
      repository.updateTask(task.copy(date = newDate))
    }
  }

  fun deleteTask(id: String) {
    viewModelScope.launch {
      repository.deleteTask(id)
    }
  }

  fun saveDailyReflection(
    date: String,
    mood: DailyMood,
    energyLevel: Int,
    howDayWent: String,
    highlights: String,
    challenges: String,
    habitsChecked: Map<String, Boolean>
  ) {
    viewModelScope.launch {
      repository.saveReflection(
        DailyReflectionEntity(
          id = UUID.randomUUID().toString(),
          date = date,
          mood = mood.name.lowercase(),
          energyLevel = energyLevel,
          howDayWent = howDayWent,
          highlights = highlights,
          challenges = challenges,
          habitsCheckedJson = JsonUtils.serializeHabitsChecked(habitsChecked),
          updatedAt = System.currentTimeMillis()
        )
      )
    }
  }

  fun deleteReflection(id: String) {
    viewModelScope.launch {
      repository.deleteReflection(id)
    }
  }

  fun updateUserFinancialConfig(
    userName: String,
    income: Double,
    fixedCosts: Double,
    savingsTarget: Double
  ) {
    val currentConfig = userConfig.value
    viewModelScope.launch {
      repository.saveUserConfig(
        currentConfig.copy(
          userName = userName,
          monthlyIncome = income,
          monthlyFixedCosts = fixedCosts,
          monthlySavingsTarget = savingsTarget
        )
      )
    }
  }

  fun updateConfig(
    userName: String,
    currency: String,
    income: Double,
    savingsTarget: Double,
    fixedCosts: Double,
    habits: List<String>
  ) {
    viewModelScope.launch {
      repository.saveUserConfig(
        UserConfigEntity(
          id = 1,
          userName = userName,
          currency = currency,
          monthlyIncome = income,
          monthlySavingsTarget = savingsTarget,
          monthlyFixedCosts = fixedCosts,
          habitsListJson = JsonUtils.serializeHabits(habits)
        )
      )
    }
  }
}
