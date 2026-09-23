package com.example.data.model

import java.util.UUID

enum class CurrencyCode(val symbol: String, val displayName: String) {
  RWF("RWF", "Rwandan Franc (RWF)"),
  USD("$", "US Dollar ($)"),
  EUR("€", "Euro (€)"),
  GBP("£", "British Pound (£)")
}

enum class DailyMood(val emoji: String, val label: String) {
  GREAT("😄", "Great"),
  GOOD("🙂", "Good"),
  NEUTRAL("😐", "Neutral"),
  TOUGH("😔", "Tough"),
  STRESSED("😫", "Stressed");

  companion object {
    fun fromString(value: String): DailyMood {
      return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
        ?: GOOD
    }
  }
}

enum class SleepQuality(val label: String, val score: Int) {
  POOR("Poor", 1),
  FAIR("Fair", 2),
  GOOD("Good", 3),
  EXCELLENT("Excellent", 4);

  companion object {
    fun fromString(value: String): SleepQuality {
      return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
        ?: GOOD
    }
  }
}

enum class TaskCategory(val label: String) {
  INTERVIEW("Interview"),
  STUDY("Study"),
  CAREER("Career"),
  PERSONAL("Personal"),
  ERRAND("Errand");

  companion object {
    fun fromString(value: String): TaskCategory {
      return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
        ?: PERSONAL
    }
  }
}

enum class TaskPriority(val label: String) {
  HIGH("High"),
  MEDIUM("Medium"),
  LOW("Low");

  companion object {
    fun fromString(value: String): TaskPriority {
      return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
        ?: MEDIUM
    }
  }
}

enum class TaskStatus {
  PENDING,
  IN_PROGRESS,
  COMPLETED;

  companion object {
    fun fromString(value: String): TaskStatus {
      return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
    }
  }
}

data class SubtaskItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val completed: Boolean = false
)

enum class TimeCategory(val label: String) {
  WORK("Work"),
  STUDY("Study"),
  CODING("Coding"),
  FITNESS("Fitness"),
  PERSONAL("Personal"),
  LEISURE("Leisure");

  companion object {
    fun fromString(value: String): TimeCategory {
      return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
        ?: WORK
    }
  }
}

enum class TransactionType {
  EXPENSE,
  INCOME;

  companion object {
    fun fromString(value: String): TransactionType {
      return entries.find { it.name.equals(value, ignoreCase = true) } ?: EXPENSE
    }
  }
}

data class PillarAlignmentState(
  val sleepLogged: Boolean = false,
  val sleepSummary: String? = null,
  val financeLogged: Boolean = false,
  val isZeroSpend: Boolean = false,
  val financeSummary: String? = null,
  val focusTimeLogged: Boolean = false,
  val focusSummary: String? = null,
  val tasksLogged: Boolean = false,
  val taskSummary: String? = null,
  val reflectionLogged: Boolean = false,
  val reflectionSummary: String? = null
) {
  val loggedCount: Int
    get() {
      var count = 0
      if (sleepLogged) count++
      if (financeLogged || isZeroSpend) count++
      if (focusTimeLogged) count++
      if (tasksLogged) count++
      if (reflectionLogged) count++
      return count
    }

  val completenessPercentage: Int
    get() = (loggedCount * 100) / 5
}

data class ActivityStreamItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val pillar: String,
  val timestamp: Long,
  val iconType: String
)
