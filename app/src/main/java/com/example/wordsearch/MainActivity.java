package com.example.wordsearch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int WORD_BANK_TEXT_SIZE = 15;
    TableLayout wordSearch;
    LinearLayout wordBank;
    static Context context;


    static Orientation orientation = Orientation.UNDECIDED;
    static String selectedText = "";
//    start letter is the topmost for vertical, the leftmost for diagonal and horizontal
    static Endpoint startLetter = new Endpoint(true);
    static Endpoint endLetter = new Endpoint(false);
    static List<WordSearchTextView> selectedTextViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        wordSearch = findViewById(R.id.word_search_grid);
        wordBank = findViewById(R.id.word_bank);

        initWordSearch(context);
        initWordBank(context);

    }

    /** callback from textview
     * uses row and column to determine actions
     * if endpoint letters are adjacent and matching orientation to the selection
     *  set selection as new end point
     *  append letter to the current word
     * else clear selection and start with selected letter as both endpoints with undecided orientation*/
    public static void handleTouch(WordSearchTextView wordSearchTextView) {
        int rowIndex = wordSearchTextView.rowIndex;
        int colIndex = wordSearchTextView.colIndex;
        char letter = wordSearchTextView.getText().charAt(0);

        switch (orientation) {
            case UNDECIDED:
                if (selectedTextViews.isEmpty()) {
                    reassignRootLetter(rowIndex, colIndex, letter, wordSearchTextView);
                } else {
                    setOrientationAndEndpoint(rowIndex, colIndex, letter, wordSearchTextView);
                }
                break;
            case VERTICAL:
                extendSelectionOrtho(startLetter.colIndex, colIndex, startLetter.rowIndex, endLetter.rowIndex, rowIndex, letter, wordSearchTextView);
                break;
            case HORIZONTAL:
                extendSelectionOrtho(startLetter.rowIndex, rowIndex, startLetter.colIndex, endLetter.colIndex, colIndex, letter, wordSearchTextView);
                break;
            case DIAGONAL_POSITIVE:
                extendSelectionDiagonal(1, rowIndex, colIndex, letter, wordSearchTextView);
                break;
            case DIAGONAL_NEGATIVE:
                extendSelectionDiagonal(-1, rowIndex, colIndex, letter, wordSearchTextView);
                break;
        }

//        debugging todo
        Toast.makeText(context,
                "list: " + selectedTextViews.size() + "\ncase:" + orientation + "\nstart: " + startLetter.rowIndex + " " + startLetter.colIndex + " \n" +
                        "end: " + endLetter.rowIndex + " " + endLetter.colIndex + "\nselectedtext: " + selectedText, Toast.LENGTH_SHORT).show();
    }

    /** multiplier is slope of diagonal, should be -1 or 1
     * returns true if diagonal extended, does not need reset
     * returns false when selection is cleared*/
    private static boolean extendSelectionDiagonal(int multiplier, int currRow, int currCol, char letter, WordSearchTextView wordSearchTextView) {
        int startRow = startLetter.rowIndex;
        int startCol = startLetter.colIndex;
        int endCol = endLetter.colIndex;
            if (startRow + multiplier * (currCol - startCol) == currRow) {
//                on the correct diagonal
                if (currCol >= startCol && currCol <= endCol) {
//                    nothing to be done, already selected
                    return true;
                } else if (currCol == startCol - 1) {
                    // becomes starting thing
                    startLetter.update(currRow, currCol);
                    selectedText = selectedText.concat(String.valueOf(letter));
                } else if (currCol == endCol + 1) {
//                becomes ending thing
                    endLetter.update(currRow, currCol);
                    selectedText = String.valueOf(letter).concat(selectedText);
                } else {
//                    clear selection
                    reassignRootLetter(currRow, currCol, letter, wordSearchTextView);
                    return false;
                }
                //        indicate selection (will always be selected)
                wordSearchTextView.setBackgroundColor(Color.YELLOW);
                selectedTextViews.add(wordSearchTextView);
                return true;
            }
        reassignRootLetter(currRow, currCol, letter, wordSearchTextView);
        return false;
    }

    /** if given index is adjacent to the existing letter, sets orientation and appropriate endpoint and returns true
     * otherwise, sets endpoints to the newly selected index and returns false
     * called when selecting the second letter*/
    private static boolean setOrientationAndEndpoint(int rowIndex, int colIndex, char letter, WordSearchTextView wordSearchTextView) {
        int otherRowIndex = startLetter.rowIndex;
        int otherColIndex = startLetter.colIndex;
        if (rowIndex == otherRowIndex && colIndex == otherColIndex) {
//            selected same thing, nothing need be done
            return true;
        } else if (rowIndex == otherRowIndex+1 && colIndex == otherColIndex) {
//                        bottom
            endLetter.update(rowIndex, colIndex);
            orientation = Orientation.VERTICAL;
            selectedText = selectedText.concat(String.valueOf(letter));
        } else if (rowIndex == otherRowIndex-1 && colIndex == otherColIndex) {
//                        top
            startLetter.update(rowIndex, colIndex);
            orientation = Orientation.VERTICAL;
            selectedText = String.valueOf(letter).concat(selectedText);
        } else if (rowIndex == otherRowIndex && colIndex == otherColIndex+1) {
//                        right
            endLetter.update(rowIndex, colIndex);
            orientation = Orientation.HORIZONTAL;
            selectedText = selectedText.concat(String.valueOf(letter));
        } else if (rowIndex == otherRowIndex && colIndex == otherColIndex-1) {
//                        left
            orientation = Orientation.HORIZONTAL;
            startLetter.update(rowIndex, colIndex);
            selectedText = String.valueOf(letter).concat(selectedText);
        } else if (rowIndex == otherRowIndex-1 && colIndex == otherColIndex-1) {
//            top left
            orientation = Orientation.DIAGONAL_NEGATIVE;
            startLetter.update(rowIndex, colIndex);
            selectedText = String.valueOf(letter).concat(selectedText);
        } else if (rowIndex == otherRowIndex-1 && colIndex == otherColIndex+1) {
//            top right
            orientation = Orientation.DIAGONAL_POSITIVE;
            endLetter.update(rowIndex, colIndex);
            selectedText = selectedText.concat(String.valueOf(letter));
        } else if (rowIndex == otherRowIndex+1 && colIndex == otherColIndex-1) {
//            bottom left
            orientation = Orientation.DIAGONAL_POSITIVE;
            startLetter.update(rowIndex, colIndex);
            selectedText = String.valueOf(letter).concat(selectedText);
        } else if (rowIndex == otherRowIndex+1 && colIndex == otherColIndex+1) {
//            bottom right
            orientation = Orientation.DIAGONAL_NEGATIVE;
            endLetter.update(rowIndex, colIndex);
            selectedText = selectedText.concat(String.valueOf(letter));
        } else {
//            if not adjacent
            reassignRootLetter(rowIndex, colIndex, letter, wordSearchTextView);
            return false;
        }
        //        indicate selection (will always be selected)
        wordSearchTextView.setBackgroundColor(Color.YELLOW);
        selectedTextViews.add(wordSearchTextView);
        return true;
    }

    private static void reassignRootLetter(int rowIndex, int colIndex, char letter, WordSearchTextView selectedView) {

        //        selection indication cleared as needed
        Toast.makeText(context, "reset", Toast.LENGTH_SHORT).show();
        orientation = Orientation.UNDECIDED;
        for (TextView tv :
                selectedTextViews) {
            tv.setBackgroundColor(Color.TRANSPARENT);
        }
        selectedTextViews.clear();

        startLetter.update(rowIndex, colIndex);
        endLetter.update(rowIndex, colIndex);
        selectedText = String.valueOf(letter);

        //        indicate selection (will always be selected)
        selectedView.setBackgroundColor(Color.YELLOW);
        selectedTextViews.add(selectedView);

    }

    /** expected and actual are to make sure selections are linear
     * returns true if selection successfully extended*/
    private static boolean extendSelectionOrtho(int expected, int actual, int startIndex, int endIndex, int actualIndex, char letter, WordSearchTextView wordSearchTextView) {
//        Toast.makeText(context, expected + actual, Toast.LENGTH_SHORT).show();
        if (expected == actual) {
            if (actualIndex >= startIndex && actualIndex <= endIndex) {
//                select already selected
                return true;
            } else if (actualIndex == startIndex-1) {
//                 means it will be added in front
                updateEndpoint(startLetter, actual, actualIndex);
                selectedText = String.valueOf(letter).concat(selectedText);
                return true;
            } else if (actual == endIndex+1) {
//                 means it will be added in the back
                updateEndpoint(endLetter, actual, actualIndex);
                selectedText = selectedText.concat(String.valueOf(letter));
                return true;
            }
        }
//         otherwise reset
        reassignRootLetter(wordSearchTextView.rowIndex, wordSearchTextView.colIndex, letter, wordSearchTextView);
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
            tempTextView.setText(word.toUpperCase());
            wordBank.addView(tempTextView);
        }
    }

    /** populate table and views associated with each letter*/
    private void initWordSearch(Context context) {
        String[] content = getResources().getStringArray(R.array.test_content);

        for (int m = 0; m < content.length; m++) {
            String row = content[m].toUpperCase();
            TableRow tempTableRow = new TableRow(context);
//            add each character in row as separate textView to tableRow
            for (int n = 0; n < row.length(); n++) {
//                special textview format
                TextView tempTextView = new WordSearchTextView(context, m, n);
                tempTextView.setText(row.substring(n,n+1));
                tempTableRow.addView(tempTextView);
            }
//            add tableRow to TableLayout
            wordSearch.addView(tempTableRow);
        }
    }

//    removes words from wordbank as they are found
    public void submitWord(View view) {
        for (int i = 0; i < wordBank.getChildCount(); i++) {
            TextView textView = (TextView) wordBank.getChildAt(i);
            if (wordsEqual(String.valueOf(textView.getText()), selectedText)) {
                wordBank.removeView(textView);
                break;
            }
        }
        if (wordBank.getChildCount() == 0) {
            wordBank.removeAllViews();
            TextView end = new TextView(context);
            end.setText("Congratulations, You Win!");
        }
    }

//    returns true if the two strings are equal backwards and or forwards
    private boolean wordsEqual(String text, String selectedText) {
        int length = text.length();
        if (length != selectedText.length()) return false;
        if (text.equals(selectedText)) return true;
        for (int i = 0; i < length; i++) {
            if (!text.substring(i, i + 1).equals(selectedText.substring(length - i - 1, length - i))) return false;
        }
        return true;
    }

    private enum Orientation {
        UNDECIDED, HORIZONTAL, VERTICAL, DIAGONAL_POSITIVE, DIAGONAL_NEGATIVE
    }

    private static class Endpoint {
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
