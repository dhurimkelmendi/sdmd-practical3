package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.service.MealService;

/**
 * Created by trumpets on 4/24/17.
 */
public class MealsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

    private static final String[] PROJECTION = {
            FoodNetworkContract.Meal._ID,
            FoodNetworkContract.Meal.COLUMN_TITLE,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE
    };

    private static final String SORT_ORDER = FoodNetworkContract.Meal.COLUMN_CREATED_AT + " DESC";

    private static final int MEALS_LOADER = 10;

    private final static String[] FROM_COLUMNS = {
            FoodNetworkContract.Meal.COLUMN_TITLE,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR,};

    private final static int[] TO_IDS = {
            R.id.tv_meal_title,
            R.id.tv_meal_prep_time};

    public static Intent getStartIntent(Context context, long mealTypeServerId) {
        Intent intent = new Intent(context, MealsActivity.class);
        intent.putExtra(EXTRA_MEAL_TYPE_SERVER_ID, mealTypeServerId);

        return intent;
    }

    private long mealTypeServerId;
    private CursorAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        this.mealTypeServerId = getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, -1);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateMealsRefresh();
            }
        });

        adapter = new SimpleCursorAdapter(this, R.layout.item_meal, null, FROM_COLUMNS, TO_IDS, 0);
        ((SimpleCursorAdapter) adapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR) && view instanceof TextView) {
                    // we have to build a human readable string of prep time

                    TextView textView = (TextView) view;
                    textView.setText(getString(
                            R.string.prep_time_w_placeholder,
                            cursor.getInt(columnIndex),  // we know this is prep time hour
                            cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE))));
                    return true;
                } else {
                    return false;
                }
            }
        });

        ListView resultsListView = (ListView) findViewById(android.R.id.list);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(MealDetailsActivity.getStartIntent(MealsActivity.this, id));
            }
        });

        findViewById(R.id.btn_add_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CreateMealActivity.getStartIntent(MealsActivity.this, mealTypeServerId));
            }
        });

        getSupportLoaderManager().initLoader(MEALS_LOADER, null, this);

        MealService.startFetchMeals(this, mealTypeServerId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MEALS_LOADER:
                return new CursorLoader(this,
                        FoodNetworkContract.Meal.CONTENT_URI,
                        PROJECTION,
                        FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID + " = ?",
                        new String[]{String.valueOf(mealTypeServerId)},
                        SORT_ORDER
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private void initiateMealsRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new FetchMealsAsyncTask().execute(mealTypeServerId);
    }

    private class FetchMealsAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            MealService.startFetchMeals(MealsActivity.this, params[0]);

            try {
                // giving the service ample time to finish
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
