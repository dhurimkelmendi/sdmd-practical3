package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.service.MealTypeService;
import gr.academic.city.sdmd.foodnetwork.ui.fragment.MealDetailsFragment;
import gr.academic.city.sdmd.foodnetwork.ui.fragment.MealListFragment;

public class MainActivity extends ToolBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {
            FoodNetworkContract.MealType._ID,
            FoodNetworkContract.MealType.COLUMN_NAME,
            FoodNetworkContract.MealType.COLUMN_SERVER_ID
    };

    public static final String LOG_TAG = "MAIN ACTIVITY";

    private static final String SORT_ORDER = FoodNetworkContract.MealType.COLUMN_PRIORITY + " ASC";

    private static final int MEAL_TYPES_LOADER = 0;

    private final static String[] FROM_COLUMNS = {
            FoodNetworkContract.MealType.COLUMN_NAME};

    private final static int[] TO_IDS = {
            R.id.tv_meal_type_name};

    private CursorAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SimpleCursorAdapter(this, R.layout.item_meal_type, null, FROM_COLUMNS, TO_IDS, 0);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateMealTypesRefresh();
            }
        });

        ListView resultsListView = (ListView) findViewById(android.R.id.list);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                if (cursor.moveToPosition(position)) {
                    onMealItemSelected(cursor.getLong(cursor.getColumnIndexOrThrow(FoodNetworkContract.MealType.COLUMN_SERVER_ID)));
                }
            }
        });

        getSupportLoaderManager().initLoader(MEAL_TYPES_LOADER, null, this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleResource() {
        return R.string.main_activity_title;
    }

    @Override
    protected void onResume() {
        super.onResume();

        MealTypeService.startFetchMealTypes(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MEAL_TYPES_LOADER:
                return new CursorLoader(this,
                        FoodNetworkContract.MealType.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
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

    private void initiateMealTypesRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new FetchMealTypesAsyncTask().execute();
    }

    public void onMealItemSelected(long mealId) {
        startActivity(MealsActivity.getStartIntent(this, mealId));
    }

    private class FetchMealTypesAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            MealTypeService.startFetchMealTypes(MainActivity.this);

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