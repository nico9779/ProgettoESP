package it.gliandroidiani.progettoesp;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        if(currentAlarm.isRingtone())
            viewHolder.ringtoneImg.setColorFilter(ContextCompat.getColor(viewHolder.ringtoneImg.getContext(), R.color.colorPrimary));
        else
            viewHolder.ringtoneImg.setColorFilter(ContextCompat.getColor(viewHolder.ringtoneImg.getContext(), R.color.grigioScuro));
        if(currentAlarm.isVibration())
            viewHolder.vibrationImg.setColorFilter(ContextCompat.getColor(viewHolder.vibrationImg.getContext(), R.color.colorPrimary));
        else
            viewHolder.vibrationImg.setColorFilter(ContextCompat.getColor(viewHolder.vibrationImg.getContext(), R.color.grigioScuro));
        if(currentAlarm.isActive()) {
            viewHolder.title.setTextColor(ContextCompat.getColor(viewHolder.title.getContext(), R.color.nero));
            viewHolder.time.setTextColor(ContextCompat.getColor(viewHolder.time.getContext(), R.color.nero));
            viewHolder.repetition.setTextColor(ContextCompat.getColor(viewHolder.repetition.getContext(), R.color.nero));
            viewHolder.alarmImg.setImageResource(R.drawable.ic_alarm_on);
        }
        else {
            viewHolder.title.setTextColor(ContextCompat.getColor(viewHolder.title.getContext(), R.color.grigioScuro));
            viewHolder.time.setTextColor(ContextCompat.getColor(viewHolder.time.getContext(), R.color.grigioScuro));
            viewHolder.repetition.setTextColor(ContextCompat.getColor(viewHolder.repetition.getContext(), R.color.grigioScuro));
            viewHolder.alarmImg.setImageResource(R.drawable.ic_alarm_off);
        }
        if(currentAlarm.getRepetitionType().equals("Una sola volta") || currentAlarm.getRepetitionType().equals("Giornalmente"))
            viewHolder.repetition.setText(currentAlarm.getRepetitionType());
        else {
            String item = "";
            String[] repetitionOptionsDaysShort = new String[] {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};
            for (int j = 0; j < currentAlarm.getRepetitionDays().length; j++) {
                if(currentAlarm.getRepetitionDays()[j]){
                    item = item + repetitionOptionsDaysShort[j] + " ";
                }
            }
            viewHolder.repetition.setText(item);
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    void setAlarms(List<Alarm> alarms){
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    Alarm getAlarmAt(int position){
        return alarms.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView time;
        private TextView repetition;
        private ImageView alarmImg;
        private ImageView ringtoneImg;
        private ImageView vibrationImg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.alarm_title_item);
            time = itemView.findViewById(R.id.alarm_time_item);
            ringtoneImg = itemView.findViewById(R.id.ringtone_img_item);
            vibrationImg = itemView.findViewById(R.id.vibration_img_item);
            alarmImg = itemView.findViewById(R.id.alarm_img);
            repetition = itemView.findViewById(R.id.alarm_repetition_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(alarms.get(position));
                }
            });

            alarmImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION)
                        listener.onImageClick(alarms.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Alarm alarm);
        void onImageClick(Alarm alarm);
    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
