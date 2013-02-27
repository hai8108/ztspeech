package com.ztspeech.unisay.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ztspeech.unisay.utils.LogInfo;

public class ZTSSpinner extends LinearLayout implements OnClickListener, android.content.DialogInterface.OnClickListener {
    Button textButton;
    String selectedValue;
    String itemValue[];
    String itemString[];
    AlertDialog dialog;

    public ZTSSpinner(Context context, AttributeSet set) {
        super(context, set);
        textButton = new Button(getContext());
        textButton.setText("TlcySpinner");
        textButton.setBackgroundResource(android.R.drawable.btn_dropdown);
        addView(textButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    public ZTSSpinner(Context context) {
        super(context);
        textButton = new Button(getContext());
        textButton.setText("TlcySpinner");
        textButton.setBackgroundResource(android.R.drawable.btn_dropdown);
        addView(textButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    public void init(String[] itemStrings, String[] itemValues, int defaultItem) {
        itemString = itemStrings;
        itemValue = itemValues;
        textButton.setText(itemStrings[defaultItem]);
        textButton.setOnClickListener(this);
        dialog = new AlertDialog.Builder(getContext()).setSingleChoiceItems(itemStrings, defaultItem, this).create();
        selectedValue = itemValues[defaultItem];
    }

    public void setButton(String button) {
        dialog.setButton(button, this);
    }

    public void SetButton(String button, android.content.DialogInterface.OnClickListener listener) {
        dialog.setButton(button, listener);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which >= 0 && which < itemString.length) {
            textButton.setText(itemString[which]);
            selectedValue = itemValue[which];
            textButton.postInvalidate();
        }
        dialog.dismiss();
        LogInfo.LogOut("TlcySpinner.DialogInterface.onClick");

    }

    @Override
    public void onClick(View v) {
        dialog.show();
        LogInfo.LogOut("TlcySpinner.button.onClick");
    }

    public String getSelectedValue() {
        return selectedValue;
    }
}
