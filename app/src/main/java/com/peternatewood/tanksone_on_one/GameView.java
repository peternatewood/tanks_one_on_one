package com.peternatewood.tanksone_on_one;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.util.Log;

public class GameView extends SurfaceView implements Runnable {
  volatile boolean isRunning = false;

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
  private final int TILE_SIZE = 32;
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

    joy1 = new Joystick(w, h, true);
    joy2 = new Joystick(w, h, false);

    surfaceHolder = getHolder();
    paint = new Paint();
    paint.setStrokeWidth(2);
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
    tank1.update();
    tank2.update();
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

      drawTank(tank1);
      drawTank(tank2);

      drawJoystick(joy1);
      drawJoystick(joy2);

      // Unlock canvas?
      surfaceHolder.unlockCanvasAndPost(canvas);
    }
  }

  private void drawTank(Tank tank) {
    // Draw tank fill
    Path tankPath = new Path();
    float[] points = tank.points();

    int pointsCount = tank.pointsCount();
    tankPath.moveTo(scale * points[0], TILES_Y + scale * points[1]);
    for (int i = 1; i < pointsCount; i++) {
      tankPath.lineTo(scale * points[2 * i], TILES_Y + scale * points[2 * i + 1]);
    }
    paint.setColor(TANK_PALETTE[0]);
    paint.setStyle(Style.FILL);
    canvas.drawPath(tankPath, paint);

    // Draw tank outline
    float x1, y1, x2, y2;
    float[] outline = tank.outline();
    paint.setColor(TANK_PALETTE[1]);
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

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        // First touch event
        joy1.handleActionDown(x, y, index);
        joy2.handleActionDown(x, y, index);
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        // Subsequent touch events
        joy1.handleActionDown(x, y, index);
        joy2.handleActionDown(x, y, index);
        break;
      case MotionEvent.ACTION_MOVE:
        // User swipes
        joy1.handleActionMove(x, y, index);
        tank1.setAcc(joy1._xAcc(), joy1._yAcc());
        joy2.handleActionMove(x, y, index);
        tank2.setAcc(joy2._xAcc(), joy2._yAcc());
        break;
      case MotionEvent.ACTION_POINTER_UP:
        // Every touch release except the last one
        joy1.handleActionUp(x, y, index);
        tank1.setAcc(joy1._xAcc(), joy1._yAcc());
        joy2.handleActionUp(x, y, index);
        tank2.setAcc(joy1._xAcc(), joy1._yAcc());
        break;
      case MotionEvent.ACTION_UP:
        // Last touch to be released
        joy1.handleActionUp(x, y, index);
        tank1.setAcc(joy1._xAcc(), joy1._yAcc());
        joy2.handleActionUp(x, y, index);
        tank2.setAcc(joy1._xAcc(), joy1._yAcc());
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
}
