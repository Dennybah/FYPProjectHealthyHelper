package com.example.healthyHelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class MainMenu extends AppCompatActivity {

    private boolean doubleBackPressed = false;

    private Button articleButton;
    private Button bloodRecorderButton;
    private Button shoppingCartButton;
    private Button recipesButton;
    private Button howToUseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Main Menu");

        TextView mainText = findViewById(R.id.tvMain);
        mainText.setMovementMethod(new ScrollingMovementMethod());

        articleButton = findViewById(R.id.articlesButton);
        bloodRecorderButton = findViewById(R.id.bloodRecorderButton);
        recipesButton = findViewById(R.id.recipeButton);
        shoppingCartButton = findViewById(R.id.shopCartButton);
        howToUseButton = findViewById(R.id.howToUseButton);

        mainMenuButtons();
    }

    //Below controls the functionality of the main menu buttons and the activity the user is brought to when a button is pressed
    private void mainMenuButtons(){
        articleButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenu.this, ArticlesMenu.class);
            startActivity(intent);
        });

        bloodRecorderButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenu.this, BloodGlucoseRecorderMenu.class);
            startActivity(intent);
        });

        recipesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenu.this, RecipesMenu.class);
            startActivity(intent);
        });

        shoppingCartButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenu.this, ShoppingCartMenu.class);
            startActivity(intent);
        });

        howToUseButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenu.this, HowToUseFeatures.class);
            startActivity(intent);
        });
    }

    //Controls the functionality requiring the user to press the back button twice to exit the app
    @Override
    public void onBackPressed() {
        if(doubleBackPressed){
            finishAffinity();
        }
        else {
            doubleBackPressed=true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(() -> doubleBackPressed=false, 2000);
        }
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        //Exclude current page
        MenuItem hidden = menu.findItem(R.id.homeMenuItem);
        hidden.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.articlesMenuItem):
                intent = new Intent(MainMenu.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(MainMenu.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(MainMenu.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(MainMenu.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(MainMenu.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}