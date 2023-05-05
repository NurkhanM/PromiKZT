package product.promikz.screens.auth.confirm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtil.setAlarmSetTime(0, context)
    }
}