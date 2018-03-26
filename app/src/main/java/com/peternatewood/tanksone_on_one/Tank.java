package com.peternatewood.tanksone_on_one;

public class Tank {
  private final int FIRE_DELAY = 30;
  private final int POINTS_COUNT = 9;
  static int SIZE = 18;
  private final int MAX_LIFE = 120;
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

  private boolean fire;
  private float x, y, r, startX, startY, startR;
  private int minX, maxX, minY, maxY, fireTimer, life;
  private float xAcc, yAcc;
  private float[] points  = { 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0 };
  private float[] outline = { 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0 };
  private float[] turret  = { 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0 };
  private Shell[] shells = { null, null, null };

  public Tank(float _startX, float _startY, float radians, int tileSize) {
    fire = false;
    x = _startX;
    y = _startY;
    startX = _startX;
    startY = _startY;
    r = radians;
    startR = radians;
    fireTimer = 0;
    life = MAX_LIFE;

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

    shells[0] = new Shell(this, 0);
    shells[1] = new Shell(this, 0);
    shells[2] = new Shell(this, 0);
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

  public Shell[] _shells() {
    return shells;
  }

  public int _life() {
    return life;
  }

  public boolean isAlive() {
    return life == MAX_LIFE;
  }

  public void destroy() {
    life--;
  }

  public void reset() {
    fire = false;
    x = startX;
    y = startY;
    r = startR;
    fireTimer = 0;
    life = MAX_LIFE;

    xAcc = 0.f;
    yAcc = 0.f;
    // Render points
    updatePoints();

    shells[0] = new Shell(this, 0);
    shells[1] = new Shell(this, 0);
    shells[2] = new Shell(this, 0);
  }

  public void toggleFire(boolean isButtonDown) {
    fire = isButtonDown;
  }

  public void setAcc(float joyX, float joyY) {
    xAcc = joyX;
    yAcc = joyY;
  }

  public void update(int[] level, Tank other) {
    if (isAlive()) {
      if (xAcc != 0) {
        r += xAcc * ROT_INCREMENT;
      }
      if (yAcc != 0) {
        float velocity = yAcc > 0 ? 2 : -1;
        // If player is in water, divide velocity by two

        float xVel = velocity * (float) Math.cos(r);
        float yVel = velocity * (float) Math.sin(r);
        x += xVel;
        y += yVel;

        // Detect collisions
        int collision = GameView.getCollision(x, y, xVel, yVel, SIZE, level);

        if (collision % 2 == 1) {
          x -= xVel;
        }
        if (collision >= 2) {
          y -= yVel;
        }
      }
      updatePoints();

      // Handle firing shells and fire delay
      if (fire && fireTimer == 0) {
        // Find an empty slot for a shell
        for (int i = 0; i < 3; i++) {
          if (shells[i]._life() == 0) {
            shells[i].fire(this);

            fireTimer = FIRE_DELAY;
            break;
          }
        }
      }
      if (fireTimer > 0) {
        fireTimer--;
      }
    }
    else if (life > 0) {
      life--;
    }

    // Update shells
    float sX, sY;
    for (int i = 0; i < 3; i++) {
      if (shells[i]._life() > 0) {
        shells[i].update(level);
        sX = shells[i]._x() - other._x();
        sY = shells[i]._y() - other._y();

        if (sX * sX + sY * sY < SIZE * SIZE) {
          // Shell hit other tank
          other.destroy();
          shells[i].destroy();
        }
        else {
          sX = shells[i]._x() - x;
          sY = shells[i]._y() - y;
          if (sX * sX + sY * sY < SIZE * SIZE) {
            // Shell hit tank
            destroy();
            shells[i].destroy();
          }
        }
      }
    }
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
