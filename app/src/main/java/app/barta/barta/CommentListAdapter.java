package app.barta.barta;

import android.content.Context;
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

import org.threeten.bp.OffsetDateTime;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentHolder> {

    private static final String TAG = "CommentListAdapter";

    private Context context;
    private List<Comment> comments;

    public CommentListAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        TextView commentTextView;
        ImageButton upvoteButton;
        TextView upvoteCountView;
        ImageButton downvoteButton;
        TextView downvoteCountView;
        TextView timeTextView;
        CircleImageView commenterIcon;

        public CommentHolder(View view) {
            super(view);
            commentTextView = view.findViewById(R.id.commentText);
            upvoteButton = view.findViewById(R.id.upvoteButton);
            upvoteCountView = view.findViewById(R.id.upvoteCountText);
            downvoteButton = view.findViewById(R.id.downvoteButton);
            downvoteCountView = view.findViewById(R.id.downvoteCountText);
            timeTextView = view.findViewById(R.id.timeText);
            commenterIcon = view.findViewById(R.id.commenterIcon);
        }
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_list_item, viewGroup, false);
        CommentHolder commentHolder = new CommentHolder(view);
        return commentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder commentHolder, int i) {
        commentHolder.commentTextView.setText(comments.get(i).text);
        commentHolder.upvoteCountView.setText(Integer.toString(comments.get(i).upvoteCount));
        commentHolder.downvoteCountView.setText(Integer.toString(comments.get(i).downvoteCount));
        commentHolder.timeTextView.setText(comments.get(i).getHourDifferenceFrom(OffsetDateTime.now()) + "h");
        setCommenterIcon(commentHolder, comments.get(i).authorIdentifier);
        final int index = i;
        commentHolder.upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvote(comments.get(index).commentUrl);
            }
        });
        commentHolder.downvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downvote(comments.get(index).commentUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private void setCommenterIcon(CommentHolder commentHolder, int commenterId) {
        if (!(commenterId >= 0 && commenterId <= 99))
            return;
        int[] icons = {R.drawable.ic_eagle, R.drawable.ic_elephant, R.drawable.ic_frog, R.drawable.ic_hummingbird, R.drawable.ic_lion, R.drawable.ic_owl, R.drawable.ic_ox, R.drawable.ic_pig, R.drawable.ic_rat, R.drawable.ic_reindeer};
        int[] colors = {R.color.icon_color_deepblue, R.color.icon_color_orange, R.color.icon_color_lightgreen, R.color.icon_color_brown, R.color.icon_color_gray, R.color.icon_color_yellow, R.color.icon_color_red, R.color.icon_color_deepgreen, R.color.icon_color_purple, R.color.icon_color_lightblue};
        commentHolder.commenterIcon.setImageResource(icons[commenterId % 10]);
        commentHolder.commenterIcon.setCircleBackgroundColorResource(colors[commenterId / 10]);
    }

    private void upvote(String commentUrl) {
        String url = commentUrl + "/upvoters";

        final String request = ((CommentActivity) context).getUserUrl();
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

    private void downvote(String commentUrl) {
        String url = commentUrl + "/downvoters";

        final String request = ((CommentActivity) context).getUserUrl();
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
