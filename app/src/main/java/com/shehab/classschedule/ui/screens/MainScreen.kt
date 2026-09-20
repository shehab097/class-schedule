package com.shehab.classschedule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shehab.classschedule.data.RoutineManager
import com.shehab.classschedule.data.timeToMinutes
import com.shehab.classschedule.model.DAY_ORDER
import com.shehab.classschedule.ui.components.ClassCard
import com.shehab.classschedule.ui.theme.BgColor
import com.shehab.classschedule.ui.theme.SurfaceColor
import com.shehab.classschedule.ui.theme.PrimaryBlue
import com.shehab.classschedule.ui.theme.TextPrimary
import com.shehab.classschedule.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun ScheduleAppUI(
    onNavigateToEdit: () -> Unit,
    onNavigateToTableView: () -> Unit,
    onNavigateToJsonEditor: () -> Unit
) {
    val context = LocalContext.current
    val routine = remember(RoutineManager.version) { RoutineManager.loadRoutine(context) }
    var selectedDay by rememberSaveable { mutableStateOf(getCurrentDayString()) }
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu")

    val currentClasses = remember(routine, selectedDay) {
        routine.filter { it.day == selectedDay }.sortedBy { timeToMinutes(it.startTime) }
    }

    Scaffold(
        containerColor = BgColor,
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
                        Icon(Icons.Default.DateRange, contentDescription = "Table View", tint = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToJsonEditor) {
                        Icon(Icons.Default.Build, contentDescription = "JSON Editor", tint = TextPrimary)
                    }
                }
            }

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
                            ),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = day,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                    items(currentClasses, key = { it.id }) { cls -> ClassCard(cls) }
                }
            }
        }
    }
}

fun getCurrentDayString(): String {
    val idx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1).coerceIn(0, 6)
    return if (idx > 4) "Sun" else DAY_ORDER[idx]
}
