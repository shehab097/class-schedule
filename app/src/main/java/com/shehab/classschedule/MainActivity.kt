package com.shehab.classschedule

/*
 * SETUP INSTRUCTIONS FOR ANDROID STUDIO:
 *
 * 1. Add to AndroidManifest.xml inside <application>:
 *    <receiver android:name=".ScheduleWidgetReceiver" android:exported="true">
 *        <intent-filter>
 *            <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
 *        </intent-filter>
 *        <meta-data android:name="android.appwidget.provider" android:resource="@xml/schedule_widget_info" />
 *    </receiver>
 *
 * 2. Create res/xml/schedule_widget_info.xml:
 *    <?xml version="1.0" encoding="utf-8"?>
 *    <appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
 *        android:minWidth="200dp" android:minHeight="100dp"
 *        android:updatePeriodMillis="1800000" android:initialLayout="@layout/glance_default_loading"
 *        android:widgetCategory="home_screen" />
 */


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import org.json.JSONArray
import java.util.Calendar


data class ClassSession(
    val id: Int,
    val course: String,
    val title: String,
    val teacher: String,
    val day: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val section: String,
    val isLab: Boolean
)


object RoutineManager {
    // Embedded JSON data directly in code so no assets folder is needed!
    private val ROUTINE_JSON = """
    [
      {
        "id": 1,
        "course": "CSE-3101",
        "title": "Computer Networking",
        "teacher": "JTT_Jarin Tasnim Tamanna",
        "phone": "01798614562",
        "email": "jtasnim@niter.edu.bd",
        "day": "Mon",
        "room": "119",
        "startTime": "10:30",
        "endTime": "11:45",
        "section": "A",
        "isLab": false
      },
      {
        "id": 2,
        "course": "CSE-3101",
        "title": "Computer Networking",
        "teacher": "JTT_Jarin Tasnim Tamanna",
        "phone": "01798614562",
        "email": "jtasnim@niter.edu.bd",
        "day": "Tue",
        "room": "119",
        "startTime": "09:15",
        "endTime": "10:30",
        "section": "A",
        "isLab": false
      },
      {
        "id": 3,
        "course": "CSE-3102",
        "title": "Software Engineering",
        "teacher": "TA_Tanvir Ahmed",
        "phone": "01816299924",
        "email": "tanvir@niter.edu.bd",
        "day": "Thu",
        "room": "204",
        "startTime": "08:00",
        "endTime": "09:15",
        "section": "A",
        "isLab": false
      },
      {
        "id": 4,
        "course": "CSE-3102",
        "title": "Software Engineering",
        "teacher": "TA_Tanvir Ahmed",
        "phone": "01816299924",
        "email": "tanvir@niter.edu.bd",
        "day": "Wed",
        "room": "204",
        "startTime": "09:15",
        "endTime": "10:30",
        "section": "A",
        "isLab": false
      },
      {
        "id": 5,
        "course": "CSE-3103",
        "title": "Microprocessor and Microcontroller",
        "teacher": "UKD_Utpol Kanti Das",
        "phone": "01859222633",
        "email": "ukdas@niter.edu.bd",
        "day": "Sun",
        "room": "210",
        "startTime": "10:30",
        "endTime": "11:45",
        "section": "A",
        "isLab": false
      },
      {
        "id": 6,
        "course": "CSE-3103",
        "title": "Microprocessor and Microcontroller",
        "teacher": "UKD_Utpol Kanti Das",
        "phone": "01859222633",
        "email": "ukdas@niter.edu.bd",
        "day": "Thu",
        "room": "204",
        "startTime": "09:15",
        "endTime": "10:30",
        "section": "A",
        "isLab": false
      },
      {
        "id": 7,
        "course": "CSE-3104",
        "title": "Database Management Systems -II",
        "teacher": "SSH_Shakila Shafiq",
        "phone": "01991677002",
        "email": "sshafiq@niter.edu.bd",
        "day": "Sun",
        "room": "204",
        "startTime": "11:45",
        "endTime": "13:00",
        "section": "A",
        "isLab": false
      },
      {
        "id": 8,
        "course": "CSE-3104",
        "title": "Database Management Systems -II",
        "teacher": "SSH_Shakila Shafiq",
        "phone": "01991677002",
        "email": "sshafiq@niter.edu.bd",
        "day": "Tue",
        "room": "204",
        "startTime": "08:00",
        "endTime": "09:15",
        "section": "A",
        "isLab": false
      },
      {
        "id": 9,
        "course": "CSE-3111",
        "title": "Computer Networking Lab",
        "teacher": "JTT_Jarin Tasnim Tamanna",
        "phone": "01798614562",
        "email": "jtasnim@niter.edu.bd",
        "day": "Sun",
        "room": "AC-217",
        "startTime": "13:30",
        "endTime": "16:00",
        "section": "A1",
        "isLab": true
      },
      {
        "id": 10,
        "course": "CSE-3112",
        "title": "Software Engineering Lab",
        "teacher": "TA_Tanvir Ahmed",
        "phone": "01816299924",
        "email": "tanvir@niter.edu.bd",
        "day": "Thu",
        "room": "AC-202",
        "startTime": "13:30",
        "endTime": "16:00",
        "section": "A1",
        "isLab": true
      },
      {
        "id": 11,
        "course": "CSE-3113",
        "title": "Microprocessor and Assembly Language Lab",
        "teacher": "MdAM_Md. Alam Miah",
        "phone": "01706446160",
        "email": "mdalammiah2001@gmail.com",
        "day": "Wed",
        "room": "AC-205",
        "startTime": "13:30",
        "endTime": "16:00",
        "section": "A1",
        "isLab": true
      },
      {
        "id": 12,
        "course": "CSE-3116",
        "title": "Microcontroller Lab",
        "teacher": "SSH_Shakila Shafiq",
        "phone": "01991677002",
        "email": "sshafiq@niter.edu.bd",
        "day": "Tue",
        "room": "AC-205",
        "startTime": "13:30",
        "endTime": "16:00",
        "section": "A1",
        "isLab": true
      },
      {
        "id": 13,
        "course": "MATH-3105",
        "title": "Multivariable Calculus and Geometry",
        "teacher": "KFM_Khondaker Fahad Mia",
        "phone": "01521214789",
        "email": "kfmia@niter.edu.bd",
        "day": "Thu",
        "room": "210",
        "startTime": "10:30",
        "endTime": "11:45",
        "section": "A",
        "isLab": false
      },
      {
        "id": 14,
        "course": "MATH-3105",
        "title": "Multivariable Calculus and Geometry",
        "teacher": "KFM_Khondaker Fahad Mia",
        "phone": "01521214789",
        "email": "kfmia@niter.edu.bd",
        "day": "Wed",
        "room": "204",
        "startTime": "08:00",
        "endTime": "09:15",
        "section": "A",
        "isLab": false
      }
    ]
    """.trimIndent()

    fun loadRoutine(context: Context): List<ClassSession> {
        val classList = mutableListOf<ClassSession>()
        try {
            val jsonArray = JSONArray(ROUTINE_JSON)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                classList.add(
                    ClassSession(
                        id = obj.getInt("id"),
                        course = obj.getString("course"),
                        title = obj.getString("title"),
                        teacher = obj.getString("teacher"),
                        day = obj.getString("day"),
                        room = obj.getString("room"),
                        startTime = obj.getString("startTime"),
                        endTime = obj.getString("endTime"),
                        section = obj.getString("section"),
                        isLab = obj.getBoolean("isLab")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return classList
    }

    fun getCurrentOrNextClass(context: Context): ClassSession? {
        val routine = loadRoutine(context)
        val calendar = Calendar.getInstance()

        val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val currentDay = days[calendar.get(Calendar.DAY_OF_WEEK) - 1]

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        val todaysClasses = routine.filter { it.day == currentDay }.sortedBy {
            val (h, m) = it.startTime.split(":").map(String::toInt)
            h * 60 + m
        }

        if (todaysClasses.isEmpty()) return null

        for (cls in todaysClasses) {
            val (endH, endM) = cls.endTime.split(":").map(String::toInt)
            val endTimeInMinutes = endH * 60 + endM

            if (currentTimeInMinutes <= endTimeInMinutes) {
                return cls
            }
        }

        return null
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routine = RoutineManager.loadRoutine(this)

        setContent {
            MaterialTheme {
                ScheduleAppUI(routine)
            }
        }
    }
}

@Composable
fun ScheduleAppUI(routine: List<ClassSession>) {
    var selectedDay by remember { mutableStateOf(getCurrentDayString()) }
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu")

    val currentClasses = routine
        .filter { it.day == selectedDay }
        .sortedBy {
            val (h, m) = it.startTime.split(":").map(String::toInt)
            h * 60 + m
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        // App Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1D4ED8))
                .padding(24.dp)
        ) {
            Text(
                text = "Class Schedule",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Day Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                TextButton(
                    onClick = { selectedDay = day },
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) Color(0xFF2563EB) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = day,
                        color = if (isSelected) Color.White else Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Schedule List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentClasses.isEmpty()) {
                item {
                    Text(
                        text = "No Classes Today!",
                        modifier = Modifier.padding(24.dp),
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                items(currentClasses) { cls ->
                    ClassCard(cls)
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}


@Composable
fun ClassCard(cls: ClassSession) {
    val borderColor = if (cls.isLab) Color(0xFF10B981) else Color(0xFF3B82F6)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(IntrinsicSize.Min)
                    .background(borderColor)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cls.startTime} - ${cls.endTime}",
                        color = borderColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Room ${cls.room}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = cls.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E293B)
                )

                Text(
                    text = "${cls.course} • Sec ${cls.section}",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = cls.teacher,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun getCurrentDayString(): String {
    val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val cal = Calendar.getInstance()
    var dayIdx = cal.get(Calendar.DAY_OF_WEEK) - 1
    if (dayIdx > 4) dayIdx = 0
    return days[dayIdx]
}


class ScheduleWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val nextClass = RoutineManager.getCurrentOrNextClass(context)

        provideContent {
            GlanceTheme {
                if (nextClass != null) {
                    FlashcardWidgetUI(nextClass)
                } else {
                    EmptyWidgetUI()
                }
            }
        }
    }
}

@Composable
fun FlashcardWidgetUI(cls: ClassSession) {
    val bgColor = if (cls.isLab) Color(0xFFECFDF5) else Color(0xFFEFF6FF)
    val accentColor = if (cls.isLab) Color(0xFF059669) else Color(0xFF2563EB)

    androidx.glance.layout.Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(bgColor))
            .padding(12.dp)
    ) {
        androidx.glance.layout.Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = androidx.glance.layout.Alignment.CenterVertically
        ) {
            Text(
                text = "UPCOMING CLASS",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF64748B)),
                    fontSize = 10.sp,
                    fontWeight = androidx.glance.text.FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 4.dp)
            )

            Text(
                text = cls.title,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF1E293B)),
                    fontSize = 16.sp,
                    fontWeight = androidx.glance.text.FontWeight.Bold
                ),
                maxLines = 2
            )

            androidx.glance.layout.Row(
                modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = androidx.glance.layout.Alignment.CenterVertically
            ) {
                Text(
                    text = "${cls.startTime} - ${cls.endTime}",
                    style = TextStyle(
                        color = ColorProvider(accentColor),
                        fontSize = 14.sp,
                        fontWeight = androidx.glance.text.FontWeight.Bold
                    )
                )
                androidx.glance.layout.Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = cls.room,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 12.sp,
                        fontWeight = androidx.glance.text.FontWeight.Bold
                    ),
                    modifier = GlanceModifier.background(ColorProvider(Color(0xFF334155))).padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyWidgetUI() {
    androidx.glance.layout.Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF8FAFC)))
            .padding(16.dp),
        contentAlignment = androidx.glance.layout.Alignment.Center
    ) {
        Text(
            text = "No more classes today!",
            style = TextStyle(
                color = ColorProvider(Color(0xFF64748B)),
                fontSize = 14.sp,
                fontWeight = androidx.glance.text.FontWeight.Medium
            )
        )
    }
}


class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()
}