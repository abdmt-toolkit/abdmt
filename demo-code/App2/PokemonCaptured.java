package com.judykong.catchpokemon;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.judykong.abdmt.*;
public class PokemonCaptured extends ABDMTActivity {
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_captured);
        getSupportActionBar().hide();
        backButton = findViewById(R.id.btnGoBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PokemonCaptured.this, MainActivity.class));
            }
        });
    }
}