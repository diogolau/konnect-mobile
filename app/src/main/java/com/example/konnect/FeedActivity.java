package com.example.konnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FeedActivity extends AppCompatActivity {

    private Button navFeed, navGroups, navNotifications;
    private LinearLayout feedSection, groupsSection, notificationsSection;
    private ImageButton homeButton;
    private Button postButton;
    private EditText contentInput;
    private LinearLayout feedListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navFeed = findViewById(R.id.nav_feed);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);
        homeButton = findViewById(R.id.header_home_button);
        postButton = findViewById(R.id.post_button);
        contentInput = findViewById(R.id.content_input);
        feedListContainer = findViewById(R.id.feed_list_container);

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
                finish(); // Go back to the previous activity
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentInput.getText().toString();
                if (!content.isEmpty()) {
                    addNewPost(content);
                    contentInput.setText(""); // Clear the input field
                }
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

    private void addNewPost(String content) {
        LinearLayout newPost = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between posts
        newPost.setLayoutParams(layoutParams);
        newPost.setOrientation(LinearLayout.VERTICAL);
        newPost.setPadding(32, 40, 32, 40);
        newPost.setElevation(4);
        newPost.setBackgroundResource(R.drawable.rounded_border);

        TextView userName = new TextView(this);
        userName.setText("userName");
        userName.setTextSize(16);
        userName.setTextColor(getResources().getColor(R.color.black));

        TextView postContent = new TextView(this);
        postContent.setText(content);
        postContent.setTextSize(16);
        postContent.setTextColor(getResources().getColor(R.color.gray));

        LinearLayout likeDislikeLayout = new LinearLayout(this);
        likeDislikeLayout.setOrientation(LinearLayout.HORIZONTAL);
        likeDislikeLayout.setPadding(0, 16, 0, 0);
        likeDislikeLayout.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align the layout

        ImageView likeIcon = new ImageView(this);
        likeIcon.setImageResource(R.drawable.konnect_like);
        LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        iconLayoutParams.setMargins(0, 0, 16, 0); // Increased space between icon and count
        likeIcon.setLayoutParams(iconLayoutParams);

        TextView likeCount = new TextView(this);
        likeCount.setText("4");
        likeCount.setTextSize(16);
        likeCount.setTextColor(getResources().getColor(R.color.blue)); // Change to blue color
        LinearLayout.LayoutParams likeCountLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        likeCountLayoutParams.setMargins(0, 0, 32, 0); // Increased space between like and dislike
        likeCount.setLayoutParams(likeCountLayoutParams);
        likeCount.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align the text

        ImageView dislikeIcon = new ImageView(this);
        dislikeIcon.setImageResource(R.drawable.konnect_dislike);
        dislikeIcon.setLayoutParams(iconLayoutParams);

        TextView dislikeCount = new TextView(this);
        dislikeCount.setText("12");
        dislikeCount.setTextSize(16);
        dislikeCount.setTextColor(getResources().getColor(R.color.blue)); // Change to blue color
        dislikeCount.setLayoutParams(iconLayoutParams);
        dislikeCount.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align the text

        likeDislikeLayout.addView(likeIcon);
        likeDislikeLayout.addView(likeCount);
        likeDislikeLayout.addView(dislikeIcon);
        likeDislikeLayout.addView(dislikeCount);

        newPost.addView(userName);
        newPost.addView(postContent);
        newPost.addView(likeDislikeLayout);
        feedListContainer.addView(newPost, 0); // Add the new post at the top of the list
    }
}
