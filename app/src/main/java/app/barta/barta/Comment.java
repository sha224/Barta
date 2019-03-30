package app.barta.barta;

import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;

class Comment {

    String commentUrl;
    String text;
    int authorIdentifier;
    int upvoteCount;
    int downvoteCount;
    String creationTime;

    public Comment(String commentUrl, String text, int authorIdentifier, int upvoteCount, int downvoteCount, String creationTime) {
        this.commentUrl = commentUrl;
        this.text = text;
        this.authorIdentifier = authorIdentifier;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.creationTime = creationTime;
    }

    int getHourDifferenceFrom(OffsetDateTime fromTime) {
        OffsetDateTime postTime = OffsetDateTime.parse(creationTime);
        Duration duration = Duration.between(postTime, fromTime);
        return (int) duration.toHours();
    }
}
