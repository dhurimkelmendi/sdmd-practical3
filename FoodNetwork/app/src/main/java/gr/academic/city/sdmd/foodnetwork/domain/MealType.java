package gr.academic.city.sdmd.foodnetwork.domain;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;

/**
 * Created by trumpets on 4/24/17.
 */
public class MealType {

    @SerializedName("id")
    private long serverId;

    @SerializedName("name")
    private String name;

    @SerializedName("priority")
    private long priority;

    /**
     * No args constructor for use in serialization
     *
     */
    public MealType() {
    }

    public MealType(long serverId) {
        this.serverId = serverId;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FoodNetworkContract.MealType.COLUMN_NAME, name);
        contentValues.put(FoodNetworkContract.MealType.COLUMN_PRIORITY, priority);
        contentValues.put(FoodNetworkContract.MealType.COLUMN_SERVER_ID, serverId);

        return contentValues;
    }

    public long getServerId() {
        return serverId;
    }
}
