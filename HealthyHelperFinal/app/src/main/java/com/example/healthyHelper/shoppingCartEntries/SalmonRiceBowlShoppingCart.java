package com.example.healthyHelper.shoppingCartEntries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyHelper.ArticlesMenu;
import com.example.healthyHelper.BloodGlucoseRecorderMenu;
import com.example.healthyHelper.HowToUseFeatures;
import com.example.healthyHelper.MainMenu;
import com.example.healthyHelper.R;
import com.example.healthyHelper.RecipesMenu;
import com.example.healthyHelper.ShoppingCartMenu;

import java.util.ArrayList;
import java.util.Objects;

public class SalmonRiceBowlShoppingCart extends AppCompatActivity {

    //Timing for double button press
    private static final int TIME_INTERVAL = 2000;
    private long confirmButtonPress;

    private ArrayList<Button> listOfIngredients;
    private Button resetShoppingCartButton;

    //Shared preferences file
    public static final String salmonRiceBowlIngredientsPressed= "SalmonRiceBowlIngredientsPressed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_salmon_rice_bowl);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Salmon Rice Bowl");

        Button ingredient1 = findViewById(R.id.ingredient1);
        Button ingredient2 = findViewById(R.id.ingredient2);
        Button ingredient3 = findViewById(R.id.ingredient3);
        Button ingredient4 = findViewById(R.id.ingredient4);
        Button ingredient5 = findViewById(R.id.ingredient5);
        Button ingredient6 = findViewById(R.id.ingredient6);
        Button ingredient7 = findViewById(R.id.ingredient7);
        Button ingredient8 = findViewById(R.id.ingredient8);
        Button ingredient9 = findViewById(R.id.ingredient9);
        Button ingredient10 = findViewById(R.id.ingredient10);
        Button ingredient11 = findViewById(R.id.ingredient11);
        Button ingredient12 = findViewById(R.id.ingredient12);
        Button ingredient13 = findViewById(R.id.ingredient13);
        Button ingredient14 = findViewById(R.id.ingredient14);
        Button ingredient15 = findViewById(R.id.ingredient15);

        //Adds the ingredients from recipe to array list to apply the click and long click functionality within ingredientsPressed()
        listOfIngredients= new ArrayList<>();
        listOfIngredients.add(ingredient1);
        listOfIngredients.add(ingredient2);
        listOfIngredients.add(ingredient3);
        listOfIngredients.add(ingredient4);
        listOfIngredients.add(ingredient5);
        listOfIngredients.add(ingredient6);
        listOfIngredients.add(ingredient7);
        listOfIngredients.add(ingredient8);
        listOfIngredients.add(ingredient9);
        listOfIngredients.add(ingredient10);
        listOfIngredients.add(ingredient11);
        listOfIngredients.add(ingredient12);
        listOfIngredients.add(ingredient13);
        listOfIngredients.add(ingredient14);
        listOfIngredients.add(ingredient15);

        resetShoppingCartButton = findViewById(R.id.resetShoppingCartButton);

        //For if the screen is too small to display all text
        TextView howToUse = findViewById(R.id.howToUse);
        howToUse.setMovementMethod(new ScrollingMovementMethod());

        ingredientsPressed();
        loadPreviousShoppingCart();
    }

    //When an ingredient is pressed, fade it from view or when an removed ingredients is long pressed then add it back by changing the alpha
    //value appropriately then record this change into shared preferences
    private void ingredientsPressed(){
        SharedPreferences ingredientsStoredState = getSharedPreferences(salmonRiceBowlIngredientsPressed, 0);
        SharedPreferences.Editor editor = ingredientsStoredState.edit();

        //Sets the press and on long press functionality of each button in list
        for(int i = 0; i < listOfIngredients.size(); i++) {
            Button x = listOfIngredients.get(i);
            x.setOnClickListener(view -> {
                if (!ingredientsStoredState.getBoolean(x.getText().toString(), false)) {
                    if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                        Toast.makeText(getBaseContext(), "Ingredient has been removed\nHold the button to re-add them", Toast.LENGTH_SHORT).show();
                        x.setAlpha(0.3f);
                        editor.putBoolean(x.getText().toString(), true).apply();
                        confirmButtonPress = 0;
                        return;
                    } else {
                        Toast.makeText(getBaseContext(), "Are you sure?",    Toast.LENGTH_SHORT).show();
                    }
                    confirmButtonPress = System.currentTimeMillis();
                }
            });
            x.setOnLongClickListener(view -> {
                if (ingredientsStoredState.getBoolean(x.getText().toString(), false)) {
                    Toast.makeText(getBaseContext(), "Ingredient has been re-added", Toast.LENGTH_SHORT).show();
                    x.setAlpha(1);
                    editor.putBoolean(x.getText().toString(), false).apply();
                }
                return true;
            });
        }

        //When the reset button is double pressed iterate through list of buttons and make them visible by setting their alpha value
        //to 1 then record this change in shared preferences
        resetShoppingCartButton.setOnClickListener(view -> {
            if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                Toast.makeText(getBaseContext(), "Shopping cart labels have been reset", Toast.LENGTH_SHORT).show();
                for(int i = 0; i < listOfIngredients.size(); i++) {
                    Button x = listOfIngredients.get(i);
                    x.setAlpha(1);
                    editor.putBoolean(x.getText().toString(),false).apply();
                    confirmButtonPress = 0;
                }
                return;
            } else {
                Toast.makeText(getBaseContext(), "Are you sure?", Toast.LENGTH_SHORT).show();
            }
            confirmButtonPress = System.currentTimeMillis();
        });
    }

    //Used to load the previous shopping cart by reading from memory, i.e the last time the application was opened
    private void loadPreviousShoppingCart(){
        SharedPreferences ingredientsStoredState = getSharedPreferences(salmonRiceBowlIngredientsPressed, 0);

        for (int i = 0; i < listOfIngredients.size(); i++) {
            Button x = listOfIngredients.get(i);
            if (ingredientsStoredState.getBoolean(x.getText().toString(), false)) {
                x.setAlpha(0.3f);
            }
            else {
                x.setAlpha(1);
            }
        }
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.articlesMenuItem):
                intent = new Intent(SalmonRiceBowlShoppingCart.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(SalmonRiceBowlShoppingCart.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(SalmonRiceBowlShoppingCart.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(SalmonRiceBowlShoppingCart.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(SalmonRiceBowlShoppingCart.this, MainMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(SalmonRiceBowlShoppingCart.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}