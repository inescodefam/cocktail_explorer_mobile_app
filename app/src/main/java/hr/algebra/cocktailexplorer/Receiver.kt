package hr.algebra.cocktailexplorer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.cocktailexplorer.framework.setBooleanPreference
import hr.algebra.cocktailexplorer.framework.startActivity

const val DATA_IMPORTED = "hr.algebra.cocktailexplorer.data_imported"

class Receiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // context.setBooleanPreference(DATA_IMPORTED) /// ja sam uploadao podatke
        //       context.startActivity(Intent(context, HostActivity::class.java).apply {
        //            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //        })
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity<HostActivity>()
    }
}
