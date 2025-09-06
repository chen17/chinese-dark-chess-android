package com.example.chinesedarkchess;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BoardView extends View {
    private static final String TAG = "BoardView";
    private Board board;
    private Paint paint;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Map<String, Bitmap> pieceImages;
    private Bitmap coveredImage;

    private int boardLeft, boardTop, boardWidth, boardHeight;
    private int cellWidth, cellHeight;
    private String gameMode;
    private String pieceSet = "chess"; // Default piece set
    private Handler aiHandler = new Handler();

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        board = new Board();
        paint = new Paint();
        pieceImages = new HashMap<>();

        if (context instanceof GameActivity) {
            gameMode = ((GameActivity) context).getIntent().getStringExtra("GAME_MODE");
        }
    }

    public void setPieceSet(String pieceSet) {
        this.pieceSet = pieceSet;
        loadPieceImages(); // Reload images when the set is changed
    }

    private void loadPieceImages() {
        pieceImages.clear();
        AssetManager assetManager = getContext().getAssets();
        try {
            String[] imageFiles = assetManager.list("images");
            if (imageFiles == null) {
                Log.e(TAG, "Asset folder 'images' not found or is empty.");
                return;
            }
            for (String imageFile : imageFiles) {
                if (imageFile.startsWith(pieceSet) && imageFile.endsWith(".png")) {
                    String pieceName = imageFile.substring(0, imageFile.length() - 4);
                    InputStream inputStream = assetManager.open("images/" + imageFile);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null) {
                        Log.e(TAG, "Failed to decode bitmap: " + imageFile);
                        continue;
                    }
                    pieceImages.put(pieceName, bitmap);
                }
            }
            // Load the covered piece separately
            InputStream inputStream = assetManager.open("images/covered_chess_piece.png");
            coveredImage = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            Log.e(TAG, "Error loading piece images", e);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // ... (sizing logic remains the same)
        super.onSizeChanged(w, h, oldw, oldh);
        int viewWidth = w;
        int viewHeight = h;
        int usableWidth = (int) (viewWidth * 0.95);
        int usableHeight = (int) (viewHeight * 0.95);
        if (usableWidth / 2 > usableHeight) {
            boardHeight = usableHeight;
            boardWidth = boardHeight * 2;
        } else {
            boardWidth = usableWidth;
            boardHeight = boardWidth / 2;
        }
        boardLeft = (viewWidth - boardWidth) / 2;
        boardTop = (viewHeight - boardHeight) / 2;
        cellWidth = boardWidth / 8;
        cellHeight = boardHeight / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // ... (drawing logic remains the same)
        super.onDraw(canvas);
        canvas.drawColor(Color.DKGRAY);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 8; col++) {
                int left = boardLeft + col * cellWidth;
                int top = boardTop + row * cellHeight;
                int right = left + cellWidth;
                int bottom = top + cellHeight;
                paint.setColor(Color.LTGRAY);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(left, top, right, bottom, paint);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(left, top, right, bottom, paint);
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    Bitmap pieceBitmap = piece.isFaceUp() ? pieceImages.get(getPieceImageName(piece)) : coveredImage;
                    if (pieceBitmap != null) {
                        canvas.drawBitmap(pieceBitmap, null, new Rect(left + 5, top + 5, right - 5, bottom - 5), null);
                    }
                }
            }
        }
        if (selectedRow != -1) {
            int left = boardLeft + selectedCol * cellWidth;
            int top = boardTop + selectedRow * cellHeight;
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(left, top, left + cellWidth, top + cellHeight, paint);
            paint.setStrokeWidth(1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (board.getGameStatus() != Board.GameStatus.ONGOING) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if ("PVA".equals(gameMode) && board.getCurrentPlayer() != null && board.getCurrentPlayer() == board.getAiColor()) {
                return true;
            }

            int x = (int) event.getX();
            int y = (int) event.getY();
            int col = (x - boardLeft) / cellWidth;
            int row = (y - boardTop) / cellHeight;

            if (row < 0 || row >= 4 || col < 0 || col >= 8) {
                return super.onTouchEvent(event);
            }

            handleTouch(row, col);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTouch(int row, int col) {
        // ... (touch handling logic remains the same)
        Piece clickedPiece = board.getPiece(row, col);
        if (selectedRow == -1) {
            if (clickedPiece != null) {
                if (!clickedPiece.isFaceUp()) {
                    board.flipPiece(row, col);
                } else if (clickedPiece.getColor() == board.getCurrentPlayer()) {
                    selectedRow = row;
                    selectedCol = col;
                } else {
                    Toast.makeText(getContext(), "It's " + board.getCurrentPlayer() + "'s turn!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (selectedRow == row && selectedCol == col) {
                selectedRow = -1;
                selectedCol = -1;
            } else if (board.movePiece(selectedRow, selectedCol, row, col)) {
                selectedRow = -1;
                selectedCol = -1;
            } else {
                if (clickedPiece != null && clickedPiece.getColor() == board.getCurrentPlayer()) {
                    selectedRow = row;
                    selectedCol = col;
                } else {
                    Toast.makeText(getContext(), "Invalid move!", Toast.LENGTH_SHORT).show();
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }
        invalidate();
        checkGameState();
    }

    public void startAiFirstTurn() {
        board.forceAiFirstMove();
        invalidate();
        checkGameState();
    }

    private void checkGameState() {
        Board.GameStatus status = board.getGameStatus();
        if (status != Board.GameStatus.ONGOING) {
            if (getContext() instanceof GameActivity) {
                ((GameActivity) getContext()).showWinImage(status);
            }
        } else {
            checkAiTurn();
        }
    }

    private void checkAiTurn() {
        if ("PVA".equals(gameMode) && board.getCurrentPlayer() == board.getAiColor()) {
            aiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    board.makeAiMove();
                    invalidate();
                    checkGameState();
                }
            }, 1500);
        }
    }

    private String getPieceImageName(Piece piece) {
        String color = piece.getColor() == Piece.Color.RED ? "red" : "black";
        String rank = "";
        switch (piece.getRank()) {
            case GENERAL: rank = "general"; break;
            case ADVISOR: rank = "advisor"; break;
            case ELEPHANT: rank = "elephant"; break;
            case CHARIOT: rank = "chariot"; break;
            case HORSE: rank = "horse"; break;
            case SOLDIER: rank = "soldier"; break; // Corrected typo
            case CANNON: rank = "cannon"; break;
        }
        return pieceSet + "_" + color + "_" + rank;
    }
}