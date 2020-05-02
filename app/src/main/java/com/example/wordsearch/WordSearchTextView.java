package com.example.wordsearch;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class WordSearchTextView extends androidx.appcompat.widget.AppCompatTextView {
    public static final int WORD_SEARCH_TEXT_SIZE = 25;
    public static final int WS_LETTER_HORIZ_PADDING = 20;

    Context context;

    public WordSearchTextView(Context context) {
        super(context);
        this.context = context;
        setTextSize(WORD_SEARCH_TEXT_SIZE);
        setTypeface(Typeface.MONOSPACE);
        setPadding(WS_LETTER_HORIZ_PADDING, 0, WS_LETTER_HORIZ_PADDING, 0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setBackgroundColor(Color.YELLOW);
        event.addBatch();
        return true;
    }
}
