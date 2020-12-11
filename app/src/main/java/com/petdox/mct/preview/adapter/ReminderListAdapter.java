package com.petdox.mct.preview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.petdox.mct.R;
import com.petdox.mct.callback.CallbackClickReminder;
import com.petdox.mct.preview.model.ReminderModel;

import java.util.List;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {

    List<ReminderModel> reminderList;
    Context context;
    CallbackClickReminder callbackClickReminder;

    public ReminderListAdapter(List<ReminderModel> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    public void setCallbackClickReminder(CallbackClickReminder callbackClickReminder) {
        this.callbackClickReminder = callbackClickReminder;
    }

    @NonNull
    @Override
    public ReminderListAdapter.ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.reminder_row_item, parent, false);
        return new ReminderViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderListAdapter.ReminderViewHolder holder, int position) {
        ReminderModel reminderModel = reminderList.get(position);

        holder.reminder_date.setText(reminderModel.getReminderName());

        if (reminderModel.isSelected()) {
            holder.date_click.setBackgroundResource(R.drawable.rectangle_dark_green_border);
        } else {
            holder.date_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
        }

        holder.date_click.setOnClickListener(v -> {
            for (ReminderModel bean :
                    reminderList) {
                if (!reminderModel.isSelected()) {
                    bean.setSelected(false);
                }
            }
            reminderModel.setSelected(!reminderModel.isSelected());
            if (callbackClickReminder != null) {
                callbackClickReminder.clickReminder(reminderModel);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout date_click;
        AppCompatTextView reminder_date;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            date_click = itemView.findViewById(R.id.date_click);
            reminder_date = itemView.findViewById(R.id.reminder_date);
        }
    }
}
