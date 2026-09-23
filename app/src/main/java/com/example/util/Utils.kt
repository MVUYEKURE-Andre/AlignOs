package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.model.SubtaskItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
  private val standardFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  private val displayFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
  private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
  private val monthPrefixFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

  fun today(): String = standardFormat.format(Date())

  fun formatDate(dateStr: String): String {
    return try {
      val parsed = standardFormat.parse(dateStr)
      if (parsed != null) displayFormat.format(parsed) else dateStr
    } catch (_: Exception) {
      dateStr
    }
  }

  fun currentMonthDisplay(): String = monthFormat.format(Date())

  fun currentMonthPrefix(): String = monthPrefixFormat.format(Date())

  fun daysInCurrentMonth(): Int {
    val cal = Calendar.getInstance()
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
  }

  fun addDays(dateStr: String, days: Int): String {
    return try {
      val cal = Calendar.getInstance()
      standardFormat.parse(dateStr)?.let { cal.time = it }
      cal.add(Calendar.DAY_OF_YEAR, days)
      standardFormat.format(cal.time)
    } catch (_: Exception) {
      dateStr
    }
  }

  fun isToday(dateStr: String): Boolean = dateStr == today()

  fun isTomorrow(dateStr: String): Boolean = dateStr == addDays(today(), 1)

  fun daysBetween(startStr: String, endStr: String): Int {
    return try {
      val d1 = standardFormat.parse(startStr) ?: return 0
      val d2 = standardFormat.parse(endStr) ?: return 0
      val diffMillis = d2.time - d1.time
      (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    } catch (_: Exception) {
      0
    }
  }
}

object JsonUtils {
  fun parseSubtasks(json: String?): List<SubtaskItem> {
    if (json.isNullOrBlank()) return emptyList()
    return try {
      val array = JSONArray(json)
      val list = mutableListOf<SubtaskItem>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          SubtaskItem(
            id = obj.optString("id", i.toString()),
            title = obj.optString("title", ""),
            completed = obj.optBoolean("completed", false)
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun serializeSubtasks(list: List<SubtaskItem>): String {
    val array = JSONArray()
    for (item in list) {
      val obj = JSONObject()
      obj.put("id", item.id)
      obj.put("title", item.title)
      obj.put("completed", item.completed)
      array.put(obj)
    }
    return array.toString()
  }

  fun parseHabits(json: String?): List<String> {
    if (json.isNullOrBlank()) return listOf("Read 20m", "Study/Code", "Zero Alcohol", "Workout")
    return try {
      val array = JSONArray(json)
      val list = mutableListOf<String>()
      for (i in 0 until array.length()) {
        list.add(array.getString(i))
      }
      list
    } catch (_: Exception) {
      listOf("Read 20m", "Study/Code", "Zero Alcohol", "Workout")
    }
  }

  fun serializeHabits(list: List<String>): String {
    val array = JSONArray()
    for (item in list) {
      array.put(item)
    }
    return array.toString()
  }

  fun parseHabitsChecked(json: String?): Map<String, Boolean> {
    if (json.isNullOrBlank()) return emptyMap()
    return try {
      val obj = JSONObject(json)
      val map = mutableMapOf<String, Boolean>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        map[key] = obj.optBoolean(key, false)
      }
      map
    } catch (_: Exception) {
      emptyMap()
    }
  }

  fun serializeHabitsChecked(map: Map<String, Boolean>): String {
    val obj = JSONObject()
    map.forEach { (k, v) -> obj.put(k, v) }
    return obj.toString()
  }

  fun parseStringList(json: String?): List<String> {
    if (json.isNullOrBlank()) return emptyList()
    return try {
      val array = JSONArray(json)
      val list = mutableListOf<String>()
      for (i in 0 until array.length()) {
        list.add(array.getString(i))
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun serializeStringList(list: List<String>): String {
    val array = JSONArray()
    for (item in list) {
      array.put(item)
    }
    return array.toString()
  }
}

object HapticUtils {
  fun trigger(context: Context, durationMs: Long = 40) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
          context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(
            VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
          )
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(durationMs)
        }
      }
    } catch (_: Exception) {
      // Ignore vibration error gracefully
    }
  }
}
