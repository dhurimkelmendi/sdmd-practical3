package gr.academic.city.sdmd.foodnetwork.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealDetailsActivity;

public class CreateMealFragment extends Fragment {

    private static final String EXTRA_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

    public static MealDetailsFragment newInstance(int mealTypeServerId) {
        MealDetailsFragment fragment = new MealDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MEAL_TYPE_SERVER_ID, mealTypeServerId);
        fragment.setArguments(args);
        return fragment;



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
        if (getArguments() != null) {
            mealTypeServerId = getArguments().getInt(ARG_MEAL_TYPE_SERVER_ID);

        }
}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
     super.onActivityCreated(savedInstanceState);

        Meal meal = MainActivity.

        mealTypeServerId = getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, -1);

        txtTitle = (EditText) findViewById(R.id.txt_meal_title);
        txtRecipe = (EditText) findViewById(R.id.txt_recipe);
        txtNumberOfServings = (EditText) findViewById(R.id.txt_number_of_servings);
        tvPrepTime = (TextView) findViewById(R.id.tv_prep_time);


    }
}
