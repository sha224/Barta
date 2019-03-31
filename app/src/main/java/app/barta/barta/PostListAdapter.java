package app.barta.barta;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.OffsetDateTime;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostHolder> {

    private static final String TAG = "PostListAdapter";

    private Context context;
    private List<Post> posts;
    private Location deviceLocation;

    public PostListAdapter(Context context, List<Post> posts, Location deviceLocation) {
        this.context = context;
        this.posts = posts;
        this.deviceLocation = deviceLocation;
    }

    static class PostHolder extends RecyclerView.ViewHolder {

        TextView postTextView;
        ImageButton upvoteButton;
        TextView upvoteCountView;
        ImageButton downvoteButton;
        TextView downvoteCountView;
        ImageButton commentButton;
        TextView commentCountView;
        TextView placeTextView;
        TextView timeTextView;

        public PostHolder(View view) {
            super(view);
            postTextView = view.findViewById(R.id.postText);
            upvoteButton = view.findViewById(R.id.upvoteButton);
            upvoteCountView = view.findViewById(R.id.upvoteCountText);
            downvoteButton = view.findViewById(R.id.downvoteButton);
            downvoteCountView = view.findViewById(R.id.downvoteCountText);
            commentButton = view.findViewById(R.id.commentButton);
            commentCountView = view.findViewById(R.id.commentCountText);
            placeTextView = view.findViewById(R.id.placeText);
            timeTextView = view.findViewById(R.id.timeText);
        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_list_item, viewGroup, false);
        PostHolder postHolder = new PostHolder(view);
        return postHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder postHolder, int i) {
        postHolder.postTextView.setText(posts.get(i).text);
        postHolder.upvoteCountView.setText(Integer.toString(posts.get(i).upvoteCount));
        postHolder.downvoteCountView.setText(Integer.toString(posts.get(i).downvoteCount));
        postHolder.commentCountView.setText(Integer.toString(posts.get(i).commentCount));
        postHolder.placeTextView.setText(posts.get(i).getMilesDistanceFrom(deviceLocation.getLatitude(), deviceLocation.getLongitude()) + " mi");
        postHolder.timeTextView.setText(posts.get(i).getHourDifferenceFrom(OffsetDateTime.now()));
        final int index = i;
        postHolder.upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvote(posts.get(index).postUrl);
            }
        });
        postHolder.downvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downvote(posts.get(index).postUrl);
            }
        });
        postHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(context, CommentActivity.class);
                commentsIntent.putExtra(MainActivity.EXTRA_POST_URL, posts.get(index).postUrl);
                commentsIntent.putExtra(MainActivity.EXTRA_USER_URL, ((MainActivity) context).getUserUrl());
                commentsIntent.putExtra(MainActivity.EXTRA_LOCATION, deviceLocation);
                context.startActivity(commentsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void upvote(String postUrl) {
        String url = postUrl + "/upvoters";

        final String request = ((MainActivity) context).getUserUrl();
        Log.d(TAG, "Request: " + request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void downvote(String postUrl) {
        String url = postUrl + "/downvoters";

        final String request = ((MainActivity) context).getUserUrl();
        Log.d(TAG, "Request: " + request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
