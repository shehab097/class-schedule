package com.shehab.classschedule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shehab.classschedule.data.RoutineManager
import com.shehab.classschedule.data.timeToMinutes
import com.shehab.classschedule.model.ClassSession
import com.shehab.classschedule.model.DAY_ORDER
import com.shehab.classschedule.ui.theme.BgColor
import com.shehab.classschedule.ui.theme.SurfaceColor
import com.shehab.classschedule.ui.theme.PrimaryBlue
import com.shehab.classschedule.ui.theme.AccentGreen
import com.shehab.classschedule.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoutineScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var routine by remember { mutableStateOf(RoutineManager.loadRoutine(context)) }
    var classToEdit by remember { mutableStateOf<ClassSession?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    fun saveChanges(newList: List<ClassSession>) {
        routine = newList
        RoutineManager.saveRoutine(context, newList)
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Manage Routine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        val sortedRoutine = remember(routine) { RoutineManager.sortedRoutine(routine) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sortedRoutine, key = { it.id }) { cls ->
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
                                color = if (cls.isLab) AccentGreen else PrimaryBlue,
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
fun EditClassDialog(
    initialClass: ClassSession?,
    onDismiss: () -> Unit,
    onSave: (ClassSession) -> Unit
) {
    var course by remember(initialClass) { mutableStateOf(initialClass?.course ?: "") }
    var title by remember(initialClass) { mutableStateOf(initialClass?.title ?: "") }
    var teacher by remember(initialClass) { mutableStateOf(initialClass?.teacher ?: "") }
    var day by remember(initialClass) { mutableStateOf(initialClass?.day ?: "Sun") }
    var room by remember(initialClass) { mutableStateOf(initialClass?.room ?: "") }
    var startTime by remember(initialClass) { mutableStateOf(initialClass?.startTime ?: "10:30") }
    var endTime by remember(initialClass) { mutableStateOf(initialClass?.endTime ?: "11:45") }
    var section by remember(initialClass) { mutableStateOf(initialClass?.section ?: "A") }
    var isLab by remember(initialClass) { mutableStateOf(initialClass?.isLab ?: false) }

    val timesValid = timeToMinutes(startTime) != Int.MAX_VALUE &&
            timeToMinutes(endTime) != Int.MAX_VALUE
    val dayValid = DAY_ORDER.any { it.equals(day.trim(), ignoreCase = true) }
    val canSave = title.isNotBlank() && timesValid && dayValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialClass == null) "Add Class" else "Edit Class") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course Code") })
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, isError = title.isBlank())
                OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("Day (Sun-Sat)") }, isError = !dayValid)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start HH:mm") },
                        isError = timeToMinutes(startTime) == Int.MAX_VALUE,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End HH:mm") },
                        isError = timeToMinutes(endTime) == Int.MAX_VALUE,
                        modifier = Modifier.weight(1f)
                    )
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
            Button(
                enabled = canSave,
                onClick = {
                    val normalizedDay = DAY_ORDER.first { it.equals(day.trim(), ignoreCase = true) }
                    onSave(
                        ClassSession(
                            id = initialClass?.id ?: 0,
                            course = course.trim(),
                            title = title.trim(),
                            teacher = teacher.trim(),
                            day = normalizedDay,
                            room = room.trim(),
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            section = section.trim(),
                            isLab = isLab
                        )
                    )
                }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
