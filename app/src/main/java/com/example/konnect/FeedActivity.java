package com.example.konnect;

import static com.example.konnect.NetworkUtils.makeGetRequest;
import static com.example.konnect.NetworkUtils.makePostRequest;
import static com.example.konnect.NetworkUtils.makePutRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.view.Gravity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    private ImageButton navFeed, navGroups, navNotifications, navGraph;
    private Button minGrauButton, maxGrauButton;
    private LinearLayout feedSection, groupsSection, groupsListContainer, notificationsSection, graphSection, grauButtonsContainer, feedListContainer, usersContainer;
    private ImageButton homeButton;
    private Button postButton;
    private EditText postContent;
    private ComponentHeader header;
    private EditText searchBar;
    private String username;
    private String userId;
    private String currentGroupId = null;
    private GraphView graphView;
    int minGrau = 0;
    int maxGrau = 10;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        username = getIntent().getStringExtra("username");
        userId = getIntent().getStringExtra("userId");
        navFeed = findViewById(R.id.nav_feed);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);
        navGraph = findViewById(R.id.nav_graph);
        homeButton = findViewById(R.id.header_home_button);
        postButton = findViewById(R.id.post_button);
        minGrauButton = findViewById(R.id.grau_min_button);
        maxGrauButton = findViewById(R.id.grau_max_button);
        postContent = findViewById(R.id.content_input);
        searchBar = (EditText) findViewById(R.id.search_bar);
        feedListContainer = findViewById(R.id.feed_list_container);
        usersContainer = findViewById(R.id.users_container);
        grauButtonsContainer = findViewById(R.id.grau_buttons_container);
        header = findViewById(R.id.header);

        feedSection = findViewById(R.id.feed_section);
        groupsSection = findViewById(R.id.groups_section);
        groupsListContainer = findViewById(R.id.groups_list_container);
        notificationsSection = findViewById(R.id.notifications_section);
        graphSection = findViewById(R.id.graph_section);

        header.setHeaderText(username);

        minGrauButton.setText("Grau mínimo: " + minGrau);
        maxGrauButton.setText("Grau máximo: " + maxGrau);

        loadPosts();
        loadNotifications();
        loadGroups();

        // Graph view

        graphView = findViewById(R.id.graphView);

        String listConnectionsUrl = "http://10.0.2.2:8080/server_war_exploded/api/graph";
        String connectionsResponse = makeGetRequest(listConnectionsUrl);
        Log.i("connectionsResponse", connectionsResponse);

        String listUsersUrl = "http://10.0.2.2:8080/server_war_exploded/api/users";
        String allUsersResponse = makeGetRequest(listUsersUrl);
        Log.i("allUsersResponse", allUsersResponse);

        ArrayList<String> distinctUsernames = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(connectionsResponse);
            String message = jsonObject.getString("message");
            JSONArray jsonArray = new JSONArray(message);

            JSONObject allUsersObject = new JSONObject(allUsersResponse);
            String allUsersMessage = allUsersObject.getString("message");
            JSONArray allUsersArray = new JSONArray(allUsersMessage);

            for (int i = 0; i < allUsersArray.length(); i++) {
                JSONObject connectionObject = allUsersArray.getJSONObject(i);
                String username = connectionObject.getString("username");

                if (!distinctUsernames.contains(username)) {
                    distinctUsernames.add(username);
                }
            }
            int len = distinctUsernames.size();
            if (len == 1) {
                graphView.addNode(distinctUsernames.get(0), 500, 850);
            }
            if (len == 2) {
                graphView.addNode(distinctUsernames.get(0), 266, 850);
                graphView.addNode(distinctUsernames.get(1), 732, 850);
                graphView.addEdge(0, 1);
            }
            if (len == 3) {
                graphView.addNode(distinctUsernames.get(0), 500, 633);
                graphView.addNode(distinctUsernames.get(1), 266, 866);
                graphView.addNode(distinctUsernames.get(2), 732, 866);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject connectionObject = jsonArray.getJSONObject(i);
                    String usernameTo = connectionObject.getString("usernameTo");
                    String usernameFrom = connectionObject.getString("usernameFrom");

                    if ((usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(1))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(1)))) {
                        graphView.addEdge(0, 1);
                    }
                    if ((usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(2))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(2)))) {
                        graphView.addEdge(0, 2);
                    }
                    if ((usernameTo.equals(distinctUsernames.get(1)) && usernameFrom.equals(distinctUsernames.get(2))) ||
                            (usernameFrom.equals(distinctUsernames.get(1)) && usernameTo.equals(distinctUsernames.get(2)))) {
                        graphView.addEdge(1, 2);
                    }
                }
            }
            if (len == 4) {
                graphView.addNode(distinctUsernames.get(0), 500, 525);
                graphView.addNode(distinctUsernames.get(1), 266, 850);
                graphView.addNode(distinctUsernames.get(2), 732, 850);
                graphView.addNode(distinctUsernames.get(3), 500, 1175);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject connectionObject = jsonArray.getJSONObject(i);
                    String usernameTo = connectionObject.getString("usernameTo");
                    String usernameFrom = connectionObject.getString("usernameFrom");

                    if ((usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(1))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(1))) ||
                            (usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(2))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(2))) ||
                            (usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(3))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(3))) ||
                            (usernameTo.equals(distinctUsernames.get(1)) && usernameFrom.equals(distinctUsernames.get(2))) ||
                            (usernameFrom.equals(distinctUsernames.get(1)) && usernameTo.equals(distinctUsernames.get(2))) ||
                            (usernameTo.equals(distinctUsernames.get(1)) && usernameFrom.equals(distinctUsernames.get(3))) ||
                            (usernameFrom.equals(distinctUsernames.get(1)) && usernameTo.equals(distinctUsernames.get(3)) ||
                            (usernameTo.equals(distinctUsernames.get(2)) && usernameFrom.equals(distinctUsernames.get(3))) ||
                            (usernameFrom.equals(distinctUsernames.get(2)) && usernameTo.equals(distinctUsernames.get(3))))) {
                        graphView.addEdge(distinctUsernames.indexOf(usernameFrom), distinctUsernames.indexOf(usernameTo));
                    }
                }
            }

            if (len == 5) {
                graphView.addNode(distinctUsernames.get(0), 820, 620);
                graphView.addNode(distinctUsernames.get(1), 280, 1175);
                graphView.addNode(distinctUsernames.get(2), 730, 1175);
                graphView.addNode(distinctUsernames.get(3), 185, 620);
                graphView.addNode(distinctUsernames.get(4), 500, 200);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject connectionObject = jsonArray.getJSONObject(i);
                    String usernameTo = connectionObject.getString("usernameTo");
                    String usernameFrom = connectionObject.getString("usernameFrom");

                    if ((usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(1))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(1))) ||
                            (usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(2))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(2))) ||
                            (usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(3))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(3))) ||
                            (usernameTo.equals(distinctUsernames.get(0)) && usernameFrom.equals(distinctUsernames.get(4))) ||
                            (usernameFrom.equals(distinctUsernames.get(0)) && usernameTo.equals(distinctUsernames.get(4))) ||
                            (usernameTo.equals(distinctUsernames.get(1)) && usernameFrom.equals(distinctUsernames.get(2))) ||
                            (usernameFrom.equals(distinctUsernames.get(1)) && usernameTo.equals(distinctUsernames.get(2))) ||
                            (usernameTo.equals(distinctUsernames.get(1)) && usernameFrom.equals(distinctUsernames.get(3))) ||
                            (usernameFrom.equals(distinctUsernames.get(1)) && usernameTo.equals(distinctUsernames.get(3))) ||
                            (usernameTo.equals(distinctUsernames.get(1)) && usernameFrom.equals(distinctUsernames.get(4))) ||
                            (usernameFrom.equals(distinctUsernames.get(1)) && usernameTo.equals(distinctUsernames.get(4))) ||
                            (usernameTo.equals(distinctUsernames.get(2)) && usernameFrom.equals(distinctUsernames.get(3))) ||
                            (usernameFrom.equals(distinctUsernames.get(2)) && usernameTo.equals(distinctUsernames.get(3))) ||
                            (usernameTo.equals(distinctUsernames.get(2)) && usernameFrom.equals(distinctUsernames.get(4))) ||
                            (usernameFrom.equals(distinctUsernames.get(2)) && usernameTo.equals(distinctUsernames.get(4))) ||
                            (usernameTo.equals(distinctUsernames.get(3)) && usernameFrom.equals(distinctUsernames.get(4))) ||
                            (usernameFrom.equals(distinctUsernames.get(3)) && usernameTo.equals(distinctUsernames.get(4)))) {
                        graphView.addEdge(distinctUsernames.indexOf(usernameFrom), distinctUsernames.indexOf(usernameTo));
                    }
                }

            }
        } catch (Exception e) {
            Log.i("Erro listando conexões", e.toString());
        }

        navFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab();
                showSection(feedSection);
                grauButtonsContainer.setVisibility(View.VISIBLE);
                header.setGroupName(null);
                currentGroupId = null;
                loadPosts(); // Refresh the post list
            }
        });

        navGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab();
                showSection(groupsSection);
                loadGroups();
            }
        });

        navNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab();
                showSection(notificationsSection);
                loadNotifications();
            }
        });

        navGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab();
                showSection(graphSection);
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

        searchBar.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String searchText = s.toString();

                loadUsers(searchText);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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

                String url = "http://10.0.2.2:8080/server_war_exploded/api/post";
                String body = String.format("{\"content\":\"%s\", \"userId\":\"%s\", \"groupId\":\"%s\"}", postMessage, userId, currentGroupId != null ? currentGroupId : "null");

                String response = makePostRequest(url, body);
                Log.i("response", response);

                try {
                    if (!response.contains("__error__")) {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        JSONObject userObject = new JSONObject(message);

                        String id = userObject.getString("id");
                        String content = userObject.getString("content");
                        String userId = userObject.getString("userId");
                        String knId = userObject.getString("knId");

                        addNewPost(content, username, "0", "0", id);
                        // finish();
                        // startActivity(getIntent());
                    } else {
                        Toast.makeText(getBaseContext(), "Erro no post", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.i("Error post button", e.toString());
                }

                postContent.setText("");
            }
        });

        navFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab();
                showSection(feedSection);
                grauButtonsContainer.setVisibility(View.VISIBLE);
                header.setGroupName(null);
                currentGroupId = null;
                loadPosts(); // Refresh the post list
            }
        });

        // Default to Feed tab
        setActiveTab();
        showSection(feedSection);

        // Grau Minimo and Grau Maximo Buttons
        minGrauButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog((Button) v, "Grau Mínimo");
            }
        });

        maxGrauButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog((Button) v, "Grau Máximo");
            }
        });
    }

    private void loadUsers(String searchText) {
        // Clear previous search results
        usersContainer.removeAllViews();

        if (searchText.isEmpty()) {
            grauButtonsContainer.setVisibility(View.VISIBLE);
            postContent.setVisibility(View.VISIBLE);
            postButton.setVisibility(View.VISIBLE);
            feedListContainer.setVisibility(View.VISIBLE);
            usersContainer.setVisibility(View.GONE);
        } else {
            grauButtonsContainer.setVisibility(View.GONE);
            postContent.setVisibility(View.GONE);
            postButton.setVisibility(View.GONE);
            feedListContainer.setVisibility(View.GONE);
            usersContainer.setVisibility(View.VISIBLE);

            String listUsersUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/connection?userId=%s&searchFilter=%s", userId, searchText);
            String usersResponse = makeGetRequest(listUsersUrl);
            Log.i("usersResponse", usersResponse);

            try {
                JSONObject jsonObject = new JSONObject(usersResponse);
                String message = jsonObject.getString("message");
                JSONArray jsonArray = new JSONArray(message);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject knObject = jsonArray.getJSONObject(i);
                    String id = knObject.getString("id");
                    String username = knObject.getString("username");
                    String status = knObject.getString("status");

                    addNewConnection(id, username, status);
                }
            } catch (Exception e) {
                Log.i("Erro listando conexões", e.toString());
            }
        }
    }

    private void setActiveTab() {
        navFeed.setBackgroundResource(0);
        navGroups.setBackgroundResource(0);
        navNotifications.setBackgroundResource(0);
        navGraph.setBackgroundResource(0);
    }

    private void showSection(LinearLayout section) {
        feedSection.setVisibility(section == feedSection ? View.VISIBLE : View.GONE);
        groupsSection.setVisibility(section == groupsSection ? View.VISIBLE : View.GONE);
        notificationsSection.setVisibility(section == notificationsSection ? View.VISIBLE : View.GONE);
        graphSection.setVisibility(section == graphSection ? View.VISIBLE : View.GONE);
    }

    private void loadPosts() {
        // Clear post list
        feedListContainer.removeAllViews();

        String listPostsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?minDepth=%s&maxDepth=%s&userId=%s&groupId=%s", minGrau, maxGrau, userId, currentGroupId != null ? currentGroupId : "null");

        String postsResponse = makeGetRequest(listPostsUrl);
        Log.i("abloble", postsResponse);

        try {
            JSONObject jsonObject = new JSONObject(postsResponse);
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
    }

    private void loadGroups() {
        groupsListContainer.removeAllViews();
        String listKnsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/kn?userId=%s", userId);
        String knsResponse = makeGetRequest(listKnsUrl);

        try {
            JSONObject jsonObject = new JSONObject(knsResponse);
            String message = jsonObject.getString("message");
            JSONArray jsonArray = new JSONArray(message);

            if (jsonArray.length() == 0) {
                LinearLayout noGroupsLayout = new LinearLayout(this);
                noGroupsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                noGroupsLayout.setGravity(Gravity.CENTER);
                noGroupsLayout.setOrientation(LinearLayout.VERTICAL);

                TextView noGroupsView = new TextView(this);
                noGroupsView.setText("Nenhum grupo encontrado");
                noGroupsView.setTextSize(18);
                noGroupsView.setTextColor(getResources().getColor(R.color.gray));
                noGroupsView.setGravity(Gravity.CENTER);

                int paddingInDp = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
                noGroupsView.setPadding(paddingInDp, 0, paddingInDp, 0);

                noGroupsLayout.addView(noGroupsView);
                groupsListContainer.addView(noGroupsLayout);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject knObject = jsonArray.getJSONObject(i);
                    String id = knObject.getString("id");
                    String name = knObject.getString("name");

                    addNewKn(id, name);
                }
            }
        } catch (Exception e) {
            Log.i("Erro listando grupos", e.toString());
        }
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

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentLikes = Integer.parseInt(likeCount.getText().toString());
                currentLikes++;
                likeCount.setText(String.valueOf(currentLikes));

                String postId = (String) v.getTag();
                String url = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?postId=%s&vote=upvote", postId);
                new Thread(() -> makePutRequest(url)).start(); // Send the request in a separate thread
            }
        });

        ImageView dislikeIcon = new ImageView(this);
        dislikeIcon.setImageResource(R.drawable.konnect_dislike);
        dislikeIcon.setLayoutParams(iconLayoutParams);
        dislikeIcon.setTag(postId);

        TextView dislikeCount = new TextView(this);
        dislikeCount.setText(downvotesContent);
        dislikeCount.setTextSize(16);
        dislikeCount.setTextColor(getResources().getColor(R.color.primary));
        dislikeCount.setLayoutParams(iconLayoutParams);

        dislikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentDislikes = Integer.parseInt(dislikeCount.getText().toString());
                currentDislikes++;
                dislikeCount.setText(String.valueOf(currentDislikes));

                String postId = (String) v.getTag();
                String url = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?postId=%s&vote=downvote", postId);
                new Thread(() -> makePutRequest(url)).start(); // Send the request in a separate thread
            }
        });

        likeDislikeLayout.addView(likeIcon);
        likeDislikeLayout.addView(likeCount);
        likeDislikeLayout.addView(dislikeIcon);
        likeDislikeLayout.addView(dislikeCount);

        newPost.addView(userName);
        newPost.addView(postContent);
        newPost.addView(likeDislikeLayout);
        feedListContainer.addView(newPost, 0); // Add the new post at the top of the list
    }

    private void loadNotifications() {
        notificationsSection.removeAllViews();
        String listNotificationsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/notification?userId=%s", userId);
        String notificationsResponse = makeGetRequest(listNotificationsUrl);

        try {
            JSONObject jsonObject = new JSONObject(notificationsResponse);
            String message = jsonObject.getString("message");
            JSONArray jsonArray = new JSONArray(message);

            if (jsonArray.length() == 0) {
                LinearLayout noNotificationsLayout = new LinearLayout(this);
                noNotificationsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                noNotificationsLayout.setGravity(Gravity.CENTER);
                noNotificationsLayout.setOrientation(LinearLayout.VERTICAL);

                TextView noNotificationsView = new TextView(this);
                noNotificationsView.setText("Nenhuma notificação por enquanto");
                noNotificationsView.setTextSize(18);
                noNotificationsView.setTextColor(getResources().getColor(R.color.gray));
                noNotificationsView.setGravity(Gravity.CENTER);

                int paddingInDp = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
                noNotificationsView.setPadding(paddingInDp, 0, paddingInDp, 0);

                noNotificationsLayout.addView(noNotificationsView);
                notificationsSection.addView(noNotificationsLayout);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject notificationObject = jsonArray.getJSONObject(i);
                    String id = notificationObject.getString("id");
                    String username = notificationObject.getString("username");

                    // Check if status exists before accessing it
                    if (notificationObject.has("status")) {
                        String status = notificationObject.getString("status");
                        if (status.equals("pending")) {
                            addNewNotification(id, username);
                        }
                    } else {
                        // If no status is present, handle it as pending by default
                        addNewNotification(id, username);
                    }
                }
            }
        } catch (Exception e) {
            Log.i("Erro listando notificações", e.toString());
        }
    }


    private void addNewNotification(String id, String username) {
        LinearLayout notification = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between notifications
        notification.setLayoutParams(layoutParams);
        notification.setOrientation(LinearLayout.VERTICAL);
        notification.setPadding(32, 40, 32, 40);
        notification.setElevation(4);
        notification.setBackgroundResource(R.drawable.rounded_border);

        TextView notificationName = new TextView(this);
        notificationName.setText(username);
        notificationName.setTextSize(18);
        notificationName.setTextColor(getResources().getColor(R.color.black, null));

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(android.view.Gravity.END);

        Button acceptButton = new Button(this);
        acceptButton.setText("Aceitar");
        acceptButton.setTextColor(getResources().getColor(android.R.color.white, null));
        acceptButton.setBackgroundTintList(getResources().getColorStateList(R.color.green, null));
        LinearLayout.LayoutParams acceptButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        acceptButtonParams.setMarginStart(8);
        acceptButton.setLayoutParams(acceptButtonParams);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format("http://10.0.2.2:8080/server_war_exploded/api/notification?userFromId=%s&userToId=%s&action=accept", id, userId);
                String response = makePutRequest(url);
                Log.i("AcceptResponse", response);
                loadNotifications(); // Reload notifications
            }
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Excluir");
        deleteButton.setTextColor(getResources().getColor(android.R.color.white, null));
        deleteButton.setBackgroundTintList(getResources().getColorStateList(R.color.red, null));
        LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        deleteButtonParams.setMarginStart(8);
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format("http://10.0.2.2:8080/server_war_exploded/api/notification?userFromId=%s&userToId=%s&action=delete", id, userId);
                String response = makePutRequest(url);
                Log.i("DeleteResponse", response);
                loadNotifications(); // Reload notifications
            }
        });

        buttonLayout.addView(deleteButton);
        buttonLayout.addView(acceptButton);

        notification.addView(notificationName);
        notification.addView(buttonLayout);
        notificationsSection.addView(notification, 0); // Add the new notification at the top of the list
    }


    private void addNewKn(String id, String name) {
        LinearLayout groupBox = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between groups
        groupBox.setLayoutParams(layoutParams);
        groupBox.setOrientation(LinearLayout.VERTICAL);
        groupBox.setPadding(32, 40, 32, 40);
        groupBox.setElevation(4);
        groupBox.setBackgroundResource(R.drawable.rounded_border);

        LinearLayout groupHeader = new LinearLayout(this);
        groupHeader.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        groupHeader.setOrientation(LinearLayout.HORIZONTAL);
        groupHeader.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView groupName = new TextView(this);
        groupName.setText(name);
        groupName.setTextSize(16);
        groupName.setTextColor(getResources().getColor(R.color.black));
        groupHeader.addView(groupName);

        View spacer = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        spacer.setLayoutParams(spacerParams);
        groupHeader.addView(spacer);

        groupBox.addView(groupHeader);

        groupBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedWithGroup(name, id);
            }
        });

        groupsListContainer.addView(groupBox);
    }

    private void addNewConnection(String id, String name, String status) {
        LinearLayout newUser = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between users
        newUser.setLayoutParams(layoutParams);
        newUser.setOrientation(LinearLayout.VERTICAL);
        newUser.setPadding(32, 40, 32, 40);
        newUser.setElevation(4);
        newUser.setBackgroundResource(R.drawable.rounded_border);

        TextView userName = new TextView(this);
        userName.setText(name);
        userName.setTextSize(18);
        userName.setTextColor(getResources().getColor(R.color.black, null));

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(android.view.Gravity.END);

        View spacer = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        spacer.setLayoutParams(spacerParams);

        Button followButton = new Button(this);
        if (status.equals("pending")) {
            followButton.setText("Aguardando");
            followButton.setTextColor(getResources().getColor(R.color.white, null));
            followButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray, null));
            followButton.setEnabled(false);
        } else if (status.equals("active")) {
            followButton.setText("Seguindo");
            followButton.setTextColor(getResources().getColor(R.color.primary, null));
            followButton.setBackgroundTintList(getResources().getColorStateList(R.color.secondary, null));
            followButton.setEnabled(false);
        } else {
            followButton.setText("Seguir");
            followButton.setTextColor(getResources().getColor(android.R.color.white, null));
            followButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));
        }
        LinearLayout.LayoutParams followButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        followButtonParams.setMarginStart(8);
        followButton.setLayoutParams(followButtonParams);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inviteUrl = "http://10.0.2.2:8080/server_war_exploded/api/connection" ;
                String body = String.format("{\"userFromId\":\"%s\", \"userToId\":\"%s\"}", userId, id);

                String response = makePostRequest(inviteUrl, body);
                if (response.contains("__error__")) {
                    Toast.makeText(getBaseContext(), "Erro ao seguir usuário", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Seguindo usuário", Toast.LENGTH_LONG).show();

                    String searchText = searchBar.getText().toString();
                    loadUsers(searchText); // Reload users
                }
            }
        });

        buttonLayout.addView(spacer);
        buttonLayout.addView(followButton);

        newUser.addView(userName);
        newUser.addView(buttonLayout);
        usersContainer.addView(newUser, 0); // Add the new user at the top of the list
    }

    private void openFeedWithGroup(String groupName, String groupId) {
        setActiveTab();
        showSection(feedSection);
        grauButtonsContainer.setVisibility(View.GONE);
        header.setGroupName(groupName);
        currentGroupId = groupId;
        loadGroupPosts(groupId);
    }

    private void loadGroupPosts(String groupId) {
        feedListContainer.removeAllViews();
        String listPostsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?minDepth=%s&maxDepth=%s&userId=%s&groupId=%s", "0", "10", userId, groupId);

        String postsResponse = makeGetRequest(listPostsUrl);
        Log.i("abloble", postsResponse);

        try {
            JSONObject jsonObject = new JSONObject(postsResponse);
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
                if (title.equals("Grau Mínimo")) {
                    minGrau = which;
                } else {
                    maxGrau = which;
                }

                loadPosts(); // Reload posts
            }
        });

        builder.show();
    }
}
