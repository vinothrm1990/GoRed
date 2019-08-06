package shadowws.in.gored.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("request")
    @Expose
    private List<Request> request = null;

    public Boolean getError() {
        return error;
    }

    public List<Request> getRequest() {
        return request;
    }

    public class Request{

        @SerializedName("id")
        @Expose
        private String id;

        public String getId() {
            return id;
        }
    }
}
