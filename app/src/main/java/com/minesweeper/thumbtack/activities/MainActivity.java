package com.minesweeper.thumbtack.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.minesweeper.thumbtack.io.BestTimes;
import com.minesweeper.thumbtack.io.Settings;
import com.minesweeper.thumbtack.models.Cell;
import com.minesweeper.thumbtack.GameTimerTask;
import com.minesweeper.thumbtack.GridAdapter;
import com.minesweeper.thumbtack.R;
import com.minesweeper.thumbtack.listeners.CellOnClickListener;
import com.minesweeper.thumbtack.listeners.CellOnLongClickListener;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    public static final String GAME_OVER = "Game Over";
    public static final String INTENT_EXTRA_NUM_MINES = "intent_extra_num_mines";
    public static Context appContext;
    private GridAdapter adapter;
    private Menu menu;
    private Timer t;
    private TimerTask task;
    private BestTimes times;
    public boolean gameHasStarted;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        buildGame();
        times = new BestTimes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (adapter.isInCheatMode()) {
            modifyMenuOnCheat();
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.new_game) {
            stopTimer();
            modifyMenuOnNewGame();
            buildGame();
            return true;
        } else if (id == R.id.validate) {
            stopTimer();
            validate();
            return true;
        } else if (id == R.id.cheat) {
            stopTimer();
            cheat();
            return true;
        } else if (id == R.id.high_scores) {
            startActivity(buildBestTimesIntent());
            return true;
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent buildBestTimesIntent() {
        Intent intent = new Intent(this, BestTimesActivity.class);
        intent.putExtra(INTENT_EXTRA_NUM_MINES, settings.numMines);
        return intent;
    }

    /*
     * Create an upwards counting timer to display
     */
    public void startTimer() {
        gameHasStarted = true;
        t = new Timer();
        task = new GameTimerTask(this);
        t.scheduleAtFixedRate(task, 0, 1000);
    }

    /*
     * Stop the timer
     */
    public void stopTimer() {
        gameHasStarted = false;
        if (t != null) {
            t.cancel();
        }

        if (task != null) {
            task.cancel();
        }
    }

    /*
     * Helper method to build the minesweeper game
     */
    private void buildGame() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        TextView flagCountView = (TextView) findViewById(R.id.flag_value);
        TextView timeLabelView = (TextView) findViewById(R.id.timer);
        timeLabelView.setText("0:00");

        settings = new Settings();
        try {
            settings.fetch();
        } catch (FileNotFoundException e) {
            settings.setMineCount(Settings.DEFAULT_MINE_COUNT);
        }

        adapter = new GridAdapter(this, flagCountView, settings);
        gridview.setAdapter(adapter);
        AlertDialog.Builder alert = createEndGameAlertDialog();
        gridview.setOnItemClickListener(new CellOnClickListener(adapter, alert, this));
        gridview.setOnItemLongClickListener(new CellOnLongClickListener(adapter));
    }

    /*
     * Helper method to modify the menu when they press new game
     */
    private void modifyMenuOnNewGame() {
        MenuItem validateItem = menu.findItem(R.id.validate);
        MenuItem cheatItem = menu.findItem(R.id.cheat);
        validateItem.setVisible(true);
        cheatItem.setVisible(true);
        this.invalidateOptionsMenu();
    }

    /*
     * Helper method to modify the menu when they press cheat
     */
    private void modifyMenuOnCheat() {
        MenuItem validateItem = menu.findItem(R.id.validate);
        MenuItem cheatItem = menu.findItem(R.id.cheat);
        validateItem.setVisible(false);
        cheatItem.setVisible(false);
        this.invalidateOptionsMenu();
    }

    /*
     * When the user decides to cheat, modify the menu and display the solution
     */
    private void cheat() {
        // Hide items in the menu when they cheat so that they can only
        // start a new game
        modifyMenuOnCheat();
        adapter.cheat();
    }

    /*
     * Check if the player's selections are correct
     */
    private void validate() {
        boolean victory = true;
        for (Cell c : adapter.getCells()) {
            if (c.isMine && !c.isFlagged) {
                victory = false;
            }
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(GAME_OVER);
        if (victory) {
            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            final TextView timer = (TextView) findViewById(R.id.timer);
            final String readableTime = (String)timer.getText();
            Integer time = times.toSeconds(readableTime);
            final int index = times.getBestTimeIndex(settings.numMines, time);

            if (index < BestTimes.TOTAL_SCORES_TO_STORE) {
                alert.setTitle("New high score!");
                alert.setMessage("Please enter your name");
                alert.setView(input);
                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        if (value.isEmpty()) { return; }
                        times.insert(settings.numMines, value, readableTime, index);
                        stopTimer();
                        buildGame();
                    }
                });
            } else {
                alert.setMessage("You won!");
                setDefaultButtons(alert);
            }
        } else {
            alert.setMessage("Sorry, you lost.");
            setDefaultButtons(alert);
        }
        alert.setCancelable(false);
        alert.show();
    }

    /*
     * Helper to add buttons to alert dialog
     */
    private void setDefaultButtons(AlertDialog.Builder alert) {
        alert.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            stopTimer();
            buildGame();
            }
        });
        alert.setNeutralButton("Show best times", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            startActivity(buildBestTimesIntent());
            }
        });
    }

    /*
     * Simple helper to create an alert dialog when a mine is pushed
     */
    private AlertDialog.Builder createEndGameAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(GAME_OVER);
        alert.setMessage("You hit a mine, try again!");
        alert.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                stopTimer();
                buildGame();
            }
        });
        alert.setNeutralButton("Show Solution", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                stopTimer();
                cheat();
            }
        });
        alert.setCancelable(false);
        return alert;
    }
}
