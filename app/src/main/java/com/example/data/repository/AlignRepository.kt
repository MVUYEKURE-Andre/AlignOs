package com.example.data.repository

import com.example.data.local.AlignDao
import com.example.data.local.DailyReflectionEntity
import com.example.data.local.SleepRecordEntity
import com.example.data.local.TaskItemEntity
import com.example.data.local.TimeLogEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserConfigEntity
import com.example.data.local.ZeroSpendEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlignRepository(private val dao: AlignDao) {

  val userConfig: Flow<UserConfigEntity> = dao.getUserConfig().map {
    it ?: UserConfigEntity()
  }

  suspend fun saveUserConfig(config: UserConfigEntity) = dao.saveUserConfig(config)

  val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

  fun getTransactionsByDate(date: String): Flow<List<TransactionEntity>> =
    dao.getTransactionsByDate(date)

  fun getTransactionsByMonth(monthPrefix: String): Flow<List<TransactionEntity>> =
    dao.getTransactionsByMonth(monthPrefix)

  suspend fun insertTransaction(transaction: TransactionEntity) =
    dao.insertTransaction(transaction)

  suspend fun deleteTransaction(id: String) = dao.deleteTransaction(id)

  val allTimeLogs: Flow<List<TimeLogEntity>> = dao.getAllTimeLogs()

  fun getTimeLogsByDate(date: String): Flow<List<TimeLogEntity>> =
    dao.getTimeLogsByDate(date)

  suspend fun insertTimeLog(timeLog: TimeLogEntity) = dao.insertTimeLog(timeLog)

  suspend fun deleteTimeLog(id: String) = dao.deleteTimeLog(id)

  val allSleepRecords: Flow<List<SleepRecordEntity>> = dao.getAllSleepRecords()

  fun getSleepRecordByDate(date: String): Flow<SleepRecordEntity?> =
    dao.getSleepRecordByDate(date)

  suspend fun insertSleepRecord(record: SleepRecordEntity) = dao.insertSleepRecord(record)

  suspend fun deleteSleepRecord(id: String) = dao.deleteSleepRecord(id)

  val allTasks: Flow<List<TaskItemEntity>> = dao.getAllTasks()

  fun getTasksByDate(date: String): Flow<List<TaskItemEntity>> =
    dao.getTasksByDate(date)

  val interviewTasks: Flow<List<TaskItemEntity>> = dao.getInterviewTasks()

  suspend fun insertTask(task: TaskItemEntity) = dao.insertTask(task)

  suspend fun updateTask(task: TaskItemEntity) = dao.updateTask(task)

  suspend fun deleteTask(id: String) = dao.deleteTask(id)

  val allReflections: Flow<List<DailyReflectionEntity>> = dao.getAllReflections()

  fun getReflectionByDate(date: String): Flow<DailyReflectionEntity?> =
    dao.getReflectionByDate(date)

  fun searchReflections(query: String): Flow<List<DailyReflectionEntity>> =
    dao.searchReflections(query)

  suspend fun saveReflection(reflection: DailyReflectionEntity) =
    dao.saveReflection(reflection)

  suspend fun deleteReflection(id: String) = dao.deleteReflection(id)

  fun getZeroSpendByDate(date: String): Flow<ZeroSpendEntity?> =
    dao.getZeroSpendByDate(date)

  val allZeroSpendDays: Flow<List<ZeroSpendEntity>> = dao.getAllZeroSpendDays()

  suspend fun markZeroSpend(date: String) =
    dao.markZeroSpend(ZeroSpendEntity(date = date))

  suspend fun removeZeroSpend(date: String) = dao.removeZeroSpend(date)
}
