package net.uofitorn.asteroidrunner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class AsteroidRunner {

    private final static String TAG = "AsteroidRunner";

    private int playerX = 0;
    private int playerY = 0;
    private int gridSquareLength = 0;
    private int gridSquareHeight = 0;

    private static final int boardSize = 12;

    public static final double DIFFICULTY_EASY = 0.20;
    public static final double DIFFICULTY_MEDIUM = 0.30;
    public static final double DIFFICULTY_HARD = 0.40;

    private int[][] gameBoard = new int[boardSize][boardSize];
    private double difficultyLevel = DIFFICULTY_EASY;

    private Bitmap backgroundImage;
    private Bitmap playerShipSprite;
    private Bitmap spaceMineSprite;

    public AsteroidRunner(Context context) {
        Resources res = context.getResources();
        backgroundImage = BitmapFactory.decodeResource(res, R.drawable.starbackground);
        playerShipSprite = BitmapFactory.decodeResource(res, R.drawable.lander_plain);
        spaceMineSprite = BitmapFactory.decodeResource(res, R.drawable.spacemine);
        /*do {
            shuffleMap();
        } while (!testIfSolvable());  */
        shuffleMap();
        testIfSolvable();
    }

    public void drawGrid(int boardWidth, int boardHeight, Canvas canvas) {
        Paint gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setARGB(255, 255, 255, 255);
        gridSquareLength = boardWidth / 12;
        gridSquareHeight = gridSquareLength;
        for (int i = 0; i <= boardSize; i++) {
            canvas.drawLine(0, i * gridSquareHeight, boardWidth, i * gridSquareHeight, gridPaint);
        }
        for (int i = 0; i < boardSize; i++) {
            canvas.drawLine(i * gridSquareLength, 0, i * gridSquareLength, boardHeight, gridPaint);
        }
    }

    public void drawMines(Canvas canvas) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (gameBoard[i][j] == 1) {
                    spaceMineSprite = Bitmap.createScaledBitmap(spaceMineSprite, gridSquareLength, gridSquareHeight, true);
                    canvas.drawBitmap(spaceMineSprite, i * gridSquareLength, j * gridSquareHeight, null);
                }
            }
        }
    }

    public void drawBackground(Canvas canvas, int canvasWidth, int canvasHeight) {
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, canvasWidth, canvasHeight, true);
        canvas.drawBitmap(backgroundImage, 0, 0, null);
    }

    public void drawPlayerShip(Canvas canvas) {
        playerShipSprite = Bitmap.createScaledBitmap(playerShipSprite, gridSquareLength, gridSquareHeight, true);
        canvas.drawBitmap(playerShipSprite, playerX * gridSquareLength, playerY * gridSquareHeight + 5, null);
    }

    public void handleMoveUp() {
        if (playerY != 0) {
            playerY--;
        }
    }

    public void handleMoveDown() {
        if (playerY != boardSize - 1) {
            playerY++;
        }
    }

    public void handleMoveRight() {
        if (playerX != boardSize - 1) {
            playerX++;
        }
    }

    public void handleMoveLeft() {
        if (playerX != 0) {
            playerX--;
        }
    }

    private void shuffleMap() {
        double difficultyThreshold = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                difficultyThreshold = difficultyLevel;
                double rand = Math.random();
                if (rand < difficultyThreshold) {
                    gameBoard[i][j] = 1;
                } else {
                    gameBoard[i][j] = 0;
                }
            }
        }
    }

    private void testIfSolvable() {
        if (findPath(0, 0))
            Log.i(TAG, "Path returned true");
        else
            Log.i(TAG, "Path returned false");
    }

    private boolean findPath(int x, int y) {
        Log.i(TAG, "Testing x: " + x + " and y: " + y);
        if ((x > 11) || (y > 11) || (x < 0) || (y < 0))
            return false;
        if ((x == 11) && (y == 11))
            return true;
        if (gameBoard[x][y] == 1)
            return false;
        if (findPath(x, y + 1))
            return true;
        if (findPath(x + 1, y))
            return true;
        if (findPath(x, y - 1))
            return true;
        if (findPath(x - 1, y))
            return true;
        return false;
    }

}
