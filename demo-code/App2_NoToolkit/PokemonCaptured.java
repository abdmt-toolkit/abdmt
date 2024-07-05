package com.judykong.catchpokemon_notoolkit;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class PokemonCaptured extends AppCompatActivity {
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