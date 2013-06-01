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

    public static final int GAMESTATE_PLAYING = 0;
    public static final int GAMESTATE_WON_GAME = 1;
    public static final int GAMESTATE_LOST_GAME = 2;

    private int playerX = 0;
    private int playerY = 0;
    private int surroundingMines = 0;
    private int gridSquareLength = 0;
    private int gridSquareHeight = 0;
    private int canvasWidth = 0;
    private int canvasHeight = 0;
    private int gameState;

    public static final double DIFFICULTY_EASY = 0.20;
    public static final double DIFFICULTY_MEDIUM = 0.25;
    public static final double DIFFICULTY_HARD = 0.45;

    private static final int boardSize = 12;
    private int[][] gameBoard = new int[boardSize][boardSize];
    private int[][] playerVisited = new int[boardSize][boardSize];
    private double difficultyLevel = DIFFICULTY_MEDIUM;

    private Bitmap backgroundImage;
    private Bitmap playerShipSprite;
    private Bitmap spaceMineSprite;
    private Bitmap squareBG;
    private Bitmap explosionSprite;
    private Bitmap[] numerals = new Bitmap[8];

    public AsteroidRunner(Context context) {
        Resources res = context.getResources();
        backgroundImage = BitmapFactory.decodeResource(res, R.drawable.starbackground);
        playerShipSprite = BitmapFactory.decodeResource(res, R.drawable.lander_plain);
        spaceMineSprite = BitmapFactory.decodeResource(res, R.drawable.spacemine);
        squareBG = BitmapFactory.decodeResource(res, R.drawable.bgsquare);
        explosionSprite = BitmapFactory.decodeResource(res, R.drawable.explosion);

        numerals[0] = BitmapFactory.decodeResource(res, R.drawable.zero);
        numerals[1] = BitmapFactory.decodeResource(res, R.drawable.one);
        numerals[2] = BitmapFactory.decodeResource(res, R.drawable.two);
        numerals[3] = BitmapFactory.decodeResource(res, R.drawable.three);
        numerals[4] = BitmapFactory.decodeResource(res, R.drawable.four);
        numerals[5] = BitmapFactory.decodeResource(res, R.drawable.five);
        numerals[6] = BitmapFactory.decodeResource(res, R.drawable.six);
        numerals[7] = BitmapFactory.decodeResource(res, R.drawable.seven);

        do {
            shuffleMap();
            Log.i(TAG, "Shuffling game board");
        } while (!isSolvable());

        calcSurroundingMines();

        /*for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                playerVisited[i][j] = 0;
            }
        } */

        gameState = GAMESTATE_PLAYING;
    }

    public void drawGrid(int boardWidth, int boardHeight, Canvas canvas) {
        Paint gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setARGB(255, 255, 255, 255);
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
                    canvas.drawBitmap(spaceMineSprite, i * gridSquareLength, j * gridSquareHeight, null);
                }
            }
        }
    }

    public void drawSquareCover(Canvas canvas) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (playerVisited[i][j] == 0) {
                    if (i == 0 && j == 0)
                        continue;
                    if (i == boardSize - 1 && j == boardSize - 1)
                        continue;
                    canvas.drawBitmap(squareBG, i * gridSquareLength, j * gridSquareHeight, null);
                }
            }
        }
    }

    public void drawBackground(Canvas canvas) {
        canvas.drawBitmap(backgroundImage, 0, 0, null);
    }

    public void drawPlayerShip(Canvas canvas) {
        canvas.drawBitmap(playerShipSprite, playerX * gridSquareLength, playerY * gridSquareHeight + 5, null);
    }

    public void drawMineCount(Canvas canvas) {
        canvas.drawBitmap(numerals[surroundingMines], 50, 50  + (12 * gridSquareHeight), null);
    }

    public void calcSurroundingMines() {
        int numMines = 0;
        Log.i(TAG, "PlayerX: " + playerX + " and playerY: " + playerY);
        if (playerX != 0 && playerY != 0 && (gameBoard[playerX - 1][playerY - 1] == 1)) {
            numMines++;
        }
        if (playerY != 0 && (gameBoard[playerX][playerY - 1] == 1)) {
            numMines++;
        }
        if (playerY != 0 && (playerX != boardSize - 1) && (gameBoard[playerX + 1][playerY - 1] == 1)) {
            numMines++;
        }
        if ((playerX != boardSize - 1) && (gameBoard[playerX + 1][playerY] == 1)) {
            numMines++;
        }
        if ((playerX != boardSize - 1) && (gameBoard[playerX + 1][playerY + 1] == 1)) {
            numMines++;
        }
        if ((playerY != boardSize - 1) && (gameBoard[playerX][playerY + 1] == 1)) {
            numMines++;
        }
        if (playerX != 0 && (playerY != boardSize - 1) && (gameBoard[playerX - 1][playerY + 1] == 1)) {
            numMines++;
        }
        if (playerX != 0 && (gameBoard[playerX - 1][playerY] == 1)) {
            numMines++;
        }
        Log.i(TAG, "Surrounding mine count: " + numMines);
        surroundingMines = numMines;
    }

    public void handleMoveUp() {
        if (playerY != 0) {
            playerY--;
            playerVisited[playerX][playerY] = 1;
        }
    }

    public void handleMoveDown() {
        if (playerY != boardSize - 1) {
            playerY++;
            playerVisited[playerX][playerY] = 1;
        }
    }

    public void handleMoveRight() {
        if (playerX != boardSize - 1) {
            playerX++;
            playerVisited[playerX][playerY] = 1;
        }
    }

    public void handleMoveLeft() {
        if (playerX != 0) {
            playerX--;
            playerVisited[playerX][playerY] = 1;
        }
    }

    public void calculateCollision() {
        if (gameBoard[playerX][playerY] == 1) {
            gameState = GAMESTATE_LOST_GAME;
        }
    }

    public void drawExplosion(Canvas canvas) {
        canvas.drawBitmap(explosionSprite, playerX * gridSquareLength, playerY * gridSquareHeight, null);
    }

    public int getGameState() {
        return gameState;
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
        gameBoard[0][0] = 0;
        gameBoard[boardSize - 1][boardSize - 1] = 0;
    }

    private boolean isSolvable() {
        if (findPath(0, 0)) {
            Log.i(TAG, "Path returned true, board is solvable.");
            return true;
        }
        else {
            Log.i(TAG, "Path returned false, board is not solvable");
            return false;
        }
    }

    private boolean findPath(int x, int y) {
        if ((x > 11) || (y > 11) || (x < 0) || (y < 0))
            return false;
        if ((x == 11) && (y == 11))
            return true;
        if (gameBoard[x][y] == 1)
            return false;
        if (gameBoard[x][y] == 3)
            return false;

        // Mark that we have visited this square
        gameBoard[x][y] = 3;

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

    void initializeBounds(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        gridSquareLength = canvasWidth / boardSize;
        gridSquareHeight = gridSquareLength;
    }

    void initializeImages() {
        squareBG = Bitmap.createScaledBitmap(squareBG, gridSquareLength, gridSquareHeight, true);
        spaceMineSprite = Bitmap.createScaledBitmap(spaceMineSprite, gridSquareLength, gridSquareHeight, true);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, canvasWidth, canvasHeight, true);
        playerShipSprite = Bitmap.createScaledBitmap(playerShipSprite, gridSquareLength, gridSquareHeight, true);
        explosionSprite = Bitmap.createScaledBitmap(explosionSprite, gridSquareLength, gridSquareHeight, true);
    }
}
