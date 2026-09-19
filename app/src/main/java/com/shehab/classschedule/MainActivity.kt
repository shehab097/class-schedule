package com.shehab.classschedule

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import androidx.glance.appwidget.lazy.LazyColumn as GlanceLazyColumn
import androidx.glance.appwidget.lazy.items as glanceItems
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background as glanceBackground
import androidx.glance.layout.*
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Box as GlanceBox
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.Row as GlanceRow
import androidx.glance.layout.Spacer as GlanceSpacer
import androidx.glance.text.Text as GlanceText
import androidx.glance.text.TextStyle as GlanceTextStyle
import androidx.glance.text.FontWeight as GlanceFontWeight
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Calendar

// --- Theme Colors ---
val BgColor = Color(0xFF0F172A)
val SurfaceColor = Color(0xFF1E293B)
val PrimaryBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)

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

    fun loadRawJson(context: Context): String {
        val file = File(context.filesDir, FILE_NAME)
        return try {
            if (file.exists()) {
                file.readText()
            } else {
                val assetJson = context.assets.open("routine.json").bufferedReader().use { it.readText() }
                file.writeText(assetJson)
                assetJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "[]"
        }
    }

    private fun updateWidgets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            UpcomingWidget().updateAll(context)
            DailyScheduleWidget().updateAll(context)
            FullRoutineWidget().updateAll(context)
        }
    }

    fun saveRawJson(context: Context, jsonString: String): Boolean {
        return try {
            JSONArray(jsonString)
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonString)
            updateWidgets(context)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun resetToDefault(context: Context): Boolean {
        return try {
            val assetJson = context.assets.open("routine.json").bufferedReader().use { it.readText() }
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(assetJson)
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
            val jsonString = loadRawJson(context)
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
            updateWidgets(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTodaysClasses(context: Context): List<ClassSession> {
        val routine = loadRoutine(context)
        val calendar = Calendar.getInstance()
        val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val currentDay = days[calendar.get(Calendar.DAY_OF_WEEK) - 1]

        return routine.filter { it.day == currentDay }.sortedBy {
            val (h, m) = it.startTime.split(":").map(String::toInt)
            h * 60 + m
        }
    }

    fun getCurrentOrNextClass(context: Context): ClassSession? {
        val todaysClasses = getTodaysClasses(context)
        if (todaysClasses.isEmpty()) return null

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

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

enum class ScreenState { HOME, EDIT, JSON_EDITOR, TABLE_VIEW }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkColors = darkColorScheme(
                background = BgColor,
                surface = SurfaceColor,
                primary = PrimaryBlue,
                secondary = AccentGreen,
                onBackground = TextPrimary,
                onSurface = TextPrimary,
                onPrimary = TextPrimary
            )
            MaterialTheme(colorScheme = darkColors) {
                var currentScreen by remember { mutableStateOf(ScreenState.HOME) }

                when (currentScreen) {
                    ScreenState.HOME -> ScheduleAppUI(
                        onNavigateToEdit = { currentScreen = ScreenState.EDIT },
                        onNavigateToTableView = { currentScreen = ScreenState.TABLE_VIEW },
                        onNavigateToJsonEditor = { currentScreen = ScreenState.JSON_EDITOR }
                    )
                    ScreenState.EDIT -> EditRoutineScreen(
                        onNavigateBack = { currentScreen = ScreenState.HOME }
                    )
                    ScreenState.JSON_EDITOR -> JsonEditorScreen(
                        onNavigateBack = { currentScreen = ScreenState.HOME }
                    )
                    ScreenState.TABLE_VIEW -> TableViewScreen(
                        onNavigateBack = { currentScreen = ScreenState.HOME }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleAppUI(
    onNavigateToEdit: () -> Unit,
    onNavigateToTableView: () -> Unit,
    onNavigateToJsonEditor: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val routine = RoutineManager.loadRoutine(context)
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
                containerColor = PrimaryBlue,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Schedule")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(paddingValues)
        ) {
            // Modern Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Class Schedule",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Stay organized and on time",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                Row {
                    IconButton(onClick = onNavigateToTableView) {
                        Icon(Icons.Default.Menu, contentDescription = "Table View", tint = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToJsonEditor) {
                        Icon(Icons.Default.Settings, contentDescription = "JSON Editor", tint = TextPrimary)
                    }
                }
            }

            // Day Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(SurfaceColor, RoundedCornerShape(16.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.forEach { day ->
                    val isSelected = selectedDay == day
                    TextButton(
                        onClick = { selectedDay = day },
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) PrimaryBlue else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = day,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Schedule List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                if (currentClasses.isEmpty()) {
                    item {
                        Text(
                            text = "Relax! No classes for $selectedDay.",
                            modifier = Modifier.padding(24.dp),
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    items(currentClasses) { cls ->
                        ClassCard(cls)
                    }
                }
            }
        }
    }
}

@Composable
fun ClassCard(cls: ClassSession) {
    val accentColor = if (cls.isLab) AccentGreen else PrimaryBlue

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(IntrinsicSize.Min)
                    .background(accentColor)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cls.startTime} - ${cls.endTime}",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Surface(
                        color = BgColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Room ${cls.room}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = cls.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )

                Text(
                    text = "${cls.course} • Section ${cls.section}",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BgColor, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = cls.teacher,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// --- Screens (Edit, JSON, Table) ---

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
                title = { Text("Manage Routine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceColor,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    classToEdit = null
                    isEditing = true
                },
                containerColor = PrimaryBlue,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val sortedRoutine = routine.sortedBy {
                val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                days.indexOf(it.day) * 10000 + it.startTime.replace(":", "").toInt()
            }

            items(sortedRoutine) { cls ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${cls.day} | ${cls.startTime}",
                                color = if(cls.isLab) AccentGreen else PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = cls.title, color = TextPrimary)
                        }
                        Row {
                            IconButton(onClick = {
                                classToEdit = cls
                                isEditing = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue)
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
        }
    }

    if (isEditing) {
        EditClassDialog(
            initialClass = classToEdit,
            onDismiss = { isEditing = false },
            onSave = { updatedClass ->
                val newList = if (classToEdit == null) {
                    val newId = (routine.maxOfOrNull { it.id } ?: 0) + 1
                    routine + updatedClass.copy(id = newId)
                } else {
                    routine.map { if (it.id == updatedClass.id) updatedClass else it }
                }
                saveChanges(newList)
                isEditing = false
            }
        )
    }
}

@Composable
fun EditClassDialog(initialClass: ClassSession?, onDismiss: () -> Unit, onSave: (ClassSession) -> Unit) {
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
        title = { Text(if (initialClass == null) "Add Class" else "Edit Class") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course Code") })
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("Day (Sun-Thu)") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher") })
                OutlinedTextField(value = section, onValueChange = { section = it }, label = { Text("Section") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isLab, onCheckedChange = { isLab = it })
                    Text("Lab Class")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(ClassSession(initialClass?.id ?: 0, course, title, teacher, day, room, startTime, endTime, section, isLab)) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonEditorScreen(onNavigateBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var jsonText by remember { mutableStateOf(RoutineManager.loadRawJson(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raw JSON Editor") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor, titleContentColor = TextPrimary)
            )
        }
    ) { pv ->
        Column(modifier = Modifier.fillMaxSize().background(BgColor).padding(pv).padding(16.dp)) {
            OutlinedTextField(
                value = jsonText,
                onValueChange = { jsonText = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary)
            )
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { if(RoutineManager.saveRawJson(context, jsonText)) onNavigateBack() }, modifier = Modifier.weight(1f)) { Text("Save") }
                Button(onClick = { RoutineManager.resetToDefault(context); jsonText = RoutineManager.loadRawJson(context) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Reset") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableViewScreen(onNavigateBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val routine = RoutineManager.loadRoutine(context)
    val daysOrder = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val sorted = routine.sortedWith(compareBy({ daysOrder.indexOf(it.day) }, { it.startTime }))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overview Table") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor, titleContentColor = TextPrimary)
            )
        }
    ) { pv ->
        Box(modifier = Modifier.fillMaxSize().background(BgColor).padding(pv).padding(16.dp)) {
            val hScroll = rememberScrollState()
            Column(modifier = Modifier.horizontalScroll(hScroll)) {
                Row(modifier = Modifier.background(SurfaceColor).padding(8.dp)) {
                    TableHeaderCell("Day", 60.dp); TableHeaderCell("Time", 100.dp); TableHeaderCell("Course", 90.dp); TableHeaderCell("Room", 60.dp)
                }
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    sorted.forEach { cls ->
                        Row(modifier = Modifier.padding(8.dp)) {
                            TableCell(cls.day, 60.dp); TableCell(cls.startTime, 100.dp); TableCell(cls.course, 90.dp); TableCell(cls.room, 60.dp)
                        }
                        HorizontalDivider(color = SurfaceColor)
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(text, modifier = Modifier.width(width), fontWeight = FontWeight.Bold, color = TextPrimary)
}

@Composable
fun TableCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(text, modifier = Modifier.width(width), color = TextSecondary, fontSize = 14.sp)
}

fun getCurrentDayString(): String {
    val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val cal = Calendar.getInstance()
    var idx = cal.get(Calendar.DAY_OF_WEEK) - 1
    if (idx > 4) idx = 0
    return days[idx]
}

// --- Glance Widgets ---

// 1. UpcomingWidget: Shows only the single next class
class UpcomingWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val nextClass = RoutineManager.getCurrentOrNextClass(context)
        provideContent {
            GlanceTheme {
                WidgetContainer {
                    if (nextClass != null) {
                        UpcomingWidgetUI(nextClass)
                    } else {
                        EmptyWidgetUI("No more classes today! 🎉")
                    }
                }
            }
        }
    }
}

// 2. DailyScheduleWidget: Shows all classes for today
class DailyScheduleWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val classes = RoutineManager.getTodaysClasses(context)
        provideContent {
            GlanceTheme {
                WidgetContainer {
                    if (classes.isNotEmpty()) {
                        GlanceColumn {
                            GlanceText("Today's Schedule", style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.primary_blue), fontWeight = GlanceFontWeight.Bold, fontSize = 14.sp))
                            GlanceSpacer(GlanceModifier.height(8.dp))
                            GlanceLazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                                glanceItems(classes) { cls ->
                                    DailyItemUI(cls)
                                }
                            }
                        }
                    } else {
                        EmptyWidgetUI("No classes scheduled for today.")
                    }
                }
            }
        }
    }
}

// 3. FullRoutineWidget: Shows a full overview (Scrollable)
class FullRoutineWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val routine = RoutineManager.loadRoutine(context)
        provideContent {
            GlanceTheme {
                WidgetContainer {
                    GlanceColumn {
                        GlanceText("Weekly Routine", style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.primary_blue), fontWeight = GlanceFontWeight.Bold, fontSize = 14.sp))
                        GlanceSpacer(GlanceModifier.height(8.dp))
                        GlanceLazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                            glanceItems(routine.sortedBy { it.day }) { cls ->
                                FullItemUI(cls)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Widget UI Components ---

@Composable
fun WidgetContainer(content: @Composable () -> Unit) {
    GlanceBox(
        modifier = GlanceModifier.fillMaxSize().glanceBackground(androidx.glance.unit.ColorProvider(R.color.bg_color)).padding(12.dp),
        contentAlignment = GlanceAlignment.TopStart
    ) {
        content()
    }
}

@Composable
fun UpcomingWidgetUI(cls: ClassSession) {
    val accent = if (cls.isLab) R.color.accent_green else R.color.primary_blue
    GlanceColumn(modifier = GlanceModifier.fillMaxSize()) {
        GlanceText("UPCOMING", style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_secondary), fontSize = 10.sp, fontWeight = GlanceFontWeight.Bold))
        GlanceText(cls.title, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_primary), fontSize = 16.sp, fontWeight = GlanceFontWeight.Bold), maxLines = 1)
        GlanceSpacer(GlanceModifier.height(4.dp))
        GlanceRow(verticalAlignment = GlanceAlignment.CenterVertically) {
            GlanceText("${cls.startTime} - ${cls.endTime}", style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(accent), fontSize = 13.sp, fontWeight = GlanceFontWeight.Medium))
            GlanceSpacer(GlanceModifier.defaultWeight())
            GlanceText(cls.room, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_primary), fontSize = 11.sp), modifier = GlanceModifier.glanceBackground(androidx.glance.unit.ColorProvider(R.color.surface_color)).padding(4.dp))
        }
    }
}

@Composable
fun DailyItemUI(cls: ClassSession) {
    val accent = if (cls.isLab) R.color.accent_green else R.color.primary_blue
    GlanceRow(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp).glanceBackground(androidx.glance.unit.ColorProvider(R.color.surface_color)).padding(8.dp)) {
        GlanceColumn(modifier = GlanceModifier.defaultWeight()) {
            GlanceText(cls.course, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_primary), fontWeight = GlanceFontWeight.Bold, fontSize = 12.sp))
            GlanceText(cls.startTime, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_secondary), fontSize = 10.sp))
        }
        GlanceText("R${cls.room}", style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(accent), fontSize = 12.sp))
    }
}

@Composable
fun FullItemUI(cls: ClassSession) {
    GlanceRow(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp)) {
        GlanceText(cls.day, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.primary_blue), fontSize = 10.sp, fontWeight = GlanceFontWeight.Bold), modifier = GlanceModifier.width(35.dp))
        GlanceText(cls.course, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_primary), fontSize = 10.sp), modifier = GlanceModifier.defaultWeight())
        GlanceText(cls.startTime, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_secondary), fontSize = 10.sp))
    }
}

@Composable
fun EmptyWidgetUI(msg: String) {
    GlanceBox(modifier = GlanceModifier.fillMaxSize(), contentAlignment = GlanceAlignment.Center) {
        GlanceText(msg, style = GlanceTextStyle(color = androidx.glance.unit.ColorProvider(R.color.text_secondary), fontSize = 12.sp))
    }
}

// --- Receivers ---

class UpcomingWidgetReceiver : GlanceAppWidgetReceiver() { override val glanceAppWidget = UpcomingWidget() }
class DailyScheduleWidgetReceiver : GlanceAppWidgetReceiver() { override val glanceAppWidget = DailyScheduleWidget() }
class FullRoutineWidgetReceiver : GlanceAppWidgetReceiver() { override val glanceAppWidget = FullRoutineWidget() }
