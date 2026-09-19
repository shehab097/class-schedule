package com.shehab.classschedule.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.shehab.classschedule.model.ClassSession
import com.shehab.classschedule.model.DAY_ORDER
import com.shehab.classschedule.widget.UpcomingWidget
import com.shehab.classschedule.widget.DailyScheduleWidget
import com.shehab.classschedule.widget.FullRoutineWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Calendar

/** Safe "HH:mm" -> minutes. Never throws; bad values sort to the end. */
fun timeToMinutes(time: String): Int = try {
    val parts = time.trim().split(":")
    val h = parts[0].trim().toInt()
    val m = parts.getOrElse(1) { "0" }.trim().toInt()
    h * 60 + m
} catch (e: Exception) {
    Int.MAX_VALUE
}

object RoutineManager {
    private const val FILE_NAME = "routine.json"

    /** Bumped on every write so Compose screens can recompose off fresh data. */
    var version by mutableIntStateOf(0)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun loadRawJson(context: Context): String {
        val file = File(context.filesDir, FILE_NAME)
        return try {
            if (file.exists()) {
                file.readText()
            } else {
                val assetJson = context.assets.open("routine.json")
                    .bufferedReader().use { it.readText() }
                file.writeText(assetJson)
                assetJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "[]"
        }
    }

    private fun updateWidgets(context: Context) {
        val appContext = context.applicationContext
        scope.launch {
            try {
                UpcomingWidget().updateAll(appContext)
                DailyScheduleWidget().updateAll(appContext)
                FullRoutineWidget().updateAll(appContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveRawJson(context: Context, jsonString: String): Boolean {
        return try {
            JSONArray(jsonString) // validate
            File(context.filesDir, FILE_NAME).writeText(jsonString)
            version++
            updateWidgets(context)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun resetToDefault(context: Context): Boolean {
        return try {
            val assetJson = context.assets.open("routine.json")
                .bufferedReader().use { it.readText() }
            File(context.filesDir, FILE_NAME).writeText(assetJson)
            version++
            updateWidgets(context)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadRoutine(context: Context): List<ClassSession> {
        val classList = mutableListOf<ClassSession>()
        try {
            val jsonArray = JSONArray(loadRawJson(context))
            for (i in 0 until jsonArray.length()) {
                try {
                    val obj = jsonArray.optJSONObject(i) ?: continue
                    classList.add(
                        ClassSession(
                            id = obj.optInt("id", i + 1),
                            course = obj.optString("course", ""),
                            title = obj.optString("title", "Untitled"),
                            teacher = obj.optString("teacher", ""),
                            day = obj.optString("day", "Sun"),
                            room = obj.optString("room", "-"),
                            startTime = obj.optString("startTime", "00:00"),
                            endTime = obj.optString("endTime", "00:00"),
                            section = obj.optString("section", ""),
                            isLab = obj.optBoolean("isLab", false)
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return classList
    }

    fun saveRoutine(context: Context, routine: List<ClassSession>) {
        try {
            val jsonArray = JSONArray()
            for (cls in routine) {
                jsonArray.put(JSONObject().apply {
                    put("id", cls.id)
                    put("course", cls.course)
                    put("title", cls.title)
                    put("teacher", cls.teacher)
                    put("day", cls.day)
                    put("room", cls.room)
                    put("startTime", cls.startTime)
                    put("endTime", cls.endTime)
                    put("section", cls.section)
                    put("isLab", cls.isLab)
                })
            }
            File(context.filesDir, FILE_NAME).writeText(jsonArray.toString())
            version++
            updateWidgets(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sortedRoutine(routine: List<ClassSession>): List<ClassSession> =
        routine.sortedWith(
            compareBy(
                { DAY_ORDER.indexOf(it.day).let { i -> if (i < 0) Int.MAX_VALUE else i } },
                { timeToMinutes(it.startTime) }
            )
        )

    fun getTodaysClasses(context: Context): List<ClassSession> {
        val today = todayKey()
        return loadRoutine(context)
            .filter { it.day.equals(today, ignoreCase = true) }
            .sortedBy { timeToMinutes(it.startTime) }
    }

    fun getCurrentOrNextClass(context: Context): ClassSession? {
        val todaysClasses = getTodaysClasses(context)
        if (todaysClasses.isEmpty()) return null

        val cal = Calendar.getInstance()
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        return todaysClasses.firstOrNull { nowMinutes <= timeToMinutes(it.endTime) }
    }

    private fun todayKey(): String {
        val idx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)
            .coerceIn(0, DAY_ORDER.lastIndex)
        return DAY_ORDER[idx]
    }
}
