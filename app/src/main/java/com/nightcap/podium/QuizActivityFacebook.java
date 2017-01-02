package com.nightcap.podium;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizActivityFacebook extends AppCompatActivity {
    private final String TAG = "Podium";

    // Data stuff
    DataHandler statsHandler;
    final String statsFile = "stats_facebook_audiences";
    private List<String[]> allData;
    FacebookData item1, item2;

    // Views
    TextView tv1, tv2, lastScoreView, currentScoreView, highScoreView;
    ImageView iv11, iv12, iv21, iv22;

    MediaPlayer mpCorrect, mpIncorrect;

    // Vibrator
    Vibrator v;

    // Initialise variables
    int currentScore = 0, lastScore = 0, zeroStreak = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data handler for statistics
        this.statsHandler = new DataHandler(getApplicationContext(), statsFile);

        // Set content via xml
        setContentView(R.layout.activity_quiz_facebook);

        // Colourise status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        // Get reference to views
        // Score bar
        lastScoreView = (TextView) findViewById(R.id.last_score);
        currentScoreView = (TextView) findViewById(R.id.current_score_text);
        highScoreView = (TextView) findViewById(R.id.high_score);

        // Cards
        iv11 = (ImageView) findViewById(R.id.image_11);
        iv12 = (ImageView) findViewById(R.id.image_12);
        tv1 = (TextView) findViewById(R.id.label_1);

        iv21 = (ImageView) findViewById(R.id.image_21);
        iv22 = (ImageView) findViewById(R.id.image_22);
        tv2 = (TextView) findViewById(R.id.label_2);

        // Update score views
        updateScoreViews();

        // Extract icon color
//        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_quiz_fb);
//        String hexColor;
//        if (iconBitmap != null && !iconBitmap.isRecycled()) {
//            Palette palette = Palette.from(iconBitmap).generate();
//            hexColor = String.format("#%06X", (0xFFFFFF & palette.getMutedColor(0)));
//            Log.d(TAG, "Color: " + hexColor);
//        }

        // Create an instance of the dialog fragment and show it
        new MaterialDialog.Builder(this)
                .title(R.string.fb_start_dialog_title)
//                .iconRes(R.drawable.ic_quiz_fb)
                .content(R.string.fb_start_dialog_message)
                .positiveText(R.string.fb_start_dialog_positive_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .cancelable(false)
                .show();

        // Initialise data list
        boolean dataImported;
        allData = new ArrayList<String[]>();

        dataImported = importCsvData();
        if (dataImported) {
            showRandomItems(findViewById(R.id.button));
        }

        // Get instance of Vibrator from current Context
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onRetryClick() {
        updateScoreViews();
        showRandomItems(null);
    }

    private void updateScoreViews() {
        lastScoreView.setText(String.valueOf(lastScore));
        currentScoreView.setText(String.valueOf(currentScore));
        highScoreView.setText(String.valueOf(statsHandler.getHighScore()));
    }

    private boolean importCsvData() {
        InputStream is;
        CSVReader reader;
        boolean isImportSuccessful;

        try {
            is = getAssets().open("data/fb_audience_data_reduced.csv");
            reader = new CSVReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            allData = reader.readAll();
            isImportSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
            isImportSuccessful = false;
        }

        return isImportSuccessful;
    }

    public void showRandomItems(View view) {
        Random r = new Random();

        // Get the first item
        int index1 = r.nextInt(allData.size() - 1) + 1;
        item1 = new FacebookData(allData.get(index1));

        // Get a different item
        int index2 = index1;
        while (isNotIdeal(item1, index2)) {
            index2 = r.nextInt(allData.size() - 1) + 1;
        }
        item2 = new FacebookData(allData.get(index2));

        // Update text labels
        tv1.setText(item1.name);
        tv2.setText(item2.name);

        // Update emojis
        setEmoji(item1, iv11, iv12);
        setEmoji(item2, iv21, iv22);
    }

    private boolean isNotIdeal(FacebookData item, int index) {
        boolean isNotIdeal = true;
        FacebookData candidateItem = new FacebookData(allData.get(index));

        // Calculate audience ratio
        int ratioTarget = 20;
        float ratio = (float) Math.max(item.audience, candidateItem.audience)
                / (float) Math.min(item.audience, candidateItem.audience);

        // Deal with people
        int threshold = 8000000;
        if ((item.topic.equals("People") && item.audience < threshold)
                || (candidateItem.topic.equals("People") && candidateItem.audience < threshold)) {
            ratioTarget = 5;
        }

//        Log.d(TAG, "Topic 1: " + item.topic + ", audience: " + item.audience);
//        Log.d(TAG, "Topic 2: " + candidateItem.topic + ", audience: " + candidateItem.audience);
//        Log.i(TAG, "Audience ratio: " + ratio);

        // Candidate must satisfy all conditions
        if (!item.name.equals(candidateItem.name) && ratio < ratioTarget) {
            isNotIdeal = false;
        }
        // TODO: Add other conditions

        return isNotIdeal;
    }

    private void setEmoji(FacebookData item, ImageView iv1, ImageView iv2) {
        String emojiFile1, emojiFile2;

        // Prepare emoji filenames
        if (item.emojiCode.isEmpty()) {
            emojiFile1 = "emojione/1f6ab.png";
        } else {
            emojiFile1 = "emojione/" + item.emojiCode + ".png";
        }

        if (item.emojiExtra.isEmpty()) {
            emojiFile2 = null;
        } else {
            emojiFile2 = "emojione/" + item.emojiExtra + ".png";
        }

        try {
            // Get input stream
            InputStream ims = getAssets().open(emojiFile1);
            // Load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // Set image to ImageView
            iv1.setImageDrawable(d);

            if (emojiFile2 == null){
                iv2.setImageDrawable(null);

                // Eliminate margin
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iv2.getLayoutParams());
                lp.setMargins(0, 0, 0, 0);
                iv2.setLayoutParams(lp);
            } else {
                // Get input stream
                ims = getAssets().open(emojiFile2);
                // Load image as Drawable
                d = Drawable.createFromStream(ims, null);
                // Set image to ImageView
                iv2.setImageDrawable(d);

                // Add margin between emojis
                Resources r = getApplicationContext().getResources();
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10, // dp measure
                        r.getDisplayMetrics()
                );

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iv2.getLayoutParams());
                lp.setMargins(marginPx, 0, 0, 0);
                iv2.setLayoutParams(lp);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean compareItems(View view){
        String chosenItem = view.getResources().getResourceName(view.getId());
        int chosenIndex = Integer.valueOf(chosenItem.substring(chosenItem.length() - 1));
        boolean isCorrect = false;

        // Determine if clicked item is more popular
        switch (chosenIndex) {
            case 1:
                if (item1.audience > item2.audience) {
                    isCorrect = true;
                }
                break;
            case 2:
                if (item2.audience > item1.audience) {
                    isCorrect = true;
                }
                break;
        }

        if (isCorrect) {
            // Play correct sound
            mpCorrect = MediaPlayer.create(this, R.raw.sound_right);
            mpCorrect.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.pause();
                }
            });
            mpCorrect.start();

            // Add one to score
            currentScore += 1;

            // Update score counter view
            currentScoreView.setText(String.valueOf(currentScore));

            // Vibrate score
//            startVibratePattern(currentScore);

            // Show snackbar message
            String message = makeSnackMessage(currentScore);
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setDuration(10000)
                    .setAction(null, null)
                    .show();

            // Reset zero streak counter
            if (zeroStreak != 0) {
                zeroStreak = 0;
            }

            // Show another set of items
            showRandomItems(view);
        } else { // These things happen when the game is lost
            // Play wrong sound
            mpIncorrect = MediaPlayer.create(this, R.raw.sound_wrong);
            mpIncorrect.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.pause();
                }
            });
            mpIncorrect.start();

            // Stop any vibrations
            v.cancel();

            // Update statistics
            statsHandler.incrementGamesPlayed();
            statsHandler.checkHighScore(currentScore);
            statsHandler.incrementCumulativeScore(currentScore);

            if (currentScore == 0) {
                statsHandler.incrementCumulativeZeros();
                zeroStreak += 1;
            }

            final boolean isRageQuit = (zeroStreak >= 5);

            // Now that the stats are calculated, can show these in the end game dialog...
            StringBuilder sb = new StringBuilder();

            sb.append(getString(R.string.fb_end_dialog_message));
            sb.append(": ");
            sb.append(currentScore);
            sb.append("\n");

            sb.append("Cumulative average: ");
            sb.append(String.format("%.2f", statsHandler.getAverageScore()));

            String endMessage = sb.toString();

            // Rage quit?
            int quitMessageId = R.string.fb_end_dialog_negative_button;
            if (isRageQuit) {
                quitMessageId = R.string.fb_end_dialog_negative_button_rage;
            }

            // Create an instance of the dialog fragment and show it
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .title(R.string.fb_end_dialog_title)
                    .content(endMessage)
                    .positiveText(R.string.fb_end_dialog_positive_button)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            onRetryClick();
                            dialog.dismiss();
                        }
                    })
                    .negativeText(quitMessageId)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .cancelable(false);
            MaterialDialog endDialog = builder.build();
            endDialog.show();

            // Reset the counters
            lastScore = currentScore;
            currentScore = 0;
        }

        return isCorrect;
    }

    private String makeSnackMessage(int score) {
        String message = getString(R.string.snack_default);
        final int highScore = statsHandler.getHighScore();

        switch (score) {
            case 3:
                message = getString(R.string.snack_3);
                break;
            case 10:
                message = getString(R.string.snack_10);
                break;
            case 20:
                message = getString(R.string.snack_20);
                break;
        }

        // Override for record scores
        if (score == highScore) {
            message = getString(R.string.snack_high_equal);
        } else if (score == highScore + 1) {
            message = getString(R.string.snack_high_new);
        }

        return message;
    }

}