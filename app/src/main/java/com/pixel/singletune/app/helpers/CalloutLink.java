package com.pixel.singletune.app.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pixel.singletune.app.R;

/**
 * Created by mrsmith on 5/30/14.
 */
public class CalloutLink extends ClickableSpan {
    Context mContext;

    public CalloutLink(Context context){
        super();
        mContext = context;
    }

    @Override
    public void updateDrawState(TextPaint ds){
        ds.setARGB(255, 51, 51, 51);
        ds.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public void onClick(View widget){
        TextView textView = (TextView) widget;
        Spanned spanned = (Spanned) textView.getText();
        int start = spanned.getSpanStart(this);
        int end = spanned.getSpanEnd(this);
        String theWord = spanned.subSequence(start + 1, end).toString();

        // Todo: This is where you replace with Intent
        Toast.makeText(mContext, String.format(mContext.getString(R.string.callout_toast_message), theWord), Toast.LENGTH_SHORT).show();
    }

}
