package com.minesweeper.thumbtack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private GridAdapter adapter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        adapter = new GridAdapter(this);
        gridview.setAdapter(adapter);
        AlertDialog.Builder alert = createEndGameAlertDialog();
        gridview.setOnItemClickListener(new CellOnClickListener(adapter, alert));
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
            modifyMenuOnNewGame();
            onCreate(null);
            return true;
        } else if (id == R.id.validate) {
            Toast.makeText(getApplicationContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.cheat) {
            cheat();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
     * Simple helper to create an alert dialog when a mine is pushed
     */
    private AlertDialog.Builder createEndGameAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Game Over");
        alert.setMessage("You hit a mine, try again!");
        alert.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onCreate(null);
            }
        });
        alert.setNeutralButton("Show Solution", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                cheat();
            }
        });
        alert.setCancelable(false);
        return alert;
    }
}
