package gr.academic.city.sdmd.foodnetwork.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by trumpets on 4/24/17.
 */
public class FoodNetworkDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "food_network.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String SHORT_TYPE = " SHORT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_MEAL_TYPES =
            "CREATE TABLE " + FoodNetworkContract.MealType.TABLE_NAME + " (" +
                    FoodNetworkContract.MealType._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    FoodNetworkContract.MealType.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    FoodNetworkContract.MealType.COLUMN_PRIORITY + INT_TYPE + COMMA_SEP +
                    FoodNetworkContract.MealType.COLUMN_SERVER_ID + INT_TYPE +
                    " )";

    private static final String SQL_CREATE_MEALS =
            "CREATE TABLE " + FoodNetworkContract.Meal.TABLE_NAME + " (" +
                    FoodNetworkContract.Meal._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    FoodNetworkContract.Meal.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_RECIPE + TEXT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_NUMBER_OF_SERVINGS + INT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR + INT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE + INT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_CREATED_AT + INT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_UPLOADED_TO_SERVER + SHORT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_SERVER_ID + INT_TYPE + COMMA_SEP +
                    FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID + INT_TYPE +
                    " )";

    private static final String SQL_DELETE_MEAL_TYPES =
            "DROP TABLE IF EXISTS " + FoodNetworkContract.MealType.TABLE_NAME;

    private static final String SQL_DELETE_MEALS =
            "DROP TABLE IF EXISTS " + FoodNetworkContract.Meal.TABLE_NAME;

    public FoodNetworkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MEAL_TYPES);
        db.execSQL(SQL_CREATE_MEALS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade
        // policy is to simply discard the data and start over
        // although one can argue that we should prevent the deletion of
        // data still not sent to the server
        // However, that is not the scope of this coursework
        db.execSQL(SQL_DELETE_MEALS);
        db.execSQL(SQL_DELETE_MEAL_TYPES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
