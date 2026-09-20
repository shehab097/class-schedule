package com.shehab.classschedule.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shehab.classschedule.data.RoutineManager
import com.shehab.classschedule.model.ClassSession
import com.shehab.classschedule.model.DAY_ORDER
import com.shehab.classschedule.ui.theme.BgColor
import com.shehab.classschedule.ui.theme.SurfaceColor
import com.shehab.classschedule.ui.theme.TextPrimary

/**
 * A small, deterministic palette used to color-code each unique course.
 * Cycled by the course's hash so the same course always gets the same color.
 */
private val courseAccentColors = listOf(
    Color(0xFF6C5CE7), // violet
    Color(0xFF00B894), // teal
    Color(0xFFE17055), // coral
    Color(0xFF0984E3), // blue
    Color(0xFFE84393), // pink
    Color(0xFFFDCB6E), // amber
    Color(0xFF00CEC9), // aqua
    Color(0xFFD63031), // red
)

private fun accentFor(course: String): Color {
    val idx = (course.hashCode().and(0x7FFFFFFF)) % courseAccentColors.size
    return courseAccentColors[idx]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableViewScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val routine = remember(RoutineManager.version) { RoutineManager.loadRoutine(context) }
    val sorted = remember(routine) { RoutineManager.sortedRoutine(routine) }

    val groupedByDay = remember(sorted) {
        val grouped = sorted.groupBy { it.day }
        DAY_ORDER.mapNotNull { day -> grouped[day]?.let { day to it } }
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Weekly Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            "${sorted.size} classes • ${groupedByDay.size} days",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                    }
                },
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
        ) {
            if (groupedByDay.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupedByDay.forEach { (day, sessions) ->
                        item(key = "header_$day") {
                            DaySectionHeader(day = day, count = sessions.size)
                        }

                        itemsIndexed(
                            items = sessions,
                            key = { _, item -> item.id }
                        ) { index, cls ->
                            ClassRowCard(cls = cls)
                        }

                        item(key = "spacer_$day") { Spacer(Modifier.height(4.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🗓️",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "No class schedule found",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary.copy(alpha = 0.6f)
            )
        }
    }
}

private fun getFullDayName(day: String): String = when (day) {
    "Sun" -> "Sunday"
    "Mon" -> "Monday"
    "Tue" -> "Tuesday"
    "Wed" -> "Wednesday"
    "Thu" -> "Thursday"
    "Fri" -> "Friday"
    "Sat" -> "Saturday"
    else -> day
}

@Composable
private fun DaySectionHeader(day: String, count: Int) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = getFullDayName(day).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            ) {
                Text(
                    text = if (count == 1) "1 class" else "$count classes",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ClassRowCard(cls: ClassSession) {
    val accent = remember(cls.course) { accentFor(cls.course) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent strip, color-coded per course
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(accent, accent.copy(alpha = 0.55f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // Top row: course + title on the left, room badge on the right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cls.course,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleSmall,
                                color = accent
                            )
                            if (cls.isLab) {
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    color = accent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "LAB",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = accent
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = cls.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    RoomBadge(room = cls.room)
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.08f))
                Spacer(Modifier.height(8.dp))

                // Bottom row: time + teacher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cls.startTime} – ${cls.endTime}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary.copy(alpha = 0.75f)
                    )

                    if (cls.teacher.isNotBlank()) {
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary.copy(alpha = 0.35f)
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = cls.teacher,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary.copy(alpha = 0.65f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomBadge(room: String) {
    val displayRoom = room.ifBlank { "N/A" }
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = displayRoom,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}