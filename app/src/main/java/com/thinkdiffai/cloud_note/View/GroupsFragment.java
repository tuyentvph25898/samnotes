package com.thinkdiffai.cloud_note.View;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.GroupAdapter;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.GroupModel;
import com.thinkdiffai.cloud_note.Model.GET.ResponseGroup;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.TextNoteActvity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsFragment extends Fragment {
    Context context;
    private RecyclerView recyclerView;
    Login daoLogin;
    Model_State_Login user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView = view.findViewById(R.id.recycler_View);
        daoLogin = new Login(getContext());
        user = daoLogin.getLogin();
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListGroup();
    }

    private void getListGroup() {
        APINote.apiService.getAllGroup(user.getIdUer()).enqueue(new Callback<ResponseGroup>() {
            @Override
            public void onResponse(Call<ResponseGroup> call, Response<ResponseGroup> response) {
                if (response.isSuccessful()) {
                    ResponseGroup responseGroup = response.body();
                    if (responseGroup != null) {
                        List<GroupModel> groupList = responseGroup.getGroups();
                        GroupAdapter adapter = new GroupAdapter(groupList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        Log.e("Error", "Null group model");
                    }
                } else {
                    Log.e("Error", "Response unsuccessful: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseGroup> call, Throwable t) {
                Log.e("onFailure: ",t+"" );
            }
        });
    }
}