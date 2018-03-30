package com.peternatewood.tanksone_on_one;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TitleActivity extends AppCompatActivity implements View.OnClickListener {
  /**
   * Whether or not the system UI should be auto-hidden after
   * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
   */
  private static final boolean AUTO_HIDE = true;

  /**
   * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
   * user interaction before hiding the system UI.
   */
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

  /**
   * Some older devices needs a small delay between UI widget updates
   * and a change of the status and navigation bar.
   */
  private static final int UI_ANIMATION_DELAY = 300;
  private final Handler mHideHandler = new Handler();

  private View mContentView;
  private View mControlsView;

  @SuppressLint("InlinedApi")
  public void run() {
    // Delayed display of UI elements
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.show();
    }
    mControlsView.setVisibility(View.VISIBLE);

    // Delayed removal of status and navigation bar

    // Note that some of these constants are new as of API 16 (Jelly Bean)
    // and API 19 (KitKat). It is safe to use them, as they are inlined
    // at compile-time and do nothing on earlier devices.
    mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_title);

    mControlsView = findViewById(R.id.fullscreen_content_controls);
    mContentView = findViewById(R.id.title_text);
    Button startButton = (Button) findViewById(R.id.start_button);

    startButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    startActivity(new Intent(this, MenuActivity.class));
  }
}
