package com.example.chinesedarkchess;

public class Piece {
    public enum Rank {
        GENERAL, ADVISOR, ELEPHANT, CHARIOT, HORSE, SOLDIER, CANNON
    }

    public enum Color {
        RED, BLACK
    }

    private final Rank rank;
    private final Color color;
    private boolean faceUp;

    public Piece(Rank rank, Color color) {
        this.rank = rank;
        this.color = color;
        this.faceUp = false;
    }

    public Rank getRank() {
        return rank;
    }

    public Color getColor() {
        return color;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void flip() {
        faceUp = true;
    }
}
