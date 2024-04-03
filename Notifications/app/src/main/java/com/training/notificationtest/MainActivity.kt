package com.training.notificationtest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.training.notificationtest.ui.theme.NotificationTestTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = "When the app start u will see" +
                            "a notification\nand when the onPause() envent is executed")
                    MyNotification("IT WORKS!!!!", this)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        MyNotification("The onPause() has been executed", this)
    }

}


@RequiresApi(Build.VERSION_CODES.O)
fun MyNotification(txtToShow: String, context: Context) {

    //This is to set which activity open when the notification is clicked
    val intent = Intent(context,MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    //first of all build the notification
    val builder = NotificationCompat.Builder(context,"Main Channel ID")// we must pass the app context and the name of the notification channel
        .setSmallIcon(R.drawable.ic_launcher_foreground)// what icon will be displayed on the notifications bar
        .setContentTitle("Ismael Notification App")// the notification title
        .setContentText(txtToShow)// the text of the notification
        .setContentIntent(pendingIntent) // set what activity must be open when the notification is clicked
        .setAutoCancel(true) // delete de notification from the status bar when is clicked

    //now show the notification
    with(NotificationManagerCompat.from(context)){
        //the notification manager takes care to show the notification, what to do if the permission
        //is not allowed by the user, set a Notification channel, etc

        //set what to do in case the permission is not allowed
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        //create a notification channel
        val channel = NotificationChannel(
            "Main Channel ID",
            "Main Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        //set the notification channel
        createNotificationChannel(channel)

        //show the notification
        notify(1,builder.build())

        // --- is not mandatory to create and set the notification channel here
        // --- this logic could be divided for better handling
    }
}

