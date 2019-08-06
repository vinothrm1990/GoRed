package shadowws.in.gored.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shadowws.in.gored.R;
import shadowws.in.gored.adapter.NotificationAdapter;
import shadowws.in.gored.other.InternetListener;
import shadowws.in.gored.other.PaginationScrollListener;
import shadowws.in.gored.response.PushResponse;
import shadowws.in.gored.retrofit.RetrofitAPI;
import shadowws.in.gored.retrofit.RetrofitBASE;

public class NotificationActivity extends AppCompatActivity implements InternetListener.InternetReceiverListener {

    RecyclerView rvNotification;
    NotificationAdapter notificationAdapter;
    LinearLayoutManager layoutManager;
    ProgressLoadingJIGB progress;
    SwipeRefreshLayout refreshLayout;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private String uid;
    RetrofitAPI api;
    LinearLayout dataLayout, emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("NOTIFICATIONS");
        title.setTextSize(18);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "big_john_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        uid = Prefs.getString("lid", null);

        refreshLayout = findViewById(R.id.swipe_notify);
        rvNotification = findViewById(R.id.rv_notify);
        dataLayout = findViewById(R.id.notify_data_layout);
        emptyLayout = findViewById(R.id.notify_empty_layout);

        notificationAdapter = new NotificationAdapter(NotificationActivity.this);
        layoutManager = new LinearLayoutManager(this);
        rvNotification.setLayoutManager(layoutManager);
        rvNotification.setAdapter(notificationAdapter);

        notification();

        rvNotification.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {

                if (TOTAL_PAGES >= 10){
                    isLoading = true;
                    currentPage++;
                    loadNextPage();
                }
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void notificationConnected(boolean isConnected) {

        if (!isConnected){

            Alerter.create(NotificationActivity.this)
                    .setTitle("Connection Error :")
                    .setTitleAppearance(R.style.AlertTextAppearance_Title)
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                    .setText("No Internet Connection Available")
                    .setTextAppearance(R.style.AlertTextAppearance_Text)
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                    .setIcon(R.drawable.network)
                    .setIconColorFilter(0)
                    .setBackgroundColorRes(R.color.colorWarning)
                    .show();

        }else {

            api = RetrofitBASE.getRetrofitInstance(this).create(RetrofitAPI.class);
            loadFirstPage();
            refreshLayout.setOnRefreshListener(this::onRefresh);
        }
    }

    private void loadFirstPage() {

        progress.startLoadingJIGB(NotificationActivity.this,
                R.raw.progress, "Please Wait...",
                0,300,300);

        currentPage = PAGE_START;

        callNotificationData().enqueue(new Callback<PushResponse>() {
            @Override
            public void onResponse(Call<PushResponse> call, Response<PushResponse> response) {

                try {

                    if (response.isSuccessful()){

                        PushResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();

                            if (error == false){

                                dataLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(NotificationActivity.this);

                                List<PushResponse.Notification> results = fetchResults(response);
                                notificationAdapter.addAll(results);

                                String totalPages = results.get(0).getTotalPages();
                                TOTAL_PAGES = Integer.parseInt(totalPages);

                                if (currentPage >= TOTAL_PAGES){
                                    isLastPage = true;
                                }

                            }else {

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(NotificationActivity.this);

                                Alerter.create(NotificationActivity.this)
                                        .setTitle("Null Exception :")
                                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                        .setText("No Data")
                                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                        .setIcon(R.drawable.error)
                                        .setIconColorFilter(0)
                                        .setBackgroundColorRes(R.color.colorError)
                                        .show();
                            }

                        }else {

                            refreshLayout.setRefreshing(false);
                            progress.finishLoadingJIGB(NotificationActivity.this);

                            Alerter.create(NotificationActivity.this)
                                    .setTitle("Null Exception :")
                                    .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                    .setText("No Data")
                                    .setTextAppearance(R.style.AlertTextAppearance_Text)
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                    .setIcon(R.drawable.error)
                                    .setIconColorFilter(0)
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();
                        }
                    }

                }catch (Exception e){

                    if (e.getMessage().equalsIgnoreCase("Index: 0, Size: 0")){
                        dataLayout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);

                    }else {

                        refreshLayout.setRefreshing(false);
                        progress.finishLoadingJIGB(NotificationActivity.this);

                        Alerter.create(NotificationActivity.this)
                                .setTitle("Exception Caught :")
                                .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                .setText(e.getMessage())
                                .setTextAppearance(R.style.AlertTextAppearance_Text)
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                .setIcon(R.drawable.error)
                                .setIconColorFilter(0)
                                .setBackgroundColorRes(R.color.colorError)
                                .show();
                    }

                }
            }

            @Override
            public void onFailure(Call<PushResponse> call, Throwable t) {

                if (t.getMessage().equalsIgnoreCase("connect timed out")){

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(NotificationActivity.this);
                    call.cancel();
                    loadFirstPage();

                }else {

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(NotificationActivity.this);

                    call.cancel();
                    Alerter.create(NotificationActivity.this)
                            .setTitle("Exception Throwed :")
                            .setTitleAppearance(R.style.AlertTextAppearance_Title)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                            .setText(t.getMessage())
                            .setTextAppearance(R.style.AlertTextAppearance_Text)
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                            .setIcon(R.drawable.error)
                            .setIconColorFilter(0)
                            .setBackgroundColorRes(R.color.colorError)
                            .show();
                }

            }
        });
    }

    private void loadNextPage() {

        progress.startLoadingJIGB(NotificationActivity.this,
                R.raw.progress, "Please Wait...",
                0,300,300);

        currentPage = PAGE_START;

        callNotificationData().enqueue(new Callback<PushResponse>() {
            @Override
            public void onResponse(Call<PushResponse> call, Response<PushResponse> response) {

                try {

                    if (response.isSuccessful()){

                        isLoading = false;
                        PushResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();

                            if (error == false){

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(NotificationActivity.this);

                                List<PushResponse.Notification> results = fetchResults(response);
                                notificationAdapter.addAll(results);

                                String totalPages = results.get(0).getTotalPages();
                                TOTAL_PAGES = Integer.parseInt(totalPages);

                                if (currentPage == TOTAL_PAGES){
                                    isLastPage = true;
                                }

                            }else {

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(NotificationActivity.this);

                                Alerter.create(NotificationActivity.this)
                                        .setTitle("Null Exception :")
                                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                        .setText("No Data")
                                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                        .setIcon(R.drawable.error)
                                        .setIconColorFilter(0)
                                        .setBackgroundColorRes(R.color.colorError)
                                        .show();
                            }

                        }else {

                            refreshLayout.setRefreshing(false);
                            progress.finishLoadingJIGB(NotificationActivity.this);

                            Alerter.create(NotificationActivity.this)
                                    .setTitle("Null Exception :")
                                    .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                    .setText("No Data")
                                    .setTextAppearance(R.style.AlertTextAppearance_Text)
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                    .setIcon(R.drawable.error)
                                    .setIconColorFilter(0)
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();
                        }
                    }

                }catch (Exception e){

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(NotificationActivity.this);

                    Alerter.create(NotificationActivity.this)
                            .setTitle("Exception Caught :")
                            .setTitleAppearance(R.style.AlertTextAppearance_Title)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                            .setText(e.getMessage())
                            .setTextAppearance(R.style.AlertTextAppearance_Text)
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                            .setIcon(R.drawable.error)
                            .setIconColorFilter(0)
                            .setBackgroundColorRes(R.color.colorError)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<PushResponse> call, Throwable t) {

                if (t.getMessage().equalsIgnoreCase("connect timed out")){

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(NotificationActivity.this);
                    call.cancel();
                    loadFirstPage();

                }else {

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(NotificationActivity.this);

                    call.cancel();
                    Alerter.create(NotificationActivity.this)
                            .setTitle("Exception Throwed :")
                            .setTitleAppearance(R.style.AlertTextAppearance_Title)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                            .setText(t.getMessage())
                            .setTextAppearance(R.style.AlertTextAppearance_Text)
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                            .setIcon(R.drawable.error)
                            .setIconColorFilter(0)
                            .setBackgroundColorRes(R.color.colorError)
                            .show();
                }

            }
        });
    }

    private Call<PushResponse> callNotificationData(){
        return api.getNotificationData(
                currentPage,
                uid
        );
    }

    private List<PushResponse.Notification> fetchResults(Response<PushResponse> response) {
        PushResponse pushResponse = response.body();
        return pushResponse.getNotification();
    }

    private void onRefresh() {
        progress.finishLoadingJIGB(NotificationActivity.this);
        notificationAdapter.getData().clear();
        notificationAdapter.notifyDataSetChanged();
        loadFirstPage();
        refreshLayout.setRefreshing(false);
    }

    private void notification() {
        boolean isConnected = InternetListener.isConnected();
        notificationConnected(isConnected);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        notificationConnected(isConnected);
    }
}
