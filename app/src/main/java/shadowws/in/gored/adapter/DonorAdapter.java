package shadowws.in.gored.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shadowws.in.gored.R;
import shadowws.in.gored.response.DonorResponse;
import shadowws.in.gored.response.HelpResponse;
import shadowws.in.gored.response.NotificationResponse;
import shadowws.in.gored.retrofit.RetrofitAPI;
import shadowws.in.gored.retrofit.RetrofitBASE;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {

    Context context;
    List<DonorResponse.User> userList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    ProgressLoadingJIGB progress;

    public DonorAdapter(Context context) {
        this.context = context;
        userList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donor_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvName.setText(userList.get(position).getName());
        holder.tvLocation.setText(userList.get(position).getCity());
        holder.tvBlood.setText(userList.get(position).getBlood());

        char avatarName = userList.get(position).getName().charAt(0);

        Picasso.get()
                .load("https://ui-avatars.com/api/?name="+avatarName+"&background=EF241A&color=fff&rounded=true&uppercase=true&bold=true&size=512&length=1")
                .into(holder.avatarView);

        holder.cardView.setOnClickListener((View v) -> {

            String fid = Prefs.getString("lid", null);
            String fname = Prefs.getString("lname", null);
            String tid = String.valueOf(userList.get(position).getId());
            String tname = userList.get(position).getName();
            String title = "Help Request";
            String message = fname+"Request for Your Blood";
            String type = "individual";
            String fireid = userList.get(position).getFirebase();

            help(title, message, type, fireid, fid, fname, holder.tvHelp, tname, tid, holder.cardView);
        });
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == userList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarView;
        TextView tvName, tvLocation, tvBlood, tvHelp;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.donor_avatar);
            tvName = itemView.findViewById(R.id.donor_name_tv);
            tvLocation = itemView.findViewById(R.id.donor_location_tv);
            tvBlood = itemView.findViewById(R.id.donor_blood_tv);
            tvHelp = itemView.findViewById(R.id.donor_help_tv);
            cardView = itemView.findViewById(R.id.donor_cv);
        }
    }

    public void add(DonorResponse.User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }

    public void addAll(List<DonorResponse.User> userList) {
        for (DonorResponse.User list : userList) {
            add(list);
        }
    }

    public List<DonorResponse.User> getData() {
        return userList;
    }

    private void help(String title, String message, String type, String fireid, String uid, String uname, TextView tvHelp, String tname, String tid, CardView cardView) {

        progress.startLoadingJIGB(context,
                R.raw.progress, "Please Wait...",
                0,300,300);

        RetrofitAPI api = RetrofitBASE.getRetrofitInstance(context).create(RetrofitAPI.class);
        Call<NotificationResponse> call = api.postNotification(title, message, type, fireid, uid, uname);

        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {

                try {

                    if (response.isSuccessful()){

                        NotificationResponse data = response.body();

                        if (data != null){

                            //String to = data.getTo();

                            progress.finishLoadingJIGB(context);
                            Toast.makeText(context, "Requested Successfully to\t"+tname, Toast.LENGTH_SHORT).show();

                            tvHelp.setText("Requested");
                            cardView.setEnabled(false);

                            Long tsLong = System.currentTimeMillis()/1000;
                            String timestamp = tsLong.toString();

                            notification(uid, tid, title, message, type, fireid, timestamp);

                        }else {

                            progress.finishLoadingJIGB(context);
                            Toast.makeText(context, "No Response", Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch (Exception e){

                    progress.finishLoadingJIGB(context);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                progress.finishLoadingJIGB(context);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notification(String uid, String tid, String title, String message, String type, String fireid, String timestamp) {

        RetrofitAPI api = RetrofitBASE.getRetrofitInstance(context).create(RetrofitAPI.class);
        Call<HelpResponse> call = api.postHelpRequest(uid, tid, title, message, type, "Help", fireid, timestamp);

        call.enqueue(new Callback<HelpResponse>() {
            @Override
            public void onResponse(Call<HelpResponse> call, Response<HelpResponse> response) {

                try {

                    if (response.isSuccessful()){

                        HelpResponse data = response.body();

                        if (data != null){

                            boolean error = data.getError();
                            String message = data.getMessage();

                            if (error == true){
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HelpResponse> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
