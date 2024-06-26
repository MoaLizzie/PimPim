package com.jonfp.birdhouse;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class HammeringActivity extends AccelerometerActivity {

    private boolean hammer_is_extended = false;
    private int hammer_movement_changes = 0;

    private boolean redirecting = false;
    private int hammer_count = 0;
    private static final int MOVEMENT_THRESHOLD = 13; // Adjusted threshold for hammering
    private static final int movement_changes_THRESHOLD = 1; // Adjusted threshold for hammering
    private ImageView background;
    private TextView statusText;

    private final Handler handler = new Handler();
    @Override
    protected void setupMedia() {
        mediaPlayer = MediaPlayer.create(this, R.raw.hammer_sound);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hammering);
            background = findViewById(R.id.imageView);
            statusText = findViewById(R.id.textView3);
    }


    @Override
    protected void handleMotion(float x, float y, float z) {
        // Hammering logic goes here

        if(Math.abs(x) > MOVEMENT_THRESHOLD){
            hammer_is_extended = true;
            hammer_movement_changes++;
        } else{
            hammer_is_extended = false;
        }

        if(hammer_movement_changes > movement_changes_THRESHOLD) {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            hammer_movement_changes = 0;
            hammer_count++;
            if (hammer_count >= 8) {
                redirectToNewActivity();
            }

        }
    }

    private void redirectToNewActivity() {
        System.out.println("redirect");
        if(redirecting){
            return;
        }
        redirecting = true;
        // Play sound effect for transitioning to the next activity
        statusText.setText("");
        background.setImageResource(R.drawable.house);
        System.out.println("hammer done");
        MediaPlayer nextActivitySound = MediaPlayer.create(this, R.raw.finished);
        if (nextActivitySound != null) {
            nextActivitySound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            nextActivitySound.start();
        }



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Play sound effect for transitioning to the next activity

                Intent intent = new Intent(HammeringActivity.this, ToolSelectionActivity.class);
                intent.putExtra("tool", "color");
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to it on back press
            }
        }, 1500);

    }
}
