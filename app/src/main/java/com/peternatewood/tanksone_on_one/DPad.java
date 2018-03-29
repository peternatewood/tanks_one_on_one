package com.peternatewood.tanksone_on_one;

import android.graphics.Rect;

import android.util.Log;

public class DPad {
  private final float buttonSize = 64.f;

  private boolean button, up, down, left, right;
  private int x, y, w, h, padX, padY;
  private Rect rect, arrowUp, arrowDown, arrowLeft, arrowRight;
  private int buttonX, buttonY;
  private int buttonIndex, upIndex, downIndex, leftIndex, rightIndex; // MotionEvent indexes
  private float xAcc, yAcc, xTouch, yTouch;
  private float[] arrowUpPoints    = { 0,0, 0,0, 0,0, 0,0 };
  private float[] arrowDownPoints  = { 0,0, 0,0, 0,0, 0,0 };
  private float[] arrowLeftPoints  = { 0,0, 0,0, 0,0, 0,0 };
  private float[] arrowRightPoints = { 0,0, 0,0, 0,0, 0,0 };

  public DPad(int width, int height, boolean isOnBottom) {
    x = 0;
    w = width;
    h = 256;
    y = isOnBottom ? height - h : 0;

    rect = new Rect(x, y, x + w, y + h);

    padX = isOnBottom ? h / 2 : w - h / 2;
    padY = y + h / 2;

    upIndex = -1;
    downIndex = -1;
    leftIndex = -1;
    rightIndex = -1;

    buttonX = isOnBottom ? w - h / 2 : h / 2;
    buttonY = y + h / 2;
    buttonIndex = -1;

    button = false;
    up = false;
    down = false;
    left = false;
    right = false;

    int centerX = isOnBottom ? 224 : w - 224;
    int centerY = y + h / 2;
    if (isOnBottom) {
      arrowUp     = new Rect(centerX - 80, centerY - 80, centerX + 80, centerY);
      arrowDown   = new Rect(centerX - 80, centerY + 16, centerX + 80, centerY + 80);
      arrowLeft   = new Rect(centerX - 160, centerY - 80, centerX - 96, centerY + 80);
      arrowRight  = new Rect(centerX + 96, centerY - 80, centerX + 160, centerY + 80);

      arrowUpPoints[0] = centerX - 64; arrowUpPoints[1] = centerY - 24;
      arrowUpPoints[2] = centerX;      arrowUpPoints[3] = centerY - 56;
      arrowUpPoints[4] = centerX + 64; arrowUpPoints[5] = centerY - 24;
      arrowUpPoints[6] = centerX;      arrowUpPoints[7] = centerY - 32;

      arrowDownPoints[0] = centerX - 64; arrowDownPoints[1] = centerY + 32;
      arrowDownPoints[2] = centerX;      arrowDownPoints[3] = centerY + 56;
      arrowDownPoints[4] = centerX + 64; arrowDownPoints[5] = centerY + 32;
      arrowDownPoints[6] = centerX;      arrowDownPoints[7] = centerY + 40;

      arrowLeftPoints[0] = centerX - 112; arrowLeftPoints[1] = centerY - 56;
      arrowLeftPoints[2] = centerX - 144; arrowLeftPoints[3] = centerY;
      arrowLeftPoints[4] = centerX - 112; arrowLeftPoints[5] = centerY + 56;
      arrowLeftPoints[6] = centerX - 128; arrowLeftPoints[7] = centerY;

      arrowRightPoints[0] = centerX + 112; arrowRightPoints[1] = centerY - 56;
      arrowRightPoints[2] = centerX + 144; arrowRightPoints[3] = centerY;
      arrowRightPoints[4] = centerX + 112; arrowRightPoints[5] = centerY + 56;
      arrowRightPoints[6] = centerX + 128; arrowRightPoints[7] = centerY;
    }
    else {
      arrowUp     = new Rect(centerX - 80, centerY, centerX + 80, centerY + 80);
      arrowDown   = new Rect(centerX - 80, centerY - 80, centerX + 80, centerY - 16);
      arrowLeft   = new Rect(centerX + 96, centerY - 80, centerX + 160, centerY + 80);
      arrowRight  = new Rect(centerX - 160, centerY - 80, centerX - 96, centerY + 80);

      arrowUpPoints[0] = centerX - 64; arrowUpPoints[1] = centerY + 24;
      arrowUpPoints[2] = centerX;      arrowUpPoints[3] = centerY + 56;
      arrowUpPoints[4] = centerX + 64; arrowUpPoints[5] = centerY + 24;
      arrowUpPoints[6] = centerX;      arrowUpPoints[7] = centerY + 32;

      arrowDownPoints[0] = centerX - 64; arrowDownPoints[1] = centerY - 32;
      arrowDownPoints[2] = centerX;      arrowDownPoints[3] = centerY - 56;
      arrowDownPoints[4] = centerX + 64; arrowDownPoints[5] = centerY - 32;
      arrowDownPoints[6] = centerX;      arrowDownPoints[7] = centerY - 40;

      arrowLeftPoints[0] = centerX + 112; arrowLeftPoints[1] = centerY - 56;
      arrowLeftPoints[2] = centerX + 144; arrowLeftPoints[3] = centerY;
      arrowLeftPoints[4] = centerX + 112; arrowLeftPoints[5] = centerY + 56;
      arrowLeftPoints[6] = centerX + 128; arrowLeftPoints[7] = centerY;

      arrowRightPoints[0] = centerX - 112; arrowRightPoints[1] = centerY - 56;
      arrowRightPoints[2] = centerX - 144; arrowRightPoints[3] = centerY;
      arrowRightPoints[4] = centerX - 112; arrowRightPoints[5] = centerY + 56;
      arrowRightPoints[6] = centerX - 128; arrowRightPoints[7] = centerY;
    }
  }

  public boolean _button() {
    return button;
  }

  public Rect _rect() {
    return rect;
  }

  public Rect _arrowUp() {
    return arrowUp;
  }

  public Rect _arrowDown() {
    return arrowDown;
  }

  public Rect _arrowLeft() {
    return arrowLeft;
  }

  public Rect _arrowRight() {
    return arrowRight;
  }

  public int _padX() {
    return padX;
  }

  public int _padY() {
    return padY;
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

  public float _xAcc() {
    return xAcc;
  }

  public float _yAcc() {
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

  public void handleActionDown(float xPos, float yPos, int index) {
    // Check all buttons and activate if in button bounds
    if (isOnButton(xPos, yPos)) {
      button = true;
      buttonIndex = index;
    }
    else if (isInBounds(xPos, yPos)) {
      // Update acceleration based on which button is pressed
      if (isInRect(arrowUp, xPos, yPos)) {
        up = true;
        upIndex = index;
      }
      if (isInRect(arrowDown, xPos, yPos)) {
        down = true;
        downIndex = index;
      }
      if (isInRect(arrowLeft, xPos, yPos)) {
        left = true;
        leftIndex = index;
      }
      if (isInRect(arrowRight, xPos, yPos)) {
        right = true;
        rightIndex = index;
      }

      updateAcc();
    }
  }

  public void handleActionMove(float xPos, float yPos, int index) {
    // Check whether touch moves outside the bounds
    if (isInBounds(xPos, yPos)) {
      if (index == buttonIndex && !isOnButton(xPos, yPos)) {
        button = false;
        buttonIndex = -1;
      }
      else {
        if (index == upIndex) {
          if (!isInRect(arrowUp, xPos, yPos)) {
            up = false;
            upIndex = -1;
          }
        }
        else if (index == downIndex) {
          if (!isInRect(arrowDown, xPos, yPos)) {
            down = false;
            downIndex = -1;
          }
        }
        else if (index == leftIndex) {
          if (!isInRect(arrowLeft, xPos, yPos)) {
            left = false;
            leftIndex = -1;
          }
        }
        else if (index == rightIndex) {
          if (!isInRect(arrowRight, xPos, yPos)) {
            right = false;
            rightIndex = -1;
          }
        }
        updateAcc();
      }
    }
    else {
      handleActionUp(xPos, yPos, index);
    }
  }

  public void handleActionUp(float xPos, float yPos, int index) {
    if (index == buttonIndex) {
      button = false;
      buttonIndex = -1;
    }
    else {
      if (index == upIndex) {
        up = false;
        upIndex = -1;
      }
      else if (index == downIndex) {
        down = false;
        downIndex = -1;
      }
      else if (index == leftIndex) {
        left = false;
        leftIndex = -1;
      }
      else if (index == rightIndex) {
        right = false;
        rightIndex = -1;
      }

      updateAcc();
    }
  }

  private void updateAcc() {
    yAcc = 0;
    if (up) {
      yAcc++;
    }
    if (down) {
      yAcc--;
    }
    xAcc = 0;
    if (left) {
      xAcc--;
    }
    if (right) {
      xAcc++;
    }
  }

  // Check whether a point is on the button
  private boolean isOnButton(float xPos, float yPos) {
    return (xPos - buttonX) * (xPos - buttonX) + (yPos - buttonY) * (yPos - buttonY) <= buttonSize * buttonSize;
  }

  // Check whether a point is within a rectangle
  private boolean isInRect(Rect r, float xPos, float yPos) {
    return xPos > r.left && xPos < r.right && yPos > r.top && yPos < r.bottom;
  }

  // Check whether a point is within the padstick's boundaries
  private boolean isInBounds(float xPos, float yPos) {
    return xPos >= x && xPos <= x + w && yPos >= y && yPos <= y + h;
  }
}
