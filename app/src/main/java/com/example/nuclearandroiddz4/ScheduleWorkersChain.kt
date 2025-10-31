package com.example.nuclearandroiddz4

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class ScheduleWorkersChain(val context: Context) {
    val generatorWorker = OneTimeWorkRequestBuilder<Generator>().build()
    val filterWorker = OneTimeWorkRequestBuilder<Filter>().build()
    val analyzerWorker = OneTimeWorkRequestBuilder<Analyzer>().build()

    fun workerChain() {
        WorkManager.getInstance(context)
            .beginWith(generatorWorker)
            .then(filterWorker)
            .then(analyzerWorker)
            .enqueue()
    }
}