package shadowws.in.gored.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse {

    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getTo() {
        return to;
    }

    public Data getData() {
        return data;
    }

    public class Data{

        @SerializedName("data")
        @Expose
        private Data_ data;

        public Data_ getData() {
            return data;
        }

        public class Data_{

            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("is_background")
            @Expose
            private Boolean isBackground;
            @SerializedName("message")
            @Expose
            private String message;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("payload")
            @Expose
            private Payload payload;
            @SerializedName("timestamp")
            @Expose
            private String timestamp;

            public String getTitle() {
                return title;
            }

            public Boolean getBackground() {
                return isBackground;
            }

            public String getMessage() {
                return message;
            }

            public String getImage() {
                return image;
            }

            public Payload getPayload() {
                return payload;
            }

            public String getTimestamp() {
                return timestamp;
            }
        }

        public class Payload {

            @SerializedName("uid")
            @Expose
            private String uid;
            @SerializedName("uname")
            @Expose
            private String uname;

            public String getUid() {
                return uid;
            }

            public String getUname() {
                return uname;
            }
        }
    }
}
