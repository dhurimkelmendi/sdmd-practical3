package gr.academic.city.sdmd.foodnetwork.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import gr.academic.city.sdmd.foodnetwork.service.MealService;
import gr.academic.city.sdmd.foodnetwork.service.PushToServerService;

/**
 * Created by trumpets on 4/24/17.
 */
public class TriggerPushToServerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TRIGGER = "gr.academic.city.sdmd.foodnetwork.TRIGGER";
    public static final String ACTION_TRIGGER_UPVOTE = "gr.academic.city.sdmd.foodnetwork.TRIGGER_UPVOTE";
    private static final String EXTRA_MEAL_SERVER_ID = "meal_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String action = intent.getAction();

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if(action == ACTION_TRIGGER)
                PushToServerService.startPushMealsToServer(context);
            else if(action == ACTION_TRIGGER_UPVOTE) {
                long mealServerId = intent.getLongExtra(EXTRA_MEAL_SERVER_ID, -1);
                PushToServerService.startUpvoteMealToServer(context, mealServerId);
            }
        }
    }
}
