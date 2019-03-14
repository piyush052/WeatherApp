package com.piyush052.weatherapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.Window;

import com.piyush052.weatherapp.R;

public class Loader extends Dialog {

    private Context context;

    public Loader(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loader_layout);
        getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.loaderBackground)));

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
