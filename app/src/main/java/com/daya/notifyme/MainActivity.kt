 package com.daya.notifyme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



 class MainActivity : AppCompatActivity() {

     companion object {
         const val PRIMARY_CHANNEL_ID = "primary_channel_id"
         const val NOTIFICATION_ID = 0

         const val ACTION_UPDATE_NOTIFICATION =
             "com.daya.notifyme.ACTION_UPDATE_NOTIFICATION"
     }

     lateinit var mNotifyManager: NotificationManager
      var mReceiver =NotificationReceiver()

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)
         setNotificationButtonState(
             isNotifyEnabled = true,
             isUpdateEnabled = false,
             isCancelEnabled = false
         )

         button_notify.setOnClickListener {
             sendNotification()
         }

         button_update.setOnClickListener {
             updateNotification()
         }

         button_cancel.setOnClickListener {
             cancelNotification()
         }

         registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
     }

     override fun onDestroy() {
         unregisterReceiver(mReceiver)
         super.onDestroy()
     }


     fun sendNotification() {
         val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
         val updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,updateIntent,PendingIntent.FLAG_ONE_SHOT)

         mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             //create a notification channel
             val notificationChannel = NotificationChannel(
                 PRIMARY_CHANNEL_ID,
                 "Mascot Notification", NotificationManager.IMPORTANCE_HIGH
             )

             notificationChannel.apply {
                 enableLights(true)
                 lightColor = Color.RED
                 enableVibration(true)
                 description = "Notification from Mascot"
             }
             mNotifyManager.createNotificationChannel(notificationChannel)
             val notifyBuilder = getNotificationBuilder()
             notifyBuilder.addAction(R.drawable.ic_update ,"Update Notification",updatePendingIntent)

             mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
         }
         setNotificationButtonState(
             isNotifyEnabled = false,
             isUpdateEnabled = true,
             isCancelEnabled = true
         )
     }


     private fun getNotificationBuilder(): NotificationCompat.Builder {
         val notificationIntent = Intent(this, MainActivity::class.java)

         val notificationPendingIntent = PendingIntent.getActivity(
             this,
             NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
         )

         return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
             .setContentTitle("You've been notified!")
             .setContentText("This is your notification text.")
             .setSmallIcon(R.drawable.ic_android)
             .setContentIntent(notificationPendingIntent)
             .setAutoCancel(true)
             .setPriority(NotificationCompat.PRIORITY_HIGH)
             .setDefaults(NotificationCompat.DEFAULT_ALL)
     }


     fun updateNotification() {
         val androidImage: Bitmap = BitmapFactory
             .decodeResource(resources, R.drawable.mascot_1)

         val notifyBuilder = getNotificationBuilder()

         notifyBuilder.setStyle(
             NotificationCompat.BigPictureStyle()
                 .bigPicture(androidImage)
                 .setBigContentTitle("Notification Updated")
         )

         mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())


         setNotificationButtonState(
             isNotifyEnabled = false,
             isUpdateEnabled = true,
             isCancelEnabled = true
         )
     }

     fun cancelNotification() {
         mNotifyManager.cancel(NOTIFICATION_ID)
         setNotificationButtonState(
             isNotifyEnabled = true,
             isUpdateEnabled = false,
             isCancelEnabled = false)

     }

     fun setNotificationButtonState(
         isNotifyEnabled :Boolean ,
         isUpdateEnabled :Boolean ,
         isCancelEnabled :Boolean
     ) {
         button_notify.setEnabled(isNotifyEnabled)
         button_update.setEnabled(isUpdateEnabled)
         button_cancel.setEnabled(isCancelEnabled)

     }

     inner class NotificationReceiver : BroadcastReceiver(){
         override fun onReceive(context: Context?, intent: Intent?) {
             updateNotification()
         }
     }

 }