package app.barta.barta;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostHolder> {

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
        postHolder.timeTextView.setText(posts.get(i).getHourDifferenceFrom(OffsetDateTime.now()) + "h");
        final int index = i;
        postHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(context, CommentActivity.class);
                commentsIntent.putExtra(MainActivity.EXTRA_POST_URL, posts.get(index).postUrl);
                commentsIntent.putExtra(MainActivity.EXTRA_LOCATION, deviceLocation);
                context.startActivity(commentsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
