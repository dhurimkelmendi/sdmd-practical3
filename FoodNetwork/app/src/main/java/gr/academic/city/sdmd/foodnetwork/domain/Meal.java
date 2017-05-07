package gr.academic.city.sdmd.foodnetwork.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;

/**
 * Created by trumpets on 4/24/17.
 */
public class Meal {

    @SerializedName("id")
    private long serverId;

    @SerializedName("title")
    private String title;

    @SerializedName("recipe")
    private String recipe;

    @SerializedName("numberOfServings")
    private int numberOfServings;

    @SerializedName("prepTimeHour")
    private int prepTimeHour;

    @SerializedName("prepTimeMinute")
    private int prepTimeMinute;

    @SerializedName("createdAt")
    private long createdAt;

    @SerializedName("mealType")
    private MealType mealType;

    /**
     * No args constructor for use in serialization
     */
    public Meal() {
    }

    public Meal(String title, String recipe, int numberOfServings, int prepTimeHour, int prepTimeMinute, long mealTypeServerId) {
        this.title = title;
        this.recipe = recipe;
        this.numberOfServings = numberOfServings;
        this.prepTimeHour = prepTimeHour;
        this.prepTimeMinute = prepTimeMinute;
        this.createdAt = new Date().getTime();

        this.mealType = new MealType(mealTypeServerId);
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FoodNetworkContract.Meal.COLUMN_TITLE, title);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_RECIPE, recipe);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_NUMBER_OF_SERVINGS, numberOfServings);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR, prepTimeHour);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE, prepTimeMinute);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_CREATED_AT, createdAt);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_SERVER_ID, serverId);
        contentValues.put(FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID, mealType.getServerId());

        return contentValues;
    }

    public long getServerId() {
        return serverId;
    }
}
