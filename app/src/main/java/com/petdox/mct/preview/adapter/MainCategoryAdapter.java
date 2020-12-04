package com.petdox.mct.preview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petdox.mct.R;
import com.petdox.mct.callback.CallbackMainCategory;
import com.petdox.mct.preview.model.MainCategoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.MainCategoryViewHolder> {

    List<MainCategoryModel> mainCategoryModelList = new ArrayList<>();
    Context context;
    CallbackMainCategory callbackMainCategory;

    public MainCategoryAdapter(Context context) {
        this.context = context;
    }

    public void setCallbackMainCategory(CallbackMainCategory callbackMainCategory) {
        this.callbackMainCategory = callbackMainCategory;
    }

    public void refreshList(List<MainCategoryModel> mainCategoryModel) {
        if (mainCategoryModelList != null && mainCategoryModelList.size() > 0) {
            mainCategoryModelList.clear();
        }

        mainCategoryModelList = mainCategoryModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.category_row_item, parent, false);
        return new MainCategoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainCategoryViewHolder holder, int position) {

        MainCategoryModel mainCategoryModel = mainCategoryModelList.get(position);
        holder.category_name.setText(mainCategoryModel.getCategoryName());
        if (mainCategoryModel.isSaved()) {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_dark_green_border);
        } else if (mainCategoryModel.isSelected()) {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_dark_blue_border);
        } else if (!mainCategoryModel.getCategoryName().contains("other")) {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_dark_grey_border);
        } else {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_light_grey_border);
        }

        holder.main_category.setOnClickListener(v -> {

            if (!mainCategoryModel.getCategoryName().contains("other")) {
                for (MainCategoryModel bean :
                        mainCategoryModelList) {
                    if (!bean.isSaved()) {
                        bean.setSelected(false);
                    }
                }
                mainCategoryModel.setSelected(!mainCategoryModel.isSelected());
            }
            if (callbackMainCategory != null) {
                callbackMainCategory.selectMainCategory(mainCategoryModel);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mainCategoryModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MainCategoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout category_image;
        AutofitTextView category_name;
        RelativeLayout main_category;

        public MainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_name = itemView.findViewById(R.id.category_name);
            main_category = itemView.findViewById(R.id.main_category);
        }
    }
}
