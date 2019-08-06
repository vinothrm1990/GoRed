package shadowws.in.gored.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private User user;

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public class User{

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("sex")
        @Expose
        private String sex;
        @SerializedName("blood")
        @Expose
        private String blood;
        @SerializedName("visible")
        @Expose
        private String visible;
        @SerializedName("city")
        @Expose
        private String city;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getMobile() {
            return mobile;
        }

        public String getSex() {
            return sex;
        }

        public String getBlood() {
            return blood;
        }

        public String getVisible() {
            return visible;
        }

        public String getCity() {
            return city;
        }
    }
}
