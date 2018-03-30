package com.peternatewood.tanksone_on_one;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MenuActivity extends AppCompatActivity {
  private String[] controls = { "joy", "joy" };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_menu);
  }

  public void setJoyControls(View v) {
    int playerNum = Integer.parseInt(v.getTag().toString());
    if (playerNum == 0 || playerNum == 1) {
      controls[playerNum] = "joy";
    }
  }

  public void setPadControls(View v) {
    int playerNum = Integer.parseInt(v.getTag().toString());
    if (playerNum == 0 || playerNum == 1) {
      controls[playerNum] = "pad";
    }
  }

  public void startBattle(View v) {
    Intent intent = new Intent(this, GameActivity.class);
    intent.putExtra("controls0", controls[0]);
    intent.putExtra("controls1", controls[1]);
    startActivity(intent);
  }
}
