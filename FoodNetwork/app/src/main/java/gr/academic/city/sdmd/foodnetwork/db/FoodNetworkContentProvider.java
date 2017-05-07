package gr.academic.city.sdmd.foodnetwork.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by trumpets on 4/24/17.
 */
public class FoodNetworkContentProvider extends ContentProvider {

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher;

    private static final int MEAL_TYPES = 1;
    private static final int MEAL_TYPE_ITEM = 2;

    private static final int MEALS = 11;
    private static final int MEAL_ITEM = 12;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FoodNetworkContract.AUTHORITY, FoodNetworkContract.MealType.TABLE_NAME, MEAL_TYPES);
        uriMatcher.addURI(FoodNetworkContract.AUTHORITY, FoodNetworkContract.MealType.TABLE_NAME + "/#", MEAL_TYPE_ITEM);
        uriMatcher.addURI(FoodNetworkContract.AUTHORITY, FoodNetworkContract.Meal.TABLE_NAME, MEALS);
        uriMatcher.addURI(FoodNetworkContract.AUTHORITY, FoodNetworkContract.Meal.TABLE_NAME + "/#", MEAL_ITEM);
    }

    private SQLiteOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new FoodNetworkDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case MEAL_TYPE_ITEM:
                selection = FoodNetworkContract.MealType._ID + "=" + uri.getLastPathSegment();
            case MEAL_TYPES:
                tableName = FoodNetworkContract.MealType.TABLE_NAME;
                break;
            case MEAL_ITEM:
                selection = FoodNetworkContract.Meal._ID + "=" + uri.getLastPathSegment();
            case MEALS:
                tableName = FoodNetworkContract.Meal.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs,
                null, null, sortOrder);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != MEAL_TYPES && uriMatcher.match(uri) != MEALS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Uri contentUri = null;
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case MEAL_TYPES:
                contentUri = FoodNetworkContract.MealType.CONTENT_URI;
                tableName = FoodNetworkContract.MealType.TABLE_NAME;
                break;
            case MEALS:
                contentUri = FoodNetworkContract.Meal.CONTENT_URI;
                tableName = FoodNetworkContract.Meal.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and insert
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(contentUri, rowId);

            // Signal all cursor which monitor this URI that there is new data and
            // they should re-query
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to insert row into " + uri);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case MEAL_TYPE_ITEM:
                selection = FoodNetworkContract.MealType._ID + "=" + uri.getLastPathSegment();
            case MEAL_TYPES:
                tableName = FoodNetworkContract.MealType.TABLE_NAME;
                break;
            case MEAL_ITEM:
                selection = FoodNetworkContract.Meal._ID + "=" + uri.getLastPathSegment();
            case MEALS:
                tableName = FoodNetworkContract.Meal.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and update
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(tableName, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = null;

        switch (uriMatcher.match(uri)) {
            case MEAL_TYPE_ITEM:
                selection = FoodNetworkContract.MealType._ID + "=" + uri.getLastPathSegment();
            case MEAL_TYPES:
                tableName = FoodNetworkContract.MealType.TABLE_NAME;
                break;
            case MEAL_ITEM:
                selection = FoodNetworkContract.Meal._ID + "=" + uri.getLastPathSegment();
            case MEALS:
                tableName = FoodNetworkContract.Meal.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and delete
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(tableName, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return count;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        String subType;
        String tableName;
        switch (uriMatcher.match(uri)) {
            case MEAL_TYPE_ITEM:
                subType = "vnd.android.cursor.item/";
            case MEAL_TYPES:
                tableName = FoodNetworkContract.MealType.TABLE_NAME;
                subType = "vnd.android.cursor.dir/";
                break;
            case MEAL_ITEM:
                subType = "vnd.android.cursor.item/";
            case MEALS:
                tableName = FoodNetworkContract.Meal.TABLE_NAME;
                subType = "vnd.android.cursor.dir/";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return subType += "vnd." + FoodNetworkContract.AUTHORITY + "." + tableName;

    }
}
