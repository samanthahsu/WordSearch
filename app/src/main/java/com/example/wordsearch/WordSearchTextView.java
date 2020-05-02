package com.example.wordsearch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class WordSearchTextView extends androidx.appcompat.widget.AppCompatTextView {
    public static final int WORD_SEARCH_TEXT_SIZE = 35;
    public static final int WS_LETTER_HORIZ_PADDING = 20;

    Context context;
    int rowIndex;
    int colIndex;

    public WordSearchTextView(Context context, int rowIndex, int colIndex) {
        super(context);
        this.context = context;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        setTextSize(WORD_SEARCH_TEXT_SIZE);
        setTypeface(Typeface.MONOSPACE);
        setPadding(WS_LETTER_HORIZ_PADDING, 0, WS_LETTER_HORIZ_PADDING, 0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MainActivity.handleTouch(this);
        return true;
    }
}
