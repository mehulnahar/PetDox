package com.petdox.mct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class MainSelectedCategoryAdapter extends RecyclerView.Adapter<MainSelectedCategoryAdapter.MainSelectedCategoryViewHolder> {

    List<String> selectedCategoryModelList = new ArrayList<>();
    Context context;

    public MainSelectedCategoryAdapter(Context context) {
        this.context = context;
    }

    public void addCategory(List<String> selectedCategoryModel) {
        if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
            selectedCategoryModelList.clear();
        }
        assert selectedCategoryModelList != null;
        selectedCategoryModelList.addAll(selectedCategoryModel);
        Collections.sort(selectedCategoryModelList, String::compareToIgnoreCase);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainSelectedCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.main_selected_category_row_item, parent, false);
        return new MainSelectedCategoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainSelectedCategoryViewHolder holder, int position) {

        String selectedCategoryModel = selectedCategoryModelList.get(position);
        holder.category_name.setText(selectedCategoryModel);
        if (!selectedCategoryModel.toLowerCase().contains("other")) {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_dark_green_border);
        } else {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_light_grey_border);
        }


    }

    @Override
    public int getItemCount() {
        return selectedCategoryModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MainSelectedCategoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout category_image;
        AppCompatTextView category_name;

        public MainSelectedCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_name = itemView.findViewById(R.id.category_name);
        }
    }
}
