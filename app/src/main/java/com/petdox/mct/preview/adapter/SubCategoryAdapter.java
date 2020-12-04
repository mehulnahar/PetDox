package com.petdox.mct.preview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muddzdev.styleabletoast.StyleableToast;
import com.petdox.mct.R;
import com.petdox.mct.callback.CallbackSubCategory;
import com.petdox.mct.preview.model.MainCategoryModel;
import com.petdox.mct.preview.model.SubCategoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

import static com.petdox.mct.preview.PreviewActivity.selectedCategoryModelList;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {

    List<SubCategoryModel> subCategoryModelList = new ArrayList<>();
    Context context;
    MainCategoryModel mainCategoryModel = null;
    CallbackSubCategory callbackSubCategory;

    public SubCategoryAdapter(Context context) {
        this.context = context;
    }

    public void setCallbackSubCategory(CallbackSubCategory callbackSubCategory) {
        this.callbackSubCategory = callbackSubCategory;
    }

    public void refreshList(MainCategoryModel mainCategoryModel) {
        if (subCategoryModelList != null && subCategoryModelList.size() > 0) {
            subCategoryModelList.clear();
        }
        this.mainCategoryModel = mainCategoryModel;
        subCategoryModelList = mainCategoryModel.getSubCategoryModelList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.category_row_item, parent, false);
        return new SubCategoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

        SubCategoryModel subCategoryModel = subCategoryModelList.get(position);
        holder.category_name.setText(subCategoryModel.getCategoryName());
        if (subCategoryModel.isSaved()) {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_dark_green_border);
        } else {
            holder.category_image.setBackgroundResource(R.drawable.rectangle_dark_blue_grey_border);
        }

        holder.main_category.setOnClickListener(v -> {

            for (SubCategoryModel bean :
                    subCategoryModelList) {
                if (!bean.isSaved()) {
                    bean.setSelected(false);
                }
            }

            if (subCategoryModel.isSaved()) {
                subCategoryModel.setSaved(!subCategoryModel.isSaved());

                boolean anySavedRemaining = false;

                for (SubCategoryModel bean :
                        subCategoryModelList) {
                    if (bean.isSaved()) {
                        anySavedRemaining = true;
                        break;
                    }
                }

                if (mainCategoryModel != null) {
                    mainCategoryModel.setSaved(anySavedRemaining);
                }

                if (callbackSubCategory != null) {
                    callbackSubCategory.selectSubCategory(subCategoryModel, mainCategoryModel, anySavedRemaining);
                }
                notifyDataSetChanged();
            } else {
                if (selectedCategoryModelList != null && selectedCategoryModelList.size() == 10) {
                    new StyleableToast
                            .Builder(context)
                            .text("You cannot add more than 10 sub-categories")
                            .textColor(Color.WHITE)
                            .length(Toast.LENGTH_SHORT)
                            .backgroundColor(ContextCompat.getColor(context, R.color.toast_background))
                            .show();
                } else {
                    subCategoryModel.setSaved(!subCategoryModel.isSaved());

                    boolean anySavedRemaining = false;

                    for (SubCategoryModel bean :
                            subCategoryModelList) {
                        if (bean.isSaved()) {
                            anySavedRemaining = true;
                            break;
                        }
                    }

                    if (mainCategoryModel != null) {
                        mainCategoryModel.setSaved(anySavedRemaining);
                    }

                    if (callbackSubCategory != null) {
                        callbackSubCategory.selectSubCategory(subCategoryModel, mainCategoryModel, anySavedRemaining);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subCategoryModelList.size();
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
        AutofitTextView category_name;
        RelativeLayout main_category;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            category_image = itemView.findViewById(R.id.category_image);
            category_name = itemView.findViewById(R.id.category_name);
            main_category = itemView.findViewById(R.id.main_category);
        }
    }
}
