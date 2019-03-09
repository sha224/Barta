package app.barta.barta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostHolder> {

    private List<Post> posts;

    public PostListAdapter(List<Post> posts) {
        this.posts = posts;
    }

    static class PostHolder extends RecyclerView.ViewHolder {

        TextView postTextView;
        TextView upvoteCountView;
        TextView downvoteCountView;
        TextView commentCountView;
        TextView placeTextView;
        TextView timeTextView;

        public PostHolder(View view) {
            super(view);
            postTextView = view.findViewById(R.id.postText);
            upvoteCountView = view.findViewById(R.id.upvoteCountText);
            downvoteCountView = view.findViewById(R.id.downvoteCountText);
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
    public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {
        postHolder.postTextView.setText(posts.get(i).text);
        postHolder.upvoteCountView.setText(Integer.toString(posts.get(i).upvoteCount));
        postHolder.downvoteCountView.setText(Integer.toString(posts.get(i).downvoteCount));
        postHolder.commentCountView.setText(Integer.toString(posts.get(i).commentCount));
        postHolder.placeTextView.setText(posts.get(i).getMilesDistanceFrom(43.456236, -76.542157) + " mi");
        postHolder.timeTextView.setText(posts.get(i).getHourDifferenceFrom(OffsetDateTime.now()) + "h");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
