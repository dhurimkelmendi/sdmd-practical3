package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.service.MealService;


/**
 * Created by trumpets on 4/13/16.
 */
public class CreateMealActivity extends AppCompatActivity {

    private static final String EXTRA_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

    public static Intent getStartIntent(Context context, long mealTypeServerId) {
        Intent intent = new Intent(context, CreateMealActivity.class);
        intent.putExtra(EXTRA_MEAL_TYPE_SERVER_ID, mealTypeServerId);

        return intent;
    }

    private long mealTypeServerId;

    private EditText txtTitle;
    private EditText txtRecipe;
    private EditText txtNumberOfServings;
    private TextView tvPrepTime;

    private int prepTimeHour;
    private int prepTimeMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        mealTypeServerId = getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, -1);

        txtTitle = (EditText) findViewById(R.id.txt_meal_title);
        txtRecipe = (EditText) findViewById(R.id.txt_recipe);
        txtNumberOfServings = (EditText) findViewById(R.id.txt_number_of_servings);
        tvPrepTime = (TextView) findViewById(R.id.tv_prep_time);

        setPrepTime(0, 0);

        findViewById(R.id.tv_prep_time_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        tvPrepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewMeal();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveNewMeal() {
        String title = txtTitle.getText().toString();
        String recipe = txtRecipe.getText().toString();
        String numberOfServings = txtNumberOfServings.getText().toString();

        if (title.trim().isEmpty()) {
            txtTitle.setError(getString(R.string.required));
            txtTitle.requestFocus();
            return;
        }

        if (recipe.trim().isEmpty()) {
            txtRecipe.setError(getString(R.string.required));
            txtRecipe.requestFocus();
            return;
        }

        if (numberOfServings.trim().isEmpty()) {
            txtNumberOfServings.setError(getString(R.string.required));
            txtNumberOfServings.requestFocus();
            return;
        }

        if (prepTimeHour == 0 && prepTimeMinute == 0) {
            Toast.makeText(this, R.string.nothing_is_made_in_no_time, Toast.LENGTH_SHORT).show();
            return;
        }

        MealService.startCreateMeal(this,
                mealTypeServerId,
                title,
                recipe,
                Integer.parseInt(numberOfServings),
                prepTimeHour,
                prepTimeMinute);

        finish();
    }

    private void showTimePicker() {

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setPrepTime(hourOfDay, minute);
            }
        }, prepTimeHour, prepTimeMinute, false);

        dialog.show();
    }

    private void setPrepTime(int prepTimeHour, int prepTimeMinute) {
        this.prepTimeHour = prepTimeHour;
        this.prepTimeMinute = prepTimeMinute;

        tvPrepTime.setText(getString(R.string.prep_time_w_placeholder, prepTimeHour, prepTimeMinute));
    }
}
