package com.example.healthyHelper;

import static com.example.healthyHelper.recipes.CrunchyBlueberryYogurtRecipe.blueberryYogurtRecipeState;
import static com.example.healthyHelper.recipes.PoachedPearsRecipe.poachedPearsRecipeState;
import static com.example.healthyHelper.recipes.HummusRecipe.hummusRecipeState;
import static com.example.healthyHelper.recipes.SalmonRiceBowlRecipe.salmonRiceBowlRecipeState;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthyHelper.shoppingCartEntries.CrunchyBlueberryYogurtShoppingCart;
import com.example.healthyHelper.shoppingCartEntries.HummusShoppingCart;
import com.example.healthyHelper.shoppingCartEntries.PoachedPearsShoppingCart;
import com.example.healthyHelper.shoppingCartEntries.SalmonRiceBowlShoppingCart;

import java.util.Objects;

public class ShoppingCartMenu extends AppCompatActivity {

    private TextView space;
    private Button newButton;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_shopping_cart);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Shopping Cart");

        linearLayout = findViewById(R.id.menuShoppingCartLinearLayout);

        TextView mainText = findViewById(R.id.tvMain);
        mainText.setMovementMethod(new ScrollingMovementMethod());

        //Controls whether a recipe is added to the shopping cart or not when the button is pressed within its respective recipe
        addBlueberryYogurt();
        addHummus();
        addSalmonRiceBowl();
        addPoachedPears();
    }

    private void addBlueberryYogurt() {
        //Reads boolean from shared preferences, ie if the button should be shown when user re-opens app or not
        SharedPreferences blueBerryYogurtStoredState = getSharedPreferences(blueberryYogurtRecipeState,0);

        if(blueBerryYogurtStoredState.getBoolean("added", false)){
            newButton = new Button(this);
            newButton.setText("\nCrunchy Blueberry Yogurt Ingredients\n");
            newButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            newButton.setBackgroundColor(getResources().getColor(R.color.appYellow));
            newButton.setTextSize(24);
            newButton.setOnClickListener(view -> {
                Intent intent = new Intent(ShoppingCartMenu.this, CrunchyBlueberryYogurtShoppingCart.class);
                startActivity(intent);
            });
            linearLayout.addView(newButton);

            //Add white space between buttons
            space= new TextView(this);
            linearLayout.addView(space);
        }
    }

    private void addHummus() {
        //Reads boolean from shared preferences, ie if the button should be shown when user re-opens app or not
        SharedPreferences hummusStoredState = getSharedPreferences(hummusRecipeState,0);

        if(hummusStoredState.getBoolean("added", false)){
            newButton = new Button(this);
            newButton.setText("\nHummus ingredients\n");
            newButton.setBackgroundColor(getResources().getColor(R.color.appYellow));
            newButton.setTextSize(24);
            newButton.setOnClickListener(view -> {
                Intent intent = new Intent(ShoppingCartMenu.this, HummusShoppingCart.class);
                startActivity(intent);
            });
            linearLayout.addView(newButton);

            //Add white space between buttons
            space= new TextView(this);
            linearLayout.addView(space);
        }
    }

    private void addSalmonRiceBowl() {
        //Reads boolean from shared preferences, ie if the button should be shown when user re-opens app or not
        SharedPreferences salmonRiceBowlStoredState = getSharedPreferences(salmonRiceBowlRecipeState,0);

        if(salmonRiceBowlStoredState.getBoolean("added", false)){
            newButton = new Button(this);
            newButton.setText("\nSalmon Rice Bowl Ingredients\n");
            newButton.setBackgroundColor(getResources().getColor(R.color.appYellow));
            newButton.setTextSize(24);
            newButton.setOnClickListener(view -> {
                Intent intent = new Intent(ShoppingCartMenu.this, SalmonRiceBowlShoppingCart.class);
                startActivity(intent);
            });
            linearLayout.addView(newButton);

            //Add white space between buttons
            space= new TextView(this);
            linearLayout.addView(space);
        }
    }

    private void addPoachedPears() {
        //Reads boolean from shared preferences, ie if the button should be shown when user re-opens app or not
        SharedPreferences poachedPearsStoredState = getSharedPreferences(poachedPearsRecipeState,0);

        if(poachedPearsStoredState.getBoolean("added", false)){
            newButton = new Button(this);
            newButton.setText("\nPoached Pears Ingredients\n");
            newButton.setBackgroundColor(getResources().getColor(R.color.appYellow));
            newButton.setTextSize(24);
            newButton.setOnClickListener(view -> {
                Intent intent = new Intent(ShoppingCartMenu.this, PoachedPearsShoppingCart.class);
                startActivity(intent);
            });
            linearLayout.addView(newButton);

            //Add white space between buttons
            space= new TextView(this);
            linearLayout.addView(space);
        }
    }

    //Restarts activity when back button is pressed to ensure button has been added
    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        //Exclude current page
        MenuItem hidden = menu.findItem(R.id.shoppingCartMenuItem);
        hidden.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.articlesMenuItem):
                intent = new Intent(ShoppingCartMenu.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(ShoppingCartMenu.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(ShoppingCartMenu.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(ShoppingCartMenu.this, MainMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(ShoppingCartMenu.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}