package com.example.konnect;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ComponentHeader extends LinearLayout {

    public ComponentHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_header, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ComponentHeader, 0, 0);

        String headerText;
        boolean showExitButton;

        try {
            headerText = a.getString(R.styleable.ComponentHeader_headerText);
            showExitButton = a.getBoolean(R.styleable.ComponentHeader_showExitButton, false);
        } finally {
            a.recycle();
        }

        TextView textView = findViewById(R.id.header_text);
        ImageButton exitButton = findViewById(R.id.header_home_button);

        if (headerText != null) {
            textView.setText(headerText);
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }

        if (showExitButton) {
            exitButton.setVisibility(VISIBLE);
            exitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });
        } else {
            exitButton.setVisibility(GONE);
        }
    }
}
