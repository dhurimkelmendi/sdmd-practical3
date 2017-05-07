package gr.academic.city.sdmd.foodnetwork.util;

import android.support.v4.BuildConfig;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by trumpets on 4/24/17.
 */
public final class Commons {

    public enum ConnectionMethod {
        GET, POST
    }

    public interface ResponseCallback {
        void onResponse(int responseCode, String responsePayload);
    }

    private Commons() {}

    public static void executeRequest(String urlToConnectTo, ConnectionMethod connectionMethod, String jsonPayload, ResponseCallback responseCallback) {
        InputStream is = null;

        try {
            URL url = new URL(urlToConnectTo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setRequestMethod(connectionMethod.toString());
            conn.setDoInput(true);

            if (connectionMethod == ConnectionMethod.POST) {
                conn.setDoOutput(true);
                conn.addRequestProperty("Content-Type", "application/json");

                if (jsonPayload != null) {
                    Log.d(BuildConfig.APPLICATION_ID, "Payload for request " + jsonPayload);

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    writer.write(jsonPayload);
                    writer.flush();
                    writer.close();

                    conn.getOutputStream().close();
                }
            }

            // Starts the post
            conn.connect();

            int response = conn.getResponseCode();

            Log.d(BuildConfig.APPLICATION_ID, "The response is: " + response);

            String result = null;

            // Convert the InputStream into a bitmap
            is = conn.getInputStream();
            result = convertStreamToString(is);

            responseCallback.onResponse(response, result);

        } catch (Exception e) {
            Log.e(BuildConfig.APPLICATION_ID, "Exception executing request", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(BuildConfig.APPLICATION_ID, "Exception closing stream", e);
                }
            }
        }

    }

    private static String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        return baos.toString();
    }
}
