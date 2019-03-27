package app.barta.barta;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";

    private TextView postText;
    private TextView upvoteCountText;
    private TextView downvoteCountText;
    private TextView commentCountText;
    private TextView placeText;
    private TextView timeText;
    private RecyclerView recyclerView;

    private String postUrl;
    private Location deviceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postText = findViewById(R.id.postText);
        upvoteCountText = findViewById(R.id.upvoteCountText);
        downvoteCountText = findViewById(R.id.downvoteCountText);
        commentCountText = findViewById(R.id.commentCountText);
        placeText = findViewById(R.id.placeText);
        timeText = findViewById(R.id.timeText);
        recyclerView = findViewById(R.id.commentListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postUrl = getIntent().getStringExtra(MainActivity.EXTRA_POST_URL);
        deviceLocation = getIntent().getParcelableExtra(MainActivity.EXTRA_LOCATION);
        fetch();
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
                        Comment comment = new Comment(commentJson.getString("text"), commentJson.getInt("upvotes"), commentJson.getInt("downvotes"), commentJson.getString("creationTime"));
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
                timeText.setText(post.getHourDifferenceFrom(OffsetDateTime.now()) + "h");
                recyclerView.setAdapter(new CommentListAdapter(comments));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error while fetching posts", error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
