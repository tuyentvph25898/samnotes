package com.thinkdiffai.cloud_note.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.Model.POST.GroupMember;

import java.util.List;

public class AdapterMember extends RecyclerView.Adapter<AdapterMember.ViewHilder>{
    private List<GroupMember> list;

    public AdapterMember(List<GroupMember> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHilder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHilder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHilder holder, int position) {
        GroupMember member = list.get(position);
        holder.tvGmail.setText(member.getGmail());
        holder.btnDelete.setOnClickListener(view -> {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, list.size());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHilder extends RecyclerView.ViewHolder{
        TextView tvGmail;
        ImageView btnDelete;
        public ViewHilder(@NonNull View itemView) {
            super(itemView);
            tvGmail = itemView.findViewById(R.id.tvGmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
