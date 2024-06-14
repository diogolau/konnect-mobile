package com.example.konnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences;
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
    private LinearLayout feedSection, groupsSection, groupsListContainer, notificationsSection, notificationsListContainer, graphSection, grauButtonsContainer, feedListContainer, usersContainer, navFeedContainer, navGroupsContainer, navNotificationsContainer, navGraphContainer;
    private ImageButton homeButton;
    private Button postButton;
    private EditText postContent;
    private ComponentHeader header;
    private EditText searchBar;
    private String username;
    private String userId;
    private String currentGroupId = null;
    private GraphView graphView;
    private SharedPreferences sharedPreferences;
    int minGrau = 0;
    int maxGrau = 10;
    private View navFeedIndicator, navGroupsIndicator, navNotificationsIndicator, navGraphIndicator;

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
        navFeedContainer = findViewById(R.id.nav_feed_container);
        navGroupsContainer = findViewById(R.id.nav_groups_container);
        navNotificationsContainer = findViewById(R.id.nav_notifications_container);
        navGraphContainer = findViewById(R.id.nav_graph_container);
        homeButton = findViewById(R.id.header_home_button);
        postButton = findViewById(R.id.post_button);
        minGrauButton = findViewById(R.id.grau_min_button);
        maxGrauButton = findViewById(R.id.grau_max_button);
        postContent = findViewById(R.id.content_input);
        searchBar = findViewById(R.id.search_bar);
        feedListContainer = findViewById(R.id.feed_list_container);
        usersContainer = findViewById(R.id.users_container);
        grauButtonsContainer = findViewById(R.id.grau_buttons_container);
        header = findViewById(R.id.header);

        sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);

        feedSection = findViewById(R.id.feed_section);
        groupsSection = findViewById(R.id.groups_section);
        groupsListContainer = findViewById(R.id.groups_list_container);
        notificationsSection = findViewById(R.id.notifications_section);
        notificationsListContainer = findViewById(R.id.notifications_list_container);
        graphSection = findViewById(R.id.graph_section);

        navFeedIndicator = findViewById(R.id.nav_feed_indicator);
        navGroupsIndicator = findViewById(R.id.nav_groups_indicator);
        navNotificationsIndicator = findViewById(R.id.nav_notifications_indicator);
        navGraphIndicator = findViewById(R.id.nav_graph_indicator);

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
        
            // Dynamically position nodes based on the number of distinct usernames
            int numUsers = distinctUsernames.size();
            double centerX = 500;
            double centerY = 850;
            double radius = 300;
            double angleIncrement = 2 * Math.PI / numUsers;
        
            for (int i = 0; i < numUsers; i++) {
                double angle = i * angleIncrement;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                graphView.addNode(distinctUsernames.get(i), x, y);
            }
        
            // Create edges between distinct usernames
            for (int i = 0; i < numUsers; i++) {
                for (int j = i + 1; j < numUsers; j++) {
                    String username1 = distinctUsernames.get(i);
                    String username2 = distinctUsernames.get(j);
        
                    // Check if there is a connection between username1 and username2
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject connectionObject = jsonArray.getJSONObject(k);
                        String usernameTo = connectionObject.getString("usernameTo");
                        String usernameFrom = connectionObject.getString("usernameFrom");
        
                        if ((usernameTo.equals(username1) && usernameFrom.equals(username2)) ||
                                (usernameFrom.equals(username1) && usernameTo.equals(username2))) {
                            graphView.addEdge(i, j);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i("Erro listando conexões", e.toString());
        }

        navFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navFeed);
                showSection(feedSection);
                grauButtonsContainer.setVisibility(View.VISIBLE);
                header.setGroupName(null);
                currentGroupId = null;
                loadPosts(); // Refresh the post list
            }
        });

        navFeedIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navFeed);
                showSection(feedSection);
                grauButtonsContainer.setVisibility(View.VISIBLE);
                header.setGroupName(null);
                currentGroupId = null;
                loadPosts(); // Refresh the post list
            }
        });

        navFeedContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navFeed);
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
                setActiveTab(navGroups);
                showSection(groupsSection);
                loadGroups();
            }
        });

        navGroupsIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navGroups);
                showSection(groupsSection);
                loadGroups();
            }
        });

        navGroupsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navGroups);
                showSection(groupsSection);
                loadGroups();
            }
        });

        navNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navNotifications);
                showSection(notificationsSection);
                loadNotifications();
            }
        });

        navNotificationsIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navNotifications);
                showSection(notificationsSection);
                loadNotifications();
            }
        });

        navNotificationsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navNotifications);
                showSection(notificationsSection);
                loadNotifications();
            }
        });

        navGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navGraph);
                showSection(graphSection);
            }
        });

        navGraphIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navGraph);
                showSection(graphSection);
            }
        });

        navGraphContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveTab(navGraph);
                showSection(graphSection);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Clear all shared preferences to reset login state
                editor.apply();

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
                NetworkUtils.makePostRequest(url, body, new NetworkUtils.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
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
                            } else {
                                Toast.makeText(getBaseContext(), "Erro no post", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.i("Error post button", e.toString());
                        }
                        postContent.setText("");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getBaseContext(), "Erro no post", Toast.LENGTH_LONG).show();
                        Log.e("Network Error", "Error making post request", e);
                    }
                });
            }
        });

        // Default to Feed tab
        setActiveTab(navFeed);
        showSection(feedSection);

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
            NetworkUtils.makeGetRequest(listUsersUrl, new NetworkUtils.NetworkCallback() {
                @Override
                public void onSuccess(String usersResponse) {
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

                @Override
                public void onFailure(Exception e) {
                    Log.e("Network Error", "Error fetching users", e);
                }
            });
        }
    }

    private void setActiveTab(View activeTab) {
        navFeedIndicator.setBackgroundColor(getResources().getColor(activeTab == navFeed ? R.color.white : android.R.color.transparent));
        navGroupsIndicator.setBackgroundColor(getResources().getColor(activeTab == navGroups ? R.color.white : android.R.color.transparent));
        navNotificationsIndicator.setBackgroundColor(getResources().getColor(activeTab == navNotifications ? R.color.white : android.R.color.transparent));
        navGraphIndicator.setBackgroundColor(getResources().getColor(activeTab == navGraph ? R.color.white : android.R.color.transparent));
    }

    private void showSection(LinearLayout section) {
        feedSection.setVisibility(section == feedSection ? View.VISIBLE : View.GONE);
        groupsSection.setVisibility(section == groupsSection ? View.VISIBLE : View.GONE);
        notificationsSection.setVisibility(section == notificationsSection ? View.VISIBLE : View.GONE);
        graphSection.setVisibility(section == graphSection ? View.VISIBLE : View.GONE);
    }

    private void loadPosts() {
        feedListContainer.removeAllViews();
        String listPostsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?minDepth=%s&maxDepth=%s&userId=%s&groupId=%s", minGrau, maxGrau, userId, currentGroupId != null ? currentGroupId : "null");

        NetworkUtils.makeGetRequest(listPostsUrl, new NetworkUtils.NetworkCallback() {
            @Override
            public void onSuccess(String postsResponse) {
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

            @Override
            public void onFailure(Exception e) {
                Log.e("Network Error", "Error fetching posts", e);
            }
        });
    }

    private void loadGroups() {
        groupsListContainer.removeAllViews();
        String listKnsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/kn?userId=%s", userId);
        NetworkUtils.makeGetRequest(listKnsUrl, new NetworkUtils.NetworkCallback() {
            @Override
            public void onSuccess(String knsResponse) {
                try {
                    JSONObject jsonObject = new JSONObject(knsResponse);
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = new JSONArray(message);

                    if (jsonArray.length() == 0) {
                        LinearLayout noGroupsLayout = new LinearLayout(FeedActivity.this);
                        noGroupsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                        noGroupsLayout.setGravity(Gravity.CENTER);
                        noGroupsLayout.setOrientation(LinearLayout.VERTICAL);

                        TextView noGroupsView = new TextView(FeedActivity.this);
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

            @Override
            public void onFailure(Exception e) {
                Log.e("Network Error", "Error fetching groups", e);
            }
        });
    }

    private void addNewPost(String content, String userNameContent, String upvotesContent, String downvotesContent, String postId) {
        LinearLayout newPost = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between posts
        newPost.setLayoutParams(layoutParams);
        newPost.setOrientation(LinearLayout.VERTICAL);
        newPost.setPadding(64, 48, 64, 56);
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
        postContentPadding.setMargins(0, 32, 0, 32);
        postContent.setLayoutParams(postContentPadding);

        LinearLayout likeDislikeLayout = new LinearLayout(this);
        likeDislikeLayout.setOrientation(LinearLayout.HORIZONTAL);
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
                NetworkUtils.makePutRequest(url, new NetworkUtils.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Handle success if needed
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Network Error", "Error making put request", e);
                    }
                });
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
                NetworkUtils.makePutRequest(url, new NetworkUtils.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Handle success if needed
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Network Error", "Error making put request", e);
                    }
                });
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
        notificationsListContainer.removeAllViews();
        String listNotificationsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/notification?userId=%s", userId);
        NetworkUtils.makeGetRequest(listNotificationsUrl, new NetworkUtils.NetworkCallback() {
            @Override
            public void onSuccess(String notificationsResponse) {
                try {
                    JSONObject jsonObject = new JSONObject(notificationsResponse);
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = new JSONArray(message);

                    if (jsonArray.length() == 0) {
                        LinearLayout noNotificationsLayout = new LinearLayout(FeedActivity.this);
                        noNotificationsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                        noNotificationsLayout.setGravity(Gravity.CENTER);
                        noNotificationsLayout.setOrientation(LinearLayout.VERTICAL);

                        TextView noNotificationsView = new TextView(FeedActivity.this);
                        noNotificationsView.setText("Nenhuma notificação por enquanto");
                        noNotificationsView.setTextSize(18);
                        noNotificationsView.setTextColor(getResources().getColor(R.color.gray));
                        noNotificationsView.setGravity(Gravity.CENTER);

                        int paddingInDp = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
                        noNotificationsView.setPadding(paddingInDp, 0, paddingInDp, 0);

                        noNotificationsLayout.addView(noNotificationsView);
                        notificationsListContainer.addView(noNotificationsLayout);
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

            @Override
            public void onFailure(Exception e) {
                Log.e("Network Error", "Error fetching notifications", e);
            }
        });
    }

    private void addNewNotification(String id, String username) {
        LinearLayout notification = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between notifications
        notification.setLayoutParams(layoutParams);
        notification.setOrientation(LinearLayout.VERTICAL);
        notification.setPadding(48, 52, 48, 52);
        notification.setElevation(4);
        notification.setBackgroundResource(R.drawable.rounded_border);

        TextView notificationName = new TextView(this);
        notificationName.setText(username);
        notificationName.setTextSize(20);
        notificationName.setTextColor(getResources().getColor(R.color.black, null));

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(android.view.Gravity.END);

        Button acceptButton = new Button(this);
        acceptButton.setText("Aceitar");
        acceptButton.setBackground(getResources().getDrawable(R.drawable.rounded_primary_button, null));
        acceptButton.setTextColor(getResources().getColor(android.R.color.white, null));
        acceptButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));
        LinearLayout.LayoutParams acceptButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        acceptButtonParams.setMarginStart(8);
        acceptButton.setLayoutParams(acceptButtonParams);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format("http://10.0.2.2:8080/server_war_exploded/api/notification?userFromId=%s&userToId=%s&action=accept", id, userId);
                NetworkUtils.makePutRequest(url, new NetworkUtils.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i("AcceptResponse", response);
                        loadNotifications(); // Reload notifications
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Network Error", "Error making put request", e);
                    }
                });
            }
        });

        buttonLayout.addView(acceptButton);

        notification.addView(notificationName);
        notification.addView(buttonLayout);
        notificationsListContainer.addView(notification, 0); // Add the new notification at the top of the list
    }

    private void addNewKn(String id, String name) {
        LinearLayout groupBox = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 32, 16, 0); // Double the margin between kns
        groupBox.setLayoutParams(layoutParams);
        groupBox.setOrientation(LinearLayout.VERTICAL);
        groupBox.setPadding(48, 52, 48, 52);
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
        groupName.setTextSize(18);
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
        followButton.setBackground(getResources().getDrawable(R.drawable.rounded_primary_button, null));
        followButton.setPadding(16, 8, 16, 8);
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
                String inviteUrl = "http://10.0.2.2:8080/server_war_exploded/api/connection";
                String body = String.format("{\"userFromId\":\"%s\", \"userToId\":\"%s\"}", userId, id);

                NetworkUtils.makePostRequest(inviteUrl, body, new NetworkUtils.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        if (response.contains("__error__")) {
                            Toast.makeText(getBaseContext(), "Erro ao seguir usuário", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Seguindo usuário", Toast.LENGTH_LONG).show();

                            String searchText = searchBar.getText().toString();
                            loadUsers(searchText); // Reload users
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getBaseContext(), "Erro ao seguir usuário", Toast.LENGTH_LONG).show();
                        Log.e("Network Error", "Error making post request", e);
                    }
                });
            }
        });

        buttonLayout.addView(spacer);
        buttonLayout.addView(followButton);

        newUser.addView(userName);
        newUser.addView(buttonLayout);
        usersContainer.addView(newUser, 0); // Add the new user at the top of the list
    }

    private void openFeedWithGroup(String groupName, String groupId) {
        setActiveTab(navFeed);
        showSection(feedSection);
        grauButtonsContainer.setVisibility(View.GONE);
        header.setGroupName(groupName);
        currentGroupId = groupId;
        loadGroupPosts(groupId);
    }

    private void loadGroupPosts(String groupId) {
        feedListContainer.removeAllViews();
        String listPostsUrl = String.format("http://10.0.2.2:8080/server_war_exploded/api/post?minDepth=%s&maxDepth=%s&userId=%s&groupId=%s", "0", "10", userId, groupId);

        NetworkUtils.makeGetRequest(listPostsUrl, new NetworkUtils.NetworkCallback() {
            @Override
            public void onSuccess(String postsResponse) {
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

            @Override
            public void onFailure(Exception e) {
                Log.e("Network Error", "Error fetching posts", e);
            }
        });
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
