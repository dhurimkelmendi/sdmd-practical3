package gr.academic.city.sdmd.foodnetwork.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.domain.MealType;
import gr.academic.city.sdmd.foodnetwork.util.Commons;
import gr.academic.city.sdmd.foodnetwork.util.Constants;
import gr.academic.city.sdmd.foodnetwork.util.GsonResponseCallback;

import static gr.academic.city.sdmd.foodnetwork.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/24/17.
 */
public class MealTypeService extends IntentService {

    private static final String ACTION_FETCH_MEAL_TYPES = "gr.academic.city.sdmd.foodnetwork.FETCH_MEAL_TYPES";

    public static void startFetchMealTypes(Context context) {
        Intent intent = new Intent(context, MealTypeService.class);
        intent.setAction(ACTION_FETCH_MEAL_TYPES);

        context.startService(intent);
    }

    public MealTypeService() {
        super("MealTypeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_FETCH_MEAL_TYPES.equals(intent.getAction())) {
            fetchMealTypes();
        } else {
            throw new UnsupportedOperationException("Action not supported: " + intent.getAction());
        }
    }

    private void fetchMealTypes() {
        executeRequest(Constants.MEAL_TYPES_URL, Commons.ConnectionMethod.GET, null, new GsonResponseCallback<MealType[]>(MealType[].class) {
            @Override
            protected void onResponse(int responseCode, MealType[] mealTypes) {
                for (MealType mealType : mealTypes) {
                    if (getContentResolver().query(
                            FoodNetworkContract.MealType.CONTENT_URI,
                            null,
                            FoodNetworkContract.MealType.COLUMN_SERVER_ID + " = ?",
                            new String[]{String.valueOf(mealType.getServerId())},
                            null).getCount() == 0) {

                        // this meal type is not in db, add it
                        getContentResolver().insert(
                                FoodNetworkContract.MealType.CONTENT_URI,
                                mealType.toContentValues());
                    }
                }
            }
        });
    }
}
