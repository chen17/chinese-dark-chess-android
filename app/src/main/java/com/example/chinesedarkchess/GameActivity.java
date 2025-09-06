package com.example.chinesedarkchess;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private BoardView boardView;
    private ImageView redWinImageView;
    private ImageView blackWinImageView;
    private String pieceSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.boardView);
        redWinImageView = findViewById(R.id.red_win_image);
        blackWinImageView = findViewById(R.id.black_win_image);

        String gameMode = getIntent().getStringExtra("GAME_MODE");
        pieceSet = getIntent().getStringExtra("PIECE_SET");
        boardView.setPieceSet(pieceSet);

        if ("PVA".equals(gameMode)) {
            showChooseTurnDialog();
        }
    }

    private void showChooseTurnDialog() {
        new AlertDialog.Builder(this, R.style.DarkDialog)
                .setTitle("Choose Your Turn")
                .setMessage("Do you want to go first or second?")
                .setPositiveButton("先手(First)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("後手(Second)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boardView.startAiFirstTurn();
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("看運氣(Random)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (new Random().nextBoolean()) {
                            Toast.makeText(GameActivity.this, "You go first! (先手)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GameActivity.this, "AI goes first! (後手)", Toast.LENGTH_SHORT).show();
                            boardView.startAiFirstTurn();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void showWinImage(Board.GameStatus status) {
        if (status == Board.GameStatus.RED_WIN) {
            redWinImageView.setVisibility(View.VISIBLE);
        } else if (status == Board.GameStatus.BLACK_WIN) {
            blackWinImageView.setVisibility(View.VISIBLE);
        }
    }
}