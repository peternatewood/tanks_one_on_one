package com.peternatewood.tanksone_on_one;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;

public class GameActivity extends AppCompatActivity {
  private GameView gameView;
  private Point size;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Get the screen size
    Display display = getWindowManager().getDefaultDisplay();
    // This stores the resolution in a Point object
    size = new Point();
    display.getSize(size);

    gameView = new GameView(this, size.x, size.y);
    setContentView(gameView);
  }

  @Override
  protected void onPause() {
    super.onPause();
    gameView.pause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    gameView.resume();
  }
}
