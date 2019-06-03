package it.gliandroidiani.progettoesp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarm> alarms = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmAdapter.ViewHolder viewHolder, int i) {
        Alarm currentAlarm = alarms.get(i);
        viewHolder.title.setText(currentAlarm.getTitle());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, currentAlarm.getHours());
        c.set(Calendar.MINUTE, currentAlarm.getMinute());
        c.set(Calendar.SECOND, 0);
        viewHolder.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()));
        if(currentAlarm.isVibration())
            viewHolder.vibration.setText(R.string.vibration_on);
        else
            viewHolder.vibration.setText(R.string.vibration_off);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<Alarm> alarms){
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    public Alarm getAlarmAt(int position){
        return alarms.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView time;
        private TextView vibration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.alarm_title_item);
            time = itemView.findViewById(R.id.alarm_time_item);
            vibration = itemView.findViewById(R.id.alarm_vibration_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(alarms.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Alarm alarm);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}