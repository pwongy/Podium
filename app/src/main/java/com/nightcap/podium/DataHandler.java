package com.nightcap.podium;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

/**
 * This class is the only object with direct access to the website. It handles all things related to
 * keeping data up-to-date.
 */
public class DataHandler {
    private String TAG = "DataHandler";

    // Things for interacting with the data store go here.
    private SharedPreferences statistics;
    private static final String KEY_GAMES_PLAYED = "games_played";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_CUM_SCORE = "cumulative_score";
    private static final String KEY_CUM_ZEROS = "cumulative_zeros";

    // Constructor
    public DataHandler(Context context, String name) {
        this.statistics = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    // High score
    public void checkHighScore(int candidate) {
        int highest = getHighScore();
        if (candidate > highest) {
            saveHighScore(candidate);
        }
    }

    public void saveHighScore(int score) {
        SharedPreferences.Editor editor = statistics.edit();
        editor.putInt(KEY_HIGH_SCORE, score);
        editor.apply();
    }

    public int getHighScore() {
        return statistics.getInt(KEY_HIGH_SCORE, 0);
    }

    // Total score
    public void incrementCumulativeScore(int increment) {
        int newTotal = getCumulativeScore() + increment;

        SharedPreferences.Editor editor = statistics.edit();
        editor.putInt(KEY_CUM_SCORE, newTotal);
        editor.apply();
    }

    public int getCumulativeScore() {
        return statistics.getInt(KEY_CUM_SCORE, 0);
    }

    public float getAverageScore() {
        return (float) getCumulativeScore() / (float) getGamesPlayed();
    }

    // Games played
    public void incrementGamesPlayed() {
        int newTotal = getGamesPlayed() + 1;

        SharedPreferences.Editor editor = statistics.edit();
        editor.putInt(KEY_GAMES_PLAYED, newTotal);
        editor.apply();
    }

    public int getGamesPlayed() {
        return statistics.getInt(KEY_GAMES_PLAYED, 0);
    }

    // Total zeroes
    public void incrementCumulativeZeros() {
        int newTotal = getCumulativeZeros() + 1;

        SharedPreferences.Editor editor = statistics.edit();
        editor.putInt(KEY_CUM_ZEROS, newTotal);
        editor.apply();
    }

    public int getCumulativeZeros() {
        return statistics.getInt(KEY_CUM_ZEROS, 0);
    }
}