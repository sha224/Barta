package app.barta.barta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentHolder> {

    private List<Comment> comments;

    public CommentListAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        TextView commentTextView;
        TextView upvoteCountView;
        TextView downvoteCountView;
        TextView timeTextView;

        public CommentHolder(View view) {
            super(view);
            commentTextView = view.findViewById(R.id.commentText);
            upvoteCountView = view.findViewById(R.id.upvoteCountText);
            downvoteCountView = view.findViewById(R.id.downvoteCountText);
            timeTextView = view.findViewById(R.id.timeText);
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
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
