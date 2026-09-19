package com.shehab.classschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.shehab.classschedule.ui.screens.ScreenState
import com.shehab.classschedule.ui.screens.ScheduleAppUI
import com.shehab.classschedule.ui.screens.EditRoutineScreen
import com.shehab.classschedule.ui.screens.JsonEditorScreen
import com.shehab.classschedule.ui.screens.TableViewScreen
import com.shehab.classschedule.ui.theme.ClassScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClassScheduleTheme {
                var currentScreen by rememberSaveable { mutableStateOf(ScreenState.HOME) }

                when (currentScreen) {
                    ScreenState.HOME -> ScheduleAppUI(
                        onNavigateToEdit = { currentScreen = ScreenState.EDIT },
                        onNavigateToTableView = { currentScreen = ScreenState.TABLE_VIEW },
                        onNavigateToJsonEditor = { currentScreen = ScreenState.JSON_EDITOR }
                    )
                    ScreenState.EDIT -> EditRoutineScreen { currentScreen = ScreenState.HOME }
                    ScreenState.JSON_EDITOR -> JsonEditorScreen { currentScreen = ScreenState.HOME }
                    ScreenState.TABLE_VIEW -> TableViewScreen { currentScreen = ScreenState.HOME }
                }
            }
        }
    }
}
