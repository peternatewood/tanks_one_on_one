package com.peternatewood.tanksone_on_one;

import android.graphics.Rect;

import android.util.Log;

public class Joystick {
  private final int JOY_DIRECTION_MOD;
  private final float JOY_MAX = 32.f;
  private final float buttonSize = 64.f;

  private boolean button;
  private int x, y, w, h, joyX, joyY;
  private Rect rect;
  private int buttonX, buttonY;
  private int buttonIndex, joyIndex; // MotionEvent indexes
  private float xAcc, yAcc, xTouch, yTouch;

  public Joystick(int width, int height, boolean isOnBottom) {
    x = 0;
    w = width;
    h = 256;
    y = isOnBottom ? height - h : 0;

    rect = new Rect(x, y, x + w, y + h);

    joyX = isOnBottom ? h / 2 : w - h / 2;
    joyY = y + h / 2;
    joyIndex = -1;

    buttonX = isOnBottom ? w - h / 2 : h / 2;
    buttonY = y + h / 2;
    buttonIndex = -1;

    button = false;
    JOY_DIRECTION_MOD = isOnBottom ? 1 : -1;
  }

  public boolean _button() {
    return button;
  }

  public Rect _rect() {
    return rect;
  }

  public int _joyX() {
    return joyX;
  }

  public int _joyY() {
    return joyY;
  }

  public int _buttonX() {
    return buttonX;
  }

  public int _buttonY() {
    return buttonY;
  }

  public float _buttonSize() {
    return buttonSize;
  }

  public float _xVel() {
    return xAcc;
  }

  public float _yVel() {
    return yAcc;
  }

  public float _xAcc() {
    return JOY_DIRECTION_MOD * xAcc / JOY_MAX;
  }

  public float _yAcc() {
    return JOY_DIRECTION_MOD * yAcc / -JOY_MAX; // Invert sign so tank moves forward
  }

  public void handleActionDown(float xPos, float yPos, int index) {
    Log.i("Action Down", Integer.toString(index));
    if (isOnButton(xPos, yPos)) {
      button = true;
      buttonIndex = index;
    }
    else if (isInBounds(xPos, yPos)) {
      // Set initial touch position
      xTouch = xPos;
      yTouch = yPos;
      joyIndex = index;
    }
  }

  public void handleActionMove(float xPos, float yPos, int index) {
    if (index == joyIndex) {
      // Calculate acceleration in x and y directions based on position relative to initial touch position
      xAcc = clamp(xPos - xTouch, -JOY_MAX, JOY_MAX);
      yAcc = clamp(yPos - yTouch, -JOY_MAX, JOY_MAX);
    }
  }

  public void handleActionUp(float xPos, float yPos, int index) {
    if (index == buttonIndex) {
      button = false;
      buttonIndex = -1;
    }
    else if (index == joyIndex) {
      xTouch = -1;
      yTouch = -1;
      xAcc = 0;
      yAcc = 0;
      joyIndex = -1;
    }
  }

  // Check whether a point is on the button
  private boolean isOnButton(float xPos, float yPos) {
    return (xPos - buttonX) * (xPos - buttonX) + (yPos - buttonY) * (yPos - buttonY) <= buttonSize * buttonSize;
  }

  // Check whether a point is within the joystick's boundaries
  private boolean isInBounds(float xPos, float yPos) {
    return xPos >= x && xPos <= x + w && yPos >= y && yPos <= y + h;
  }

  private float clamp(float val, float min, float max) {
    return val < min ? min : (val > max ? max : val);
  }
}
