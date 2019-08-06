package shadowws.in.gored.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import shadowws.in.gored.adapter.DonorAdapter;
import shadowws.in.gored.other.InternetListener;
import shadowws.in.gored.other.PaginationScrollListener;
import shadowws.in.gored.response.DonorResponse;
import shadowws.in.gored.retrofit.RetrofitAPI;
import shadowws.in.gored.retrofit.RetrofitBASE;

public class DonorActivity extends AppCompatActivity {

    LinearLayout dataLayout, emptyLayout;
    RecyclerView rvDonor;
    DonorAdapter donorAdapter;
    LinearLayoutManager layoutManager;
    ProgressLoadingJIGB progress;
    SwipeRefreshLayout refreshLayout;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    RetrofitAPI api;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        dataLayout = findViewById(R.id.donor_data_layout);
        emptyLayout = findViewById(R.id.donor_empty_layout);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("FIND DONOR'S");
        title.setTextSize(18);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "big_john_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        refreshLayout = findViewById(R.id.swipe_donor);
        rvDonor = findViewById(R.id.rv_donor);

        donorAdapter = new DonorAdapter(DonorActivity.this);
        layoutManager = new LinearLayoutManager(this);
        rvDonor.setLayoutManager(layoutManager);
        rvDonor.setAdapter(donorAdapter);

        id = Prefs.getString("lid", null);

        donor();

        rvDonor.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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

    private void loadFirstPage() {

        progress.startLoadingJIGB(DonorActivity.this,
                R.raw.progress, "Please Wait...",
                0,300,300);

        currentPage = PAGE_START;

        callDonorData().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {

                try {

                    if (response.isSuccessful()){

                        DonorResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();

                            if (error == false){

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(DonorActivity.this);

                                List<DonorResponse.User> results = fetchResults(response);
                                donorAdapter.addAll(results);

                                String totalPages = results.get(0).getTotalPages();
                                TOTAL_PAGES = Integer.parseInt(totalPages);

                                if (currentPage >= TOTAL_PAGES){
                                    isLastPage = true;
                                }

                            }else {

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(DonorActivity.this);

                                Alerter.create(DonorActivity.this)
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
                            progress.finishLoadingJIGB(DonorActivity.this);

                            Alerter.create(DonorActivity.this)
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
                    progress.finishLoadingJIGB(DonorActivity.this);

                    Alerter.create(DonorActivity.this)
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
            public void onFailure(Call<DonorResponse> call, Throwable t) {


                if (t.getMessage().equalsIgnoreCase("connect timed out")){

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(DonorActivity.this);
                    call.cancel();
                    loadFirstPage();

                }else {

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(DonorActivity.this);

                    call.cancel();
                    Alerter.create(DonorActivity.this)
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

        progress.startLoadingJIGB(DonorActivity.this,
                R.raw.progress, "Please Wait...",
                0,300,300);

        callDonorData().enqueue(new Callback<DonorResponse>() {
            @Override
            public void onResponse(Call<DonorResponse> call, Response<DonorResponse> response) {

                try {

                    if (response.isSuccessful()){

                        isLoading = false;
                        DonorResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();

                            if (error == false){

                                refreshLayout.setRefreshing(false);
                                progress.finishLoadingJIGB(DonorActivity.this);

                                List<DonorResponse.User> results = fetchResults(response);

                                String totalPages = results.get(0).getTotalPages();
                                TOTAL_PAGES = Integer.parseInt(totalPages);
                                donorAdapter.addAll(results);

                                if (currentPage == TOTAL_PAGES){
                                    isLastPage = true;
                                }
                            }

                        }else {

                            refreshLayout.setRefreshing(false);
                            progress.finishLoadingJIGB(DonorActivity.this);

                            Alerter.create(DonorActivity.this)
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
                    progress.finishLoadingJIGB(DonorActivity.this);

                    Alerter.create(DonorActivity.this)
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
            public void onFailure(Call<DonorResponse> call, Throwable t) {

                if (t.getMessage().equalsIgnoreCase("connect timed out")){

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(DonorActivity.this);
                    call.cancel();
                    loadNextPage();

                }else {

                    refreshLayout.setRefreshing(false);
                    progress.finishLoadingJIGB(DonorActivity.this);

                    call.cancel();
                    Alerter.create(DonorActivity.this)
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

    private void donorConnected(boolean isConnected) {

        if (!isConnected){

            Alerter.create(DonorActivity.this)
                    .setTitle("Connection Error :")
                    .setTitleAppearance(R.style.AlertTextAppearance_Title)
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "sans_bold.ttf"))
                    .setText("No Internet Connection Available")
                    .setTextAppearance(R.style.AlertTextAppearance_Text)
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "sans_regular.ttf"))
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

    private void onRefresh() {
        progress.finishLoadingJIGB(DonorActivity.this);
        refreshLayout.setRefreshing(false);
        currentPage = PAGE_START;
        isLastPage = false;
        donorAdapter.getData().clear();
        donorAdapter.notifyDataSetChanged();
        donor();
    }

    private Call<DonorResponse> callDonorData(){
        return api.getDonorData(
                currentPage,
                id
        );
    }

    private List<DonorResponse.User> fetchResults(Response<DonorResponse> response) {
        DonorResponse donorResponse = response.body();
        return donorResponse.getUsers();
    }


    private void donor() {
        boolean isConnected = InternetListener.isConnected();
        donorConnected(isConnected);
    }


}
