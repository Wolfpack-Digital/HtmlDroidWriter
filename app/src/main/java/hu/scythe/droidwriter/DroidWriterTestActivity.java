package hu.scythe.droidwriter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import co.zipperstudios.supporthtml.SupportHtml;
import hu.scythe.droidwriter.customEditText.HtmlAssetsImageGetter;

import static android.text.Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL;

public class DroidWriterTestActivity extends Activity {

    private DroidWriterEditText dwEdit;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ToggleButton boldToggle = (ToggleButton) findViewById(R.id.BoldButton);
        ToggleButton italicsToggle = (ToggleButton) findViewById(R.id.ItalicsButton);
        ToggleButton underlinedToggle = (ToggleButton) findViewById(R.id.UnderlineButton);

        View coolButton = findViewById(R.id.CoolButton);
        View cryButton = findViewById(R.id.CryButton);

        Button clearButton = (Button) findViewById(R.id.ClearButton);

        dwEdit = (DroidWriterEditText) findViewById(R.id.DwEdit);
        dwEdit.setImageGetter(new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = null;

                try {
                    if (source.equals("smiley_cool.gif")) {
                        drawable = getResources().getDrawable(R.drawable.smiley_cool);
                    } else if (source.equals("smiley_cry.gif")) {
                        drawable = getResources().getDrawable(R.drawable.smiley_cry);
                    } else {
                        drawable = null;
                    }

                    // Important
                    if (drawable != null) {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    }
                } catch (Exception e) {
                    Log.e("DroidWriterTestActivity", "Failed to load inline image!");
                }
                return drawable;
            }
        });
        dwEdit.setSingleLine(false);
        dwEdit.setMinLines(10);
        dwEdit.setBoldToggleButton(boldToggle);
        dwEdit.setItalicsToggleButton(italicsToggle);
        dwEdit.setUnderlineToggleButton(underlinedToggle);

        dwEdit.setImageInsertButton(coolButton, "smiley_cool.gif");
        dwEdit.setImageInsertButton(cryButton, "smiley_cry.gif");

        dwEdit.setClearButton(clearButton);

//		dwEdit.setHtml("<p><i><b><u>everythingis good very good</u></b></i></p>",
//		dwEdit.setHtml("<p><i><b><u>everythingis good very good</u></b></i></p>",
        dwEdit.setHtml("<p>Decision <b>first line</b></p><p><b>Bold format we like </b>like <b>like</b> its nothing <u>besides underlines </u>this is hard</p><p><i>Italic only here</i></p><p><u>underline</u></p><p><i><u>underlineItalic</u></i></p><p><i><b><u>everythingis good very good</u></b></i></p>",
//        dwEdit.setHtml("<p>simple <b>bold </b><b><i>bolditalic </i></b><b><i><u>bolditalicunderline</u></i></b></p>",
//        dwEdit.setHtml("<p><u><b>first </b></u><i><b>second </b></i></p>",
//		dwEdit.setHtml("<p>Decision <b>first line</b></p>",
//        dwEdit.setHtml("<p><u><i><b>initial</b></i></u></p>",
                new HtmlAssetsImageGetter(dwEdit));
//		dwEdit.setText(fromHtml("<p>Decision <b>first line</b></p><p><b>Bold format</b></p><p><i>Italic only here</i></p><p><u>underline</u></p><p><i><u>underlineItalic</u></i></p><p><i><b><u>everything</u></b></i></p>"));

//        SpannableString spannableString = new SpannableString("bold");
//        spannableString.setSpan(new StyleSpan(BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        dwEdit.setText(spannableString);
    }

    @Override
    protected void onPause() {
        super.onPause();

        String htmlText = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmlText = Html.toHtml(dwEdit.getSpannedText(), TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        } else {
            htmlText = SupportHtml.toHtml(dwEdit.getSpannedText(), SupportHtml.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        }

        dwEdit.setHtml(htmlText,
                new HtmlAssetsImageGetter(dwEdit));

//        dwEdit.setText(hu.scythe.droidwriter.SupportHtml.fromHtml(htmlText, hu.scythe.droidwriter.SupportHtml.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH));
//        Log.d("VLAD", "onPause: " + htmlText);
        htmlText = htmlText.replaceAll("(<\\w+)[^>]*(>)", "$1$2");
        Log.d("VLAD", "onPause: After changes for styles and data " + htmlText);
    }
}