package com.peternatewood.tanksone_on_one;

public class Shell {
  private final int SPEED = 8;
  private final int MAX_LIFE = 120;

  private int life;
  private float x, y, xVel, yVel;

  public Shell (Tank tank, int startLife) {
    life = startLife >= 0 ? startLife : MAX_LIFE;

    double r = tank._r();
    xVel = (float) Math.cos(r);
    yVel = (float) Math.sin(r);

    x = tank._x();
    y = tank._y();
  }

  public float _x() {
    return x;
  }
  public float _y() {
    return y;
  }

  public float _xVel() {
    return xVel;
  }
  public float _yVel() {
    return yVel;
  }

  public int _life() {
    return life;
  }

  public void destroy() {
    life = 0;
  }

  public void update(int[] level) {
    life--;
    for (int i = 0; i < SPEED; i++) {
      int collision = GameView.getCollision(x, y, xVel, yVel, 4, level);

      if (collision % 2 == 1) {
        x -= xVel;
        xVel *= -1;
      }
      if (collision >= 2) {
        y -= yVel;
        yVel *= -1;
      }

      x += xVel;
      y += yVel;
    }
  }

  public void fire(Tank tank) {
    life = MAX_LIFE;

    double r = tank._r();
    xVel = (float) Math.cos(r);
    yVel = (float) Math.sin(r);

    x = tank._x() + Tank.SIZE * xVel;
    y = tank._y() + Tank.SIZE * yVel;
  }
}
