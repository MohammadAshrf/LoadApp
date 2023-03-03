package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


private val NOTIFICATION_ID = 0


@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(status: String, fileName:String, applicationContext: Context) {

    if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
        return // notifications are not enabled, return early
    }

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.apply {
        putExtra("status",status)
        putExtra("fileName",fileName)
    }
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val downloadedFileImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.cloud_icon
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadedFileImage)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.file_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(downloadedFileImage)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.explore),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}