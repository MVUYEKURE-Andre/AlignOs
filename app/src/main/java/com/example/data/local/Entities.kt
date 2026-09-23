package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_config")
data class UserConfigEntity(
  @PrimaryKey val id: Int = 1,
  val userName: String = "Alex",
  val currency: String = "USD",
  val monthlyIncome: Double = 3200.0,
  val monthlySavingsTarget: Double = 800.0,
  val monthlyFixedCosts: Double = 1200.0,
  val habitsListJson: String = "[\"Read 20m\",\"Study/Code\",\"Zero Alcohol\",\"Workout\"]"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey val id: String,
  val userId: String = "user_default",
  val type: String, // "expense" | "income"
  val category: String,
  val amount: Double,
  val date: String, // YYYY-MM-DD
  val description: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "time_logs")
data class TimeLogEntity(
  @PrimaryKey val id: String,
  val userId: String = "user_default",
  val activity: String,
  val category: String, // work, study, coding, fitness, personal, leisure
  val durationMinutes: Int,
  val isProductive: Boolean = true,
  val date: String, // YYYY-MM-DD
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sleep_records")
data class SleepRecordEntity(
  @PrimaryKey val id: String,
  val userId: String = "user_default",
  val sleepDate: String, // YYYY-MM-DD
  val bedTime: String,   // HH:mm
  val wakeTime: String,  // HH:mm
  val durationMinutes: Int,
  val quality: String,   // poor, fair, good, excellent
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_items")
data class TaskItemEntity(
  @PrimaryKey val id: String,
  val userId: String = "user_default",
  val title: String,
  val category: String, // interview, study, career, personal, errand
  val priority: String, // high, medium, low
  val date: String,     // YYYY-MM-DD
  val status: String,   // pending, in_progress, completed
  val notes: String = "",
  val subtasksJson: String = "[]",
  val interviewCompany: String? = null,
  val interviewRole: String? = null,
  val interviewTime: String? = null,
  val interviewPrepChecklistJson: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_reflections")
data class DailyReflectionEntity(
  @PrimaryKey val id: String,
  val userId: String = "user_default",
  val date: String, // YYYY-MM-DD
  val mood: String, // great, good, neutral, tough, stressed
  val energyLevel: Int, // 1 to 5
  val howDayWent: String,
  val highlights: String = "",
  val challenges: String = "",
  val habitsCheckedJson: String = "{}",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "zero_spend_days")
data class ZeroSpendEntity(
  @PrimaryKey val date: String, // YYYY-MM-DD
  val declaredAt: Long = System.currentTimeMillis()
)
