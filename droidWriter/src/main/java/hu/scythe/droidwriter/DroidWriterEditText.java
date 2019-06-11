package hu.scythe.droidwriter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import org.sufficientlysecure.htmltextview.HtmlEditText;


public class DroidWriterEditText extends HtmlEditText {

    // Log tag
    public static final String TAG = "DroidWriter";

    // Style constants
    private static final int STYLE_BOLD = 0;
    private static final int STYLE_ITALIC = 1;
    private static final int STYLE_UNDERLINED = 2;

    // Optional styling button references
    private ToggleButton boldToggle;
    private ToggleButton italicsToggle;
    private ToggleButton underlineToggle;

    // SupportHtml image getter that handles the loading of inline images
    private Html.ImageGetter imageGetter;

    public DroidWriterEditText(Context context) {
        super(context);
        initialize();
    }

    public DroidWriterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DroidWriterEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        // Add a default imageGetter
        imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                return null;
            }
        };

        // Add TextWatcher that reacts to text changes and applies the selected
        // styles
        this.addTextChangedListener(new DWTextWatcher());
    }

    /**
     * When the user selects a section of the text, this method is used to
     * toggle the defined style on it. If the selected text already has the
     * style applied, we remove it, otherwise we apply it.
     *
     * @param style The styles that should be toggled on the selected text.
     */
    private void toggleStyle(int style) {
        // Gets the current cursor position, or the starting position of the
        // selection
        int selectionStart = this.getSelectionStart();

        // Gets the current cursor position, or the end position of the
        // selection
        // Note: The end can be smaller than the start
        int selectionEnd = this.getSelectionEnd();

        // Reverse if the case is what's noted above
        if (selectionStart > selectionEnd) {
            int temp = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = temp;
        }

        // The selectionEnd is only greater then the selectionStart position
        // when the user selected a section of the text. Otherwise, the 2
        // variables
        // should be equal (the cursor position).
        if (selectionEnd > selectionStart) {
            Spannable str = this.getText();
            boolean exists = false;
            StyleSpan[] styleSpans;

            switch (style) {
                case STYLE_BOLD:
                    styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                    // If the selected text-part already has BOLD style on it, then
                    // we need to disable it
                    for (int i = 0; i < styleSpans.length; i++) {
                        if (styleSpans[i].getStyle() == android.graphics.Typeface.BOLD) {
                            str.removeSpan(styleSpans[i]);
                            exists = true;
                        }
                    }

                    // Else we set BOLD style on it
                    if (!exists) {
                        str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), selectionStart, selectionEnd,
                                getSpanFlag(getText(), selectionStart));
                    }

                    this.setSelection(selectionStart, selectionEnd);
                    break;
                case STYLE_ITALIC:
                    styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                    // If the selected text-part already has ITALIC style on it,
                    // then we need to disable it
                    for (int i = 0; i < styleSpans.length; i++) {
                        if (styleSpans[i].getStyle() == android.graphics.Typeface.ITALIC) {
                            str.removeSpan(styleSpans[i]);
                            exists = true;
                        }
                    }

                    // Else we set ITALIC style on it
                    if (!exists) {
                        str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), selectionStart, selectionEnd,
                                getSpanFlag(getText(), selectionStart));
                    }

                    this.setSelection(selectionStart, selectionEnd);
                    break;
                case STYLE_UNDERLINED:
                    UnderlineSpan[] underSpan = str.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);

                    // If the selected text-part already has UNDERLINE style on it,
                    // then we need to disable it
                    for (int i = 0; i < underSpan.length; i++) {
                        str.removeSpan(underSpan[i]);
                        exists = true;
                    }

                    // Else we set UNDERLINE style on it
                    if (!exists) {
                        str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, getSpanFlag(getText(), selectionStart));
                    }

                    this.setSelection(selectionStart, selectionEnd);
                    break;
            }
        }
    }

    /**
     * This method makes sure that the optional style toggle buttons update
     * their state correctly when the user moves the cursor around the EditText,
     * or when the user selects sections of the text.
     */
    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        boolean boldExists = false;
        boolean italicsExists = false;
        boolean underlinedExists = false;

        // If the user only placed the cursor around
        if (selStart > 0 && selStart == selEnd) {
            CharacterStyle[] styleSpans = this.getText().getSpans(selStart - 1, selStart, CharacterStyle.class);

            for (int i = 0; i < styleSpans.length; i++) {
                if (styleSpans[i] instanceof StyleSpan) {
                    if (((StyleSpan) styleSpans[i]).getStyle() == android.graphics.Typeface.BOLD) {
                        boldExists = true;
                    } else if (((StyleSpan) styleSpans[i]).getStyle() == android.graphics.Typeface.ITALIC) {
                        italicsExists = true;
                    } else if (((StyleSpan) styleSpans[i]).getStyle() == android.graphics.Typeface.BOLD_ITALIC) {
                        italicsExists = true;
                        boldExists = true;
                    }
                } else if (styleSpans[i] instanceof UnderlineSpan) {
                    underlinedExists = true;
                }
            }
        }

        // Else if the user selected multiple characters
        else {
            CharacterStyle[] styleSpans = this.getText().getSpans(selStart, selEnd, CharacterStyle.class);

            for (int i = 0; i < styleSpans.length; i++) {
                if (styleSpans[i] instanceof StyleSpan) {
                    if (((StyleSpan) styleSpans[i]).getStyle() == android.graphics.Typeface.BOLD) {
                        if (this.getText().getSpanStart(styleSpans[i]) <= selStart
                                && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                            boldExists = true;
                        }
                    } else if (((StyleSpan) styleSpans[i]).getStyle() == android.graphics.Typeface.ITALIC) {
                        if (this.getText().getSpanStart(styleSpans[i]) <= selStart
                                && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                            italicsExists = true;
                        }
                    } else if (((StyleSpan) styleSpans[i]).getStyle() == android.graphics.Typeface.BOLD_ITALIC) {
                        if (this.getText().getSpanStart(styleSpans[i]) <= selStart
                                && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                            italicsExists = true;
                            boldExists = true;
                        }
                    }
                } else if (styleSpans[i] instanceof UnderlineSpan) {
                    if (this.getText().getSpanStart(styleSpans[i]) <= selStart
                            && this.getText().getSpanEnd(styleSpans[i]) >= selEnd) {
                        underlinedExists = true;
                    }
                }
            }
        }

        // Display the format settings
        if (boldToggle != null) {
            if (boldExists)
                boldToggle.setChecked(true);
            else
                boldToggle.setChecked(false);
        }

        if (italicsToggle != null) {
            if (italicsExists)
                italicsToggle.setChecked(true);
            else
                italicsToggle.setChecked(false);
        }

        if (underlineToggle != null) {
            if (underlinedExists)
                underlineToggle.setChecked(true);
            else
                underlineToggle.setChecked(false);
        }
    }

    // Get and set Spanned, styled text
    public Spanned getSpannedText() {
        return this.getText();
    }

    public void setSpannedText(Spanned text) {
        this.setText(text);
    }

    // Get and set simple text as simple strings
    public String getStringText() {
        return this.getText().toString();
    }

    public void setStringText(String text) {
        this.setText(text);
    }

    // Get and set styled HTML text
    public String getTextHTML() {
        return Html.toHtml(this.getText());
    }

    public void setTextHTML(String text) {
        this.setText(Html.fromHtml(text, imageGetter, null));
    }

    // Set the default image getter that handles the loading of inline images
    public void setImageGetter(Html.ImageGetter imageGetter) {
        this.imageGetter = imageGetter;
    }

    // Style toggle button setters
    public void setBoldToggleButton(ToggleButton button) {
        boldToggle = button;

        boldToggle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                toggleStyle(STYLE_BOLD);
            }
        });
    }

    public void setItalicsToggleButton(ToggleButton button) {
        italicsToggle = button;

        italicsToggle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                toggleStyle(STYLE_ITALIC);
            }
        });
    }

    public void setUnderlineToggleButton(ToggleButton button) {
        underlineToggle = button;

        underlineToggle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                toggleStyle(STYLE_UNDERLINED);
            }
        });
    }

    public void setImageInsertButton(View button, final String imageResource) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Selection.getSelectionStart(DroidWriterEditText.this.getText());

                Spanned e = Html.fromHtml("<img src=\"" + imageResource + "\">", imageGetter, null);

                DroidWriterEditText.this.getText().insert(position, e);
            }
        });
    }

    public void setClearButton(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DroidWriterEditText.this.setText("");
            }
        });
    }

    private int getSpanFlag(Editable editable, int beginIndex) {
//        if (beginIndex >= 0 && beginIndex == 0 || editable.charAt(beginIndex - 1) == '\n')
//            return Spannable.SPAN_INCLUSIVE_INCLUSIVE;
        return Spannable.SPAN_EXCLUSIVE_INCLUSIVE;
    }

    private class DWTextWatcher implements TextWatcher {
        private RelativeSizeSpan span;
        private Spannable spannable;

        @Override
        public void afterTextChanged(Editable editable) {
//            int position = Selection.getSelectionStart(DroidWriterEditText.this.getText());
//            if (position < 0) {
//                position = 0;
//            }
//
//            if (position > 0) {
//                CharacterStyle[] appliedStyles = (CharacterStyle[])editable.getSpans(position - 1, position, CharacterStyle.class);
//                StyleSpan currentBoldSpan = null;
//                StyleSpan currentItalicSpan = null;
//                UnderlineSpan currentUnderlineSpan = null;
//
//                int underLineStart;
//                for(underLineStart = 0; underLineStart < appliedStyles.length; ++underLineStart) {
//                    if (appliedStyles[underLineStart] instanceof StyleSpan) {
//                        if (((StyleSpan)appliedStyles[underLineStart]).getStyle() == 1) {
//                            currentBoldSpan = (StyleSpan)appliedStyles[underLineStart];
//                        } else if (((StyleSpan)appliedStyles[underLineStart]).getStyle() == 2) {
//                            currentItalicSpan = (StyleSpan)appliedStyles[underLineStart];
//                        }
//                    } else if (appliedStyles[underLineStart] instanceof UnderlineSpan) {
//                        currentUnderlineSpan = (UnderlineSpan)appliedStyles[underLineStart];
//                    }
//                }
//
//                int underLineEnd;
//                if (DroidWriterEditText.this.boldToggle != null) {
//                    if (DroidWriterEditText.this.boldToggle.isChecked() && currentBoldSpan == null) {
//                        editable.setSpan(new StyleSpan(1), position - 1, position, 34);
//                    } else if (!DroidWriterEditText.this.boldToggle.isChecked() && currentBoldSpan != null) {
//                        underLineStart = editable.getSpanStart(currentBoldSpan);
//                        underLineEnd = editable.getSpanEnd(currentBoldSpan);
//                        editable.removeSpan(currentBoldSpan);
//                        if (underLineStart <= position - 1) {
//                            editable.setSpan(new StyleSpan(1), underLineStart, position - 1, 34);
//                        }
//
//                        if (underLineEnd > position) {
//                            editable.setSpan(new StyleSpan(1), position, underLineEnd, 34);
//                        }
//                    }
//                }
//
//                if (DroidWriterEditText.this.italicsToggle != null && DroidWriterEditText.this.italicsToggle.isChecked() && currentItalicSpan == null) {
//                    editable.setSpan(new StyleSpan(2), position - 1, position, 34);
//                } else if (DroidWriterEditText.this.italicsToggle != null && !DroidWriterEditText.this.italicsToggle.isChecked() && currentItalicSpan != null) {
//                    underLineStart = editable.getSpanStart(currentItalicSpan);
//                    underLineEnd = editable.getSpanEnd(currentItalicSpan);
//                    editable.removeSpan(currentItalicSpan);
//                    if (underLineStart <= position - 1) {
//                        editable.setSpan(new StyleSpan(2), underLineStart, position - 1, 34);
//                    }
//
//                    if (underLineEnd > position) {
//                        editable.setSpan(new StyleSpan(2), position, underLineEnd, 34);
//                    }
//                }
//
//                if (DroidWriterEditText.this.underlineToggle != null && DroidWriterEditText.this.underlineToggle.isChecked() && currentUnderlineSpan == null) {
//                    editable.setSpan(new UnderlineSpan(), position - 1, position, 34);
//                } else if (DroidWriterEditText.this.underlineToggle != null && !DroidWriterEditText.this.underlineToggle.isChecked() && currentUnderlineSpan != null) {
//                    underLineStart = editable.getSpanStart(currentUnderlineSpan);
//                    underLineEnd = editable.getSpanEnd(currentUnderlineSpan);
//                    editable.removeSpan(currentUnderlineSpan);
//                    if (underLineStart <= position - 1) {
//                        editable.setSpan(new UnderlineSpan(), underLineStart, position - 1, 34);
//                    }
//
//                    if (underLineEnd > position) {
//                        editable.setSpan(new UnderlineSpan(), position, underLineEnd, 34);
//                    }
//                }
//            }
            int beginIndex = spannable.getSpanStart(span);
            int endIndex = spannable.getSpanEnd(span);

            // Cleanup
            spannable.removeSpan(span);
            span = null;
            spannable = null;

            boolean characterDeleted = beginIndex == endIndex;


            StyleSpan currentBoldSpan = null;
            StyleSpan currentItalicSpan = null;
            UnderlineSpan currentUnderlineSpan = null;

            CharacterStyle[] appliedStyles = editable.getSpans(beginIndex, endIndex, CharacterStyle.class);
            // Look for possible styles already applied to the entered text
            for (CharacterStyle appliedStyle : appliedStyles) {
                if (appliedStyle instanceof StyleSpan) {
                    if (((StyleSpan) appliedStyle).getStyle() == Typeface.BOLD) {
                        // Bold style found
                        StyleSpan potentialSpan = (StyleSpan) appliedStyle;
                        if (characterDeleted && spanIsEmpty(editable, potentialSpan)) {
                            editable.removeSpan(potentialSpan);
                        } else {
                            currentBoldSpan = potentialSpan;
                        }
                    } else if (((StyleSpan) appliedStyle).getStyle() == Typeface.ITALIC) {
                        // Italic style found
                        StyleSpan potentialSpan = (StyleSpan) appliedStyle;
                        if (characterDeleted && spanIsEmpty(editable, potentialSpan)) {
                            editable.removeSpan(potentialSpan);
                        } else {
                            currentItalicSpan = potentialSpan;
                        }
                    }
                } else if (appliedStyle instanceof UnderlineSpan) {
                    // Underlined style found
                    UnderlineSpan potentialSpan = (UnderlineSpan) appliedStyle;
                    if (characterDeleted && spanIsEmpty(editable, potentialSpan)) {
                        editable.removeSpan(potentialSpan);
                    } else {
                        currentUnderlineSpan = potentialSpan;
                    }
                }
            }

            if (characterDeleted) {
                onSelectionChanged(beginIndex, endIndex);
            }

            if (endIndex > 0) {
                // If text is added on the first position on a line and the following text already
                // has the same spans applied append this new text to the existing spans
                if (beginIndex >= 0 && beginIndex == 0 || editable.charAt(beginIndex - 1) == '\n') {

                    boolean createsNewSpans = false;
                    if (boldToggle == null ||
                            (boldToggle.isChecked() == (currentBoldSpan == null))) {
                        createsNewSpans = true;
                    } else if (italicsToggle == null ||
                            (italicsToggle.isChecked() == (currentItalicSpan == null))) {
                        createsNewSpans = true;
                    } else if (underlineToggle == null ||
                            (underlineToggle.isChecked() == (currentUnderlineSpan == null))) {
                        createsNewSpans = true;
                    }

                    if (createsNewSpans) {
                        // Looking for existing spans that match the toggles on the next position
                        CharacterStyle[] stylesInRange = editable.getSpans(beginIndex, endIndex + 1, CharacterStyle.class);
                        StyleSpan inRangeBoldSpan = null;
                        StyleSpan inRangeItalicSpan = null;
                        UnderlineSpan inRangeUnderlineSpan = null;

                        // Look for possible styles on the position next to it
                        for (CharacterStyle aStylesInRange : stylesInRange) {
                            if (aStylesInRange instanceof StyleSpan) {
                                if (((StyleSpan) aStylesInRange).getStyle() == Typeface.BOLD) {
                                    // Bold style found
                                    inRangeBoldSpan = (StyleSpan) aStylesInRange;
                                } else if (((StyleSpan) aStylesInRange).getStyle() == Typeface.ITALIC) {
                                    // Italic style found
                                    inRangeItalicSpan = (StyleSpan) aStylesInRange;
                                }
                            } else if (aStylesInRange instanceof UnderlineSpan) {
                                // Underlined style found
                                inRangeUnderlineSpan = (UnderlineSpan) aStylesInRange;
                            }
                        }

                        // Checking if the spans match the toggles
                        boolean shouldAppendToNextSpan = true;
                        if (boldToggle == null ||
                                (boldToggle.isChecked() == (inRangeBoldSpan == null))) {
                            shouldAppendToNextSpan = false;
                        } else if (italicsToggle == null ||
                                (italicsToggle.isChecked() == (inRangeItalicSpan == null))) {
                            shouldAppendToNextSpan = false;
                        } else if (underlineToggle == null ||
                                (underlineToggle.isChecked() == (inRangeUnderlineSpan == null))) {
                            shouldAppendToNextSpan = false;
                        }

                        // Remove existing spans so new ones are created that include the newly
                        // added text
                        if (shouldAppendToNextSpan) {
                            int endPosition = endIndex;
                            if (inRangeBoldSpan != null) {
                                endPosition = editable.getSpanEnd(inRangeBoldSpan);
                                editable.removeSpan(inRangeBoldSpan);
                            }
                            if (inRangeItalicSpan != null) {
                                endPosition = editable.getSpanEnd(inRangeItalicSpan);
                                editable.removeSpan(inRangeItalicSpan);
                            }
                            if (inRangeUnderlineSpan != null) {
                                endPosition = editable.getSpanEnd(inRangeUnderlineSpan);
                                editable.removeSpan(inRangeUnderlineSpan);
                            }

                            endIndex = endPosition;
                        }
                    }
                }

                // Handle the bold style toggle button if it's present
                if (boldToggle != null) {
                    if (boldToggle.isChecked() && currentBoldSpan == null) {
                        // The user switched the bold style button on and the
                        // character doesn't have any bold
                        // style applied, so we start a new bold style span. The
                        // span is inclusive,
                        // so any new characters entered right after this one
                        // will automatically get this style.
                        editable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), beginIndex, endIndex,
                                getSpanFlag(editable, beginIndex));
                    } else if (!boldToggle.isChecked() && currentBoldSpan != null) {
                        // The user switched the bold style button off and the
                        // character has bold style applied.
                        // We need to remove the old bold style span, and define
                        // a new one that end 1 position right
                        // before the newly entered character.
                        int boldStart = editable.getSpanStart(currentBoldSpan);
                        int boldEnd = editable.getSpanEnd(currentBoldSpan);

                        editable.removeSpan(currentBoldSpan);
                        if (boldStart <= (beginIndex)) {
                            editable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, beginIndex,
                                    getSpanFlag(editable, beginIndex));
                        }

                        // The old bold style span end after the current cursor
                        // position, so we need to define a
                        // second newly created style span too, which begins
                        // after the newly entered character and
                        // ends at the old span's ending position. So we split
                        // the span.
                        if (boldEnd > endIndex) {
                            editable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), endIndex, boldEnd,
                                    getSpanFlag(editable, beginIndex));
                        }
                    }
                }

                // Handling italics and underlined styles is the same as
                // handling bold styles.

                // Handle the italics style toggle button if it's present
                if (italicsToggle != null && italicsToggle.isChecked() && currentItalicSpan == null) {
                    editable.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), beginIndex, endIndex,
                            getSpanFlag(editable, beginIndex));
                } else if (italicsToggle != null && !italicsToggle.isChecked() && currentItalicSpan != null) {
                    int italicStart = editable.getSpanStart(currentItalicSpan);
                    int italicEnd = editable.getSpanEnd(currentItalicSpan);

                    editable.removeSpan(currentItalicSpan);
                    if (italicStart <= (beginIndex)) {
                        editable.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), italicStart, beginIndex,
                                getSpanFlag(editable, beginIndex));
                    }

                    // Split the span
                    if (italicEnd > endIndex) {
                        editable.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), endIndex, italicEnd,
                                getSpanFlag(editable, beginIndex));
                    }
                }

                // Handle the underlined style toggle button if it's present
                if (underlineToggle != null && underlineToggle.isChecked() && currentUnderlineSpan == null) {
                    editable.setSpan(new UnderlineSpan(), beginIndex, endIndex, getSpanFlag(editable, beginIndex));
                } else if (underlineToggle != null && !underlineToggle.isChecked() && currentUnderlineSpan != null) {
                    int underLineStart = editable.getSpanStart(currentUnderlineSpan);
                    int underLineEnd = editable.getSpanEnd(currentUnderlineSpan);

                    editable.removeSpan(currentUnderlineSpan);
                    if (underLineStart <= (beginIndex)) {
                        editable.setSpan(new UnderlineSpan(), underLineStart, beginIndex,
                                getSpanFlag(editable, beginIndex));
                    }

                    // We need to split the span
                    if (underLineEnd > endIndex) {
                        editable.setSpan(new UnderlineSpan(), endIndex, underLineEnd,
                                getSpanFlag(editable, beginIndex));
                    }
                }
            }
        }

        private boolean spanIsEmpty(Editable editable, CharacterStyle styleSpan) {
            int start = editable.getSpanStart(styleSpan);
            int end = editable.getSpanEnd(styleSpan);
            return end - start <= 0;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Unused
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s instanceof Spannable) {
                spannable = (Spannable) s;
            } else {
                spannable = new SpannableString(s); // Fallback it will break the logic
            }

            span = new RelativeSizeSpan(1.0f);
            spannable.setSpan(span, start, start + count, Spanned.SPAN_COMPOSING);
        }
    }
}
