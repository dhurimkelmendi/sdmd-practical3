package gr.academic.city.sdmd.foodnetwork.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by trumpets on 4/24/17.
 */
public final class FoodNetworkContract {

    public static final String AUTHORITY = "gr.academic.city.sdsm.foodnetwork";

    // To prevent someone from accidentally instantiating the,
    // contract class, give it a private empty constructor.
    private FoodNetworkContract() {
    }

    // Inner class that defines the table contents
    // BaseColumns allow us to inherit a primary key field called _ID
    // that most Android classes expect
    public static abstract class MealType implements BaseColumns {

        public static final String TABLE_NAME = "meal_type";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_SERVER_ID = "server_id";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }

    public static abstract class Meal implements BaseColumns {

        public static final String TABLE_NAME = "meal";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RECIPE = "recipe";
        public static final String COLUMN_NUMBER_OF_SERVINGS = "number_of_servings";
        public static final String COLUMN_PREP_TIME_HOUR = "prep_time_hour";
        public static final String COLUMN_PREP_TIME_MINUTE = "prep_time_minute";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPLOADED_TO_SERVER = "uploaded_to_server";
        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME);
    }
}