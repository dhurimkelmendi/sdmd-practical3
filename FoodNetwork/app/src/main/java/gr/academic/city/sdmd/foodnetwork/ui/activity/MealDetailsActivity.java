package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


/**
 * Created by trumpets on 4/13/16.
 */
public class MealDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_MEAL_ID = "meal_id";
    private static final String LOG_TAG = "AsTask-DonwloadImage";

    private static final int MEAL_LOADER = 20;

    public static Intent getStartIntent(Context context, long mealId) {
        Intent intent = new Intent(context, MealDetailsActivity.class);
        intent.putExtra(EXTRA_MEAL_ID, mealId);

        return intent;
    }

    private long mealId;

    private TextView tvTitle;
    private TextView tvRecipe;
    private TextView tvNumberOfServings;
    private TextView tvPrepTime;
    private TextView tvCreationDate;
    private TextView tvUpvotes;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meal_details);


        mealId = getIntent().getLongExtra(EXTRA_MEAL_ID, -1);

        tvTitle = (TextView) findViewById(R.id.tv_meal_title);
        tvRecipe = (TextView) findViewById(R.id.tv_meal_recipe);
        tvNumberOfServings = (TextView) findViewById(R.id.tv_number_of_servings);
        tvPrepTime = (TextView) findViewById(R.id.tv_prep_time);
        tvCreationDate = (TextView) findViewById(R.id.tv_meal_creation_date);
        tvUpvotes = (TextView) findViewById(R.id.tv_upvotes);

        getSupportLoaderManager().initLoader(MEAL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MEAL_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(FoodNetworkContract.Meal.CONTENT_URI, mealId),
                        null, // NULL because we want every column
                        null,
                        null,
                        null
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateView(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateView(null);
    }

    private void updateView(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_TITLE)));
            tvRecipe.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_RECIPE)));
            tvNumberOfServings.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_NUMBER_OF_SERVINGS))));

            int prepTimeHour = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR));
            int prepTimeMinute = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE));

            tvPrepTime.setText(getString(R.string.prep_time_w_placeholder, prepTimeHour, prepTimeMinute));
            tvCreationDate.setText(dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(
                    FoodNetworkContract.Meal.COLUMN_CREATED_AT)))));

            tvUpvotes.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_UPVOTES)));
            startImageDownload(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREVIEW)));
        }

        if (cursor != null) {
            cursor.close();
        }
    }
    private void startImageDownload(String imageUrl) {
        if(imageUrl == null)
            Toast.makeText(MealDetailsActivity.this, "No image for this meal!", Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, imageUrl);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadImageTask().execute(imageUrl);
        } else {
            // display error
            Toast.makeText(this, R.string.err_no_internet, Toast.LENGTH_SHORT).show();
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadImage(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Log.e(LOG_TAG, "Unable to retrieve image. URL may be invalid.");
                Toast.makeText(MealDetailsActivity.this, "Unable to diplay image!", Toast.LENGTH_LONG);
                return;
            }

            ImageView imageView = (ImageView) findViewById(R.id.iv_meal_preview);
            imageView.setImageBitmap(result);
        }
    }

    public static Bitmap downloadImage(String imageUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            boolean redirect = false;
            int response = conn.getResponseCode();
            if (response != HttpURLConnection.HTTP_OK) {
                if (response == HttpURLConnection.HTTP_MOVED_TEMP
                        || response == HttpURLConnection.HTTP_MOVED_PERM
                        || response == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {
                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
            }

            response = conn.getResponseCode();
            is = conn.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
