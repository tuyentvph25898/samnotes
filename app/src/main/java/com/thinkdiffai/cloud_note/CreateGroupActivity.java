package com.thinkdiffai.cloud_note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.AdapterMember;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.ResponseUser;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.Model.POST.GroupMember;
import com.thinkdiffai.cloud_note.Model.POST.GroupPostModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity {
    Model_State_Login user;
    private EditText groupName, describe, gmail;
    private Button btnAdd;
    private List<GroupMember> userList;
    private List<GroupMember> selectedMembers = new ArrayList<>();
    private ImageButton btnDone;
    Login daoLogin;
    RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        daoLogin = new Login(CreateGroupActivity.this);
        user = daoLogin.getLogin();
        groupName = findViewById(R.id.groupName);
        describe = findViewById(R.id.describe);
        gmail = findViewById(R.id.member);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.rcv_member);
        btnDone = findViewById(R.id.btn_done);
        getAllUser();
        AdapterMember adapterMember = new AdapterMember(selectedMembers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterMember);
        btnAdd.setOnClickListener(view -> {
            String member = gmail.getText().toString();
            addMemberToList(member);
            adapterMember.notifyDataSetChanged();
        });
        btnDone.setOnClickListener(view -> {
            GroupPostModel groupPostModel = new GroupPostModel();
            groupPostModel.setName(groupName.getText().toString());
            groupPostModel.setDescribe(describe.getText().toString());
            groupPostModel.setIdOwner(user.getIdUer());
            groupPostModel.setMembers(selectedMembers);
            groupPostModel.setCreateAt("");
            postGroup(groupPostModel);
            selectedMembers.clear();
        });
    }
    public void getAllUser() {
        APINote.apiSV.getAllUser().enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    ResponseUser responseUser = response.body();
                        userList = responseUser.getMembers();
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                Log.e("onFailure: ", t+"");
            }
        });
    }

    private void addMemberToList(String memberEmail) {
        boolean isMemberExists = false;
        for (GroupMember member : userList) {
            if (member.getGmail().equals(memberEmail)) {
                isMemberExists = true;
                selectedMembers.add(member);
                gmail.setText("");
                break;
            }
        }
        if (isMemberExists) {
            Toast.makeText(CreateGroupActivity.this, "Thêm thành viên thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CreateGroupActivity.this, "Không tìm thấy thành viên trong danh sách", Toast.LENGTH_SHORT).show();
        }
    }
    private void postGroup(GroupPostModel obj){
        APINote.apiSV.postGroup(user.getIdUer(), obj).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(CreateGroupActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("onFailure: ", t+"");
            }
        });
    }
}