package com.example.nuclearandroiddz4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        val scheduleWorker = ScheduleWorkersChain(applicationContext)
        scheduleWorker.workerChain()
    }
}

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

        return Result.success()
    }
}

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

object NotificationLogic {
    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channelId",
                "Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "channelId")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}
