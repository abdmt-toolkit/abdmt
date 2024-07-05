package com.judykong.catchpokemon_notoolkit;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "com.abdmt.ACTION_PROCESS_ACTIVITY_TRANSITIONS";
    public AppCompatActivity activity;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            if (result != null && result.getTransitionEvents() != null) {
                List<ActivityTransitionEvent> detectedActivities = result.getTransitionEvents();
                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(MainActivity.DETECTED_ACTIVITY,
                                detectedActivitiesToJson(detectedActivities))
                        .apply();
            }
        }
    }
    public static String detectedActivitiesToJson(List<ActivityTransitionEvent> detectedActivitiesList) {
        Type type = new TypeToken<ArrayList<ActivityTransitionEvent>>() {}.getType();
        ArrayList<ActivityTransitionEvent> tmp = new ArrayList<ActivityTransitionEvent>();
        for (int i = 0; i < detectedActivitiesList.size(); i++) {
            tmp.add(detectedActivitiesList.get(i));
        }
        return new Gson().toJson(tmp, type);
    }
    public static ArrayList<ActivityTransitionEvent> detectedActivitiesFromJson(String jsonArray) {
        Type listType = new TypeToken<ArrayList<ActivityTransitionEvent>>(){}.getType();
        ArrayList<ActivityTransitionEvent> detectedActivities = new Gson().fromJson(jsonArray, listType);
        if (detectedActivities == null) {
            detectedActivities = new ArrayList<>();
        }
        return detectedActivities;
    }
}