package com.shehab.classschedule

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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
    private const val FILE_NAME = "routine_data.json"

    fun loadRoutine(context: Context): List<ClassSession> {
        val file = File(context.filesDir, FILE_NAME)
        val classList = mutableListOf<ClassSession>()

        try {
            // If internal file doesn't exist, copy from assets/routine.json
            val jsonString = if (file.exists()) {
                file.readText()
            } else {
                val assetJson = context.assets.open("routine.json").bufferedReader().use { it.readText() }
                file.writeText(assetJson)
                assetJson
            }

            val jsonArray = JSONArray(jsonString)
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

    fun saveRoutine(context: Context, routine: List<ClassSession>) {
        try {
            val jsonArray = JSONArray()
            for (cls in routine) {
                val obj = JSONObject().apply {
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
                }
                jsonArray.put(obj)
            }

            File(context.filesDir, FILE_NAME).writeText(jsonArray.toString())

            // Update the widget to reflect new data immediately
            CoroutineScope(Dispatchers.IO).launch {
                ScheduleWidget().updateAll(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

enum class ScreenState { HOME, EDIT }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var currentScreen by remember { mutableStateOf(ScreenState.HOME) }

                when (currentScreen) {
                    ScreenState.HOME -> ScheduleAppUI(
                        onNavigateToEdit = { currentScreen = ScreenState.EDIT }
                    )
                    ScreenState.EDIT -> EditRoutineScreen(
                        onNavigateBack = { currentScreen = ScreenState.HOME }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleAppUI(onNavigateToEdit: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var routine by remember { mutableStateOf(RoutineManager.loadRoutine(context)) }
    var selectedDay by remember { mutableStateOf(getCurrentDayString()) }
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu")

    val currentClasses = routine
        .filter { it.day == selectedDay }
        .sortedBy {
            val (h, m) = it.startTime.split(":").map(String::toInt)
            h * 60 + m
        }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEdit,
                containerColor = Color(0xFF1D4ED8),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Schedule")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(paddingValues)
        ) {
            // App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1D4ED8))
                    .padding(24.dp)
            ) {
                androidx.compose.material3.Text(
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
                        androidx.compose.material3.Text(
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
                        androidx.compose.material3.Text(
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
                item { Spacer(modifier = Modifier.height(80.dp)) } // Spacer for FAB
            }
        }
    }
}

@Composable
fun EditRoutineScreen(onNavigateBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var routine by remember { mutableStateOf(RoutineManager.loadRoutine(context)) }
    var classToEdit by remember { mutableStateOf<ClassSession?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    fun saveChanges(newList: List<ClassSession>) {
        routine = newList
        RoutineManager.saveRoutine(context, newList)
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { androidx.compose.material3.Text("Edit Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D4ED8),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    classToEdit = null
                    isEditing = true
                },
                containerColor = Color(0xFF10B981),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F5F9))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sortedRoutine = routine.sortedBy {
                val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                days.indexOf(it.day) * 10000 + it.startTime.replace(":", "").toInt()
            }

            items(sortedRoutine) { cls ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            androidx.compose.material3.Text(text = "${cls.day} | ${cls.startTime}-${cls.endTime}", fontWeight = FontWeight.Bold)
                            androidx.compose.material3.Text(text = cls.title, fontSize = 14.sp)
                            androidx.compose.material3.Text(text = cls.course, fontSize = 12.sp, color = Color.Gray)
                        }
                        androidx.compose.foundation.layout.Row {
                            IconButton(onClick = {
                                classToEdit = cls
                                isEditing = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB))
                            }
                            IconButton(onClick = {
                                saveChanges(routine.filter { it.id != cls.id })
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (isEditing) {
        EditClassDialog(
            initialClass = classToEdit,
            onDismiss = { isEditing = false },
            onSave = { updatedClass ->
                val newList = if (classToEdit == null) {
                    // Adding new
                    val newId = (routine.maxOfOrNull { it.id } ?: 0) + 1
                    routine + updatedClass.copy(id = newId)
                } else {
                    // Updating existing
                    routine.map { if (it.id == updatedClass.id) updatedClass else it }
                }
                saveChanges(newList)
                isEditing = false
            }
        )
    }
}

@Composable
fun EditClassDialog(
    initialClass: ClassSession?,
    onDismiss: () -> Unit,
    onSave: (ClassSession) -> Unit
) {
    var course by remember { mutableStateOf(initialClass?.course ?: "") }
    var title by remember { mutableStateOf(initialClass?.title ?: "") }
    var teacher by remember { mutableStateOf(initialClass?.teacher ?: "") }
    var day by remember { mutableStateOf(initialClass?.day ?: "Sun") }
    var room by remember { mutableStateOf(initialClass?.room ?: "") }
    var startTime by remember { mutableStateOf(initialClass?.startTime ?: "10:30") }
    var endTime by remember { mutableStateOf(initialClass?.endTime ?: "11:45") }
    var section by remember { mutableStateOf(initialClass?.section ?: "A") }
    var isLab by remember { mutableStateOf(initialClass?.isLab ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text(if (initialClass == null) "Add Class" else "Edit Class") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = course, onValueChange = { course = it }, label = { androidx.compose.material3.Text("Course Code (e.g. CSE-3101)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { androidx.compose.material3.Text("Course Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { androidx.compose.material3.Text("Teacher") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = day, onValueChange = { day = it }, label = { androidx.compose.material3.Text("Day (Sun, Mon, Tue, Wed, Thu)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { androidx.compose.material3.Text("Room") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { androidx.compose.material3.Text("Start (HH:MM)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { androidx.compose.material3.Text("End (HH:MM)") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = section, onValueChange = { section = it }, label = { androidx.compose.material3.Text("Section") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Checkbox(checked = isLab, onCheckedChange = { isLab = it })
                    androidx.compose.material3.Text("Is Lab Class?")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    ClassSession(
                        id = initialClass?.id ?: 0,
                        course = course, title = title, teacher = teacher,
                        day = day, room = room, startTime = startTime,
                        endTime = endTime, section = section, isLab = isLab
                    )
                )
            }) {
                androidx.compose.material3.Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("Cancel")
            }
        }
    )
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
                    androidx.compose.material3.Text(
                        text = "${cls.startTime} - ${cls.endTime}",
                        color = borderColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Room ${cls.room}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                androidx.compose.material3.Text(
                    text = cls.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E293B)
                )

                androidx.compose.material3.Text(
                    text = "${cls.course} • Sec ${cls.section}",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                androidx.compose.material3.Text(
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