package gr.academic.city.sdmd.foodnetwork.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import gr.academic.city.sdmd.foodnetwork.service.PushToServerService;

/**
 * Created by trumpets on 4/24/17.
 */
public class TriggerPushToServerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TRIGGER = "gr.academic.city.sdmd.foodnetwork.TRIGGER";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            PushToServerService.startPushMealsToServer(context);
        }
    }
}
