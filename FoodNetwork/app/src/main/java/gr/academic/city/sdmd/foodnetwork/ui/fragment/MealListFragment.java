package gr.academic.city.sdmd.foodnetwork.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.domain.Meal;
import gr.academic.city.sdmd.foodnetwork.service.MealService;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealDetailsActivity;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealsActivity;

public class MealListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MealDetailsFragment.OnFragmentInteractionListener{

    private static final String LOG_TAG = "MEAL LIST FRAGMENT";
    private static final String[] PROJECTION = {
            FoodNetworkContract.Meal._ID,
            FoodNetworkContract.Meal.COLUMN_TITLE,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE,
            FoodNetworkContract.Meal.COLUMN_UPVOTES};
    private static final String SORT_ORDER = FoodNetworkContract.Meal.COLUMN_UPVOTES + " DESC";
    private static final int MEALS_LOADER = 10;
    private final static String[] FROM_COLUMNS = {
            FoodNetworkContract.Meal.COLUMN_TITLE,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR,
            FoodNetworkContract.Meal.COLUMN_UPVOTES};

    private final static int[] TO_IDS = {
            R.id.tv_meal_title,
            R.id.tv_meal_prep_time,
            R.id.tv_meal_upvote};

    private CursorAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private long mealTypeId;
    private OnFragmentInteractionListener mListener;

    public MealListFragment() {}

    public static MealListFragment newInstance() {
        return new MealListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "ARGUMENTS ----- " + getArguments());
        if (getArguments() != null) {
            mealTypeId = getArguments().getLong("meal_type_server_id", -1);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mealTypeId > 0) {
            Log.d(LOG_TAG, "on activity created");

            swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initiateMealsRefresh();
                }
            });

            adapter = new SimpleCursorAdapter(getContext(), R.layout.item_meal, null, FROM_COLUMNS, TO_IDS, 0);
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

            ListView resultsListView = (ListView) getActivity().findViewById(android.R.id.list);
            resultsListView.setAdapter(adapter);
            resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = adapter.getCursor();
                    if (cursor.moveToPosition(position)) {
                        mListener.onMealItemSelected(cursor.getLong(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal._ID)));
                        Log.d(LOG_TAG, "CLICKED: " + position);
                    }
                }
            });

            getActivity().getSupportLoaderManager().initLoader(MEALS_LOADER, null, this);

            MealService.startFetchMeals(getActivity(), mealTypeId);
        }else
            return;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "ID  = " + id);
        Log.d(LOG_TAG, "meal type ID = " + mealTypeId);
        switch (id) {
            case MEALS_LOADER:
                return new CursorLoader(getActivity(),
                        FoodNetworkContract.Meal.CONTENT_URI,
                        PROJECTION,
                        FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID + " = ?",
                        new String[]{String.valueOf(mealTypeId)},
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

        new FetchMealsAsyncTask().execute(mealTypeId);
    }

    @Override
    public void onFragmentInteraction(long mealId) {
        mListener.onMealItemSelected(mealId);

    }

    private class FetchMealsAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            MealService.startFetchMeals(getActivity(), params[0]);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_list, container, false);
    }


    public void onButtonPressed(long id) {
        if (mListener != null) {
            mListener.onMealItemSelected(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onMealItemSelected(long id);
    }
}