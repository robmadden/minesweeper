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
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        adapter = new GridAdapter(this);
        gridview.setAdapter(adapter);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Oh no, you hit a mine! Try again!");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onCreate(null);
            }
        });

        gridview.setOnItemClickListener(new CellOnClickListener(adapter, alert));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.new_game) {
            onCreate(null);
            return true;
        } else if (id == R.id.validate) {
            Toast.makeText(getApplicationContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.cheat) {
            adapter.cheat();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
