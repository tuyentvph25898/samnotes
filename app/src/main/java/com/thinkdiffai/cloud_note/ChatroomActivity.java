package com.thinkdiffai.cloud_note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cloud_note.R;
import com.google.android.gms.common.api.Api;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.MessageAdapter;
import com.thinkdiffai.cloud_note.Model.GET.MessageModel;
import com.thinkdiffai.cloud_note.Model.GET.ResponseComment;
import com.thinkdiffai.cloud_note.Model.GET.ResponseMessage;
import com.thinkdiffai.cloud_note.Model.POST.ResponsePostMessageUK;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatroomActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText edMessage;
    ImageView btnSent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        recyclerView = findViewById(R.id.rcv_content);
        edMessage = findViewById(R.id.ed_message);
        btnSent = findViewById(R.id.btnSend);
        getData();
        btnSent.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            String dateFormat = "dd/MM/yyyy HH:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            String formattedTimeWithoutTimeZone = sdf.format(calendar.getTime());
            TimeZone timeZone = calendar.getTimeZone();
            int rawOffsetInMillis = timeZone.getRawOffset();
            int offsetHours = rawOffsetInMillis / (60 * 60 * 1000);
            int offsetMinutes = (Math.abs(rawOffsetInMillis) / (60 * 1000)) % 60;
            String timeZoneStr = String.format(Locale.getDefault(), "%s%02d:%02d", rawOffsetInMillis >= 0 ? "+" : "-", Math.abs(offsetHours), offsetMinutes);
            String formattedTime = formattedTimeWithoutTimeZone + " " + timeZoneStr;
            MessageModel model = new MessageModel();
            model.setContent(edMessage.getText().toString());
            model.setIdReceive(58);
            model.setIdSend(50);
            model.setSendAt(formattedTime);
            postMessage(model);
        });
    }

    private void getData() {
        APINote.apiSV.getMessageData(50).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()){
                    ResponseMessage data = response.body();
                    if (data != null){
                        List<MessageModel> list = data.getMessageModels();
                        MessageAdapter adapter = new MessageAdapter(list);
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onItemRangeInserted(int positionStart, int itemCount) {
                                super.onItemRangeInserted(positionStart, itemCount);
                                recyclerView.smoothScrollToPosition(0);
                            }
                        });
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatroomActivity.this);
                        layoutManager.setReverseLayout(true);
//                        layoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {

            }
        });
    }
    private void postMessage(MessageModel model){
        APINote.apiSV.postMessageUK(58, model).enqueue(new Callback<ResponsePostMessageUK>() {
            @Override
            public void onResponse(Call<ResponsePostMessageUK> call, Response<ResponsePostMessageUK> response) {
                if (response.isSuccessful()){
                    ResponsePostMessageUK responseMessage = response.body();
                    if (responseMessage!= null){
                        getData();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsePostMessageUK> call, Throwable t) {
                Log.e( "onFailure: ", t+"");
            }
        });
    }

}