package shadowws.in.gored.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DonorResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public Boolean getError() {
        return error;
    }

    public List<User> getUsers() {
        return users;
    }

    public class User{

        @SerializedName("id")
        @Expose
        private Integer id;
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
        @SerializedName("firebase")
        @Expose
        private String firebase;
        @SerializedName("total_pages")
        @Expose
        private String totalPages;

        public Integer getId() {
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

        public String getTotalPages() {
            return totalPages;
        }

        public String getCity() {
            return city;
        }

        public String getFirebase() {
            return firebase;
        }
    }
}
