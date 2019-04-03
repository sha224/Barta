package app.barta.barta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.OffsetDateTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";

    private SharedPreferences sharedPreferences;

    private TextView postText;
    private ImageButton upvoteButton;
    private TextView upvoteCountText;
    private ImageButton downvoteButton;
    private TextView downvoteCountText;
    private TextView commentCountText;
    private TextView placeText;
    private TextView timeText;
    private RecyclerView recyclerView;
    private EditText commentInput;
    private ImageButton commentSendButton;

    private final Handler handler = new Handler();
    private final Runnable fetcher = new Runnable() {
        @Override
        public void run() {
            fetch();
            handler.postDelayed(fetcher, 5000);
        }
    };

    private String postUrl;
    private String userUrl;
    private Location deviceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        postText = findViewById(R.id.postText);
        upvoteButton = findViewById(R.id.upvoteButton);
        upvoteCountText = findViewById(R.id.upvoteCountText);
        downvoteButton = findViewById(R.id.downvoteButton);
        downvoteCountText = findViewById(R.id.downvoteCountText);
        commentCountText = findViewById(R.id.commentCountText);
        placeText = findViewById(R.id.placeText);
        timeText = findViewById(R.id.timeText);
        recyclerView = findViewById(R.id.commentListView);
        commentInput = findViewById(R.id.commentInput);
        commentSendButton = findViewById(R.id.commentSendButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postUrl = getIntent().getStringExtra(MainActivity.EXTRA_POST_URL);
        userUrl = getIntent().getStringExtra(MainActivity.EXTRA_USER_URL);
        deviceLocation = getIntent().getParcelableExtra(MainActivity.EXTRA_LOCATION);
        handler.post(fetcher);
        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvote(postUrl);
            }
        });
        downvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downvote(postUrl);
            }
        });
        commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String commentInputText = commentInput.getText().toString();
                commentInput.setText("");
                commentInput.clearFocus();
                createComment(commentInputText);
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(fetcher);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void fetch() {
        String url = postUrl + "?projection=comments";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                Post post = null;
                List<Comment> comments = null;
                try {
                    JSONObject postJson = new JSONObject(response);
                    JSONObject locationJson = postJson.getJSONObject("location");
                    post = new Post(postUrl, postJson.getString("text"), postJson.getInt("upvotes"), postJson.getInt("downvotes"), postJson.getInt("commentCount"), new Post.Point(locationJson.getDouble("x"), locationJson.getDouble("y")), postJson.getString("creationTime"));
                    JSONArray commentsJson = postJson.getJSONArray("comments");
                    comments = new ArrayList<>();
                    for (int i = 0; i < commentsJson.length(); i++) {
                        JSONObject commentJson = commentsJson.getJSONObject(i);
                        String commentUrl = commentJson.getJSONObject("_links").getJSONObject("self").getString("href");
                        commentUrl = commentUrl.substring(0, commentUrl.indexOf("{?projection}"));
                        Comment comment = new Comment(commentUrl, commentJson.getString("text"), commentJson.getInt("authorIdentifier"), commentJson.getInt("upvotes"), commentJson.getInt("downvotes"), commentJson.getString("creationTime"));
                        comments.add(comment);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error while parsing JSON", e);
                }

                Log.d(TAG, "Finished fetching comments");

                postText.setText(post.text);
                upvoteCountText.setText(Integer.toString(post.upvoteCount));
                downvoteCountText.setText(Integer.toString(post.downvoteCount));
                commentCountText.setText(Integer.toString(post.commentCount));
                placeText.setText(post.getMilesDistanceFrom(deviceLocation.getLatitude(), deviceLocation.getLongitude()) + " mi");
                timeText.setText(post.getHourDifferenceFrom(OffsetDateTime.now()));

                String sort = sharedPreferences.getString("comment_sort_preference", "Latest");
                Log.d(TAG, "Sort Comments By: " + sort);
                if (sort.equals("Latest")) {
                    Collections.sort(comments, new Comparator<Comment>() {
                        @Override
                        public int compare(Comment c1, Comment c2) {
                            return OffsetDateTime.parse(c2.creationTime).compareTo(OffsetDateTime.parse(c1.creationTime));
                        }
                    });
                } else if (sort.equals("Highest Rating")) {
                    Collections.sort(comments, new Comparator<Comment>() {
                        @Override
                        public int compare(Comment c1, Comment c2) {
                            return (c2.upvoteCount - c2.downvoteCount) - (c1.upvoteCount - c1.downvoteCount);
                        }
                    });
                }

                recyclerView.setAdapter(new CommentListAdapter(CommentActivity.this, comments));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error while fetching comments", error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void upvote(String postUrl) {
        String url = postUrl + "/upvoters";

        final String request = userUrl;
        Log.d(TAG, "Request: " + request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                fetch();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Response", error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "text/uri-list";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return request.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Error while setting body", e);
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "Status Code: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void downvote(String postUrl) {
        String url = postUrl + "/downvoters";

        final String request = userUrl;
        Log.d(TAG, "Request: " + request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                fetch();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Response", error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "text/uri-list";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return request.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Error while setting body", e);
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "Status Code: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void createComment(String commentText) {
        String url = BuildConfig.API_URL + "/comments";

        String requestBody = null;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("text", commentText);
            jsonBody.put("post", postUrl);
            jsonBody.put("author", userUrl);
            requestBody = jsonBody.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error while making JSON Body", e);
        }

        final String request = requestBody;
        Log.d(TAG, "Request: " + request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                fetch();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Response", error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return request.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Error while setting body", e);
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "Status Code: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getUserUrl() {
        return userUrl;
    }
}
