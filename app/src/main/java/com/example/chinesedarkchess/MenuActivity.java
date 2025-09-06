package com.example.chinesedarkchess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private String selectedPieceSet = "chess"; // Default to the original set

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView selectChess = findViewById(R.id.select_chess_pieces);
        ImageView selectGChess = findViewById(R.id.select_gchess_pieces);

        selectChess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPieceSet = "chess";
                Toast.makeText(MenuActivity.this, "Selected standard pieces", Toast.LENGTH_SHORT).show();
            }
        });

        selectGChess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPieceSet = "gchess";
                Toast.makeText(MenuActivity.this, "Selected new pieces", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_player_vs_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("PVP");
            }
        });

        findViewById(R.id.btn_player_vs_ai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("PVA");
            }
        });

        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startGame(String gameMode) {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        intent.putExtra("GAME_MODE", gameMode);
        intent.putExtra("PIECE_SET", selectedPieceSet);
        startActivity(intent);
    }
}