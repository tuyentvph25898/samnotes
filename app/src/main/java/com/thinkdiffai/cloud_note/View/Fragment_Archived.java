package com.thinkdiffai.cloud_note.View;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.FolderAdapter;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.FolderModel;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.GET.Model_Notes;
import com.thinkdiffai.cloud_note.Model.GET.ResponseFolder;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.Model.POST.FolderPostModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Archived extends Fragment {
    Context context;
    Login daoLogin;
    Model_State_Login user;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment__archived, container, false);
        recyclerView = view.findViewById(R.id.rcv_folder);
        daoLogin = new Login(context);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = daoLogin.getLogin();
        getListFolder();
    }

    private void getListFolder() {
        APINote.apiSV.getFolder(user.getIdUer()).enqueue(new Callback<ResponseFolder>() {
            @Override
            public void onResponse(Call<ResponseFolder> call, Response<ResponseFolder> response) {
                if (response.isSuccessful()){
                    ResponseFolder folder = response.body();
                    if (folder != null){
                        List<FolderModel> model = folder.getFolder();
                        FolderAdapter adapter = new FolderAdapter(model);
                        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseFolder> call, Throwable t) {
                Log.e( "onFailure get Folder: ", t+"");
            }
        });
    }
    private void postFolder(FolderPostModel obj){
        APINote.apiSV.postFolder(user.getIdUer(), obj).enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                if (response.isSuccessful()){
                    ModelReturn modelReturn = response.body();
                    Log.e( "onResponse postFolder: ", modelReturn.getMessage());
                }


            }

            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                Log.e("onFailure postFolder: ", t+"");
            }
        });
    }
}