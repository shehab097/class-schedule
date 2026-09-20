package com.shehab.classschedule.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.action.clickable as glanceClickable
import androidx.glance.background as glanceBackground
import androidx.glance.layout.*
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Box as GlanceBox
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.Row as GlanceRow
import androidx.glance.layout.Spacer as GlanceSpacer
import androidx.glance.text.FontWeight as GlanceFontWeight
import androidx.glance.text.Text as GlanceText
import androidx.glance.text.TextStyle as GlanceTextStyle
import com.shehab.classschedule.MainActivity
import com.shehab.classschedule.model.ClassSession
import com.shehab.classschedule.ui.theme.WBg
import com.shehab.classschedule.ui.theme.WSurface
import com.shehab.classschedule.ui.theme.WBlue
import com.shehab.classschedule.ui.theme.WGreen
import com.shehab.classschedule.ui.theme.WTextPrimary
import com.shehab.classschedule.ui.theme.WTextSecondary

@Composable
fun WidgetContainer(content: @Composable () -> Unit) {
    GlanceBox(
        modifier = GlanceModifier
            .fillMaxSize()
            .glanceBackground(WBg)
            .padding(15.dp)
            .glanceClickable(actionStartActivity<MainActivity>()),
        contentAlignment = GlanceAlignment.TopStart
    ) {
        content()
    }
}

@Composable
fun WidgetHeader(title: String) {
    GlanceColumn(
        modifier = GlanceModifier.fillMaxWidth()
    ) {
        GlanceText(
            text = title,
            style = GlanceTextStyle(
                color = WBlue,
                fontWeight = GlanceFontWeight.Bold,
                fontSize = 14.sp
            ),
            maxLines = 1
        )
        GlanceSpacer(modifier = GlanceModifier.height(4.dp))
    }
}

//@Composable
//fun UpcomingWidgetUI(cls: ClassSession) {
//    val accent = if (cls.isLab) WGreen else WBlue
//
//    GlanceColumn(modifier = GlanceModifier.fillMaxSize()) {
//        GlanceText(
//            text = "UPCOMING",
//            style = GlanceTextStyle(
//                color = WTextSecondary,
//                fontSize = 10.sp,
//                fontWeight = GlanceFontWeight.Bold
//            ),
//            maxLines = 1
//        )
//
//        GlanceSpacer(modifier = GlanceModifier.height(2.dp))
//
//        // Display Course + Title together if course is present
//        val displayTitle = if (cls.course.isNotBlank()) "${cls.course} - ${cls.title}" else cls.title
//        GlanceText(
//            text = displayTitle,
//            style = GlanceTextStyle(
//                color = WTextPrimary,
//                fontSize = 15.sp,
//                fontWeight = GlanceFontWeight.Bold
//            ),
//            maxLines = 2
//        )
//
//        GlanceSpacer(modifier = GlanceModifier.height(6.dp))
//
//        GlanceRow(
//            modifier = GlanceModifier.fillMaxWidth(),
//            verticalAlignment = GlanceAlignment.Vertical.CenterVertically
//        ) {
//            GlanceText(
//                text = "${cls.startTime} - ${cls.endTime}",
//                style = GlanceTextStyle(
//                    color = accent,
//                    fontSize = 13.sp,
//                    fontWeight = GlanceFontWeight.Medium
//                ),
//                maxLines = 1
//            )
//
//            GlanceSpacer(modifier = GlanceModifier.defaultWeight())
//
//            GlanceText(
//                text = "R${cls.room}",
//                style = GlanceTextStyle(
//                    color = WTextPrimary,
//                    fontSize = 11.sp
//                ),
//                modifier = GlanceModifier
//                    .glanceBackground(WSurface)
//                    .padding(horizontal = 6.dp, vertical = 3.dp),
//                maxLines = 1
//            )
//        }
//    }
//}

//@Composable
//fun UpcomingWidgetUI(cls: ClassSession) {
//    val accent = if (cls.isLab) WGreen else WBlue
//
//    GlanceColumn(
//        modifier = GlanceModifier.fillMaxSize(),
//        verticalAlignment = GlanceAlignment.Top
//    ) {
//        // ১. হেডার লেবেল (Font: 8sp)
//        GlanceText(
//            text = "UPCOMING",
//            style = GlanceTextStyle(
//                color = WTextSecondary,
//                fontSize = 8.sp,
//                fontWeight = GlanceFontWeight.Bold
//            ),
//            maxLines = 1
//        )
//
//        GlanceBox(modifier = GlanceModifier.height(2.dp)) {}
//
//        // ২. কোর্স এবং টাইটেল (Font: 11sp, Max 2 lines)
//        val displayTitle = if (cls.course.isNotBlank()) "${cls.course} - ${cls.title}" else cls.title
//        GlanceText(
//            text = displayTitle,
//            style = GlanceTextStyle(
//                color = WTextPrimary,
//                fontSize = 11.sp,
//                fontWeight = GlanceFontWeight.Bold
//            ),
//            maxLines = 2
//        )
//
//        // মাঝখানের জায়গা ফিলাপ করে টাইম ও রুমকে নিচে পুশ করার জন্য
//        GlanceBox(modifier = GlanceModifier.defaultWeight()) {}
//
//        // ৩. টাইম (Font: 10sp)
//        GlanceText(
//            text = "${cls.startTime} - ${cls.endTime}",
//            style = GlanceTextStyle(
//                color = accent,
//                fontSize = 10.sp,
//                fontWeight = GlanceFontWeight.Bold
//            ),
//            maxLines = 1
//        )
//
//        GlanceBox(modifier = GlanceModifier.height(2.dp)) {}
//
//        // ৪. রুম নম্বর ব্যাজ (Font: 9sp)
//        GlanceText(
//            text = "R${cls.room}",
//            style = GlanceTextStyle(
//                color = WTextPrimary,
//                fontSize = 9.sp,
//                fontWeight = GlanceFontWeight.Medium
//            ),
//            modifier = GlanceModifier
//                .glanceBackground(WSurface)
//                .padding(horizontal = 4.dp, vertical = 2.dp),
//            maxLines = 1
//        )
//    }
//}
@Composable
fun UpcomingWidgetUI(cls: ClassSession) {
    val accent = if (cls.isLab) WGreen else WBlue

    GlanceColumn(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = GlanceAlignment.Top
    ) {
        // 1. Course Code (Top label)
        GlanceText(
            text = cls.course.ifBlank { "UPCOMING" },
            style = GlanceTextStyle(
                color = WBlue,
                fontSize = 10.sp,
                fontWeight = GlanceFontWeight.Bold
            ),
            maxLines = 1
        )

        GlanceBox(modifier = GlanceModifier.height(5.dp)) {}

        // 2. Title (Big font, multi-line)
        GlanceText(
            text = cls.title,
            style = GlanceTextStyle(
                color = WTextPrimary,
                fontSize = 14.sp,
                fontWeight = GlanceFontWeight.Bold
            ),
            maxLines = 3
        )

        // Pushes the bottom row to the end of the widget
        GlanceBox(modifier = GlanceModifier.defaultWeight()) {}

        // 3. Bottom Row: Time (Left) & Room No (Right)
        GlanceRow(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = GlanceAlignment.Vertical.CenterVertically
        ) {
            // Time (Left aligned)
            GlanceText(
                text = "${cls.startTime} - ${cls.endTime}",
                style = GlanceTextStyle(
                    color = accent,
                    fontSize = 10.sp,
                    fontWeight = GlanceFontWeight.Bold
                ),
                maxLines = 1
            )

            // Spacer to push Room to the right end
            GlanceBox(modifier = GlanceModifier.defaultWeight()) {}

            // Room No (Right aligned)
            GlanceText(
                text = cls.room,
                style = GlanceTextStyle(
                    color = WTextPrimary,
                    fontSize = 10.sp,
                    fontWeight = GlanceFontWeight.Medium
                ),
                modifier = GlanceModifier
                    .glanceBackground(WSurface)
                    .cornerRadius(4.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                maxLines = 1
            )
        }
    }
}
@Composable
fun DailyItemUI(cls: ClassSession) {
    val accent = if (cls.isLab) WGreen else WBlue

    GlanceRow(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = GlanceAlignment.Vertical.CenterVertically
    ) {
        GlanceColumn(modifier = GlanceModifier.defaultWeight()) {
            // Displays both Course code and Title
            val headerText = if (cls.course.isNotBlank() && cls.title.isNotBlank()) {
                "${cls.title} (${cls.course})"
            } else {
                cls.course.ifBlank { cls.title }
            }

            GlanceText(
                text = headerText,
                style = GlanceTextStyle(
                    color = WTextPrimary,
                    fontWeight = GlanceFontWeight.Bold,
                    fontSize = 12.sp
                ),
                maxLines = 2
            )

            GlanceSpacer(modifier = GlanceModifier.height(2.dp))

            GlanceText(
                text = "${cls.startTime} - ${cls.endTime}",
                style = GlanceTextStyle(
                    color = WTextSecondary,
                    fontSize = 10.sp,

                ),
                maxLines = 1
            )
        }

        GlanceSpacer(modifier = GlanceModifier.width(6.dp))

        GlanceText(
            text = "${cls.room}",
            style = GlanceTextStyle(
                color = accent,
                fontSize = 11.sp,
                fontWeight = GlanceFontWeight.Medium
            ),
            modifier = GlanceModifier
                .glanceBackground(WSurface)
                .padding(horizontal = 5.dp, vertical = 2.dp),
            maxLines = 1
        )
    }
}

/**
 * Table Header for Full Routine View
 */
@Composable
fun FullRoutineTableHeader() {
    GlanceRow(
        modifier = GlanceModifier
            .fillMaxWidth()
            .glanceBackground(WSurface)
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalAlignment = GlanceAlignment.Vertical.CenterVertically
    ) {
        GlanceText(
            text = "DAY",
            style = GlanceTextStyle(color = WBlue, fontSize = 9.sp, fontWeight = GlanceFontWeight.Bold),
            modifier = GlanceModifier.width(36.dp),
            maxLines = 1
        )
        GlanceText(
            text = "COURSE / TITLE",
            style = GlanceTextStyle(color = WTextSecondary, fontSize = 9.sp, fontWeight = GlanceFontWeight.Bold),
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1
        )
        GlanceText(
            text = "TIME",
            style = GlanceTextStyle(color = WTextSecondary, fontSize = 9.sp, fontWeight = GlanceFontWeight.Bold),
            modifier = GlanceModifier.width(52.dp),
            maxLines = 1
        )
        GlanceText(
            text = "ROOM",
            style = GlanceTextStyle(color = WTextSecondary, fontSize = 9.sp, fontWeight = GlanceFontWeight.Bold),
            modifier = GlanceModifier.width(36.dp),
            maxLines = 1
        )
    }
}

/**
 * Table Row Item for Full Routine View
 */
@Composable
fun FullItemUI(cls: ClassSession) {
    val accent = if (cls.isLab) WGreen else WTextPrimary

    GlanceRow(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalAlignment = GlanceAlignment.Vertical.CenterVertically
    ) {
        // Day Column
        GlanceText(
            text = cls.day,
            style = GlanceTextStyle(
                color = WBlue,
                fontSize = 10.sp,
                fontWeight = GlanceFontWeight.Bold
            ),
            modifier = GlanceModifier.width(36.dp),
            maxLines = 1
        )

        // Course & Title Stacked Column
        GlanceColumn(
            modifier = GlanceModifier
                .defaultWeight()
                .padding(end = 4.dp)
        ) {
            if (cls.course.isNotBlank()) {
                GlanceText(
                    text = cls.course,
                    style = GlanceTextStyle(
                        color = accent,
                        fontSize = 10.sp,
                        fontWeight = GlanceFontWeight.Bold
                    ),
                    maxLines = 1
                )
            }
            GlanceText(
                text = cls.title,
                style = GlanceTextStyle(
                    color = WTextPrimary,
                    fontSize = 9.sp
                ),
                maxLines = 1
            )
        }

        // Time Column
        GlanceText(
            text = cls.startTime,
            style = GlanceTextStyle(
                color = WTextSecondary,
                fontSize = 9.sp
            ),
            modifier = GlanceModifier.width(52.dp),
            maxLines = 1
        )

        // Room Column
        GlanceText(
            text = "${cls.room}",
            style = GlanceTextStyle(
                color = WTextSecondary,
                fontSize = 9.sp,
                fontWeight = GlanceFontWeight.Medium
            ),
            modifier = GlanceModifier.width(36.dp),
            maxLines = 1
        )
    }
}

@Composable
fun EmptyWidgetUI(msg: String) {
    GlanceBox(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = GlanceAlignment.Center
    ) {
        GlanceText(
            text = msg,
            style = GlanceTextStyle(
                color = WTextSecondary,
                fontSize = 12.sp
            ),
            maxLines = 3
        )
    }
}