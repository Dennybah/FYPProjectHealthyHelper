package com.example.healthyHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthyHelper.recipes.CrunchyBlueberryYogurtRecipe;
import com.example.healthyHelper.recipes.HummusRecipe;
import com.example.healthyHelper.recipes.PoachedPearsRecipe;
import com.example.healthyHelper.recipes.SalmonRiceBowlRecipe;

import java.util.Objects;

public class RecipesMenu extends AppCompatActivity {

    private boolean breakfastFilterPressed = true;
    private boolean lunchFilterPressed = true;
    private boolean dinnerFilterPressed = true;
    private boolean dessertFilterPressed = true;

    private Button breakfastFilter;
    private Button lunchFilter;
    private Button dinnerFilter;
    private Button dessertFilter;
    private Button resetFilters;

    private Button hummusRecipeButton;
    private TextView hummusRecipeDescription;

    private Button crunchyBlueBerryYogurtButton;
    private TextView crunchyBlueBerryYogurtDescription;

    private Button salmonAndRiceBowlButton;
    private TextView salmonAndRiceBowlDescription;

    private Button poachedPearsButton;
    private TextView poachedPearsDescription;

    //Timing for double button press
    private static final int TIME_INTERVAL = 2000;
    private long confirmButtonPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_recipes);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Recipes");

        breakfastFilter = findViewById(R.id.BreakfastFilterButton);
        lunchFilter = findViewById(R.id.lunchFilterButton);
        dinnerFilter = findViewById(R.id.DinnerFilterButton);
        dessertFilter = findViewById(R.id.DessertFilterButton);
        resetFilters = findViewById(R.id.resetFilterButton);

        crunchyBlueBerryYogurtButton = findViewById(R.id.crunchyBlueberryYogurtRecipeButton);
        crunchyBlueBerryYogurtDescription = findViewById(R.id.crunchyBlueberryYogurtRecipeDescription);

        hummusRecipeButton = findViewById(R.id.hummusRecipeButton);
        hummusRecipeDescription = findViewById(R.id.hummusRecipeDescription);

        salmonAndRiceBowlButton = findViewById(R.id.salmonRiceBowlRecipeButton);
        salmonAndRiceBowlDescription = findViewById(R.id.salmonRiceBowlRecipeDescription);

        poachedPearsButton = findViewById(R.id.poachedPearsRecipeButton);
        poachedPearsDescription = findViewById(R.id.poachedPearsRecipeDescription);

        recipesButtons();
        filterButtons();
    }

    //Below controls what happens if a button for a recipe is pressed, if the filters are not used then all buttons can be pressed
    //if a filter is pressed then only a specific recipe can be accessed unless the reset filters button is pressed
    private void recipesButtons() {
        crunchyBlueBerryYogurtButton.setOnClickListener(view -> {
            if (breakfastFilterPressed) {
                Intent intent = new Intent(RecipesMenu.this, CrunchyBlueberryYogurtRecipe.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "This is a breakfast recipe, please press the breakfast filter to access this recipe", Toast.LENGTH_SHORT).show();
            }
        });

        hummusRecipeButton.setOnClickListener(view -> {
            if (lunchFilterPressed) {
                Intent intent = new Intent(RecipesMenu.this, HummusRecipe.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "This is a lunch recipe, please press the lunch filter to access this recipe", Toast.LENGTH_SHORT).show();
            }
        });

        salmonAndRiceBowlButton.setOnClickListener(view -> {
            if (dinnerFilterPressed) {
                Intent intent = new Intent(RecipesMenu.this, SalmonRiceBowlRecipe.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "This is a dinner recipe, please press the dinner filter to access this recipe", Toast.LENGTH_SHORT).show();
            }
        });

        poachedPearsButton.setOnClickListener(view -> {
            if (dessertFilterPressed) {
                Intent intent = new Intent(RecipesMenu.this, PoachedPearsRecipe.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "This is a dessert recipe, please press the dessert filter to access this recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Below controls what happens if a filter button is pressed, if filter is applied the only a specific recipe can be accessed
    //Any applied can be reset via the use of the reset filters button
    private void filterButtons()
    {
        breakfastFilter.setOnClickListener(view -> {
            blueberryYogurtVisible(true);
            hummusVisible(false);
            salmonRiceBowlVisible(false);
            poachedPearsVisible(false);

            breakfastFilterPressed = true;
            lunchFilterPressed = false;
            dinnerFilterPressed = false;
            dessertFilterPressed = false;
        });

        lunchFilter.setOnClickListener(view -> {
            blueberryYogurtVisible(false);
            hummusVisible(true);
            salmonRiceBowlVisible(false);
            poachedPearsVisible(false);

            breakfastFilterPressed = false;
            lunchFilterPressed = true;
            dinnerFilterPressed = false;
            dessertFilterPressed = false;
        });

        dinnerFilter.setOnClickListener(view -> {
            blueberryYogurtVisible(false);
            hummusVisible(false);
            salmonRiceBowlVisible(true);
            poachedPearsVisible(false);

            breakfastFilterPressed = false;
            lunchFilterPressed = false;
            dinnerFilterPressed = true;
            dessertFilterPressed = false;
        });

        dessertFilter.setOnClickListener(view -> {
            blueberryYogurtVisible(false);
            hummusVisible(false);
            salmonRiceBowlVisible(false);
            poachedPearsVisible(true);

            breakfastFilterPressed = false;
            lunchFilterPressed = false;
            dinnerFilterPressed = false;
            dessertFilterPressed = true;
        });

        resetFilters.setOnClickListener(view -> {
            if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                blueberryYogurtVisible(true);
                hummusVisible(true);
                salmonRiceBowlVisible(true);
                poachedPearsVisible(true);

                breakfastFilterPressed = true;
                lunchFilterPressed = true;
                dinnerFilterPressed = true;
                dessertFilterPressed = true;

                Toast.makeText(getBaseContext(), "Filters have now been reset", Toast.LENGTH_SHORT).show();
                confirmButtonPress = 0;
                return;
            } else {
                Toast.makeText(getBaseContext(), "Are you sure?", Toast.LENGTH_SHORT).show();
            }
            confirmButtonPress = System.currentTimeMillis();
        });
    }

    //Controls level of visibility of recipes, if visible is true then the recipe is visible, if false then it is not visible
    private void blueberryYogurtVisible(Boolean visible) {
        if(visible) {
            crunchyBlueBerryYogurtDescription.setText(getResources().getString(R.string.recipesCrunchyBlueberryYogurtDescription));
            crunchyBlueBerryYogurtDescription.setTextSize(22);
            crunchyBlueBerryYogurtDescription.setGravity(Gravity.START);
            crunchyBlueBerryYogurtButton.setAlpha(0.7f);
        }
        else {
            crunchyBlueBerryYogurtDescription.setText(getResources().getString(R.string.recipesMenuFilterBreakfast));
            crunchyBlueBerryYogurtDescription.setTextSize(28);
            crunchyBlueBerryYogurtDescription.setGravity(Gravity.CENTER);
            crunchyBlueBerryYogurtButton.setAlpha(0.3f);
        }
    }

    private void hummusVisible(Boolean visible) {
        if(visible) {
            hummusRecipeDescription.setText(getResources().getString(R.string.recipesHummusDescription));
            hummusRecipeDescription.setTextSize(22);
            hummusRecipeDescription.setGravity(Gravity.START);
            hummusRecipeButton.setAlpha(0.7f);
        }
        else {
            hummusRecipeDescription.setText(getResources().getString(R.string.recipesMenuFilterLunch));
            hummusRecipeDescription.setTextSize(28);
            hummusRecipeDescription.setGravity(Gravity.CENTER);
            hummusRecipeButton.setAlpha(0.3f);
        }
    }

    private void salmonRiceBowlVisible(Boolean visible) {
        if(visible) {
            salmonAndRiceBowlDescription.setText(getResources().getString(R.string.recipesSalmonRiceBowlDescription));
            salmonAndRiceBowlDescription.setTextSize(22);
            salmonAndRiceBowlDescription.setGravity(Gravity.START);
            salmonAndRiceBowlButton.setAlpha(0.7f);
        }
        else {
            salmonAndRiceBowlDescription.setText(getResources().getString(R.string.recipesMenuFilterDinner));
            salmonAndRiceBowlDescription.setTextSize(28);
            salmonAndRiceBowlDescription.setGravity(Gravity.CENTER);
            salmonAndRiceBowlButton.setAlpha(0.3f);
        }
    }

    private void poachedPearsVisible(Boolean visible) {
        if(visible) {
            poachedPearsDescription.setText(getResources().getString(R.string.recipesPoachedPearsDescription));
            poachedPearsDescription.setTextSize(22);
            poachedPearsDescription.setGravity(Gravity.START);
            poachedPearsButton.setAlpha(0.7f);
        }
        else {
            poachedPearsDescription.setText(getResources().getString(R.string.recipesMenuFilterDessert));
            poachedPearsDescription.setTextSize(28);
            poachedPearsDescription.setGravity(Gravity.CENTER);
            poachedPearsButton.setAlpha(0.3f);
        }
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        //Exclude current page
        MenuItem hidden = menu.findItem(R.id.recipeMenuItem);
        hidden.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.articlesMenuItem):
                intent = new Intent(RecipesMenu.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(RecipesMenu.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(RecipesMenu.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(RecipesMenu.this, MainMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(RecipesMenu.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}