package com.minesweeper.thumbtack.io;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.minesweeper.thumbtack.activities.MainActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class GenericIO<T> {
    private static final transient String FAIL_STRING = "failed to fetch data " + GenericIO.class.getName();
    private static final transient String FAIL_WRITE_STRING = "failed to write data " + GenericIO.class.getName();
    private static final transient String TAG = GenericIO.class.getName();
    private transient ObjectMapper om;
    protected transient Gson gson;
    protected transient String fileName;

    public GenericIO(String fileName) {
        this.fileName = fileName;
        om = new ObjectMapper();
        om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        om.configure(SerializationFeature.INDENT_OUTPUT, true);
        gson = new Gson();
    }

    protected abstract void deserialize(String json);

    /*
    * Return the settings from the settings file
    */
    public void fetch() throws FileNotFoundException {
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = MainActivity.appContext.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }

            deserialize(total.toString());
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            Log.d(TAG, FAIL_STRING);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                Log.d(TAG, FAIL_STRING);
            }
        }
    }

   /*
    * Write a JSON to FILENAME
    */
    public void write(String json) {
        FileOutputStream fos = null;
        try {
            fos = MainActivity.appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
        } catch (Exception e) {
            Log.d(TAG, FAIL_WRITE_STRING);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    Log.d(TAG, FAIL_WRITE_STRING);
                }
            }
        }
    }
}
