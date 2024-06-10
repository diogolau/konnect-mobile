package com.example.konnect;

import static com.example.konnect.NetworkUtils.makeGetRequest;
import static com.example.konnect.NetworkUtils.makePostRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class FeedActivity extends AppCompatActivity {

    private Button navFeed, navGroups, navNotifications;
    private LinearLayout feedSection, groupsSection, notificationsSection, grauButtonsContainer;
    private ImageButton homeButton;
    private Button postButton;
    private EditText postContent;
    private LinearLayout feedListContainer;
    private ComponentHeader header;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navFeed = findViewById(R.id.nav_feed);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);
        homeButton = findViewById(R.id.header_home_button);
        postButton = findViewById(R.id.post_button);
        postContent = findViewById(R.id.content_input);
        feedListContainer = findViewById(R.id.feed_list_container);
        grauButtonsContainer = findViewById(R.id.grau_buttons_container);
        header = findViewById(R.id.header);

        feedSection = findViewById(R.id.feed_section);
        groupsSection = findViewById(R.id.groups_section);
        notificationsSection = findViewById(R.id.notifications_section);

        String url = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?minDepth=%s&maxDepth=%s&userId=%s&groupId=%s", "1", "10", "3687c3a4-47c1-45fe-afc3-cf215d0bef91", "null");

        String response = makeGetRequest(url);
        Log.i("abloble", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            JSONArray jsonArray = new JSONArray(message);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject postObject = jsonArray.getJSONObject(i);
                String id = postObject.getString("id");
                String username = postObject.getString("username");
                String content = postObject.getString("content");
                String userId = postObject.getString("userId");
                String upvotes = postObject.getString("upvotes");
                String downvotes = postObject.getString("downvotes");

                addNewPost(content, username, upvotes, downvotes, id);
            }
        } catch (Exception e) {
            Log.i("Erro listando posts", e.toString());
        }

        navFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navFeed);
                showSection(feedSection);
                grauButtonsContainer.setVisibility(View.VISIBLE);
                header.setGroupName(null);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onClick(View v) {
                String postMessage = postContent.getText().toString();

                if (postMessage.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Escreva a sua mensagem", Toast.LENGTH_LONG).show();
                    return;
                }

                String url = "http://10.0.2.2:8080/server_war_exploded/api/post" ;
                String body = String.format("{\"content\":\"%s\", \"userId\":\"%s\", \"groupId\":\"%s\"}", postMessage, "4c92f976-b7ab-4f67-8335-03b0695ead6f", "null");

                String response = makePostRequest(url, body);
                Log.i("response", response);

                if (!response.contains("__error__")) {
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(getBaseContext(), "Erro no post", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Default to Feed tab
        setActiveTab(navFeed);
        showSection(feedSection);

        findViewById(R.id.group_box_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedWithGroup("Shining Star");
            }
        });

        findViewById(R.id.group_box_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedWithGroup("Beaty queens");
            }
        });

        findViewById(R.id.group_box_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedWithGroup("Angry uncles");
            }
        });

        // Grau Minimo and Grau Maximo Buttons
        findViewById(R.id.grau_min_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog((Button) v, "Grau Mínimo");
            }
        });

        findViewById(R.id.grau_max_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog((Button) v, "Grau Máximo");
            }
        });
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

    private void addNewPost(String content, String userNameContent, String upvotesContent, String downvotesContent, String postId) {
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
        userName.setText(userNameContent);
        userName.setTextSize(18);
        userName.setTextColor(getResources().getColor(R.color.black));

        TextView postContent = new TextView(this);
        postContent.setText(content);
        postContent.setTextSize(16);
        postContent.setTextColor(getResources().getColor(R.color.gray));
        LinearLayout.LayoutParams postContentPadding = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        postContentPadding.setMargins(0, 12, 0, 4);
        postContent.setLayoutParams(postContentPadding);

        LinearLayout likeDislikeLayout = new LinearLayout(this);
        likeDislikeLayout.setOrientation(LinearLayout.HORIZONTAL);
        likeDislikeLayout.setPadding(0, 24, 0, 0);
        likeDislikeLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        ImageView likeIcon = new ImageView(this);
        likeIcon.setImageResource(R.drawable.konnect_like);
        LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        iconLayoutParams.setMargins(0, 0, 18, 0);
        likeIcon.setLayoutParams(iconLayoutParams);
        likeIcon.setTag(postId);

        TextView likeCount = new TextView(this);
        likeCount.setText(upvotesContent);
        likeCount.setTextSize(16);
        likeCount.setTextColor(getResources().getColor(R.color.primary));
        likeCount.setLayoutParams(iconLayoutParams);

        ImageView dislikeIcon = new ImageView(this);
        dislikeIcon.setImageResource(R.drawable.konnect_dislike);
        dislikeIcon.setLayoutParams(iconLayoutParams);
        dislikeIcon.setTag(postId);

        TextView dislikeCount = new TextView(this);
        dislikeCount.setText(downvotesContent);
        dislikeCount.setTextSize(16);
        dislikeCount.setTextColor(getResources().getColor(R.color.primary));
        dislikeCount.setLayoutParams(iconLayoutParams);

        likeDislikeLayout.addView(likeIcon);
        likeDislikeLayout.addView(likeCount);
        likeDislikeLayout.addView(dislikeIcon);
        likeDislikeLayout.addView(dislikeCount);

        newPost.addView(userName);
        newPost.addView(postContent);
        newPost.addView(likeDislikeLayout);
        feedListContainer.addView(newPost, 0); // Add the new post at the top of the list
    }

    private void openFeedWithGroup(String groupName) {
        setActiveTab(navFeed);
        showSection(feedSection);
        grauButtonsContainer.setVisibility(View.GONE);
        header.setGroupName(groupName);
    }

    private void showNumberPickerDialog(final Button button, final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final String[] numbers = new String[11];
        for (int i = 0; i <= 10; i++) {
            numbers[i] = String.valueOf(i);
        }

        builder.setItems(numbers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button.setText(title + ": " + numbers[which]);
            }
        });

        builder.show();
    }
}
