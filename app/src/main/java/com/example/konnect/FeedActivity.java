package com.example.konnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class FeedActivity extends AppCompatActivity {

    private Button navFeed, navGroups, navNotifications;
    private LinearLayout feedSection, groupsSection, notificationsSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navFeed = findViewById(R.id.nav_feed);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);

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
}
