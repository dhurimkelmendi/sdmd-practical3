package gr.academic.city.sdmd.foodnetwork.util;

/**
 * Created by trumpets on 4/24/17.
 */
public final class Constants {

    private Constants() {}

    public static final String SERVER_URL = "http://clubs-sdmdcity.rhcloud.com/rest/";
    public static final String MEAL_TYPES_URL = SERVER_URL + "types";
    public static final String MEALS_URL = SERVER_URL + "types/{0}/meals";
}
