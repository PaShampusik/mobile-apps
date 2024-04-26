import android.R
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.getData().containsKey("trigger") && remoteMessage.getData().get("trigger")
                .equals("on_foreground")
        ) {
            // Уведомление является триггером on_foreground
            // Создайте и настройте уведомление
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Заголовок уведомления")
                .setContentText("Текст уведомления")

            // Отправьте уведомление
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager!!.notify(0, builder.build())
        }
    }
}