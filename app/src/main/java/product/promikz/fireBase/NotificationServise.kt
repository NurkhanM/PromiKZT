package product.promikz.fireBase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import product.promikz.AppConstants.INTENT_FILTER
import product.promikz.R
import product.promikz.screens.notifications.index.NotificationsActivity

class NotificationServise : FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder


    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }

        if (remoteMessage.data.isNotEmpty()){
            val intent = Intent(INTENT_FILTER)
            remoteMessage.data.forEach { entity ->
                intent.putExtra(entity.key, entity.value)
            }
            sendBroadcast(intent)
        }





    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(title: String?, body: String?) {

        val intent = Intent(this, NotificationsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = NotificationChannel(title, body, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, title)
                .setSmallIcon(R.drawable.shops_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.shops_icon))
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.shops_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.shops_icon))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(0, builder.build())
    }


}