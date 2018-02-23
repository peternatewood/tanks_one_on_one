package com.peternatewood.tanksone_on_one;

public class Tank {
  private final int POINTS_COUNT = 9;
  private final int SIZE = 18;
  private final int X_MIN, X_MAX, Y_MIN, Y_MAX;
  private final double ROT_INCREMENT = Math.PI / 36;
  private final float[] RENDER_POINTS = {
     18,  3,
      9,  3,
      6, 10,
    -10, 10,
    -10,-10,
      6,-10,
      9, -3,
     18, -3,
     18,  3
  };
  private final float[] OUTLINE_POINTS = {
     18,  3,
      9,  3,
      6, 10,
    -10, 10,
    -10,-10,
      6,-10,
      9, -3,
     18, -3,
     18,  3
  };
  private final float[] TURRET_POINTS = {
     18,  3,
      6,  3,
      4,  7,
     -8,  6,
     -8, -6,
      4, -7,
      6, -3,
     18, -3,
     18,  3
  };

  private float x, y, r, xVel, yVel;
  private int minX, maxX, minY, maxY;
  private float xAcc, yAcc;
  private float[] points  = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
  private float[] outline = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
  private float[] turret  = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };

  public Tank(float startX, float startY, float radians, int tileSize) {
    x = startX;
    y = startY;
    r = radians;

    xVel = 0.f;
    yVel = 0.f;
    xAcc = 0.f;
    yAcc = 0.f;
    // Set bounds
    X_MIN = SIZE;
    X_MAX = 25 * tileSize - SIZE;
    Y_MIN = SIZE;
    Y_MAX = 20 * tileSize - SIZE;
    // Render points
    updatePoints();
  }

  public float _x() {
    return x;
  }

  public float _y() {
    return y;
  }

  public double _r() {
    return r;
  }

  public int pointsCount() {
    return POINTS_COUNT;
  }

  public float[] points() {
    return points;
  }

  public float[] outline() {
    return outline;
  }

  public float[] turret() {
    return turret;
  }

  public void setAcc(float joyX, float joyY) {
    xAcc = joyX;
    yAcc = joyY;
  }

  public void update() {
    if (xAcc != 0) {
      r += xAcc * ROT_INCREMENT;
    }
    if (yAcc != 0) {
      float velocity = yAcc > 0 ? 2 : -1;
      // If player is in water, divide velocity by two

      x += velocity * Math.cos(r);
      y += velocity * Math.sin(r);

      if (x < X_MIN) {
        x = X_MIN;
      }
      else if (x > X_MAX) {
        x = X_MAX;
      }
      if (y < Y_MIN) {
        y = Y_MIN;
      }
      else if (y > Y_MAX) {
        y = Y_MAX;
      }

      // Detect collisions
    }
    updatePoints();
  }

  public void updatePoints() {
    double sin = Math.sin(r);
    double cos = Math.cos(r);
    double pX, pY;

    for (int i = 0; i < POINTS_COUNT; i++) {
      pX = RENDER_POINTS[2 * i];
      pY = RENDER_POINTS[2 * i + 1];
      points[2 * i]     = x + (float) (pX * cos + pY * sin);
      points[2 * i + 1] = y + (float) (pX * sin - pY * cos);

      pX = OUTLINE_POINTS[2 * i];
      pY = OUTLINE_POINTS[2 * i + 1];
      outline[2 * i]     = x + (float) (pX * cos + pY * sin);
      outline[2 * i + 1] = y + (float) (pX * sin - pY * cos);

      pX = TURRET_POINTS[2 * i];
      pY = TURRET_POINTS[2 * i + 1];
      turret[2 * i]     = x + (float) (pX * cos + pY * sin);
      turret[2 * i + 1] = y + (float) (pX * sin - pY * cos);
    }
  }
}
