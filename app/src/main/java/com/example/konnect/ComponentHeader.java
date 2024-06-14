package com.example.konnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ComponentHeader extends LinearLayout {

    private TextView headerText;
    private TextView groupNameText;
    private ImageButton exitButton;
    private SharedPreferences sharedPreferences;

    public ComponentHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setHeaderText(String text) {
        headerText.setText(text);
    }

    private void init(Context context, AttributeSet attrs) {
        sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);

        LayoutInflater.from(context).inflate(R.layout.component_header, this, true);

        headerText = findViewById(R.id.header_text);
        groupNameText = findViewById(R.id.group_name_text);
        exitButton = findViewById(R.id.header_home_button);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ComponentHeader, 0, 0);

        try {
            String headerTextString = a.getString(R.styleable.ComponentHeader_headerText);
            boolean showExitButton = a.getBoolean(R.styleable.ComponentHeader_showExitButton, false);
            headerText.setText(headerTextString);
            exitButton.setVisibility(showExitButton ? VISIBLE : GONE);
        } finally {
            a.recycle();
        }
    }

    public void setGroupName(String groupName) {
        if (groupName != null && !groupName.isEmpty()) {
            groupNameText.setText(groupName);
            groupNameText.setVisibility(VISIBLE);
        } else {
            groupNameText.setVisibility(GONE);
        }
    }
}
