package com.example.konnect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

public class FeedActivity extends AppCompatActivity {

    private Button navFeed, navGroups, navNotifications;
    private LinearLayout feedSection, groupsSection, notificationsSection;
    private ImageButton homeButton;
    private Button grauMinButton, grauMaxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navFeed = findViewById(R.id.nav_feed);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);
        homeButton = findViewById(R.id.header_home_button);
        grauMinButton = findViewById(R.id.grau_min_button);
        grauMaxButton = findViewById(R.id.grau_max_button);

        feedSection = findViewById(R.id.feed_section);
        groupsSection = findViewById(R.id.groups_section);
        notificationsSection = findViewById(R.id.notifications_section);

        navFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navFeed);
                showSection(feedSection);
            }
        });

        navGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navGroups);
                showSection(groupsSection);
            }
        });

        navNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navNotifications);
                showSection(notificationsSection);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        grauMinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog(grauMinButton, "Select Grau Mínimo");
            }
        });

        grauMaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog(grauMaxButton, "Select Grau Máximo");
            }
        });

        // Default to Feed tab
        setActiveTab(navFeed);
        showSection(feedSection);
    }

    private void setActiveTab(Button activeButton) {
        navFeed.setTypeface(null, navFeed == activeButton ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        navGroups.setTypeface(null, navGroups == activeButton ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        navNotifications.setTypeface(null, navNotifications == activeButton ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    private void showSection(LinearLayout section) {
        feedSection.setVisibility(section == feedSection ? View.VISIBLE : View.GONE);
        groupsSection.setVisibility(section == groupsSection ? View.VISIBLE : View.GONE);
        notificationsSection.setVisibility(section == notificationsSection ? View.VISIBLE : View.GONE);
    }

    private void showNumberPickerDialog(final Button button, String title) {
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button.setText(String.valueOf(numberPicker.getValue()));
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
