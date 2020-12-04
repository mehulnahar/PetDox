package com.petdox.mct.preview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.petdox.mct.R;
import com.petdox.mct.callback.CallbackImagePreview;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class PreviewImagesAdapter extends RecyclerView.Adapter<PreviewImagesAdapter.ImageViewHolder> {

    List<String> imagesList = new ArrayList<>();
    Context context;
    CallbackImagePreview callbackImagePreview;

    public PreviewImagesAdapter(Context context) {
        this.context = context;
    }

    public void setCallbackImagePreview(CallbackImagePreview callbackImagePreview) {
        this.callbackImagePreview = callbackImagePreview;
    }

    public void addImage(String previewImageModel) {
        imagesList.add(previewImageModel);
        notifyDataSetChanged();
    }

    public void removeImage(String previewImageModel) {
        imagesList.remove(previewImageModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.images_row_item, parent, false);
        return new ImageViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String previewImageModel = imagesList.get(position);

        Glide.with(context).load(previewImageModel).into(holder.pet_image);
        holder.image_position.setText(String.format(Locale.ENGLISH, "%d/%d", position + 1, imagesList.size()));

        holder.pet_image.setOnClickListener(v -> {
            if (callbackImagePreview != null) {
                callbackImagePreview.openImagePreview();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
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
