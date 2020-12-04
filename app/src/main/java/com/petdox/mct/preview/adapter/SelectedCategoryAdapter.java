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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class SelectedCategoryAdapter extends RecyclerView.Adapter<SelectedCategoryAdapter.SubCategoryViewHolder> {

    List<String> selectedCategoryModelList = new ArrayList<>();
    Context context;

    public SelectedCategoryAdapter(Context context) {
        this.context = context;
    }

    public void addCategory(String selectedCategory) {
        selectedCategoryModelList.add(selectedCategory);
        Collections.sort(selectedCategoryModelList, String::compareToIgnoreCase);
        notifyDataSetChanged();
    }

    public void removeCategory(String selectedCategory) {
        selectedCategoryModelList.remove(selectedCategory);
        if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
            Collections.sort(selectedCategoryModelList, String::compareToIgnoreCase);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.selected_category_row_item, parent, false);
        return new SubCategoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

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

    public static class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout category_image;
        AppCompatTextView category_name;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_name = itemView.findViewById(R.id.category_name);
        }
    }
}
