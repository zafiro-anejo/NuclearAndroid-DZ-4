package com.example.nuclearandroiddz4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import kotlin.random.Random

class Generator(context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {

        val file = File(applicationContext.cacheDir, "numbersFile.txt")

        val randomNumbers: Array<Int> =  Array(10) { Random.nextInt() }

        file.writeText(randomNumbers.joinToString("\n"))

        Thread.sleep(2000)

        NotificationLogic.showNotification(
            context = applicationContext,
            title = "Generator Worker",
            message = "Data is generated"
        )

        return Result.success()
    }
}