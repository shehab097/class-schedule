package com.shehab.classschedule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shehab.classschedule.data.RoutineManager
import com.shehab.classschedule.ui.theme.BgColor
import com.shehab.classschedule.ui.theme.SurfaceColor
import com.shehab.classschedule.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonEditorScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var jsonText by remember { mutableStateOf(RoutineManager.loadRawJson(context)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Raw JSON Editor") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(pv)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = jsonText,
                onValueChange = {
                    jsonText = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary)
            )

            errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (RoutineManager.saveRawJson(context, jsonText)) {
                            onNavigateBack()
                        } else {
                            errorMessage = "Invalid JSON — expected a top-level array."
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }

                Button(
                    onClick = {
                        RoutineManager.resetToDefault(context)
                        jsonText = RoutineManager.loadRawJson(context)
                        errorMessage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Reset") }
            }
        }
    }
}
