package com.example.healthyHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class HowToUseFeatures extends AppCompatActivity {

    private TextView selectInstructions;
    private TextView articleInstructions;
    private TextView recipesInstructions;
    private TextView shoppingCartInstructions;
    private TextView bloodRecorderInstructions;
    private ArrayList<TextView> listOfInstructions;

    private Button articlesHelpButton;
    private Button recipesHelpButton;
    private Button shoppingCartHelpButton;
    private Button bloodGlucoseRecorderHelpButton;
    private Button resetZoom;

    //Timing for double button press
    private static final int TIME_INTERVAL = 2000;
    private long confirmButtonPress;

    //Used for calculating level of zoom
    private int baseDistance;
    private float ration = 1.0f;
    private float baseRation;
    private final static float step = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_how_to_use);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("How to Use");

        selectInstructions = findViewById(R.id.selectInstructions);
        articleInstructions = findViewById(R.id.articlesInstructions);
        recipesInstructions = findViewById(R.id.recipesInstructions);
        shoppingCartInstructions = findViewById(R.id.shoppingCartInstructions);
        bloodRecorderInstructions = findViewById(R.id.bloodRecorderInstructions);

        //all instruction articles are put into a list to have their zoom and scrolling functionality applied in scrollZoomInstructions()
        listOfInstructions = new ArrayList<>();
        listOfInstructions.add(selectInstructions);
        listOfInstructions.add(articleInstructions);
        listOfInstructions.add(recipesInstructions);
        listOfInstructions.add(shoppingCartInstructions);
        listOfInstructions.add(bloodRecorderInstructions);

        resetZoom = findViewById(R.id.articlesResetZoomButton);

        articlesHelpButton = findViewById(R.id.articlesHelpButton);
        recipesHelpButton = findViewById(R.id.recipesHelpButton);
        shoppingCartHelpButton = findViewById(R.id.shoppingCartHelpButton);
        bloodGlucoseRecorderHelpButton = findViewById(R.id.bloodRecorderHelpButton);

        changeInstructions();
        scrollZoomInstructions();
    }

    //Sets functionality for each of the buttons
    private void changeInstructions() {
        articlesHelpButton.setOnClickListener(view -> {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Articles");
            articleInstructions.scrollTo(0, 0);

            //Controls which instructions are visible and which are not
            selectInstructions.setVisibility(View.GONE);
            articleInstructions.setVisibility(View.VISIBLE);
            recipesInstructions.setVisibility(View.GONE);
            shoppingCartInstructions.setVisibility(View.GONE);
            bloodRecorderInstructions.setVisibility(View.GONE);
        });

        recipesHelpButton.setOnClickListener(view -> {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Recipes");
            recipesInstructions.scrollTo(0, 0);

            //Controls which instructions are visible and which are not
            selectInstructions.setVisibility(View.GONE);
            articleInstructions.setVisibility(View.GONE);
            recipesInstructions.setVisibility(View.VISIBLE);
            shoppingCartInstructions.setVisibility(View.GONE);
            bloodRecorderInstructions.setVisibility(View.GONE);
        });

        shoppingCartHelpButton.setOnClickListener(view -> {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Shopping Cart");
            shoppingCartInstructions.scrollTo(0, 0);

            //Controls which instructions are visible and which are not
            selectInstructions.setVisibility(View.GONE);
            articleInstructions.setVisibility(View.GONE);
            recipesInstructions.setVisibility(View.GONE);
            shoppingCartInstructions.setVisibility(View.VISIBLE);
            bloodRecorderInstructions.setVisibility(View.GONE);
        });

        bloodGlucoseRecorderHelpButton.setOnClickListener(view -> {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Blood Glucose Recorder");
            bloodRecorderInstructions.scrollTo(0, 0);

            //Controls which instructions are visible and which are not
            selectInstructions.setVisibility(View.GONE);
            articleInstructions.setVisibility(View.GONE);
            recipesInstructions.setVisibility(View.GONE);
            shoppingCartInstructions.setVisibility(View.GONE);
            bloodRecorderInstructions.setVisibility(View.VISIBLE);
        });

        //Requires user to double press the reset zoom button to reset the zoom in order to prevent accidental resets
        resetZoom.setOnClickListener(view -> {
            if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                Toast.makeText(getBaseContext(), "Zoom has been reset", Toast.LENGTH_SHORT).show();
                resetZoom();
                ration = 1.0f;
                confirmButtonPress = 0;
                return;
            } else {
                Toast.makeText(getBaseContext(), "Are you sure?", Toast.LENGTH_SHORT).show();
            }
            confirmButtonPress = System.currentTimeMillis();
        });
    }

    //Controls functionality for if a user scrolls through text with one finger or uses two fingers in order to pinch to zoom
    private void scrollZoomInstructions() {
        for(int i = 0; i < listOfInstructions.size(); i++) {
            TextView x = listOfInstructions.get(i);
            x.setOnTouchListener((v, motionEvent) -> {
                //Checks if user is scrolling (1 finger) or is zooming (2 fingers)
                if (motionEvent.getPointerCount() == 1) {
                    Log.d("Scroll", "1-pointer touch");
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    x.setMovementMethod(new ScrollingMovementMethod());
                }
                if (motionEvent.getPointerCount() == 2) {
                    Log.d("Zoom", "2-pointer touch");
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    zoomText(motionEvent, x);
                }
                return false;
            });
        }
    }

    //When user presses the reset button it sets the text back to the usual size and brings the user back to the top of the instructions they are reading.
    //It does not affect the other instructions and their level of zoom, requiring button presses on each one
    private void resetZoom() {
        if (selectInstructions.getVisibility()==View.VISIBLE) {
            selectInstructions.scrollTo(0, 0);
            selectInstructions.setTextSize(28);
        }
        if (articleInstructions.getVisibility()==View.VISIBLE) {
            articleInstructions.scrollTo(0, 0);
            articleInstructions.setTextSize(24);
        }
        if (recipesInstructions.getVisibility()==View.VISIBLE) {
            recipesInstructions.scrollTo(0, 0);
            recipesInstructions.setTextSize(24);
        }
        if (shoppingCartInstructions.getVisibility()==View.VISIBLE) {
            shoppingCartInstructions.scrollTo(0, 0);
            shoppingCartInstructions.setTextSize(24);
        }
        if (bloodRecorderInstructions.getVisibility()==View.VISIBLE){
            bloodRecorderInstructions.scrollTo(0, 0);
            bloodRecorderInstructions.setTextSize(24);
        }
    }

    //Controls if the user pinches or un-pinches the screen to decrease/increase the size of the text with 2 fingers
    private void zoomText(MotionEvent motionEvent, TextView textView) {
        if(motionEvent.getPointerCount()==2) {
            int action = motionEvent.getAction();
            int pureAction = action & MotionEvent.ACTION_MASK;
            if(pureAction == MotionEvent.ACTION_POINTER_DOWN){
                baseDistance = getDistance(motionEvent);
                baseRation = ration;
            }
            else {
                float delta = (getDistance(motionEvent)-baseDistance)/step;
                float multi = (float)(Math.pow(2,delta));
                ration = Math.min(1024.0f,Math.max(0.1f,multi * baseRation));
                textView.setTextSize(ration+13);
            }
        }
    }

    //Used to get distance from fingers
    private int getDistance(MotionEvent motionEvent){
        int dx = (int)(motionEvent.getX(0)-motionEvent.getX(1));
        int dy = (int)(motionEvent.getY(0)-motionEvent.getY(1));
        return (int)(Math.sqrt(dx * dx + dy * dy));
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        //Exclude current page
        MenuItem hidden = menu.findItem(R.id.howToUse);
        hidden.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.articlesMenuItem):
                intent = new Intent(HowToUseFeatures.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(HowToUseFeatures.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(HowToUseFeatures.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(HowToUseFeatures.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(HowToUseFeatures.this, MainMenu.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}