package com.ajb.merchants.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class EdittextFilterTextWatcher implements TextWatcher {

    private EditText firstEdittext;
    private EditText secondEdittext;
    private Button enableButton;

    /**
     * @param firstEdittext  相对自身的第一个Edittext
     * @param secondEdittext 相对自身的第二个Edittext
     * @param enableButton   需要操作的按钮
     */
    public EdittextFilterTextWatcher(EditText firstEdittext, EditText secondEdittext, Button enableButton) {
        this.firstEdittext = firstEdittext;
        this.secondEdittext = secondEdittext;
        this.enableButton = enableButton;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            if (TextUtils.isEmpty(firstEdittext.getText().toString().trim())) {
                enableButton.setEnabled(false);
                return;
            }
            if (TextUtils.isEmpty(secondEdittext.getText().toString().trim())) {
                enableButton.setEnabled(false);
                return;
            }
            enableButton.setEnabled(true);
        } else {
            enableButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}