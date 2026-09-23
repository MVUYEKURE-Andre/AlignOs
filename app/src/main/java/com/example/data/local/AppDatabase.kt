package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Database(
  entities = [
    UserConfigEntity::class,
    TransactionEntity::class,
    TimeLogEntity::class,
    SleepRecordEntity::class,
    TaskItemEntity::class,
    DailyReflectionEntity::class,
    ZeroSpendEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun alignDao(): AlignDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "align_os_database"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { seedInitialData(it.alignDao()) }
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    suspend fun seedInitialData(dao: AlignDao) {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val cal = Calendar.getInstance()
      val today = dateFormat.format(cal.time)

      cal.add(Calendar.DAY_OF_YEAR, -1)
      val yesterday = dateFormat.format(cal.time)

      cal.add(Calendar.DAY_OF_YEAR, -1)
      val twoDaysAgo = dateFormat.format(cal.time)

      cal.time = Date()
      cal.add(Calendar.DAY_OF_YEAR, 1)
      val tomorrow = dateFormat.format(cal.time)

      cal.add(Calendar.DAY_OF_YEAR, 2)
      val threeDaysLater = dateFormat.format(cal.time)

      // 1. User Config
      dao.saveUserConfig(
        UserConfigEntity(
          id = 1,
          userName = "Alex",
          currency = "USD",
          monthlyIncome = 4200.0,
          monthlySavingsTarget = 1000.0,
          monthlyFixedCosts = 1400.0,
          habitsListJson = "[\"Read 20m\",\"Deep Work 90m\",\"Hydrate 2.5L\",\"Workout\"]"
        )
      )

      // 2. Sleep Records
      dao.insertSleepRecord(
        SleepRecordEntity(
          id = UUID.randomUUID().toString(),
          sleepDate = today,
          bedTime = "23:15",
          wakeTime = "06:45",
          durationMinutes = 450, // 7.5 hrs
          quality = "good",
          notes = "Felt rested, no midnight wakeups. Ready for deep work."
        )
      )
      dao.insertSleepRecord(
        SleepRecordEntity(
          id = UUID.randomUUID().toString(),
          sleepDate = yesterday,
          bedTime = "23:45",
          wakeTime = "07:00",
          durationMinutes = 435, // 7.25 hrs
          quality = "good",
          notes = "Decent recovery after light evening reading."
        )
      )
      dao.insertSleepRecord(
        SleepRecordEntity(
          id = UUID.randomUUID().toString(),
          sleepDate = twoDaysAgo,
          bedTime = "00:30",
          wakeTime = "06:30",
          durationMinutes = 360, // 6 hrs
          quality = "fair",
          notes = "Slight sleep debt due to late coding session."
        )
      )

      // 3. Transactions
      dao.insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = "expense",
          category = "Food",
          amount = 14.50,
          date = today,
          description = "Nutritious lunch & espresso"
        )
      )
      dao.insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = "expense",
          category = "Study",
          amount = 29.00,
          date = yesterday,
          description = "System Design & Algorithms Book"
        )
      )
      dao.insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = "income",
          category = "Consulting",
          amount = 650.00,
          date = yesterday,
          description = "Freelance mobile milestone payment"
        )
      )

      // 4. Time Logs (Focus & Deep Work)
      dao.insertTimeLog(
        TimeLogEntity(
          id = UUID.randomUUID().toString(),
          activity = "Compose Architecture & State",
          category = "coding",
          durationMinutes = 90,
          isProductive = true,
          date = today
        )
      )
      dao.insertTimeLog(
        TimeLogEntity(
          id = UUID.randomUUID().toString(),
          activity = "Algorithm Practice - Trees & DP",
          category = "study",
          durationMinutes = 60,
          isProductive = true,
          date = today
        )
      )
      dao.insertTimeLog(
        TimeLogEntity(
          id = UUID.randomUUID().toString(),
          activity = "System Architecture Deep Dive",
          category = "work",
          durationMinutes = 120,
          isProductive = true,
          date = yesterday
        )
      )

      // 5. Tasks
      dao.insertTask(
        TaskItemEntity(
          id = UUID.randomUUID().toString(),
          title = "Complete AlignOS Tracking Alignment UI",
          category = "career",
          priority = "high",
          date = today,
          status = "completed",
          notes = "Implement circular gauge, verified list, and missing alert buttons.",
          subtasksJson = "[{\"id\":\"st1\",\"title\":\"Build 5-pillar completeness gauge\",\"completed\":true},{\"id\":\"st2\",\"title\":\"Add amber alerts with 1-tap actions\",\"completed\":true}]"
        )
      )
      dao.insertTask(
        TaskItemEntity(
          id = UUID.randomUUID().toString(),
          title = "Review Distributed Systems Latency Patterns",
          category = "study",
          priority = "high",
          date = today,
          status = "pending",
          notes = "Focus on p99 latency reduction and consensus protocols.",
          subtasksJson = "[{\"id\":\"st3\",\"title\":\"Read Raft paper section 5\",\"completed\":false},{\"id\":\"st4\",\"title\":\"Summarize cache stampede mitigations\",\"completed\":false}]"
        )
      )
      dao.insertTask(
        TaskItemEntity(
          id = UUID.randomUUID().toString(),
          title = "Lead Technical Interview with Stripe",
          category = "interview",
          priority = "high",
          date = tomorrow,
          status = "pending",
          notes = "Round 2: System design & architecture walkthrough with Principal Engineer.",
          interviewCompany = "Stripe",
          interviewRole = "Senior Mobile Systems Engineer",
          interviewTime = "14:00 (45 mins)",
          interviewPrepChecklistJson = "[\"Review idempotent payment API patterns\",\"Prepare offline transaction sync explanation\",\"Review distributed cache invalidation\"]"
        )
      )
      dao.insertTask(
        TaskItemEntity(
          id = UUID.randomUUID().toString(),
          title = "Mock Interview: Google L5 Architecture",
          category = "interview",
          priority = "medium",
          date = threeDaysLater,
          status = "pending",
          notes = "Peer interview covering large scale real-time sync engine.",
          interviewCompany = "Google",
          interviewRole = "Staff Android Engineer",
          interviewTime = "10:30 (60 mins)",
          interviewPrepChecklistJson = "[\"High throughput mobile analytics pipeline\",\"Battery and network radio optimization\"]"
        )
      )

      // 6. Daily Reflections
      dao.saveReflection(
        DailyReflectionEntity(
          id = UUID.randomUUID().toString(),
          date = yesterday,
          mood = "great",
          energyLevel = 5,
          howDayWent = "Locked in 3+ hours of unbroken deep work. Solved the offline caching sync edge case cleanly. Feeling aligned and confident.",
          highlights = "Finished milestone delivery ahead of time and stayed within daily spending allowance.",
          challenges = "Need to make sure I get to sleep by 23:00 to avoid late morning grogginess.",
          habitsCheckedJson = "{\"Read 20m\":true,\"Deep Work 90m\":true,\"Hydrate 2.5L\":true,\"Workout\":true}"
        )
      )
    }
  }
}
