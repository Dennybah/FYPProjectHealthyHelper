package com.example.healthyHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthyHelper.articles.EatingHealthyWithDiabetesActivity;
import com.example.healthyHelper.articles.KeepingActiveWithDiabetes;
import com.example.healthyHelper.articles.WhatIsPrediabetes;

import java.util.Objects;

public class ArticlesMenu extends AppCompatActivity {

    private Button whatIsPrediabetes;
    private Button eatingHealthyWithDiabetes;
    private Button keepingActiveWithDiabetes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_articles);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Articles");

        whatIsPrediabetes=findViewById(R.id.whatIsPrediabetesButton);
        eatingHealthyWithDiabetes=findViewById(R.id.eatingHealthyWithDiabetesButton);
        keepingActiveWithDiabetes=findViewById(R.id.keepingActiveWithDiabetesButton);

        articleMenuButtons();
    }

    //Controls where the user is brought upon clicking an article
    private void articleMenuButtons() {
        whatIsPrediabetes.setOnClickListener(view -> {
            Intent intent = new Intent(ArticlesMenu.this, WhatIsPrediabetes.class);
            startActivity(intent);
        });

        eatingHealthyWithDiabetes.setOnClickListener(view -> {
            Intent intent = new Intent(ArticlesMenu.this, EatingHealthyWithDiabetesActivity.class);
            startActivity(intent);
        });

        keepingActiveWithDiabetes.setOnClickListener(view -> {
            Intent intent = new Intent(ArticlesMenu.this, KeepingActiveWithDiabetes.class);
            startActivity(intent);
        });
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        //Exclude current page
        MenuItem hidden = menu.findItem(R.id.articlesMenuItem);
        hidden.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(ArticlesMenu.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(ArticlesMenu.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(ArticlesMenu.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(ArticlesMenu.this, MainMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(ArticlesMenu.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}