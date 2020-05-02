package com.example.wordsearch;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int WORD_BANK_TEXT_SIZE = 15;
    TableLayout wordSearch;
    LinearLayout wordBank;

    static Orientation orientation = Orientation.UNDECIDED;
    static String selectedText = "";
    static Endpoint startLetter = new Endpoint(true);
    static Endpoint endLetter = new Endpoint(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        wordSearch = findViewById(R.id.word_search_grid);
        wordBank = findViewById(R.id.word_bank);

        initWordSearch(context);
        initWordBank(context);

        wordSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return false;
            }
        });

    }

    /** callback from textview
     * uses row and column to determine actions
     * if endpoint letters are adjacent and matching orientation to the selection
     *  set selection as new end point
     *  append letter to the current word
     * else clear selection and start with selected letter as both endpoints with undecided orientation*/
    public static void handleTouch(int rowIndex, int colIndex, char letter) {
        switch (orientation) {
            case UNDECIDED:
                startLetter.update(rowIndex, colIndex);
                endLetter.update(rowIndex, colIndex);
                selectedText = selectedText.concat(String.valueOf(letter));
            case VERTICAL:
                extendSelection(startLetter.colIndex, colIndex, startLetter.rowIndex, endLetter.rowIndex, rowIndex, letter);
            case HORIZONTAL:
                extendSelection(startLetter.rowIndex, rowIndex, startLetter.colIndex, endLetter.colIndex, colIndex, letter);
        }
    }

    /** expected and actual are to make sure selections are linear
     * returns true if selection successfully extended*/
    private static boolean extendSelection(int expected, int actual, int startIndex, int endIndex, int actualIndex, char letter) {
        if (expected == actual) {
            if (actualIndex == startIndex-1) {
//                todo means it will be added in front
                updateEndpoint(startLetter, actual, actualIndex);
                selectedText = String.valueOf(letter).concat(selectedText);
                return true;
            } else if (actual == endIndex+1) {
//                todo means it will be added in the back
                updateEndpoint(endLetter, actual, actualIndex);
                selectedText = selectedText.concat(String.valueOf(letter));
                return true;
            }
        }

//        todo otherwise reset and make new selection
        return false;
    }

    private static void updateEndpoint(Endpoint endpoint, int actual, int actualIndex) {
        if ((orientation == Orientation.VERTICAL)) {
            endpoint.update(actualIndex, actual);
        } else {
            endpoint.update(actual, actualIndex);
        }
    }

    private void initWordBank(Context context) {
        //        fill wordbank display
        for (String word :
                getResources().getStringArray(R.array.test_words_list)) {
            TextView tempTextView = new TextView(context);
            tempTextView.setTextSize(WORD_BANK_TEXT_SIZE);
            tempTextView.setTypeface(Typeface.MONOSPACE);
            tempTextView.setText(word);
            wordBank.addView(tempTextView);
        }
    }

    private void initWordSearch(Context context) {

        for (String row :
                getResources().getStringArray(R.array.test_content)
        ) {
            TableRow tempTableRow = new TableRow(context);
//            add each character in row as separate textView to tableRow
            for (int i = 0; i < row.length(); i++) {
//                special textview format
                TextView tempTextView = new WordSearchTextView(context, handleTouch(int, int, char));
                tempTextView.setText(row.substring(i,i+1));
                tempTableRow.addView(tempTextView);
            }
//            add tableRow to TableLayout
            wordSearch.addView(tempTableRow);
        }
    }

    private enum Orientation {
        UNDECIDED, HORIZONTAL, VERTICAL;
    }

    private class Endpoint {
        private int rowIndex;
        private int colIndex;
        private boolean isLeftorTop;

        Endpoint(boolean isLeftorTop) {
            this.isLeftorTop = isLeftorTop;
        }

        public void update(int rowIndex, int colIndex) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
        }
    }
}
