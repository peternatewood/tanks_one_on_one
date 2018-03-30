package com.peternatewood.tanksone_on_one;

import android.graphics.Rect;
import android.util.Log;

public class Joystick {
  private final int JOY_DIRECTION_MOD;
  private final float DEADZONE = (float) 0.25;
  private final float JOY_MAX = 32.f;
  private final float buttonSize = 64.f;

  private boolean button;
  private int x, y, w, h, joyX, joyY;
  private Rect rect;
  private int buttonX, buttonY;
  private int buttonIndex, joyIndex; // MotionEvent indexes
  private float xAcc, yAcc, xTouch, yTouch;

  private float[] arrowUpPoints    = { 0,0, 0,0, 0,0 };
  private float[] arrowDownPoints  = { 0,0, 0,0, 0,0 };
  private float[] arrowLeftPoints  = { 0,0, 0,0, 0,0 };
  private float[] arrowRightPoints = { 0,0, 0,0, 0,0 };

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

    arrowUpPoints[0]    = joyX - 16; arrowUpPoints[1]    = joyY - 80;
    arrowUpPoints[2]    = joyX;      arrowUpPoints[3]    = joyY - 96;
    arrowUpPoints[4]    = joyX + 16; arrowUpPoints[5]    = joyY - 80;

    arrowDownPoints[0]  = joyX - 16; arrowDownPoints[1]  = joyY + 80;
    arrowDownPoints[2]  = joyX;      arrowDownPoints[3]  = joyY + 96;
    arrowDownPoints[4]  = joyX + 16; arrowDownPoints[5]  = joyY + 80;

    arrowLeftPoints[0]  = joyX - 80; arrowLeftPoints[1]  = joyY - 16;
    arrowLeftPoints[2]  = joyX - 96; arrowLeftPoints[3]  = joyY;
    arrowLeftPoints[4]  = joyX - 80; arrowLeftPoints[5]  = joyY + 16;

    arrowRightPoints[0] = joyX + 80; arrowRightPoints[1] = joyY - 16;
    arrowRightPoints[2] = joyX + 96; arrowRightPoints[3] = joyY;
    arrowRightPoints[4] = joyX + 80; arrowRightPoints[5] = joyY + 16;
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

  public int joyIndex() {
    return joyIndex;
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

  public float[] arrowUpPoints() {
    return arrowUpPoints;
  }

  public float[] arrowDownPoints() {
    return arrowDownPoints;
  }

  public float[] arrowLeftPoints() {
    return arrowLeftPoints;
  }

  public float[] arrowRightPoints() {
    return arrowRightPoints;
  }

  public float _xAcc() {
    float acc = JOY_DIRECTION_MOD * xAcc / JOY_MAX;
    if (acc > -DEADZONE && acc < DEADZONE) {
      return 0;
    }

    return acc;
  }

  public float _yAcc() {
    float acc = JOY_DIRECTION_MOD * yAcc / -JOY_MAX; // Invert sign so tank moves forward
    if (acc > -DEADZONE && acc < DEADZONE) {
      return 0;
    }

    return acc;
  }

  public void handleActionDown(float xPos, float yPos, int index) {
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

  public void handleActionMove(float xPos, float yPos) {
    // Calculate acceleration in x and y directions based on position relative to initial touch position
    xAcc = GameView.clamp(xPos - xTouch, -JOY_MAX, JOY_MAX);
    yAcc = GameView.clamp(yPos - yTouch, -JOY_MAX, JOY_MAX);
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
}
