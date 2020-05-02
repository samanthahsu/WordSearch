package com.example.wordsearch;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int WORD_SEARCH_TEXT_SIZE = 25;
    public static final int WORD_BANK_TEXT_SIZE = 15;
    public static final int WS_LETTER_HORIZ_PADDING = 20;
    TableLayout wordSearch;
    LinearLayout wordBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        wordSearch = findViewById(R.id.word_search_grid);
        wordBank = findViewById(R.id.word_bank);

        initWordSearch(context);
        initWordBank(context);

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
                TextView tempTextView = new TextView(context);
                tempTextView.setTextSize(WORD_SEARCH_TEXT_SIZE);
                tempTextView.setText(row.substring(i,i+1));
                tempTextView.setTypeface(Typeface.MONOSPACE);
                tempTextView.setPadding(WS_LETTER_HORIZ_PADDING, 0, WS_LETTER_HORIZ_PADDING, 0);
                tempTableRow.addView(tempTextView);
            }
//            add tableRow to TableLayout
            wordSearch.addView(tempTableRow);
        }
    }

}
