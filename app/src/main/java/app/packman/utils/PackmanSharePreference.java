package app.packman.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import app.packman.model.User;

/**
 * Created by sujaysudheendra on 3/12/16.
 */
public class PackmanSharePreference {

    /**
     * This application's preferences label
     */
    private static final String PREFS_NAME = "PACKMAN_SHARED_PREFERENCE";
    /**
     * This application's preferences
     */
    private static SharedPreferences settings;
    /**
     * This application's settings editor
     */
    private static SharedPreferences.Editor editor;

    public static User getUser(Context context) {
        String userJson;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
       /*
        * Get a SharedPreferences editor instance.
        * SharedPreferences ensures that updates are atomic
        * and non-concurrent
        */
        editor = settings.edit();
        userJson = settings.getString("App_user", "");
        ObjectMapper mapper = new ObjectMapper();
        User user = null;
        try {
            user = mapper.readValue(userJson, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void saveUser(User user, Context context) {
        String jsonInString = null;
        ObjectMapper mapper = new ObjectMapper();
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
       /*
        * Get a SharedPreferences editor instance.
        * SharedPreferences ensures that updates are atomic
        * and non-concurrent
        */
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            jsonInString = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        editor = settings.edit();
        editor.putString("App_user", jsonInString);
        editor.commit();
    }

    public static void deleteUser(Context context){
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove("App_user");
        editor.commit();
    }

    public static void updateUser(User user, Context context) {
        String jsonInString = null;
        ObjectMapper mapper = new ObjectMapper();
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
       /*
        * Get a SharedPreferences editor instance.
        * SharedPreferences ensures that updates are atomic
        * and non-concurrent
        */
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            jsonInString = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        editor = settings.edit();
        editor.putString("App_user", jsonInString);
        // Note that we are doing .apply and not .commit to update
        editor.apply();
    }

    public static String getUserSocialId(Context context){
        String userJson;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
       /*
        * Get a SharedPreferences editor instance.
        * SharedPreferences ensures that updates are atomic
        * and non-concurrent
        */
        editor = settings.edit();
        userJson = settings.getString("App_user", "");
        ObjectMapper mapper = new ObjectMapper();
        User user = null;
        try {
            user = mapper.readValue(userJson, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user.getPerson().getSocialId();
    }
}
