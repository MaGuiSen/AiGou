package com.ma.aigou.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BaseFragment extends Fragment{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBase();
    }

    private void initBase(){

    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput(){
        // 隐藏软键盘
        //val 不可变，var可变
        View view = getActivity().getWindow().peekDecorView();
        if (view != null){
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void  showSoftInput(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

}