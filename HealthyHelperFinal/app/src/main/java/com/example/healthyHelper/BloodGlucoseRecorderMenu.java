package com.example.healthyHelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.healthyHelper.dialog.Dialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BloodGlucoseRecorderMenu extends AppCompatActivity {

    private final Calendar myCalendar= Calendar.getInstance();
    private Date pickedByUser;
    private Date currentPhoneDate;
    private Date setCalenderPopupDate;

    //Formats date into the dd/mm/yyyy style such as 12/10/2022
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private BarChart barChart;

    private EditText enterTime;
    private EditText enterGlucoseLevel;
    private EditText enterDate;

    private Button loadDate;
    private Button addDate;
    private Button viewDates;
    private Button removeDate;

    private Button addEntryButton;
    private Button viewEntriesButton;
    private Button removeEntryButton;

    private ArrayList<BarEntry> barEntryArraylist;
    private ArrayList<String> fileNames;

    private String fileName = "";

    //Holds the time and glucose level from user input
    private float time = 0;
    private float glucoseLevel = 0;

    //Control visibility of entering in time/glucose level section of app
    private ConstraintLayout readingsLayout;

    //Used for clock dialog picker when user enters time
    private int hour, minute;

    //Timing for double button press
    private static final int TIME_INTERVAL = 2000;
    private long confirmButtonPress;

    //For use in pop up dialog
    public static String dialogString = "";

    //Format entered time and glucose level to 2 decimal places
    private final DecimalFormat dfTime = new DecimalFormat("00.00");
    private final DecimalFormat dfGlucoseLevel = new DecimalFormat("0.00");

    //Show X axis values in 2 decimal places
    private final ValueFormatter formatter = new ValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
            return String.format(Locale.getDefault(), "%.2f", value);
        }
    };

    //Shared preferences file
    public static final String BloodRecorderActivity = "bloodRecorderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_blood_glucose_recorder);

        //Adds custom toolbar to app screen
        Toolbar myToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Blood Glucose Recorder");

        barChart = findViewById(R.id.barchart);

        currentPhoneDate = Calendar.getInstance().getTime();
        readingsLayout = findViewById(R.id.readingsLayout);

        enterTime=findViewById(R.id.enterTime);
        enterTime();
        enterGlucoseLevel=findViewById(R.id.enterBloodGlucoseLevel);
        enterDate=findViewById(R.id.enterDate);
        enterDate();

        //Gets the previous dates from memory and displays an empty bar chart
        getPreviousFileNames();
        emptyBarChart();

        loadDate = findViewById(R.id.searchDate);
        addDate = findViewById(R.id.addDate);
        viewDates = findViewById(R.id.dateList);
        removeDate = findViewById(R.id.removeDate);

        addEntryButton = findViewById(R.id.addEntry);
        viewEntriesButton = findViewById(R.id.entryList);
        removeEntryButton = findViewById(R.id.removeEntry);

        //Specifies each of the buttons functionality
        loadDateButton();
        addDateButton();
        viewDatesButton();
        removeDateButton();

        addEntryButton();
        removeEntryButton();
        viewEntriesButton();
    }

    //The functionality for when a user presses the load date button, this loads a new date
    private void loadDateButton() {
        loadDate.setOnClickListener(v -> {
            //Takes fileName as the date currently entered
            fileName = enterDate.getText().toString();

            //If where the date should be entered is blank display a message
            if(fileName.equals("")) {
                Toast.makeText(BloodGlucoseRecorderMenu.this, "No date has been entered, please try again", Toast.LENGTH_SHORT).show();
            }
            else {
                //Checks if the date chosen by the user from the calendar is beyond the phones current date, displays a message and
                //prevent the user from entering any data if this is the case
                pickedByUser = myCalendar.getTime();
                if (pickedByUser.after(currentPhoneDate)) {
                    Toast.makeText(BloodGlucoseRecorderMenu.this, "The date " + fileName + " is in the future, please try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    //If the date has been previously added then it is valid and so the date is loaded along with the previous entries and is ready for use
                    if(fileNames.contains(fileName)) {
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "Date " + fileName + " has been loaded", Toast.LENGTH_SHORT).show();
                        getPreviousEntries(fileName);
                        readingsLayout.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(getSupportActionBar()).setTitle(fileName);
                    }
                    else {
                        //If a user attempts to load a date that has not been added then display an appropriate message
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "The date " + fileName + " has not yet been added, please add it and try again", Toast.LENGTH_SHORT).show();
                    }
                }
                //Changes calendar back to currently loaded date if one is opened
                setCalendarToUserInputtedDate();
            }
            //Updates the barchart with the relevant data
            reloadChart(barChart);
        });
    }

    //The functionality for when a user presses the add date button, this is used to add a date that has not been previously added
    private void addDateButton() {
        addDate.setOnClickListener(v -> {
            //Takes fileName as the date currently entered
            fileName = enterDate.getText().toString();

            //Used to identify if an entered date matches a date from within the list
            boolean matchFound = false;

            //If where the date should be entered is blank display a message
            if(fileName.equals("")) {
                Toast.makeText(BloodGlucoseRecorderMenu.this, "No date has been entered, please try again", Toast.LENGTH_SHORT).show();
            }
            else {
                //Checks if the date chosen by the user from the calendar is beyond the phones current date, displays a message and
                //prevent the user from entering any data if this is the case
                pickedByUser = myCalendar.getTime();
                if (pickedByUser.after(currentPhoneDate)) {
                    Toast.makeText(BloodGlucoseRecorderMenu.this, "The date " + fileName + " is in the future, please try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Goes through list of dates, if date entered is already found (a duplicate) display a relevant message
                    for (int i = 0; i < fileNames.size(); i++) {
                        if(fileName.equals(fileNames.get(i))) {
                            Toast.makeText(BloodGlucoseRecorderMenu.this, "The date " + fileName + " has already been found, please try again", Toast.LENGTH_SHORT).show();
                            matchFound = true;
                        }
                    }
                    //Else the date will be added along with a relevant message
                    if(!matchFound) {
                        fileNames.add(fileName);
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "Date " + fileName + " has been added", Toast.LENGTH_SHORT).show();
                    }
                }
                //Changes calendar back to currently loaded date if one is opened
                setCalendarToUserInputtedDate();
            }

            //Updates the barchart with the relevant data
            reloadChart(barChart);

            //Data change is written into memory via shared preferences
            SharedPreferences bloodRecorderStoredState = getSharedPreferences(BloodRecorderActivity, 0);
            SharedPreferences.Editor editor = bloodRecorderStoredState.edit();
            Gson gson = new Gson();
            String json = gson.toJson(fileNames);
            editor.putString("fileNamesListDates", json);
            editor.apply();
        });
    }

    //The functionality for when a user presses the view dates button, this displays all added dates within a dialog box
    private void viewDatesButton() {
        viewDates.setOnClickListener(v -> {
            //Checks if the list of filenames is empty and sets the value of the string to an appropriate message
            if(fileNames.isEmpty()) {
                dialogString = "No entries currently found";
            }
            else {
                //Constructs the string from every value in filenames
                StringBuilder stringBuilder= new StringBuilder();
                for (int i = 0; i < fileNames.size(); i++) {
                    stringBuilder.append(fileNames.get(i)).append(" \n");
                }
                dialogString = stringBuilder.toString();
            }
            //Opens the dialog box with the relevant content
            openDialogEntryBox();
        });
    }

    //The functionality for when a user presses the remove date button, this removes a valid selected date once it is pressed twice
    private void removeDateButton() {
        removeDate.setOnClickListener(v -> {
            //Takes fileName as the date currently entered
            fileName = enterDate.getText().toString();

            //Used to identify if an entered date matches a date from within the list
            boolean matchFound = false;

            //Prevents enter date changing too early from a singular press of the remove date button
            boolean changeEnterDate = false;

            //If there are no dates present within the file names list then display an appropriate message
            if(fileNames.isEmpty()) {
                Toast.makeText(BloodGlucoseRecorderMenu.this, "There are no dates that can be removed as the saved dates list is empty", Toast.LENGTH_SHORT).show();
            }
            else {
                //If where the date should be entered is blank display a message
                if(fileName.equals("")) {
                    Toast.makeText(BloodGlucoseRecorderMenu.this, "No date has been entered, please try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Checks if the date chosen by the user from the calendar is beyond the phones current date, displays a message and
                    //prevent the user from removing any data if this is the case
                    pickedByUser = myCalendar.getTime();
                    if (pickedByUser.after(currentPhoneDate)) {
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "The date " + fileName + " is in the future, please try again", Toast.LENGTH_SHORT).show();
                        setCalendarToUserInputtedDate();
                        changeEnterDate = true;
                    }
                    else {
                        //Goes through list of file names to find and remove the date if it is found
                        for (int i = 0; i < fileNames.size(); i++) {
                            if(fileName.equals(fileNames.get(i))) {
                                //Requires the user to press the remove date button twice together to remove an entry to prevent accidental entry removal
                                //NOTE, the entries will still remain present and requires the user to remove each entry individually for extra safety
                                if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                                    matchFound = true;
                                    changeEnterDate = true;
                                    fileNames.remove(fileName);

                                    //Checks if the date being removed is the current date, if it is then replace removed dates barchart with an empty barchart
                                    if(fileName.equals(Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString())) {
                                        readingsLayout.setVisibility(View.INVISIBLE);
                                        Objects.requireNonNull(getSupportActionBar()).setTitle("Blood Glucose Recorder");
                                        emptyBarChart();
                                    }

                                    Toast.makeText(BloodGlucoseRecorderMenu.this, "Date " + fileName + " has been removed", Toast.LENGTH_SHORT).show();
                                    enterDate.setText("");
                                    confirmButtonPress = 0;
                                }
                                else {
                                    Toast.makeText(getBaseContext(), "Are you sure?", Toast.LENGTH_SHORT).show();
                                    matchFound = true;
                                }
                                confirmButtonPress = System.currentTimeMillis();
                            }
                        }
                        //If the date to be removed was not found in the file names list then display an appropriate message
                        if (!matchFound) {
                            Toast.makeText(BloodGlucoseRecorderMenu.this, "The date " + fileName + " could not be found, please try again", Toast.LENGTH_SHORT).show();
                            changeEnterDate = true;
                        }
                    }
                }
                //If this is true, the enter date text box can now be changed
                if(changeEnterDate) {
                    //Changes calendar back to currently loaded date if one is opened
                    setCalendarToUserInputtedDate();
                }
            }
            //Updates the barchart with the relevant data
            reloadChart(barChart);

            //Data change is written into memory via shared preferences
            SharedPreferences bloodRecorderStoredState = getSharedPreferences(BloodRecorderActivity, 0);
            SharedPreferences.Editor editor = bloodRecorderStoredState.edit();
            Gson gson = new Gson();
            String json = gson.toJson(fileNames);
            editor.putString("fileNamesListDates", json);
            editor.apply();
        });
    }

    //The functionality for when a user adds a reading to the bar chart of a certain loaded date
    private void addEntryButton() {
        addEntryButton.setOnClickListener(v -> {
            //Takes fileName as the date currently entered
            fileName = enterDate.getText().toString();

            //Checks if the entered date matches the currently opened graph's date continue
            if(fileName.equals(Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString())) {
                //Checks if the time or blood glucose level is left empty by the user and displays a message if it is
                if (TextUtils.isEmpty(enterTime.getText().toString()) || TextUtils.isEmpty(enterGlucoseLevel.getText().toString())) {
                    Toast.makeText(BloodGlucoseRecorderMenu.this, "One or more entries are empty \nPlease try again", Toast.LENGTH_SHORT).show();
                } else {
                    //time is converted to have a . instead of a :
                    time = Float.parseFloat(String.format(Locale.getDefault(), "%02d.%02d", hour, minute, enterTime.getText().toString()));
                    glucoseLevel = Float.parseFloat(enterGlucoseLevel.getText().toString());

                    //Converts the entered time into an appropriate format ie 12:00 is converted to 12.00
                    String enteredTime = String.format(Locale.getDefault(), "%02d.%02d", hour, minute, enterTime.getText().toString());

                    //Checks if the time has already been previously plotted (duplicate)
                    for (int i = 0; i < barEntryArraylist.size(); i++) {
                        Entry e = barEntryArraylist.get(i);
                        if (enteredTime.equals(dfTime.format(e.getX()))) {
                            Toast.makeText(BloodGlucoseRecorderMenu.this, "The time " + enterTime.getText().toString() + " has already been plotted, please remove this point", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    //Checks if the glucose level entered is high
                    if(glucoseLevel >= 7) {
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "Consider looking at the blueberry yogurt recipe, berries help reduce blood sugar", Toast.LENGTH_LONG).show();
                    }
                    //Checks if the glucose level entered is very low
                    if (glucoseLevel < 4) {
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "Your blood sugar level is low, maybe eat fruit/a carbohydrate snack to increase it", Toast.LENGTH_LONG).show();
                    }

                    //Entry is added to the fileName
                    getData(fileName, time, glucoseLevel);

                    //Time and glucose level text boxes are emptied
                    enterTime.setText("");
                    enterGlucoseLevel.setText("");
                }
            }
            //If the entered date does not match the current date, an appropriate message is displayed
            else {
                Toast.makeText(BloodGlucoseRecorderMenu.this, "The chosen date " + fileName + " does not match the currently loaded date", Toast.LENGTH_SHORT).show();
            }

            //Updates the barchart with the relevant data
            reloadChart(barChart);
        });
    }

    //The functionality for when a user removes a reading from the bar chart of a certain loaded date
    private void removeEntryButton() {
        removeEntryButton.setOnClickListener(v -> {
            //Used to identify if an entered time matches a time from within the list
            boolean matchFound = false;

            //Takes fileName as the date currently entered
            fileName = enterDate.getText().toString();

            //Checks if the entered date matches the currently opened graph's date continue
            if(fileName.equals(Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getTitle()).toString())) {
                //If there are no entries within the bar chart then
                if (barChart.isEmpty()) {
                    Toast.makeText(BloodGlucoseRecorderMenu.this, "There are no entries to be removed, please add an entry and try again", Toast.LENGTH_SHORT).show();
                } else {
                    //Checks if the time is left empty by the user and displays a message if it is
                    if (TextUtils.isEmpty(enterTime.getText().toString())) {
                        Toast.makeText(BloodGlucoseRecorderMenu.this, "Time is empty please enter the time you wish to remove and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        //Goes through list of readings and remove an entry if found
                        for (int i = 0; i < barEntryArraylist.size(); i++) {
                            Entry e = barEntryArraylist.get(i);
                            //Converts the entered time into an appropriate format ie 12:00 is converted to 12.00 and checks if entered time matches a time present on the chart
                            String enteredTime = String.format(Locale.getDefault(), "%02d.%02d", hour, minute, enterTime.getText().toString());
                            if (enteredTime.equals(dfTime.format(e.getX()))) {
                                //Requires the user to press the remove reading button twice together to remove an entry to prevent accidental entry removal
                                if (confirmButtonPress + TIME_INTERVAL > System.currentTimeMillis()) {
                                    Toast.makeText(BloodGlucoseRecorderMenu.this, "Time " + enterTime.getText().toString() + " has been removed", Toast.LENGTH_SHORT).show();
                                    matchFound = true;
                                    enterTime.setText("");
                                    enterGlucoseLevel.setText("");
                                    barEntryArraylist.remove(e);
                                    confirmButtonPress = 0;
                                } else {
                                    Toast.makeText(getBaseContext(), "Are you sure?", Toast.LENGTH_SHORT).show();
                                    matchFound = true;
                                }
                                confirmButtonPress = System.currentTimeMillis();
                            }
                        }
                        //Checks if no entry could be found
                        if (!matchFound) {
                            Toast.makeText(BloodGlucoseRecorderMenu.this, "No entry matching this time could be found please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            //If the entered date does not match the current date, an appropriate message is displayed
            else {
                Toast.makeText(BloodGlucoseRecorderMenu.this, "The chosen date " + fileName + " does not match the currently loaded date", Toast.LENGTH_SHORT).show();
            }

            //Updates the barchart with the relevant data
            reloadChart(barChart);

            //Write the entries to memory
            SharedPreferences bloodRecorderStoredState = getSharedPreferences(BloodRecorderActivity, 0);
            SharedPreferences.Editor editor = bloodRecorderStoredState.edit();
            Gson gson = new Gson();
            String json = gson.toJson(barEntryArraylist);
            editor.putString(fileName, json);
            editor.apply();
        });
    }

    //Provides the functionality for when a user pressed the view readings button, this displays all added dates within a dialog box
    private void viewEntriesButton() {
        viewEntriesButton.setOnClickListener(v -> {
            //Checks if the list of bar entries is empty and sets the value of the string to an appropriate message
            if(barEntryArraylist.isEmpty()) {
                dialogString = "No entries currently found";
            }
            else {
                //Constructs the string from every entry in the bar entry array List
                StringBuilder stringBuilder= new StringBuilder();
                for (int i = 0; i < barEntryArraylist.size(); i++) {
                    Entry e = barEntryArraylist.get(i);
                    //Builds each line in accordance to the entered blood glucose level of each entry
                    if(e.getY() >= 7) {
                        stringBuilder.append("Time: ").append(dfTime.format(e.getX())).append(" Glucose Level: ").append(dfGlucoseLevel.format(e.getY())).append(" (DANGER)").append(" \n");
                    }
                    else if (e.getY() >= 6.1 && e.getY() < 7) {
                        stringBuilder.append("Time: ").append(dfTime.format(e.getX())).append(" Glucose Level: ").append(dfGlucoseLevel.format(e.getY())).append(" (WARNING)").append(" \n");
                    }
                    else if (e.getY() < 4) {
                        stringBuilder.append("Time: ").append(dfTime.format(e.getX())).append(" Glucose Level: ").append(dfGlucoseLevel.format(e.getY())).append(" (LOW?)").append(" \n");
                    }
                    else {
                        stringBuilder.append("Time: ").append(dfTime.format(e.getX())).append(" Glucose Level: ").append(dfGlucoseLevel.format(e.getY())).append(" (OK)").append(" \n");
                    }
                }
                dialogString = stringBuilder.toString();
            }
            //Opens the dialog box with the relevant content
            openDialogEntryBox();
        });
    }

    //When the date is changed in the enter date text box, this is used to bring the date back to the currently opened date if one is loaded
    private void setCalendarToUserInputtedDate() {
        //If a date is already loaded this will set the enter date section back to the loaded date
        if(Objects.requireNonNull(getSupportActionBar()).getTitle() != "Blood Glucose Recorder") {
            enterDate.setText(Objects.requireNonNull(getSupportActionBar()).getTitle());
        }

        //Converts the entered string into a date to be used by the calendar, catches if there is a parse exception
        try {
            setCalenderPopupDate = dateFormat.parse(enterDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Covers in the case nothing is entered
        if (setCalenderPopupDate != null) {
            myCalendar.setTime(setCalenderPopupDate);
        }
    }

    //Gives blank chart with visible x axis for when a date is removed or when the blood glucose recorder is initially opened
    private void emptyBarChart() {
        barEntryArraylist = new ArrayList<>();
        BarDataSet barDataSet = new BarDataSet(barEntryArraylist, "Graph");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChartFormat(barChart);
    }

    //When the blood glucose recorder is reopened, this checks if there were any previous glucose readings in the shared preferences file
    //if there are use this list of entries, if not create a new list
    private void getPreviousEntries(String x) {
        SharedPreferences bloodRecorderStoredState = getSharedPreferences(BloodRecorderActivity, 0);
        Gson gson = new Gson();
        String json = bloodRecorderStoredState.getString(x, null);
        Type type = new TypeToken<ArrayList<BarEntry>>() {}.getType();
        barEntryArraylist = gson.fromJson(json, type);

        if (barEntryArraylist == null) {
            //If the array list is empty, create new list
            barEntryArraylist = new ArrayList<>();
        }
    }

    //When the blood glucose recorder is reopened, this checks if there were any previous filenames in the shared preferences file
    //if there are use this list, if not create a new list
    private void getPreviousFileNames() {
        SharedPreferences bloodRecorderStoredState = getSharedPreferences(BloodRecorderActivity, 0);
        Gson gson = new Gson();
        String json = bloodRecorderStoredState.getString("fileNamesListDates", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        fileNames = gson.fromJson(json, type);

        if (fileNames == null) {
            // if the array list is empty, create new list
            fileNames = new ArrayList<>();
        }
    }

    //Called when a user enters information into the chart
    private void getData(String file, Float x, Float y) {
        barEntryArraylist.add(new BarEntry(x, y));

        //Takes the user entered time and converts it so it can be displayed on screen via a pop up message at the bottom of the screen/toast
        String userEnteredTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute, enterTime.getText().toString());
        Toast.makeText(BloodGlucoseRecorderMenu.this, "Glucose reading at " + userEnteredTime + " has been recorded", Toast.LENGTH_SHORT).show();

        //Write the entries to memory
        SharedPreferences bloodRecorderStoredState = getSharedPreferences(BloodRecorderActivity, 0);
        SharedPreferences.Editor editor = bloodRecorderStoredState.edit();
        Gson gson = new Gson();
        String json = gson.toJson(barEntryArraylist);
        editor.putString(file, json);
        editor.apply();
    }

    //The clock that appears when a user wishes to enter a time for a blood glucose reading
    private void enterTime() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            enterTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        };

        enterTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(BloodGlucoseRecorderMenu.this, onTimeSetListener, hour, minute, true);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });
    }

    //The calendar that is opened when a user enters a date
    private void enterDate() {
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            enterDate.setText(dateFormat.format(myCalendar.getTime()));
        };
        enterDate.setOnClickListener(view -> new DatePickerDialog(BloodGlucoseRecorderMenu.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    //This is called when the bar chart needs to be reloaded to reflect a change such as when a reading is added or removed
    private void reloadChart(BarChart barChart) {
        BarDataSet barDataSet = new BarDataSet(barEntryArraylist, "Graph");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i <  barEntryArraylist.size(); i++) {
            Entry e = barEntryArraylist.get(i);
            //Specific colours for each glucose value entered
            if (e.getY() >= 7) {
                colors.add(Color.RED);
            }
            else if (e.getY() >= 6.1 && e.getY() < 7) {
                colors.add(Color.YELLOW);
            }
            else {
                colors.add(Color.GREEN);
            }
        }

        barDataSet.setColors(colors);
        barDataSet.setValueTextSize(24f);
        barDataSet.setValueFormatter(formatter);

        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    //This is responsible for formatting the bar chart to make it appear as it does, these formatting changes can be seen below
    private void barChartFormat(BarChart barchart) {
        //Move X axis to the bottom, change color to red
        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);

        //Sets time to appear on x axis every 4 hours
        xAxis.setLabelCount(7, /*force: */true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(24);
        xAxis.setTextSize(12);

        //Remove second y axis on the right
        YAxis yAxisRemoved = barchart.getAxisRight();
        yAxisRemoved.setEnabled(false);

        //Remove description in bottom right
        Description description = new Description();
        description.setText("");
        barchart.setDescription(description);

        //Remove zoom
        barchart.setScaleEnabled(false);

        //Remove coloured legend
        barchart.getLegend().setEnabled(false);

        //Remove ability to touch bars
        barchart.setTouchEnabled(false);
    }

    //Pop up for when entries are checked (dates or entries on barchart)
    private void openDialogEntryBox() {
        Dialog exampleDialog = new Dialog();
        exampleDialog.show(getSupportFragmentManager(),"exampleDialog");
    }

    //Below relates to the tool bar menu and the activity the user is brought to when an option is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screens_menu, menu);
        //Exclude current page
        MenuItem hidden = menu.findItem(R.id.bloodRecorderMenuItem);
        hidden.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case (R.id.articlesMenuItem):
                intent = new Intent(BloodGlucoseRecorderMenu.this, ArticlesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.recipeMenuItem):
                intent = new Intent(BloodGlucoseRecorderMenu.this, RecipesMenu.class);
                startActivity(intent);
                return true;
            case (R.id.shoppingCartMenuItem):
                intent = new Intent(BloodGlucoseRecorderMenu.this, ShoppingCartMenu.class);
                startActivity(intent);
                return true;
            case (R.id.homeMenuItem):
                intent = new Intent(BloodGlucoseRecorderMenu.this, MainMenu.class);
                startActivity(intent);
                return true;
            case (R.id.howToUse):
                intent = new Intent(BloodGlucoseRecorderMenu.this, HowToUseFeatures.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}