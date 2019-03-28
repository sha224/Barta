package app.barta.barta;

import android.graphics.Color;
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
        CircleImageView commenterIcon;

        public CommentHolder(View view) {
            super(view);
            commentTextView = view.findViewById(R.id.commentText);
            upvoteCountView = view.findViewById(R.id.upvoteCountText);
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
}
