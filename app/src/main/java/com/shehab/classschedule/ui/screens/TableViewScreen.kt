package com.shehab.classschedule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shehab.classschedule.data.RoutineManager
import com.shehab.classschedule.ui.components.TableCell
import com.shehab.classschedule.ui.components.TableHeaderCell
import com.shehab.classschedule.ui.theme.BgColor
import com.shehab.classschedule.ui.theme.SurfaceColor
import com.shehab.classschedule.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableViewScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val routine = remember(RoutineManager.version) { RoutineManager.loadRoutine(context) }
    val sorted = remember(routine) { RoutineManager.sortedRoutine(routine) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Overview Table") },
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
        }
    ) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(pv)
                .padding(16.dp)
        ) {
            val hScroll = rememberScrollState()
            Column(modifier = Modifier.horizontalScroll(hScroll)) {
                Row(
                    modifier = Modifier
                        .background(SurfaceColor)
                        .padding(8.dp)
                ) {
                    TableHeaderCell("Day", 60.dp)
                    TableHeaderCell("Time", 100.dp)
                    TableHeaderCell("Course", 90.dp)
                    TableHeaderCell("Room", 60.dp)
                }
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    sorted.forEach { cls ->
                        Row(modifier = Modifier.padding(8.dp)) {
                            TableCell(cls.day, 60.dp)
                            TableCell(cls.startTime, 100.dp)
                            TableCell(cls.course, 90.dp)
                            TableCell(cls.room, 60.dp)
                        }
                        HorizontalDivider(color = SurfaceColor)
                    }
                }
            }
        }
    }
}
