package gr.academic.city.sdmd.foodnetwork.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.text.MessageFormat;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.domain.Meal;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealDetailsActivity;
import gr.academic.city.sdmd.foodnetwork.util.Commons;
import gr.academic.city.sdmd.foodnetwork.util.Constants;

import static gr.academic.city.sdmd.foodnetwork.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/24/17.
 */
public class PushToServerService extends IntentService {
    public static final String LOG_TAG = "PushToServerService";

    private static final int NOTIFICATION_ID = 187;

    private static final String ACTION_PUSH_MEALS_TO_SERVER = "gr.academic.city.sdmd.foodnetwork.ACTION_PUSH_MEALS_TO_SERVER";
    private static final String ACTION_PUSH_MEAL_UPVOTES_TO_SERVER = "gr.academic.city.sdmd.foodnetwork.ACTION_PUSH_MEAL_UPVOTES_TO_SERVER";

    private static long mealServerId;
    public static void startPushMealsToServer(Context context) {
        Intent intent = new Intent(context, PushToServerService.class);
        intent.setAction(ACTION_PUSH_MEALS_TO_SERVER);

        context.startService(intent);
    }
    public static void startUpvoteMealToServer(Context context, long mealId) {
        mealServerId = mealId;
        Intent intent = new Intent(context, PushToServerService.class);
        intent.setAction(ACTION_PUSH_MEAL_UPVOTES_TO_SERVER);
        context.startService(intent);
    }

    public PushToServerService() {
        super("PushToServerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_PUSH_MEALS_TO_SERVER.equals(intent.getAction())) {
            pushActivitiesToServer();
        }
        else if (ACTION_PUSH_MEAL_UPVOTES_TO_SERVER.equals(intent.getAction())) {
            pushUpvotesToServer();
        }
    }

    private void pushActivitiesToServer() {
        Cursor cursor = getContentResolver().query(
                FoodNetworkContract.Meal.CONTENT_URI,
                null,
                FoodNetworkContract.Meal.COLUMN_UPLOADED_TO_SERVER + " = ?",
                new String[]{String.valueOf(0)},
                null);

        while (cursor.moveToNext()) {
            final long mealDbId = cursor.getLong(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_TITLE));
            String recipe = cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_RECIPE));
            int numberOfServings = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_NUMBER_OF_SERVINGS));
            int prepTimeHour = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR));
            int prepTimeMinute = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE));
            String preview = cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREVIEW));
            int upvotes = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_UPVOTES));

            long mealTypeServerId = cursor.getLong(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID));

            executeRequest(MessageFormat.format(Constants.MEALS_URL, mealTypeServerId), Commons.ConnectionMethod.POST, new Gson().toJson(
                    new Meal(title, recipe, numberOfServings, prepTimeHour, prepTimeMinute, mealTypeServerId, preview, upvotes)),
                    new Commons.ResponseCallback() {
                @Override
                public void onResponse(int responseCode, String responsePayload) {
                    // responsePayload is the new ID of this club activity on the server
                    Meal meal = new Gson().fromJson(responsePayload, Meal.class);
                    Log.d(LOG_TAG, meal.getImagePreview());
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FoodNetworkContract.Meal.COLUMN_UPLOADED_TO_SERVER, 1);
                    contentValues.put(FoodNetworkContract.Meal.COLUMN_SERVER_ID, meal.getServerId());
                    contentValues.put(FoodNetworkContract.Meal.COLUMN_PREVIEW, meal.getImagePreview());

                    getContentResolver().update(
                            ContentUris.withAppendedId(FoodNetworkContract.Meal.CONTENT_URI, mealDbId),
                            contentValues,
                            null,
                            null
                    );
                    Log.d(LOG_TAG, contentValues.toString());

                    showNotification(mealDbId);
                }
            });
        }

        cursor.close();
    }
    private void pushUpvotesToServer(){
        executeRequest(MessageFormat.format(Constants.MEAL_UPVOTE_URL, mealServerId), Commons.ConnectionMethod.POST,
                null, new Commons.ResponseCallback(){
                    @Override
                    public void onResponse(int responseCode, String responsePayLoad){
                        Log.d(LOG_TAG, "Response from server: " + responseCode);
                    }
        });

    }

    private void showNotification(long mealDbId) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, MealDetailsActivity.getStartIntent(this, mealDbId), PendingIntent.FLAG_UPDATE_CURRENT);

        String text = getString(R.string.msg_meal_uploaded);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.meal_uploaded))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
