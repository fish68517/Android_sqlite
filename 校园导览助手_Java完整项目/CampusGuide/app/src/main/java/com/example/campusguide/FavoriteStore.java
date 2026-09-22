package com.example.campusguide;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public final class FavoriteStore {
    private static final String PREFS = "campus_guide_prefs";
    private static final String KEY_FAVORITES = "favorite_place_ids";

    private FavoriteStore() { }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isFavorite(Context context, int placeId) {
        return prefs(context).getStringSet(KEY_FAVORITES, new HashSet<>())
                .contains(String.valueOf(placeId));
    }

    public static boolean toggle(Context context, int placeId) {
        Set<String> values = new HashSet<>(
                prefs(context).getStringSet(KEY_FAVORITES, new HashSet<>()));
        String id = String.valueOf(placeId);
        boolean added;
        if (values.contains(id)) {
            values.remove(id);
            added = false;
        } else {
            values.add(id);
            added = true;
        }
        prefs(context).edit().putStringSet(KEY_FAVORITES, values).apply();
        return added;
    }
}
