package com.test.ad.demo.util;

import android.widget.TextView;

public class ViewUtil {

    public static void printLog(TextView view, String log) {
        if (view == null) {
            return;
        }
        view.setText(view.getText().toString() + log + "\n");
        //let text view to move to the last line.
        int offset = view.getLineCount() * view.getLineHeight();
        if (offset > view.getHeight()) {
            view.scrollTo(0, offset - view.getHeight());
        }
    }

}
