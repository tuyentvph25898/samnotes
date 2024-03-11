package com.thinkdiffai.cloud_note.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.Detail_Note;
import com.thinkdiffai.cloud_note.Model.GET.CommentModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private List<CommentModel> commentModels;

    public CommentAdapter(List<CommentModel> commentModels) {
        this.commentModels = commentModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentModel commentModel = commentModels.get(position);
        holder.tv_username.setText(commentModel.getUser_name());
        holder.tv_content.setText(commentModel.getContent());
        Glide.with(holder.itemView.getContext()).load(commentModel.getAvt()).into(holder.img_avatar);
    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img_avatar;
        TextView tv_username, tv_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.avt);
            tv_content = itemView.findViewById(R.id.content);
            tv_username = itemView.findViewById(R.id.username);
        }
    }
}
