package com.peternatewood.tanksone_on_one;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MenuActivity extends AppCompatActivity {
  private boolean[] playersReady = { false, false };
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

  public void togglePlayerReady(View v) {
    CompoundButton button = (CompoundButton) v;
    if (button != null) {
      int playerNum = Integer.parseInt(v.getTag().toString());
      if (playerNum == 0 || playerNum == 1) {
        playersReady[playerNum] = button.isChecked();

        if (playersReady[0] && playersReady[1]) {
          startBattle();
        }
      }
    }
  }

  private void startBattle() {
    Intent intent = new Intent(this, GameActivity.class);
    intent.putExtra("controls0", controls[0]);
    intent.putExtra("controls1", controls[1]);
    startActivity(intent);
  }
}
