package com.peternatewood.tanksone_on_one;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.util.Log;

public class GameView extends SurfaceView implements Runnable {
  volatile boolean isRunning = false;

  static double TAU = 2 * Math.PI;

  private final int w, h;
  private final float scale;
  private final int[] PALETTE = {
    Color.parseColor("#995511"),
    Color.parseColor("#779911"),
    Color.parseColor("#115588"),
    Color.parseColor("#772222"),
    Color.parseColor("#333333"),
    Color.parseColor("#CC4422")
  };
  private final int[] TANK_PALETTE = {
    Color.parseColor("#00AAFF"), // Blue fill
    Color.parseColor("#005599"), // Blue outline
    Color.parseColor("#00DD22"), // Green fill
    Color.parseColor("#007700"), // Green outline
    Color.parseColor("#EE0044"), // Red fill
    Color.parseColor("#880022"), // Red outline
    Color.parseColor("#DDEE22"), // Yellow fill
    Color.parseColor("#778800")  // Yellow outline
  };
  static final int TILE_SIZE = 32;
  private final float SCALED_TILE, TILES_Y;
  private final int LEVEL_SIZE = 500;
  private final int[][] LEVELS = {
    {
      0,0,0,1,1,1,1,1,3,0,0,0,2,2,0,1,1,1,1,1,3,3,3,3,3,
      0,0,0,1,1,1,1,1,3,0,0,0,2,2,0,1,3,3,3,3,3,3,3,3,3,
      0,0,0,1,1,1,1,1,3,0,0,0,2,2,0,1,0,1,1,1,3,3,3,3,3,
      0,0,0,0,1,1,1,1,1,0,0,0,2,2,0,1,3,0,1,1,3,3,3,3,3,
      0,0,0,0,0,1,1,1,1,0,0,0,2,2,0,1,3,0,1,1,3,3,3,3,3,
      0,0,0,0,0,0,0,1,1,0,0,0,2,2,0,1,3,1,1,1,3,3,3,3,3,
      0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,1,3,1,1,1,3,3,3,3,3,
      1,1,0,0,0,0,0,0,0,0,0,2,2,2,0,1,3,1,1,1,3,3,3,3,3,
      1,1,1,1,1,1,0,0,0,0,2,2,2,0,0,1,1,1,1,1,3,3,3,3,3,
      3,3,3,3,3,3,0,0,0,2,2,2,0,0,1,0,1,1,1,1,3,3,3,3,3,
      0,0,0,0,0,0,0,0,0,2,2,0,0,1,1,1,0,1,1,1,3,3,3,3,3,
      0,0,0,0,0,0,0,0,0,2,2,0,1,1,3,3,3,1,1,1,3,3,3,3,3,
      0,0,0,0,0,0,0,0,0,2,2,0,1,1,0,1,1,1,1,1,3,3,3,3,3,
      0,0,0,0,0,3,0,0,2,2,0,0,1,1,0,1,1,1,1,1,3,3,3,3,3,
      0,0,0,0,1,3,0,0,2,2,0,1,1,1,3,0,1,1,1,1,3,3,3,3,3,
      0,0,0,1,1,3,0,0,2,2,0,1,1,1,3,0,1,1,1,1,3,3,3,3,3,
      0,0,0,1,1,3,3,2,2,3,3,3,1,1,3,1,1,1,0,1,3,3,3,3,3,
      0,0,1,1,0,0,0,2,2,0,0,0,1,1,3,1,1,1,0,1,3,3,3,3,3,
      0,0,1,1,0,0,0,2,2,0,0,0,1,1,1,1,1,1,1,1,3,3,3,3,3,
      0,0,1,1,0,0,0,2,2,0,0,1,1,1,1,1,1,1,1,1,3,3,3,3,3
    }
  };
  // Thread for our game loop
  private Thread gameThread = null;
  private int[] scores = { 0, 0 };
  private String[] stringScores = { "0", "0" };
  private Tank tank1, tank2;
  private Joystick joy1, joy2;
  private Paint paint;
  private Point center;
  private Canvas canvas;
  private SurfaceHolder surfaceHolder;

  public GameView(Context context, int width, int height) {
    super(context);

    w = width;
    h = height;

    scale = (float) width / (25 * TILE_SIZE);
    SCALED_TILE = scale * TILE_SIZE;
    center = new Point(w / 2, h / 2);
    TILES_Y = center.y - (float) (SCALED_TILE * 10);

    tank1 = new Tank( 64.f,  64.f, 0.f, TILE_SIZE);
    tank2 = new Tank(576.f, 576.f, (float) Math.PI, TILE_SIZE);

    joy1 = new Joystick(w, h, false);
    joy2 = new Joystick(w, h, true);

    surfaceHolder = getHolder();
    paint = new Paint();
    paint.setStrokeWidth(2);
    paint.setTextSize(32);
    paint.setTypeface(Typeface.MONOSPACE);
  }

  @Override
  public void run() {
    while (isRunning) {
      update(); // Update game state
      draw();
      limitFramerate(); // Limit frame rate to ~60fps
    }
  }

  private void update() {
    boolean tank1Alive = tank1.isAlive();
    boolean tank2Alive = tank2.isAlive();

    tank1.update(LEVELS[0], tank2);
    tank2.update(LEVELS[0], tank1);

    if (tank1Alive && !tank1.isAlive() && !tank1._destroyedSelf()) {
      scores[0]++;
      stringScores[0] = String.valueOf(scores[0]);
    }
    if (tank2Alive && !tank2.isAlive() && !tank2._destroyedSelf()) {
      scores[1]++;
      stringScores[1] = String.valueOf(scores[1]);
    }

    if (tank1._life() == 0 || tank2._life() == 0) {
      // Reset
      tank1.reset();
      tank2.reset();
    }
  }

  private void draw() {
    // TODO: scale all drawing to fit the screen
    if (surfaceHolder.getSurface().isValid()) {
      // Lock canvas (what does this do?)
      canvas = surfaceHolder.lockCanvas();
      // Fill canvas with black
      canvas.drawColor(Color.BLACK);
      // Draw level
      int paletteIndex = -1;
      float tX, tY;
      for (int i = 0; i < LEVEL_SIZE; i++) {
        if (i % 25 == 20) {
          i += 4;
        }
        else {
          tX = scale * TILE_SIZE * (i % 25);
          tY = TILES_Y + scale * TILE_SIZE * (i / 25);
          // Only update paint color if different from last tile
          if (paletteIndex != LEVELS[0][i]) {
            paletteIndex = LEVELS[0][i];
            paint.setColor(PALETTE[paletteIndex]);
          }
          canvas.drawRect(tX, tY, tX + scale * TILE_SIZE, tY + scale * TILE_SIZE, paint);
        }
      }

      drawTank(tank1, 0);
      drawTank(tank2, 2);

      paint.setColor(Color.WHITE);
      Shell[] shells1 = tank1._shells();
      Shell[] shells2 = tank2._shells();
      float sX, sY;
      float size = 2 * scale;

      for (int i = 0; i < 3; i++) {
        if (shells1[i]._life() > 0) {
          sX = scale * shells1[i]._x();
          sY = TILES_Y + scale * shells1[i]._y();
          canvas.drawRect(sX - size, sY - size, sX + size, sY + size, paint);
        }
        if (shells2[i]._life() > 0) {
          sX = scale * shells2[i]._x();
          sY = TILES_Y + scale * shells2[i]._y();
          canvas.drawRect(sX - size, sY - size, sX + size, sY + size, paint);
        }
      }

      drawJoystick(joy1);
      drawJoystick(joy2);

      // Draw UI elements
      paint.setColor(Color.WHITE);
      paint.setStyle(Style.FILL);
      canvas.drawText("Player 1:", scale * TILE_SIZE * 20.5f, TILES_Y + scale * TILE_SIZE, paint);
      canvas.drawText(stringScores[0], scale * TILE_SIZE * 21, TILES_Y + scale * 2 * TILE_SIZE, paint);
      canvas.drawText("Player 2:", scale * TILE_SIZE * 20.5f, TILES_Y + scale * 5 * TILE_SIZE, paint);
      canvas.drawText(stringScores[1], scale * TILE_SIZE * 21, TILES_Y + scale * 6 * TILE_SIZE, paint);

      // Unlock canvas?
      surfaceHolder.unlockCanvasAndPost(canvas);
    }
  }

  private void drawTank(Tank tank, int colorIndex) {
    if (tank.isAlive()) {
      // Draw tank fill
      Path tankPath = new Path();
      float[] points = tank.points();

      int pointsCount = tank.pointsCount();
      tankPath.moveTo(scale * points[0], TILES_Y + scale * points[1]);
      for (int i = 1; i < pointsCount; i++) {
        tankPath.lineTo(scale * points[2 * i], TILES_Y + scale * points[2 * i + 1]);
      }
      paint.setColor(TANK_PALETTE[colorIndex]);
      paint.setStyle(Style.FILL);
      canvas.drawPath(tankPath, paint);

      // Draw tank outline
      float x1, y1, x2, y2;
      float[] outline = tank.outline();
      paint.setColor(TANK_PALETTE[colorIndex + 1]);
      for (int i = 0; i < pointsCount; i++) {
        x1 = scale * outline[2 * i];
        y1 = TILES_Y + scale * outline[2 * i + 1];
        x2 = scale * outline[(2 * i + 2) % (2 * pointsCount)];
        y2 = TILES_Y + scale * outline[(2 * i + 3) % (2 * pointsCount)];
        canvas.drawLine(x1, y1, x2, y2, paint);
      }

      // Draw tank turret
      float[] turret = tank.turret();
      for (int i = 0; i < pointsCount; i++) {
        x1 = scale * turret[2 * i];
        y1 = TILES_Y + scale * turret[2 * i + 1];
        x2 = scale * turret[(2 * i + 2) % (2 * pointsCount)];
        y2 = TILES_Y + scale * turret[(2 * i + 3) % (2 * pointsCount)];
        canvas.drawLine(x1, y1, x2, y2, paint);
      }
    }
    else if (tank._life() > 90) {
      paint.setColor(Color.WHITE);

      // Draw an empty circle
      drawCircle(tank._x(), tank._y(), 120 - tank._life());
    }
  }

  private void drawCircle(float x, float y, float radius) {
    double sides = TAU * radius / 2;

    double angleDelta = TAU / sides;
    double angle = angleDelta;

    float startX, startY;
    float endX = x + radius;
    float endY = y + 0.0f;

    for (int i = 0; i < sides; i++) {
      startX = endX;
      startY = endY;
      endX = x + (float) (radius * Math.cos(angle));
      endY = y + (float) (radius * Math.sin(angle));
      angle += angleDelta;

      // SDL_RenderDrawLine(r, startX, startY, endX, endY);
      canvas.drawLine(scale * startX, TILES_Y + scale * startY, scale * endX, TILES_Y + scale * endY, paint);
    }
  }

  private void drawJoystick(Joystick joy) {
    // Draw controls background
    paint.setColor(Color.GRAY);
    canvas.drawRect(joy._rect(), paint);

    float buttonSize = joy._buttonSize();
    float joyX = joy._joyX();
    float joyY = joy._joyY();
    float xLast = joyX + joy._xVel();
    float yLast = joyY + joy._yVel();
    float buttonX = joy._buttonX();
    float buttonY = joy._buttonY();

    // Circles behind joystick and button
    paint.setColor(Color.BLACK);
    canvas.drawCircle(joyX, joyY, buttonSize + 8.f, paint);
    canvas.drawCircle(buttonX, buttonY, buttonSize + 8.f, paint);
    // Draw joystick
    paint.setColor(Color.YELLOW);
    canvas.drawCircle(xLast, yLast, buttonSize, paint);
    // Draw Button
    paint.setColor(Color.RED);
    if (joy._button()) {
      buttonSize -= 8.f;
    }
    canvas.drawCircle(buttonX, buttonY, buttonSize, paint);
  }

  private void limitFramerate() {
    final long FRAME_DELAY = 1000 / 60;

    try {
      gameThread.sleep(FRAME_DELAY); // Sleep for 1000 / 60 to get 60 fps
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int index = event.getActionIndex();
    float x = event.getX(index);
    float y = event.getY(index);
    boolean buttonState[] = { joy1._button(), joy2._button() };

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        // First touch event
        joy1.handleActionDown(x, y, index);
        if (joy1._button() != buttonState[0]) {
          tank1.toggleFire(joy1._button());
        }

        joy2.handleActionDown(x, y, index);
        if (joy2._button() != buttonState[1]) {
          tank2.toggleFire(joy2._button());
        }
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        // Subsequent touch events
        joy1.handleActionDown(x, y, index);
        if (joy1._button() != buttonState[0]) {
          tank1.toggleFire(joy1._button());
        }

        joy2.handleActionDown(x, y, index);
        if (joy2._button() != buttonState[1]) {
          tank2.toggleFire(joy2._button());
        }
        break;
      case MotionEvent.ACTION_MOVE:
        // User swipes
        index = joy1.joyIndex();
        if (index != -1) {
          joy1.handleActionMove(event.getX(index), event.getY(index));
          tank1.setAcc(joy1._xAcc(), joy1._yAcc());
        }

        index = joy2.joyIndex();
        if (index != -1) {
          joy2.handleActionMove(event.getX(index), event.getY(index));
          tank2.setAcc(joy2._xAcc(), joy2._yAcc());
        }
        break;
      case MotionEvent.ACTION_POINTER_UP:
        // Every touch release except the last one
        joy1.handleActionUp(x, y, index);
        tank1.setAcc(joy1._xAcc(), joy1._yAcc());
        if (joy1._button() != buttonState[0]) {
          tank1.toggleFire(joy1._button());
        }

        joy2.handleActionUp(x, y, index);
        tank2.setAcc(joy1._xAcc(), joy1._yAcc());
        if (joy2._button() != buttonState[1]) {
          tank2.toggleFire(joy2._button());
        }
        break;
      case MotionEvent.ACTION_UP:
        // Last touch to be released
        joy1.handleActionUp(x, y, index);
        tank1.setAcc(joy1._xAcc(), joy1._yAcc());
        if (joy1._button() != buttonState[0]) {
          tank1.toggleFire(joy1._button());
        }

        joy2.handleActionUp(x, y, index);
        tank2.setAcc(joy1._xAcc(), joy1._yAcc());
        if (joy2._button() != buttonState[1]) {
          tank2.toggleFire(joy2._button());
        }
        break;
    }
    return true;
  }

  public void pause() {
    isRunning = false;
    try {
      // Stop the thread
      gameThread.join();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void resume() {
    isRunning = true;
    gameThread = new Thread(this);
    gameThread.start();
  }

  static int getCollision(float x, float y, float xVel, float yVel, int size, int[] level) {
    // First check edges of the space
    int collision = 0;
    if (x < 0 || x > TILE_SIZE * 25) {
      collision++;
    }
    if (y < 0 || y > TILE_SIZE * 20) {
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

        if ((distX * distX) + (distY * distY) < size * size) {
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

    return collision;
  }

  static int clamp(int value, int min, int max) {
    return value < min ? min : (value > max ? max : value);
  }
  static float clamp(float value, int min, int max) {
    return value < min ? min : (value > max ? max : value);
  }
  static float clamp(float value, float min, float max) {
    return value < min ? min : (value > max ? max : value);
  }
}
