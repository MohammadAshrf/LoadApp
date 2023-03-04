package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


@SuppressLint("UnspecifiedImmutableFlag")
internal fun NotificationManager.sendNotification(
    status: String, fileName: String, applicationContext: Context
) {
    // Check if notifications are enabled.
    if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
        return // Notifications are not enabled, return early.
    }

    // Create an intent to open the detail activity with the status and file name as extras.
    val contentIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra("status", status)
        putExtra("fileName", fileName)
    }

    // Create a pending intent for the content intent.
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        (0..Int.MAX_VALUE).random(), // Use a random request code to avoid collisions with other notifications.
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create a big picture style with the downloaded file image.
    val downloadedFileImage = BitmapFactory.decodeResource(
        applicationContext.resources, R.drawable.cloud_icon
    )
    val bigPicStyle =
        NotificationCompat.BigPictureStyle().bigPicture(downloadedFileImage).bigLargeIcon(null)

    // Create the notification builder with the title, description, icon, style, and action button.
    val builder = NotificationCompat.Builder(
        applicationContext, applicationContext.getString(R.string.file_notification_channel_id)
    ).apply {
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        setContentTitle(applicationContext.getString(R.string.notification_title))
        setContentText(applicationContext.getString(R.string.notification_description))
        setContentIntent(contentPendingIntent)
        setStyle(bigPicStyle)
        setLargeIcon(downloadedFileImage)
        addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.explore),
            contentPendingIntent
        )
        priority = NotificationCompat.PRIORITY_HIGH
        setAutoCancel(true)
    }

    // Send the notification with a random ID.
    notify((0..Int.MAX_VALUE).random(), builder.build())
}

/**
 * Cancels all notifications from this notification manager.
 */
internal fun NotificationManager.cancelNotifications() {
    cancelAll()
}
