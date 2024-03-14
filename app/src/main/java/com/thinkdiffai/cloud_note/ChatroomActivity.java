package com.thinkdiffai.cloud_note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.MessageAdapter;
import com.thinkdiffai.cloud_note.Model.GET.MessageModel;
import com.thinkdiffai.cloud_note.Model.GET.ResponseComment;
import com.thinkdiffai.cloud_note.Model.GET.ResponseMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatroomActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        recyclerView = findViewById(R.id.rcv_content);
        getData();
    }

    private void getData() {
        APINote.apiSV.getMessageData(58).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()){
                    ResponseMessage data = response.body();
                    if (data != null){
                        List<MessageModel> list = data.getMessageModels();
                        MessageAdapter adapter = new MessageAdapter(list);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {

            }
        });
    }

}