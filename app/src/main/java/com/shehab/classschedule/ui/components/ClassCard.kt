package com.shehab.classschedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shehab.classschedule.model.ClassSession
import com.shehab.classschedule.ui.theme.BgColor
import com.shehab.classschedule.ui.theme.SurfaceColor
import com.shehab.classschedule.ui.theme.PrimaryBlue
import com.shehab.classschedule.ui.theme.AccentGreen
import com.shehab.classschedule.ui.theme.TextPrimary
import com.shehab.classschedule.ui.theme.TextSecondary

@Composable
fun ClassCard(cls: ClassSession) {
    val accentColor = if (cls.isLab) AccentGreen else PrimaryBlue

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
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
                    Surface(color = BgColor, shape = RoundedCornerShape(8.dp)) {
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
