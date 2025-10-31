package com.example.nuclearandroiddz4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class Filter(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {

        val file = File(applicationContext.cacheDir, "numbersFile.txt")

        val fileContent = file.readText()

        val listOfRandomNumbers = fileContent.split("\n")
            .map { it.toInt() }
            .filter { it % 2 == 0 }

        file.writeText(listOfRandomNumbers.joinToString("\n"))

        Thread.sleep(3000)

        NotificationLogic.showNotification(
            context = applicationContext,
            title = "Filter Worker",
            message = "Data is filtered"
        )

        return Result.success()
    }
}