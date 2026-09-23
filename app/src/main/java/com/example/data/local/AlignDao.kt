package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlignDao {

  // User Config
  @Query("SELECT * FROM user_config WHERE id = 1 LIMIT 1")
  fun getUserConfig(): Flow<UserConfigEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUserConfig(config: UserConfigEntity)

  // Transactions
  @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE date = :date ORDER BY createdAt DESC")
  fun getTransactionsByDate(date: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC")
  fun getTransactionsByMonth(monthPrefix: String): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity)

  @Query("DELETE FROM transactions WHERE id = :id")
  suspend fun deleteTransaction(id: String)

  // Time Logs
  @Query("SELECT * FROM time_logs ORDER BY date DESC, createdAt DESC")
  fun getAllTimeLogs(): Flow<List<TimeLogEntity>>

  @Query("SELECT * FROM time_logs WHERE date = :date ORDER BY createdAt DESC")
  fun getTimeLogsByDate(date: String): Flow<List<TimeLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTimeLog(timeLog: TimeLogEntity)

  @Query("DELETE FROM time_logs WHERE id = :id")
  suspend fun deleteTimeLog(id: String)

  // Sleep Records
  @Query("SELECT * FROM sleep_records ORDER BY sleepDate DESC, createdAt DESC")
  fun getAllSleepRecords(): Flow<List<SleepRecordEntity>>

  @Query("SELECT * FROM sleep_records WHERE sleepDate = :date LIMIT 1")
  fun getSleepRecordByDate(date: String): Flow<SleepRecordEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSleepRecord(record: SleepRecordEntity)

  @Query("DELETE FROM sleep_records WHERE id = :id")
  suspend fun deleteSleepRecord(id: String)

  // Tasks
  @Query("SELECT * FROM task_items ORDER BY date ASC, priority DESC, createdAt DESC")
  fun getAllTasks(): Flow<List<TaskItemEntity>>

  @Query("SELECT * FROM task_items WHERE date = :date ORDER BY status ASC, priority DESC, createdAt DESC")
  fun getTasksByDate(date: String): Flow<List<TaskItemEntity>>

  @Query("SELECT * FROM task_items WHERE category = 'interview' OR interviewCompany IS NOT NULL ORDER BY date ASC")
  fun getInterviewTasks(): Flow<List<TaskItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskItemEntity)

  @Update
  suspend fun updateTask(task: TaskItemEntity)

  @Query("DELETE FROM task_items WHERE id = :id")
  suspend fun deleteTask(id: String)

  // Reflections
  @Query("SELECT * FROM daily_reflections ORDER BY date DESC")
  fun getAllReflections(): Flow<List<DailyReflectionEntity>>

  @Query("SELECT * FROM daily_reflections WHERE date = :date LIMIT 1")
  fun getReflectionByDate(date: String): Flow<DailyReflectionEntity?>

  @Query("SELECT * FROM daily_reflections WHERE howDayWent LIKE '%' || :query || '%' OR highlights LIKE '%' || :query || '%' OR challenges LIKE '%' || :query || '%' ORDER BY date DESC")
  fun searchReflections(query: String): Flow<List<DailyReflectionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveReflection(reflection: DailyReflectionEntity)

  @Query("DELETE FROM daily_reflections WHERE id = :id")
  suspend fun deleteReflection(id: String)

  // Zero Spend Days
  @Query("SELECT * FROM zero_spend_days WHERE date = :date LIMIT 1")
  fun getZeroSpendByDate(date: String): Flow<ZeroSpendEntity?>

  @Query("SELECT * FROM zero_spend_days ORDER BY date DESC")
  fun getAllZeroSpendDays(): Flow<List<ZeroSpendEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun markZeroSpend(zeroSpend: ZeroSpendEntity)

  @Query("DELETE FROM zero_spend_days WHERE date = :date")
  suspend fun removeZeroSpend(date: String)
}
