package com.example.chinesedarkchess;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Board {
    private static final String TAG = "Board";
    private final Piece[][] board;
    private Piece.Color currentPlayer;
    private Piece.Color playerColor;
    private Piece.Color aiColor;
    private boolean firstMove;
    private GameStatus gameStatus;

    public enum GameStatus {
        ONGOING,
        RED_WIN,
        BLACK_WIN
    }

    public Board() {
        board = new Piece[4][8];
        initializePieces();
        currentPlayer = null;
        playerColor = null;
        aiColor = null;
        firstMove = true;
        gameStatus = GameStatus.ONGOING;
    }

    private void initializePieces() {
        // ... (piece initialization remains the same)
        List<Piece> pieces = new ArrayList<>();
        // Red pieces
        pieces.add(new Piece(Piece.Rank.GENERAL, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.ADVISOR, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.ADVISOR, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.ELEPHANT, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.ELEPHANT, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.CHARIOT, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.CHARIOT, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.HORSE, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.HORSE, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.CANNON, Piece.Color.RED));
        pieces.add(new Piece(Piece.Rank.CANNON, Piece.Color.RED));
        // Black pieces
        pieces.add(new Piece(Piece.Rank.GENERAL, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.ADVISOR, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.ADVISOR, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.ELEPHANT, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.ELEPHANT, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.CHARIOT, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.CHARIOT, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.HORSE, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.HORSE, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.SOLDIER, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.CANNON, Piece.Color.BLACK));
        pieces.add(new Piece(Piece.Rank.CANNON, Piece.Color.BLACK));

        Collections.shuffle(pieces);

        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = pieces.get(index++);
            }
        }
    }

    public Piece getPiece(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 8) return null;
        return board[row][col];
    }

    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = null;
        switchPlayer();
        checkWinCondition();
        return true;
    }

    public void flipPiece(int row, int col) {
        Piece piece = getPiece(row, col);
        if (piece != null && !piece.isFaceUp()) {
            piece.flip();
            if (firstMove) {
                currentPlayer = piece.getColor();
                playerColor = currentPlayer;
                aiColor = (playerColor == Piece.Color.RED) ? Piece.Color.BLACK : Piece.Color.RED;
                firstMove = false;
            }
            switchPlayer();
            checkWinCondition();
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == Piece.Color.RED) ? Piece.Color.BLACK : Piece.Color.RED;
    }

    public Piece.Color getCurrentPlayer() {
        return currentPlayer;
    }

    public Piece.Color getAiColor() {
        return aiColor;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    private void checkWinCondition() {
        if (hasNoMoves(Piece.Color.RED)) {
            gameStatus = GameStatus.BLACK_WIN;
        } else if (hasNoMoves(Piece.Color.BLACK)) {
            gameStatus = GameStatus.RED_WIN;
        }
    }

    private boolean hasNoMoves(Piece.Color color) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = getPiece(r, c);
                if (p != null && !p.isFaceUp()) {
                    return false;
                }
            }
        }
        boolean hasPieces = false;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = getPiece(r, c);
                if (p != null && p.getColor() == color) {
                    hasPieces = true;
                    break;
                }
            }
            if (hasPieces) break;
        }
        if (!hasPieces) return true;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = getPiece(r, c);
                if (p != null && p.isFaceUp() && p.getColor() == color) {
                    for (int tr = 0; tr < 4; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            Piece.Color originalPlayer = currentPlayer;
                            currentPlayer = color;
                            boolean canMove = isValidMove(r, c, tr, tc);
                            currentPlayer = originalPlayer;
                            if (canMove) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean canCapture(Piece attacker, Piece defender) {
        if (attacker.getRank() == Piece.Rank.CANNON) return true;
        if (attacker.getRank() == Piece.Rank.GENERAL) return defender.getRank() != Piece.Rank.SOLDIER;
        if (attacker.getRank() == Piece.Rank.SOLDIER) return defender.getRank() == Piece.Rank.GENERAL || defender.getRank() == Piece.Rank.SOLDIER;
        return attacker.getRank().ordinal() <= defender.getRank().ordinal();
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= 4 || toCol < 0 || toCol >= 8) return false;
        Piece movingPiece = getPiece(fromRow, fromCol);
        if (movingPiece == null || !movingPiece.isFaceUp() || movingPiece.getColor() != currentPlayer) return false;
        Piece targetPiece = getPiece(toRow, toCol);

        // Case 1: Moving to an empty square
        if (targetPiece == null) {
            if (movingPiece.getRank() == Piece.Rank.CANNON) {
                // Cannon move logic: must be a straight line with no pieces in between.
                int rowDiff = toRow - fromRow;
                int colDiff = toCol - fromCol;
                if (rowDiff != 0 && colDiff != 0) return false; // Must be a straight line

                int piecesJumped = 0;
                if (rowDiff != 0) { // Vertical move
                    int step = rowDiff > 0 ? 1 : -1;
                    for (int r = fromRow + step; r != toRow; r += step) {
                        if (getPiece(r, fromCol) != null) piecesJumped++;
                    }
                } else { // Horizontal move
                    int step = colDiff > 0 ? 1 : -1;
                    for (int c = fromCol + step; c != toCol; c += step) {
                        if (getPiece(fromRow, c) != null) piecesJumped++;
                    }
                }
                return piecesJumped == 0; // Path must be clear
            } else {
                // Standard move logic for other pieces
                return Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol) == 1;
            }
        }

        // Case 2: Capturing an enemy piece
        if (targetPiece.getColor() == movingPiece.getColor() || !targetPiece.isFaceUp()) return false;
        if (movingPiece.getRank() == Piece.Rank.CANNON) {
            int rowDiff = toRow - fromRow;
            int colDiff = toCol - fromCol;
            if (rowDiff != 0 && colDiff != 0) return false;
            int piecesJumped = 0;
            if (rowDiff != 0) {
                int step = rowDiff > 0 ? 1 : -1;
                for (int r = fromRow + step; r != toRow; r += step) {
                    if (getPiece(r, fromCol) != null) piecesJumped++;
                }
            } else {
                int step = colDiff > 0 ? 1 : -1;
                for (int c = fromCol + step; c != toCol; c += step) {
                    if (getPiece(fromRow, c) != null) piecesJumped++;
                }
            }
            return piecesJumped == 1;
        } else {
            boolean isAdjacent = Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol) == 1;
            if (!isAdjacent) return false;
            return canCapture(movingPiece, targetPiece);
        }
    }

    public void forceAiFirstMove() {
        if (firstMove) {
            List<int[]> possibleFlips = new ArrayList<>();
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 8; c++) {
                    possibleFlips.add(new int[]{r, c});
                }
            }
            int[] flip = possibleFlips.get(new Random().nextInt(possibleFlips.size()));
            Piece flippedPiece = getPiece(flip[0], flip[1]);
            flippedPiece.flip();
            currentPlayer = flippedPiece.getColor();
            aiColor = currentPlayer;
            playerColor = (aiColor == Piece.Color.RED) ? Piece.Color.BLACK : Piece.Color.RED;
            firstMove = false;
            switchPlayer();
        }
    }

    private int getPieceValue(Piece piece) {
        return 7 - piece.getRank().ordinal();
    }

    public void makeAiMove() {
        if (currentPlayer == null) return;

        // ... (AI logic remains the same)
        List<int[]> possibleCaptures = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                if (getPiece(r, c) != null && getPiece(r, c).isFaceUp() && getPiece(r, c).getColor() == currentPlayer) {
                    for (int tr = 0; tr < 4; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            if (getPiece(tr, tc) != null && isValidMove(r, c, tr, tc)) {
                                possibleCaptures.add(new int[]{r, c, tr, tc});
                            }
                        }
                    }
                }
            }
        }
        if (!possibleCaptures.isEmpty()) {
            int[] bestMove = null;
            int bestValue = -100;
            for (int[] move : possibleCaptures) {
                Piece attacker = getPiece(move[0], move[1]);
                Piece defender = getPiece(move[2], move[3]);
                int value;
                if (attacker.getRank() == Piece.Rank.SOLDIER && defender.getRank() == Piece.Rank.GENERAL) {
                    value = 100;
                } else {
                    value = getPieceValue(defender) - getPieceValue(attacker);
                }
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            }
            movePiece(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
            return;
        }
        List<int[]> possibleFlips = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                if (getPiece(r, c) != null && !getPiece(r, c).isFaceUp()) {
                    possibleFlips.add(new int[]{r, c});
                }
            }
        }
        if (!possibleFlips.isEmpty()) {
            int[] flip = possibleFlips.get(new Random().nextInt(possibleFlips.size()));
            flipPiece(flip[0], flip[1]);
            return;
        }
        List<int[]> possibleMoves = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                if (getPiece(r, c) != null && getPiece(r, c).isFaceUp() && getPiece(r, c).getColor() == currentPlayer) {
                    for (int tr = 0; tr < 4; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            if (getPiece(tr, tc) == null && isValidMove(r, c, tr, tc)) {
                                possibleMoves.add(new int[]{r, c, tr, tc});
                            }
                        }
                    }
                }
            }
        }
        if (!possibleMoves.isEmpty()) {
            int[] move = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            movePiece(move[0], move[1], move[2], move[3]);
        }
    }
}