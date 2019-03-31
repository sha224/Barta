package app.barta.barta;

import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;

class Post {

    String postUrl;
    String text;
    int upvoteCount;
    int downvoteCount;
    int commentCount;
    Point location;
    String creationTime;

    static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public Post(String postUrl, String text, int upvoteCount, int downvoteCount, int commentCount, Point location, String creationTime) {
        this.postUrl = postUrl;
        this.text = text;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.commentCount = commentCount;
        this.location = location;
        this.creationTime = creationTime;
    }

    int getMilesDistanceFrom(double fromLatitude, double fromLongitude) {
        double lat1 = fromLatitude;
        double lon1 = fromLongitude;
        double lat2 = location.y;
        double lon2 = location.x;
        double R = 3958.8;
        double p1 = Math.toRadians(lat1);
        double p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2 - lat1);
        double dl = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dp / 2) * Math.sin(dp / 2) +
                Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        int miles = (int) Math.round(d);
        return miles;
    }

    String getHourDifferenceFrom(OffsetDateTime fromTime) {
        OffsetDateTime postTime = OffsetDateTime.parse(creationTime);
        Duration duration = Duration.between(postTime, fromTime);
        int hours = (int) duration.toHours();
        if (hours >= 24)
            return (int) duration.toDays() + "d";
        else
            return hours + "h";
    }
}
