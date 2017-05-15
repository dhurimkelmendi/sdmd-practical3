package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.service.MealService;
import gr.academic.city.sdmd.foodnetwork.ui.fragment.MealDetailsFragment;

/**
 * Created by trumpets on 4/13/16.
 */
public class MealDetailsActivity extends ToolBarActivity implements MealDetailsFragment.OnFragmentInteractionListener {

    private static final String EXTRA_MEAL_ID = "meal_id";

    private long mealId;

    private static final String LOG_TAG = "MealDetailsActivity ";

    public static Intent getStartIntent(Context context, long mealId) {
        Intent intent = new Intent(context, MealDetailsActivity.class);
        intent.putExtra(EXTRA_MEAL_ID, mealId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mealId = getIntent().getLongExtra(EXTRA_MEAL_ID, -1);

        if (findViewById(R.id.fragment_container_meal) != null) {

            if (savedInstanceState != null) {
                return;
            }

            MealDetailsFragment firstFragment = MealDetailsFragment.newInstance(mealId);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_meal, firstFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor c = getContentResolver().query(FoodNetworkContract.Meal.CONTENT_URI, new String[0], FoodNetworkContract.Meal._ID + " = " + mealId, null, null);
        while (c.moveToNext()) {
            getSupportActionBar().setTitle(c.getString(c.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_TITLE)));
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_meal_details;
    }

    @Override
    protected int getTitleResource() {
        return R.string.meal_details_title;
    }

    @Override
    public void onFragmentInteraction(long mealId) {

    }
}
