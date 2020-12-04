package com.petdox.mct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.petdox.mct.callback.CallbackImageClick;
import com.petdox.mct.model.CarouselModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ImageViewHolder> {

    List<CarouselModel> carouselModelList = new ArrayList<>();
    Context context;
    CallbackImageClick callbackImageClick;

    public CarouselAdapter(Context context) {
        this.context = context;
    }

    public void setCallbackImageClick(CallbackImageClick callbackImageClick) {
        this.callbackImageClick = callbackImageClick;
    }

    public void updateList(List<CarouselModel> carouselModels) {
        if (carouselModelList != null && carouselModelList.size() > 0) {
            carouselModelList.clear();
        }

        assert carouselModelList != null;
        carouselModelList.addAll(carouselModels);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.carousel_row_item, parent, false);
        return new ImageViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        CarouselModel carouselModel = carouselModelList.get(position);

        Glide.with(context).load(carouselModel.getImages().get(0)).into(holder.pet_image);
        if (carouselModel.getImages() != null && carouselModel.getImages().size() == 1) {
            holder.image_position.setText(String.format(Locale.ENGLISH, "%d pic", carouselModel.getImages().size()));
        } else {
            holder.image_position.setText(String.format(Locale.ENGLISH, "%d pics", carouselModel.getImages().size()));
        }

    }

    @Override
    public int getItemCount() {
        return carouselModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView pet_image;
        AppCompatTextView image_position;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            pet_image = itemView.findViewById(R.id.pet_image);
            image_position = itemView.findViewById(R.id.image_position);
        }
    }
}
