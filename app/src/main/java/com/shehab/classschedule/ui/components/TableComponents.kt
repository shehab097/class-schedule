package com.shehab.classschedule.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.shehab.classschedule.ui.theme.TextPrimary
import com.shehab.classschedule.ui.theme.TextSecondary

@Composable
fun TableHeaderCell(text: String, width: Dp) {
    Text(text, modifier = Modifier.width(width), fontWeight = FontWeight.Bold, color = TextPrimary)
}

@Composable
fun TableCell(text: String, width: Dp) {
    Text(text, modifier = Modifier.width(width), color = TextSecondary, fontSize = 14.sp)
}
