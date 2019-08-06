package shadowws.in.gored.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PushResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("notification")
    @Expose
    private List<Notification> notification = null;

    public Boolean getError() {
        return error;
    }

    public List<Notification> getNotification() {
        return notification;
    }

    public class Notification{

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("request")
        @Expose
        private String request;
        @SerializedName("fireid")
        @Expose
        private String fireid;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("total_pages")
        @Expose
        private String totalPages;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }

        public String getRequest() {
            return request;
        }

        public String getFireid() {
            return fireid;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getTotalPages() {
            return totalPages;
        }
    }
}
