package com.jonfp.birdhouse;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class SawingActivity extends AccelerometerActivity {

    private boolean saw_is_extended = false;
    private int saw_movement_changes = 0;
    private static final int MOVEMENT_THRESHOLD = 6;
    private static final int movement_changes_THRESHOLD = 1;
    private static final int STROKE_COUNT_THRESHOLD = 5;
    private boolean redirecting = false;
    private int strokeCount = 0;
    private ImageView background;
    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sawing);
        background = findViewById(R.id.imageView);

    }


    @Override
    protected void setupMedia() {
        mediaPlayer = MediaPlayer.create(this, R.raw.saw_short);
    }

    @Override
    protected void handleMotion(float x, float y, float z) {
        // Sawing logic goes here
        // Y axis is the forward and backwards motion of the saw (phone must be horizontally oriented)
        // X axis is checked to make sure the phone is horizontally oriented
        if(y > MOVEMENT_THRESHOLD && Math.abs(x) > MOVEMENT_THRESHOLD){
            saw_is_extended = true;
            saw_movement_changes++;
        } else{
            saw_is_extended = false;
        }
        if(saw_movement_changes > movement_changes_THRESHOLD) {
            strokeCount++;
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            if (strokeCount >= STROKE_COUNT_THRESHOLD) {
                redirectToNewActivity();
            }
            saw_movement_changes = 0;
        }
    }

    private void redirectToNewActivity() {
        System.out.println("redirect");
        if (redirecting) {
            return;
        }
        redirecting = true;

        background.setImageResource(R.drawable.planks);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Play sound effect for transitioning to the next activity
                MediaPlayer nextActivitySound = MediaPlayer.create(SawingActivity.this, R.raw.finished);
                if (nextActivitySound != null) {
                    nextActivitySound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                    nextActivitySound.start();
                }
                Intent intent = new Intent(SawingActivity.this, tool_selection.class);
                intent.putExtra("tool", "hammer"); // variableValue is the value you want to send
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it on back press
            }
        }, 3200);    }
}
