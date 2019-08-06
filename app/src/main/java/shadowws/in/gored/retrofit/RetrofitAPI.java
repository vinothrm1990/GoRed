package shadowws.in.gored.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import shadowws.in.gored.response.DashboardResponse;
import shadowws.in.gored.response.DonorResponse;
import shadowws.in.gored.response.FirebaseResponse;
import shadowws.in.gored.response.HelpResponse;
import shadowws.in.gored.response.LoginResponse;
import shadowws.in.gored.response.NotificationResponse;
import shadowws.in.gored.response.PushResponse;
import shadowws.in.gored.response.RegisterResponse;
import shadowws.in.gored.response.RequestResponse;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("createuser")
    Call<RegisterResponse> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("sex") String sex,
            @Field("blood") String blood,
            @Field("visible") String visible,
            @Field("city") String city
    );

    @FormUrlEncoded
    @POST("userlogin")
    Call<LoginResponse> loginUser(
            @Field("mobile") String mobile,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("addfirebase")
    Call<FirebaseResponse> registerFirebase(
            @Field("mobile") String mobile,
            @Field("firebase") String firebase

    );

    @FormUrlEncoded
    @POST("allusers")
    Call<DashboardResponse> dashboardData(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("allusersbylimit")
    Call<DonorResponse> getDonorData(
            @Field("page") int page,
            @Field("id") String id

    );

    @FormUrlEncoded
    @POST("message.php")
    Call<NotificationResponse> postNotification(
            @Field("title") String title,
            @Field("message") String message,
            @Field("push_type") String push_type,
            @Field("regId") String regId,
            @Field("uid") String uid,
            @Field("uname") String uname

    );

    @FormUrlEncoded
    @POST("addnotification")
    Call<HelpResponse> postHelpRequest(
            @Field("fid") String fid,
            @Field("tid") String tid,
            @Field("title") String title,
            @Field("message") String message,
            @Field("type") String type,
            @Field("request") String request,
            @Field("fireid") String fireid,
            @Field("timestamp") String timestamp

    );

    @FormUrlEncoded
    @POST("allnotificationbylimit")
    Call<PushResponse> getNotificationData(
            @Field("page") int page,
            @Field("id") String id

    );

    @FormUrlEncoded
    @POST("getrequestbyid")
    Call<RequestResponse> getRequestId(
            @Field("id") int id

    );
}
