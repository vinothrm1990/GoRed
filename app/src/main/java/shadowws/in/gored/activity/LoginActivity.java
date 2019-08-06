package shadowws.in.gored.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shadowws.in.gored.R;
import shadowws.in.gored.other.GoRed;
import shadowws.in.gored.other.InternetListener;
import shadowws.in.gored.response.LoginResponse;
import shadowws.in.gored.retrofit.RetrofitAPI;
import shadowws.in.gored.retrofit.RetrofitBASE;
import thebat.lib.validutil.ValidUtils;

public class LoginActivity extends AppCompatActivity implements InternetListener.InternetReceiverListener {

    TextView tvClick;
    EditText etMobile, etPassword;
    Button btnSignIn;
    ValidUtils utils;
    ProgressLoadingJIGB progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        utils = new ValidUtils();

        tvClick = findViewById(R.id.log_tv_click);
        etMobile = findViewById(R.id.log_et_mobile);
        etPassword = findViewById(R.id.log_et_password);
        btnSignIn = findViewById(R.id.log_btn_sigin);

        String mobile = Prefs.getString("rmobile", null);
        String password = Prefs.getString("rpassword", null);

        if (!checkNullOrEmpty(mobile) && !checkNullOrEmpty(password)){

            etMobile.setText(mobile);
            etPassword.setText(password );

        }else {

            etMobile.setText("");
            etPassword.setText("");
        }


        tvClick.setOnClickListener((View v) -> {

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            ActivityOptionsCompat options  = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
            startActivity(intent, options.toBundle());
        });

        btnSignIn.setOnClickListener((View v) -> {

            if (utils.validateEditTexts(etMobile, etPassword)){

                login();

            }else {

                Alerter.create(LoginActivity.this)
                        .setTitle("Validation Error :")
                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                        .setText("Mandatory Feilds are Empty")
                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                        .setIcon(R.drawable.info)
                        .setIconColorFilter(0)
                        .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                        .setExitAnimation(R.anim.alerter_slide_out_to_right)
                        .setBackgroundColorRes(R.color.colorInfo)
                        .show();
            }

        });
    }

    private void loginConnected(boolean isConnected) {

        if (!isConnected){

            Alerter.create(LoginActivity.this)
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

            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            progress.startLoadingJIGB(LoginActivity.this,
                    R.raw.progress, "Please Wait...",
                    0,300,300);

            RetrofitAPI api = RetrofitBASE.getRetrofitInstance(LoginActivity.this).create(RetrofitAPI.class);
            Call<LoginResponse> call = api.loginUser(mobile, password);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    try {

                        if (response.isSuccessful()){

                            LoginResponse data = response.body();

                            if (data != null){

                                boolean error = data.getError();
                                String message = data.getMessage();

                                if (error == false){

                                    progress.finishLoadingJIGB(LoginActivity.this);

                                    LoginResponse.User results = data.getUser();

                                    String id = results.getId();
                                    String name = results.getName();
                                    String email = results.getEmail();
                                    String mobile = results.getMobile();
                                    String sex = results.getSex();
                                    String blood = results.getBlood();
                                    String visible = results.getVisible();

                                    Prefs.putBoolean("loggedIn", true);
                                    Prefs.putString("lid", id);
                                    Prefs.putString("lname", name);
                                    Prefs.putString("lemail", email);
                                    Prefs.putString("lmobile", mobile);
                                    Prefs.putString("lsex", sex);
                                    Prefs.putString("lblood", blood);
                                    Prefs.putString("lvisible", visible);

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    ActivityOptionsCompat options  = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                                    startActivity(intent, options.toBundle());

                                }else {

                                    progress.finishLoadingJIGB(LoginActivity.this);
                                    Alerter.create(LoginActivity.this)
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

                                progress.finishLoadingJIGB(LoginActivity.this);
                                Alerter.create(LoginActivity.this)
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

                        progress.finishLoadingJIGB(LoginActivity.this);
                        Alerter.create(LoginActivity.this)
                                .setTitle("Exception Caught :")
                                .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                .setText(e.toString())
                                .setTextAppearance(R.style.AlertTextAppearance_Text)
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                .setIcon(R.drawable.error)
                                .setIconColorFilter(0)
                                .setBackgroundColorRes(R.color.colorError)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    if (t.getMessage().equalsIgnoreCase("connect timed out")){
                        call.cancel();
                        login();

                    }else {

                        call.cancel();
                        progress.finishLoadingJIGB(LoginActivity.this);
                        Alerter.create(LoginActivity.this)
                                .setTitle("Exception Throwed :")
                                .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                .setText(t.toString())
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

    public static boolean checkNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    private void login() {
        boolean isConnected = InternetListener.isConnected();
        loginConnected(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoRed.getInstance().setListener(this);
        if (Prefs.getBoolean("loggedIn", false)){

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            ActivityOptionsCompat options  = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
            startActivity(intent, options.toBundle());
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        loginConnected(isConnected);
    }


}
