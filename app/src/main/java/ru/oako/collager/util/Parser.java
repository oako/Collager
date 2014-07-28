package ru.oako.collager.util;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class for operations with connecting to url
 * and getting JSON data
 * Created by Alexei on 24.07.2014.
 */
public class Parser {
    private static final String LOG_TAG = Parser.class.getSimpleName();

    private String mResponse = null;

    private String[] mImageUrls;
    private String[] mThumbUrls;

    private Parser(Uri uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        InputStream is = null;

        try {
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the InputStream into a string
            if (urlConnection.getResponseCode() != 200) return;

            is = urlConnection.getInputStream();
            StringBuilder sb = new StringBuilder();
            if (is == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            if (sb.length() == 0) {
                return;
            }

            mResponse = sb.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        }
    }

    public static Parser connect(Uri uri) {
        return new Parser(uri);
    }

    /**
     * Fetches image and thumb urls from Instagram
     */
    public Parser getImages() throws JSONException {
        final String IMAGES = "images";
        final String THUMB = "thumbnail";
        final String HI_RES = "standard_resolution";
        final String URL = "url";
        final String DATA = "data";

        if (mResponse == null) return this;
        JSONObject mediaJson = new JSONObject(mResponse);
        JSONArray imagesData = mediaJson.getJSONArray(DATA);

        mThumbUrls = new String[imagesData.length()];
        mImageUrls = new String[imagesData.length()];

        for (int i = 0; i < imagesData.length(); i++) {
            JSONObject media = imagesData.getJSONObject(i);
            JSONObject image = media.getJSONObject(IMAGES);

            mThumbUrls[i] = image.getJSONObject(THUMB).getString(URL);
            mImageUrls[i] = image.getJSONObject(HI_RES).getString(URL);
        }

        return this;
    }

    public String[] getImageUrls() {
        return mImageUrls;
    }

    public String[] getThumbUrls() {
        return mThumbUrls;
    }


    /**
     * Find user by name, if user doesn't exist return null
     */
    public String getUserId() throws JSONException {
        final String USER_DATA = "data";
        final String USER_ID = "id";
        if (mResponse == null) return null;
        JSONObject userJson = new JSONObject(mResponse);

        JSONArray userData = userJson.getJSONArray(USER_DATA);
        // if user doesn't exist, than return null
        if (userData.length() == 0) {
            return null;
        }

        return userData.getJSONObject(0).getString(USER_ID);
    }
}
