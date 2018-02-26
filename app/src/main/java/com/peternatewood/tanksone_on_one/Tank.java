package com.peternatewood.tanksone_on_one;

public class Tank {
  private final int POINTS_COUNT = 9;
  private final int SIZE = 18;
  private final int X_MIN, X_MAX, Y_MIN, Y_MAX, TILE_SIZE;
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

  private float x, y, r;
  private int minX, maxX, minY, maxY;
  private float xAcc, yAcc;
  private float[] points  = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
  private float[] outline = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
  private float[] turret  = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };

  public Tank(float startX, float startY, float radians, int tileSize) {
    x = startX;
    y = startY;
    r = radians;

    xAcc = 0.f;
    yAcc = 0.f;
    TILE_SIZE = tileSize;
    // Set bounds
    X_MIN = SIZE;
    X_MAX = 25 * TILE_SIZE - SIZE;
    Y_MIN = SIZE;
    Y_MAX = 20 * TILE_SIZE - SIZE;
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

  public void update(int[] level, Tank other) {
    if (xAcc != 0) {
      r += xAcc * ROT_INCREMENT;
    }
    if (yAcc != 0) {
      float velocity = yAcc > 0 ? 2 : -1;
      // If player is in water, divide velocity by two

      double xVel = velocity * Math.cos(r);
      double yVel = velocity * Math.sin(r);
      x += xVel;
      y += yVel;

      // Detect collisions
      // First check edges of the space
      int collision = 0;
      if (x < X_MIN || x > X_MAX) {
        collision++;
      }
      if (y < Y_MIN || y > Y_MAX) {
        collision += 2;
      }

      boolean towardX, towardY;
      int left, right, top, bottom;
      float closestX, closestY, distX, distY;
      // Check all tiles in the level
      int tileCount = level.length;
      for (int i = 0; i < tileCount; i++) {
        // We break the loop if we've definitely found a corner collision
        if (collision == 3) {
          break;
        }
        // Only check wall tiles
        if (level[i] == 3 || level[i] == 4) {
          left = TILE_SIZE * (i % 25);
          right = left + TILE_SIZE;
          top = TILE_SIZE * ((int) i / 25);
          bottom = top + TILE_SIZE;

          closestX = clamp(x, left, right);
          closestY = clamp(y, top, bottom);

          distX = x - closestX;
          distY = y - closestY;

          if ((distX * distX) + (distY * distY) < SIZE * SIZE) {
            if (Math.abs(distX) > Math.abs(distY)) {
              // Determine whether player is moving towards the colliding tile
              towardX = (xVel > 0 && right > x) || (xVel < 0 && left < x);
              if (towardX && collision % 2 == 0) {
                // We haven't found an x-axis collision yet
                collision++;
              }
            }
            else {
              towardY = (yVel > 0 && bottom > y) || (yVel < 0 && top < y);
              if (towardY && collision < 2) {
                // We haven't found a y-axis collision yet
                collision += 2;
              }
            }
          }
        }
      }

      if (collision % 2 == 1) {
        x -= xVel;
      }
      if (collision >= 2) {
        y -= yVel;
      }
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

  private int clamp(int value, int min, int max) {
    return value < min ? min : (value > max ? max : value);
  }
  private float clamp(float value, int min, int max) {
    return value < min ? min : (value > max ? max : value);
  }
}
