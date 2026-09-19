package com.shehab.classschedule.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn as GlanceLazyColumn
import androidx.glance.appwidget.lazy.items as glanceItems
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.Column as GlanceColumn
import com.shehab.classschedule.data.RoutineManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpcomingWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val nextClass = withContext(Dispatchers.IO) {
            runCatching { RoutineManager.getCurrentOrNextClass(context) }.getOrNull()
        }
        provideContent {
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

class DailyScheduleWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val classes = withContext(Dispatchers.IO) {
            runCatching { RoutineManager.getTodaysClasses(context) }.getOrDefault(emptyList())
        }
        provideContent {
            WidgetContainer {
                if (classes.isNotEmpty()) {
                    GlanceColumn(modifier = GlanceModifier.fillMaxSize()) {
                        WidgetHeader("Today's Schedule")
                        GlanceLazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
                            glanceItems(classes, itemId = { it.id.toLong() }) { cls ->
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

class FullRoutineWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val routine = withContext(Dispatchers.IO) {
            runCatching { RoutineManager.sortedRoutine(RoutineManager.loadRoutine(context)) }
                .getOrDefault(emptyList())
        }
        provideContent {
            WidgetContainer {
                if (routine.isNotEmpty()) {
                    GlanceColumn(modifier = GlanceModifier.fillMaxSize()) {
                        WidgetHeader("Weekly Routine")
                        GlanceLazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
                            glanceItems(routine, itemId = { it.id.toLong() }) { cls ->
                                FullItemUI(cls)
                            }
                        }
                    }
                } else {
                    EmptyWidgetUI("No routine saved yet.")
                }
            }
        }
    }
}
