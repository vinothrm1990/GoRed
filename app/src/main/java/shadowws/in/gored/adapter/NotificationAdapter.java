package shadowws.in.gored.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import shadowws.in.gored.R;
import shadowws.in.gored.response.PushResponse;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Context context;
    List<PushResponse.Notification> notifyList;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    ProgressLoadingJIGB progress;

    public NotificationAdapter(Context context) {
        this.context = context;
        notifyList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(notifyList.get(position).getTitle());
        holder.tvMessage.setText(notifyList.get(position).getMessage());
        holder.tvTime.setText(getDate(Long.parseLong(notifyList.get(position).getTimestamp())));

    }

    @Override
    public int getItemCount() {
        return notifyList == null ? 0 : notifyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == notifyList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.notify_tv_title);
            tvMessage = itemView.findViewById(R.id.notify_tv_message);
            tvTime = itemView.findViewById(R.id.notify_tv_timestamp);
        }
    }

    public void add(PushResponse.Notification notification) {
        notifyList.add(notification);
        notifyItemInserted(notifyList.size() - 1);
    }

    public void addAll(List<PushResponse.Notification> userList) {
        for (PushResponse.Notification notification : userList) {
            add(notification);
        }
    }

    public List<PushResponse.Notification> getData() {
        return notifyList;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy h:mm:ss", cal).toString();
        return date;
    }
}
