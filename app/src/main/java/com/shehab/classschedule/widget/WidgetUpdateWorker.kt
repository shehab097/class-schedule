package com.shehab.classschedule.widget

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shehab.classschedule.data.RoutineManager

class WidgetUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        RoutineManager.updateWidgets(applicationContext)
        return Result.success()
    }
}
