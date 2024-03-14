package com.thinkdiffai.cloud_note.Adapter;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Model.GET.FolderModel;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.POST.FolderPostModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>{
    private List<FolderModel> list;

    public FolderAdapter(List<FolderModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FolderModel model = list.get(position);
        holder.folderName.setText(model.getNameFolder());
        holder.overflowMenu.setOnClickListener(view -> showPopupMenu(view, position, model.getId()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView folderName;
        ImageView overflowMenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            overflowMenu = itemView.findViewById(R.id.overflowMenu);
        }
    }
    public void showPopupMenu(View view, int position, int id) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.more_vert, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_rename:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                        builder1.setTitle("Rename");
                        final EditText input = new EditText(view.getContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        builder1.setView(input);
                        builder1.setPositiveButton("OK",(dialogInterface, i) -> {
                           String newName = input.getText().toString();
                           FolderPostModel model = new FolderPostModel();
                           model.setFolderName(newName);
                           editFolder(id,model);
                           dialogInterface.dismiss();
                           //chưa cập nhật ở client
                        });
                        builder1.setNegativeButton("Cancel", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });
                        AlertDialog alertDialog1 = builder1.create();
                        alertDialog1.show();
                        return true;
                    case R.id.menu_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete this folder?");
                        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                            deleteFolder(2, position);
                            dialogInterface.dismiss();
                        });
                        builder.setNegativeButton("No", (dialogInterface, i) -> {
                           dialogInterface.dismiss();
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void deleteFolder(int id, int position) {
        APINote.apiSV.deleteFolder(id).enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                if (response.isSuccessful()){
                    ModelReturn modelReturn = response.body();
                    list.remove(position);
                    Log.e("onResponse delFolder: ", modelReturn.getMessage());
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                Log.e("onFailure delFolder: ", t+"");
            }
        });
    }

    public void editFolder(int id, FolderPostModel model){
        APINote.apiSV.patchFolder(id, model).enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                if (response.isSuccessful()){
                    ModelReturn modelReturn = response.body();
                    Log.e("onResponse: ", modelReturn.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                Log.e( "onFailure editFolder: ", t+"");
            }
        });
    }

}
