package com.example.nuclearandroiddz4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class Analyzer(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {

        val file = File(applicationContext.cacheDir, "numbersFile.txt")

        val fileContent = file.readText()

        val averageValueOfNumbers = fileContent.split("\n")
            .map { it.toInt() }
            .average()

        NotificationLogic.showNotification(
            context = applicationContext,
            title = "Analyzer Worker",
            message = "Average is $averageValueOfNumbers"
        )

        file.delete()

        return Result.success()
    }
}