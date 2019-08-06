package shadowws.in.gored.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shadowws.in.gored.R;
import shadowws.in.gored.other.InternetListener;
import shadowws.in.gored.response.RegisterResponse;
import shadowws.in.gored.retrofit.RetrofitAPI;
import shadowws.in.gored.retrofit.RetrofitBASE;
import thebat.lib.validutil.ValidUtils;

public class RegisterActivity extends AppCompatActivity implements InternetListener.InternetReceiverListener {

    EditText etName, etMobile, etEmail, etPassword;
    RadioGroup rgGender, rgBloodPlus, rgBloodMinus;
    RadioButton rbMale, rbFemale, rbAplus, rbAminus, rbBplus, rbBminus, rbOplus, rbOminus, rbABplus, rbABminus;
    Button btnRegister;
    CheckBox cbVisible;
    String sex, blood, visible, city, strLocation;
    ValidUtils utils;
    Spinner spLocation;
    ProgressLoadingJIGB progress;
    List<String> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        utils = new ValidUtils();

        etName = findViewById(R.id.reg_et_name);
        etMobile = findViewById(R.id.reg_et_mobile);
        etEmail = findViewById(R.id.reg_et_email);
        etPassword = findViewById(R.id.reg_et_password);
        cbVisible = findViewById(R.id.reg_cb_visible);
        spLocation = findViewById(R.id.reg_sp_location);
        btnRegister = findViewById(R.id.reg_btn_register);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbAplus = findViewById(R.id.rbAplus);
        rbAminus = findViewById(R.id.rbAminus);
        rbBplus = findViewById(R.id.rbBplus);
        rbBminus = findViewById(R.id.rbBminus);
        rbOplus = findViewById(R.id.rbOplus);
        rbOminus = findViewById(R.id.rbOminus);
        rbABplus = findViewById(R.id.rbABplus);
        rbABminus = findViewById(R.id.rbABminus);
        rgGender = findViewById(R.id.rgGender);
        rgBloodPlus = findViewById(R.id.rgBloodPlus);
        rgBloodMinus = findViewById(R.id.rgBloodMinus);

        locationList = new ArrayList<>();
        locationList.add("Chennai");
        locationList.add("Coimbatore");
        locationList.add("Madurai");
        locationList.add("Tiruchirappalli");
        locationList.add("Tiruppur");
        locationList.add("Salem");
        locationList.add("Erode");
        locationList.add("Tirunelveli");
        locationList.add("Vellore");
        locationList.add("Thoothukkudi");
        locationList.add("Dindigul");
        locationList.add("Thanjavur");
        locationList.add("Ranipet");
        locationList.add("Sivakasi");
        locationList.add("Karur");
        locationList.add("Udhagamandalam");
        locationList.add("Hosur");
        locationList.add("Nagercoil");
        locationList.add("Kancheepuram");
        locationList.add("Kumarapalayam");
        locationList.add("Karaikkudi");
        locationList.add("Neyveli");
        locationList.add("Cuddalore");
        locationList.add("Kumbakonam");
        locationList.add("Tiruvannamalai");
        locationList.add("Pollachi");
        locationList.add("Rajapalayam");
        locationList.add("Gudiyatham");
        locationList.add("Pudukkottai");
        locationList.add("Vaniyambadi");
        locationList.add("Ambur");
        locationList.add("Nagapattinam");

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
        spLocation.setAdapter(locationAdapter);

        spLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                strLocation = String.valueOf(adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rgBloodPlus.setOnCheckedChangeListener((RadioGroup radioGroup, int i) ->{
            if (radioGroup.getCheckedRadioButtonId() != -1){
                rgBloodMinus.clearCheck();
            }
        });

        rgBloodMinus.setOnCheckedChangeListener((RadioGroup radioGroup, int i) ->{
            if (radioGroup.getCheckedRadioButtonId() != -1){
                rgBloodPlus.clearCheck();
            }
        });

        btnRegister.setOnClickListener((View v) -> {

            if (utils.validateEditTexts(etName, etMobile, etEmail, etPassword)){

                register();

            }else {

                Alerter.create(RegisterActivity.this)
                        .setTitle("Validation Error :")
                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                        .setText("Mandatory Feilds are Empty")
                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                        .setIcon(R.drawable.error)
                        .setIconColorFilter(0)
                        .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                        .setExitAnimation(R.anim.alerter_slide_out_to_right)
                        .setBackgroundColorRes(R.color.colorInfo)
                        .show();

            }

        });

    }

    private void registerConnected(boolean isConnected) {

        if (!isConnected){

            Alerter.create(RegisterActivity.this)
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

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (utils.validateForDigits(etMobile, 10)){

                if (rgGender.getCheckedRadioButtonId() == -1 && rgBloodPlus.getCheckedRadioButtonId() == -1
                && rgBloodMinus.getCheckedRadioButtonId() == -1){

                    Alerter.create(RegisterActivity.this)
                            .setTitle("Validation Error :")
                            .setTitleAppearance(R.style.AlertTextAppearance_Title)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                            .setText("Sex & Blood Group is UnChecked")
                            .setTextAppearance(R.style.AlertTextAppearance_Text)
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                            .setIcon(R.drawable.info)
                            .setIconColorFilter(0)
                            .setBackgroundColorRes(R.color.colorWarning)
                            .show();

                }else {

                    if (rgGender.getCheckedRadioButtonId() == -1) {

                        Alerter.create(RegisterActivity.this)
                                .setTitle("Validation Error :")
                                .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                .setText("Sex Group is UnChecked")
                                .setTextAppearance(R.style.AlertTextAppearance_Text)
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                .setIcon(R.drawable.info)
                                .setIconColorFilter(0)
                                .setBackgroundColorRes(R.color.colorWarning)
                                .show();
                    }else {

                        int genderId = rgGender.getCheckedRadioButtonId();

                        if (genderId == rbMale.getId()) {
                            sex = "Male";
                        } else if (genderId == rbFemale.getId()) {
                            sex = "Female";
                        }

                        if (rgBloodPlus.getCheckedRadioButtonId() == -1
                                && rgBloodMinus.getCheckedRadioButtonId() == -1) {

                            Alerter.create(RegisterActivity.this)
                                    .setTitle("Validation Error :")
                                    .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                    .setText("Blood Group is UnChecked")
                                    .setTextAppearance(R.style.AlertTextAppearance_Text)
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                    .setIcon(R.drawable.info)
                                    .setIconColorFilter(0)
                                    .setBackgroundColorRes(R.color.colorWarning)
                                    .show();

                        }else {

                            int bloodPlus = rgBloodPlus.getCheckedRadioButtonId();
                            int bloodMinus = rgBloodMinus.getCheckedRadioButtonId();

                            if (bloodPlus == rbAplus.getId()) {
                                blood = "A+";
                            }else if (bloodPlus == rbBplus.getId()){
                                blood = "B+";
                            }else if (bloodPlus == rbOplus.getId()){
                                blood = "O+";
                            }else if (bloodPlus == rbABplus.getId()){
                                blood = "AB+";
                            }

                            if (bloodMinus == rbAminus.getId()) {
                                blood = "A-";
                            }else if (bloodMinus == rbBminus.getId()){
                                blood = "B-";
                            }else if (bloodMinus == rbOminus.getId()){
                                blood = "O-";
                            }else if (bloodMinus == rbABminus.getId()){
                                blood = "AB-";
                            }

                            if (cbVisible.isChecked()) {

                                visible = "1";

                                city = spLocation.getSelectedItem().toString();
                                signup(name, email, mobile, password, sex, blood, visible, city);

                            } else {

                                visible = "0";

                                city = spLocation.getSelectedItem().toString();
                                signup(name, email, mobile, password, sex, blood, visible, city);
                            }
                        }

                    }
                }

            }else {

                Alerter.create(RegisterActivity.this)
                        .setTitle("Validation Error :")
                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                        .setText("Not a Valid Mobile Number")
                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                        .setIcon(R.drawable.info)
                        .setIconColorFilter(0)
                        .setBackgroundColorRes(R.color.colorWarning)
                        .show();
            }
        }
    }

    private void signup(String name, String email, String mobile, String password, String sex, String blood, String visible, String city) {

        progress.startLoadingJIGB(RegisterActivity.this,
                R.raw.progress, "Please Wait...",
                0,300,300);

        RetrofitAPI api = RetrofitBASE.getRetrofitInstance(RegisterActivity.this).create(RetrofitAPI.class);
        Call<RegisterResponse> call = api.registerUser(name, email, mobile, password, sex, blood, visible, city);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                try{

                    if (response.isSuccessful()){

                        RegisterResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();
                            String message = data.getMessage();

                            if (error == false){

                                progress.finishLoadingJIGB(RegisterActivity.this);
                                Alerter.create(RegisterActivity.this)
                                        .setTitle("Response Success :")
                                        .setTitleAppearance(R.style.AlertTextAppearance_Title)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "avenir_bold.otf"))
                                        .setText(message)
                                        .setTextAppearance(R.style.AlertTextAppearance_Text)
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "avenir_light.otf"))
                                        .setIcon(R.drawable.success)
                                        .setIconColorFilter(0)
                                        .setBackgroundColorRes(R.color.colorSuccess)
                                        .show();

                                Prefs.putString("rmobile", mobile);
                                Prefs.putString("rpassword", password);

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        ActivityOptionsCompat options  = ActivityOptionsCompat.makeSceneTransitionAnimation(RegisterActivity.this);
                                        startActivity(intent, options.toBundle());
                                    }
                                }, 2000);

                            }else {

                                progress.finishLoadingJIGB(RegisterActivity.this);
                                Alerter.create(RegisterActivity.this)
                                        .setTitle("Response Error :")
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

                            progress.finishLoadingJIGB(RegisterActivity.this);
                            Alerter.create(RegisterActivity.this)
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

                    progress.finishLoadingJIGB(RegisterActivity.this);
                    e.printStackTrace();
                    Alerter.create(RegisterActivity.this)
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
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

                progress.finishLoadingJIGB(RegisterActivity.this);
                call.cancel();
                Alerter.create(RegisterActivity.this)
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
        });

    }

    private void register() {
        boolean isConnected = InternetListener.isConnected();
        registerConnected(isConnected);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        registerConnected(isConnected);
    }
}
