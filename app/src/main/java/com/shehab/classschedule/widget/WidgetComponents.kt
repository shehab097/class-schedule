package com.shehab.classschedule.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
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
            .padding(12.dp)
            .glanceClickable(actionStartActivity<MainActivity>()),
        contentAlignment = GlanceAlignment.TopStart
    ) {
        content()
    }
}

@Composable
fun WidgetHeader(title: String) {
    GlanceColumn {
        GlanceText(
            text = title,
            style = GlanceTextStyle(color = WBlue, fontWeight = GlanceFontWeight.Bold, fontSize = 14.sp),
            maxLines = 1
        )
        GlanceSpacer(GlanceModifier.height(8.dp))
    }
}

@Composable
fun UpcomingWidgetUI(cls: ClassSession) {
    val accent = if (cls.isLab) WGreen else WBlue
    GlanceColumn(modifier = GlanceModifier.fillMaxSize()) {
        GlanceText(
            text = "UPCOMING",
            style = GlanceTextStyle(color = WTextSecondary, fontSize = 10.sp, fontWeight = GlanceFontWeight.Bold),
            maxLines = 1
        )
        GlanceText(
            text = cls.title,
            style = GlanceTextStyle(color = WTextPrimary, fontSize = 16.sp, fontWeight = GlanceFontWeight.Bold),
            maxLines = 2
        )
        GlanceSpacer(GlanceModifier.height(4.dp))
        GlanceRow(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = GlanceAlignment.Vertical.CenterVertically
        ) {
            GlanceText(
                text = "${cls.startTime} - ${cls.endTime}",
                style = GlanceTextStyle(color = accent, fontSize = 13.sp, fontWeight = GlanceFontWeight.Medium),
                maxLines = 1
            )
            GlanceSpacer(GlanceModifier.defaultWeight())
            GlanceText(
                text = cls.room,
                style = GlanceTextStyle(color = WTextPrimary, fontSize = 11.sp),
                modifier = GlanceModifier.glanceBackground(WSurface).padding(horizontal = 6.dp, vertical = 2.dp),
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
            .padding(vertical = 3.dp),
        verticalAlignment = GlanceAlignment.Vertical.CenterVertically
    ) {
        GlanceColumn(modifier = GlanceModifier.defaultWeight()) {
            GlanceText(
                text = cls.course.ifBlank { cls.title },
                style = GlanceTextStyle(color = WTextPrimary, fontWeight = GlanceFontWeight.Bold, fontSize = 12.sp),
                maxLines = 1
            )
            GlanceText(
                text = "${cls.startTime} - ${cls.endTime}",
                style = GlanceTextStyle(color = WTextSecondary, fontSize = 10.sp),
                maxLines = 1
            )
        }
        GlanceText(
            text = "R${cls.room}",
            style = GlanceTextStyle(color = accent, fontSize = 12.sp),
            maxLines = 1
        )
    }
}

@Composable
fun FullItemUI(cls: ClassSession) {
    GlanceRow(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = GlanceAlignment.Vertical.CenterVertically
    ) {
        GlanceText(
            text = cls.day,
            style = GlanceTextStyle(color = WBlue, fontSize = 10.sp, fontWeight = GlanceFontWeight.Bold),
            modifier = GlanceModifier.width(35.dp),
            maxLines = 1
        )
        GlanceText(
            text = cls.course.ifBlank { cls.title },
            style = GlanceTextStyle(color = WTextPrimary, fontSize = 10.sp),
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1
        )
        GlanceText(
            text = cls.startTime,
            style = GlanceTextStyle(color = WTextSecondary, fontSize = 10.sp),
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
            style = GlanceTextStyle(color = WTextSecondary, fontSize = 12.sp),
            maxLines = 3
        )
    }
}
