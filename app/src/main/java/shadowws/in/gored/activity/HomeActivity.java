package shadowws.in.gored.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shadowws.in.gored.R;
import shadowws.in.gored.firebase.FirebaseConfig;
import shadowws.in.gored.firebase.FirebaseUtils;
import shadowws.in.gored.other.CountDrawable;
import shadowws.in.gored.other.CustomFont;
import shadowws.in.gored.other.GoRed;
import shadowws.in.gored.other.InternetListener;
import shadowws.in.gored.response.DashboardResponse;
import shadowws.in.gored.response.FirebaseResponse;
import shadowws.in.gored.retrofit.RetrofitAPI;
import shadowws.in.gored.retrofit.RetrofitBASE;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InternetListener.InternetReceiverListener {

    boolean doubleBackToExitPressedOnce = false;
    long totalUser;
    TextView tvDonors, tvRequest;
    Button btnDonors, btnRequest;
    ProgressBar progressDonor, progressRequest;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int notificationCount = 0;
    private Menu notifyMenu;
    String count = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("DASHBOARD");
        title.setTextSize(18);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "big_john_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SubMenu subMenu = menuItem.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    fontMenu(subMenuItem);
                }
            }
            fontMenu(menuItem);
        }
        String name = Prefs.getString("lname", null);
        TextView navTitle = headerView.findViewById(R.id.navTitle);
        if (!checkNullOrEmpty(name)){
            navTitle.setText(name);
        }else {
            navTitle.setText("User");
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        tvDonors = findViewById(R.id.home_tv_donors);
        tvRequest = findViewById(R.id.home_tv_requests);
        btnDonors = findViewById(R.id.home_btn_donors);
        btnRequest = findViewById(R.id.home_btn_requests);
        progressDonor = findViewById(R.id.progress_donor);
        progressRequest = findViewById(R.id.progress_request);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(FirebaseConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(FirebaseConfig.TOPIC_GLOBAL);
                    firebase();

                } else if (intent.getAction().equals(FirebaseConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    count = String.valueOf(notificationCount++);
                    onPrepareOptionsMenu(notifyMenu);
                }
            }
        };
        firebase();
        dashboard();

        btnDonors.setOnClickListener((View v) -> {

            Intent intent = new Intent(HomeActivity.this, DonorActivity.class);
            ActivityOptionsCompat options  = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
            startActivity(intent, options.toBundle());
        });
    }

    private void firebase() {

        String mobile = Prefs.getString("lmobile", null);
        String regId = Prefs.getString("reg_Id", null);
        Log.d(TAG, "Firebase Reg Id: " + regId);

        RetrofitAPI api = RetrofitBASE.getRetrofitInstance(this).create(RetrofitAPI.class);
        Call<FirebaseResponse> call = api.registerFirebase(mobile, regId);

        call.enqueue(new Callback<FirebaseResponse>() {
            @Override
            public void onResponse(Call<FirebaseResponse> call, Response<FirebaseResponse> response) {

                try{

                    if (response.isSuccessful()){

                        FirebaseResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();
                            String message = data.getMessage();

                            if (error == true){

                                Alerter.create(HomeActivity.this)
                                        .setTitle("Response Failed :")
                                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                        .setText(message)
                                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                        .setIcon(R.drawable.error)
                                        .setIconColorFilter(0)
                                        .setBackgroundColorRes(R.color.colorError)
                                        .show();
                            }

                        }else {

                            Alerter.create(HomeActivity.this)
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

                    Alerter.create(HomeActivity.this)
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
            public void onFailure(Call<FirebaseResponse> call, Throwable t) {

                if (t.getMessage().equalsIgnoreCase("connect timed out")){
                    call.cancel();
                    firebase();

                }else {

                    call.cancel();
                    Alerter.create(HomeActivity.this)
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

    private void dashboardConnected(boolean isConnected) {

        if (!isConnected){

            Alerter.create(HomeActivity.this)
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

            String id = Prefs.getString("lid", null);

            RetrofitAPI api = RetrofitBASE.getRetrofitInstance(this).create(RetrofitAPI.class);
            Call<DashboardResponse> call = api.dashboardData(id);

            call.enqueue(new Callback<DashboardResponse>() {
                @Override
                public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {

                    try {

                        if (response.isSuccessful()){

                            DashboardResponse data = response.body();

                            if (data != null){

                                boolean error = data.getError();

                                if (error == false){

                                    progressDonor.setVisibility(View.GONE);
                                    progressRequest.setVisibility(View.GONE);
                                    tvDonors.setVisibility(View.VISIBLE);
                                    tvRequest.setVisibility(View.VISIBLE);

                                    List<DashboardResponse.User> result = data.getUsers();

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        totalUser = result.stream().count();
                                    }

                                    if (result.isEmpty()){
                                        tvDonors.setText(0 +"\t\tDonor's");
                                        tvRequest.setText(0 +"\t\tRequest's");
                                    }else {
                                        tvDonors.setText(totalUser+"\t\tDonor's");
                                        tvRequest.setText(0 +"\t\tRequest's");
                                    }

                                }else {

                                    progressDonor.setVisibility(View.GONE);
                                    progressRequest.setVisibility(View.GONE);
                                    tvDonors.setVisibility(View.VISIBLE);
                                    tvRequest.setVisibility(View.VISIBLE);

                                    tvDonors.setText(0 +"\t\tDonor's");
                                    tvRequest.setText(0 +"\t\tRequest's");
                                }

                            }else {

                                progressDonor.setVisibility(View.GONE);
                                progressRequest.setVisibility(View.GONE);
                                tvDonors.setVisibility(View.VISIBLE);
                                tvRequest.setVisibility(View.VISIBLE);

                                tvDonors.setText(0 +"\t\tDonor's");
                                tvRequest.setText(0 +"\t\tRequest's");

                                Alerter.create(HomeActivity.this)
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

                        progressDonor.setVisibility(View.GONE);
                        progressRequest.setVisibility(View.GONE);
                        tvDonors.setVisibility(View.VISIBLE);
                        tvRequest.setVisibility(View.VISIBLE);

                        tvDonors.setText(0 +"\t\tDonor's");
                        tvRequest.setText(0 +"\t\tRequest's");

                        Alerter.create(HomeActivity.this)
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
                public void onFailure(Call<DashboardResponse> call, Throwable t) {

                    progressDonor.setVisibility(View.GONE);
                    progressRequest.setVisibility(View.GONE);
                    tvDonors.setVisibility(View.VISIBLE);
                    tvRequest.setVisibility(View.VISIBLE);

                    tvDonors.setText(0 +"\t\tDonor's");
                    tvRequest.setText(0 +"\t\tRequest's");

                    if (t.getMessage().equalsIgnoreCase("connect timed out")){
                        call.cancel();
                        dashboard();

                    }else {

                        call.cancel();
                        Alerter.create(HomeActivity.this)
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
    }

    private void dashboard() {
        boolean isConnected = InternetListener.isConnected();
        dashboardConnected(isConnected);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                return;
            }else {

                this.doubleBackToExitPressedOnce = true;
                Alerter.create(HomeActivity.this)
                        .setTitle("Info")
                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                        .setText("Click BACK Again to Exit")
                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                        .setIcon(R.drawable.info)
                        .setIconColorFilter(0)
                        .setBackgroundColorRes(R.color.colorInfo)
                        .show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 1500);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        notifyMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
           /* Prefs.clear();
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);*/
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            ActivityOptionsCompat options  = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
            startActivity(intent, options.toBundle());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!count.equalsIgnoreCase("0")) {
            setCount(HomeActivity.this, menu,'"' + count + '"');
        }else {
            setCount(HomeActivity.this, menu, "0");
        }
        return true;
    }

    private void setCount(HomeActivity homeActivity, Menu menu, String count) {

        MenuItem menuItem = menu.findItem(R.id.action_notification);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        CountDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(homeActivity);
        }
        Log.e("COUNT :", count);
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.group_count, badge);

        Prefs.putString("count", count);
    }

    private void fontMenu(MenuItem subMenuItem) {
        Typeface font = Typeface.createFromAsset(getAssets(), "avenir_light.otf");
        SpannableString mNewTitle = new SpannableString(subMenuItem.getTitle());
        mNewTitle.setSpan(new CustomFont("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        subMenuItem.setTitle(mNewTitle);
    }

    public static boolean checkNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        dashboardConnected(isConnected);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GoRed.freeMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dashboard();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(FirebaseConfig.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(FirebaseConfig.PUSH_NOTIFICATION));
        FirebaseUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dashboard();
    }
}
