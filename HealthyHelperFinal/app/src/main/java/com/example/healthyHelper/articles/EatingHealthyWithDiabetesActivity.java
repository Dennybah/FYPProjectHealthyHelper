package com.example.healthyHelper.articles;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthyHelper.ArticlesMenu;
import com.example.healthyHelper.BloodGlucoseRecorderMenu;
import com.example.healthyHelper.HowToUseFeatures;
import com.example.healthyHelper.MainMenu;
import com.example.healthyHelper.R;
import com.example.healthyHelper.RecipesMenu;
import com.example.healthyHelper.ShoppingCartMenu;

import java.util.Objects;

public class EatingHealthyWithDiabetesActivity extends AppCompatActivity {

    private TextView mainTextView;
    private TextView hyperLink;
    private Button resetZoomButton;

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
        setContentView(R.layout.article_eating_healthy_with_diabetes);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Eating Healthy With Diabetes");

        hyperLink = findViewById(R.id.hyperlink);
        mainTextView = findViewById(R.id.mainText);
        resetZoomButton = findViewById(R.id.resetZoomButton);

        mainText();
    }

    //This covers the functionality of the article ie the scrolling and zooming of the main text along with the reset zoom button and hyperlink on top
    private void mainText() {
        hyperLink.setMovementMethod(LinkMovementMethod.getInstance());

        mainTextView.setOnTouchListener((v, motionEvent) -> {
            //Checks if user is scrolling (1 finger) or is zooming (2 fingers)
            if(motionEvent.getPointerCount() == 1) {
                Log.d("Scroll","1-pointer touch");
                v.getParent().requestDisallowInterceptTouchEvent(false);
                mainTextView.setMovementMethod(new ScrollingMovementMethod());
            }
            if(motionEvent.getPointerCount() == 2){
                Log.d("Zoom","2-pointer touch");
                v.getParent().requestDisallowInterceptTouchEvent(true);
                zoomText(motionEvent);
            }
            return false;
        });

        resetZoomButton.setOnClickListener(view -> {
            if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                //Goes back to top of the article, resets text size and resets ration back to normal
                mainTextView.scrollTo(0,0);
                mainTextView.setTextSize(24);
                ration = 1.0f;
                confirmButtonPress = 0;
                Toast.makeText(getBaseContext(), "Zoom has been reset", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Are you sure?", Toast.LENGTH_SHORT).show();
            }
            confirmButtonPress = System.currentTimeMillis();
        });
    }

    //Controls if the user pinches or un-pinches the screen to decrease/increase the size of the text with 2 fingers
    public void zoomText(MotionEvent motionEvent) {
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
                mainTextView.setTextSize(ration+13);
            }
        }
    }

    //Used to get distance from fingers
    int getDistance(MotionEvent motionEvent){
        int dx = (int)(motionEvent.getX(0)-motionEvent.getX(1));
        int dy = (int)(motionEvent.getY(0)-motionEvent.getY(1));
        return (int)(Math.sqrt(dx * dx + dy * dy));
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
                intent = new Intent(EatingHealthyWithDiabetesActivity.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.bloodRecorderMenuItem):
                intent = new Intent(EatingHealthyWithDiabetesActivity.this, BloodGlucoseRecorderMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(EatingHealthyWithDiabetesActivity.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(EatingHealthyWithDiabetesActivity.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(EatingHealthyWithDiabetesActivity.this, MainMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(EatingHealthyWithDiabetesActivity.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}