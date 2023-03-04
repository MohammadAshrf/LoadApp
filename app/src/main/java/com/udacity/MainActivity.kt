package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadId: Long = 0
    private var downloadedFileName = ""
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (repoUrl.isEmpty()) {
                Toast.makeText(
                    applicationContext, "Please select a file to download", Toast.LENGTH_SHORT
                ).show()

            } else {
                download()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            intent.apply {
                if (this?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE && id == downloadId) {
                    val downloadQuery = DownloadManager.Query().setFilterById(downloadId)
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = downloadManager.query(downloadQuery)
                    if (cursor.moveToFirst()) {
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_FAILED -> {
                                notificationManager.sendNotification(
                                    "Download failed", downloadedFileName, context!!
                                )
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                notificationManager.sendNotification(
                                    "Download completed", downloadedFileName, context!!
                                )
                            }
                        }
                        custom_button.updateBtnState(ButtonState.Completed)
                        custom_button.isEnabled = true
                    }
                }
            }
        }
    }

    private fun download() {

        custom_button.updateBtnState(ButtonState.Loading)
        custom_button.isEnabled = false

        notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        createChannel(
            getString(R.string.file_notification_channel_id),
            getString(R.string.file_notification_channel_name)
        )

        val request =
            DownloadManager.Request(Uri.parse(repoUrl)).setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description)).setRequiresCharging(false)
                .setAllowedOverMetered(true).setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
    }


    fun onRadioBtnChecked(view: View) {
        if (view is RadioButton) {
            val isChecked = view.isChecked

            when (view.id) {
                R.id.glide_rb -> {
                    if (isChecked) {
                        custom_button.isEnabled = true
                        repoUrl = "https://github.com/bumptech/glide/archive/master.git"
                        downloadedFileName = getString(R.string.glide_loading_rb)
                    }
                }
                R.id.load_app_rb -> {
                    if (isChecked) {
                        custom_button.isEnabled = true
                        repoUrl =
                            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                        downloadedFileName = getString(R.string.load_app_rb)
                    }
                }
                R.id.retrofit_rb -> {
                    if (isChecked) {
                        custom_button.isEnabled = true
                        repoUrl = "https://github.com/square/retrofit/archive/master.zip"
                        downloadedFileName = getString(R.string.retrofit_rb)
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.apply {
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = getString(R.string.notification_description)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private var repoUrl = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        repoUrl = ""
    }
}