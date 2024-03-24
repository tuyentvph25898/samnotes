package com.thinkdiffai.cloud_note;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.ProfileModel;
import com.thinkdiffai.cloud_note.Model.GET.ResponseProfile;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    ImageView avtProfile;
    TextView tvFullname, tvGmail;
    Login daoLogin;
    ImageButton btnEdit;
    Model_State_Login user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        avtProfile = findViewById(R.id.avt_profile);
        tvFullname = findViewById(R.id.fullname);
        tvGmail = findViewById(R.id.gmail);
        btnEdit = findViewById(R.id.btn_edit_profile);
        daoLogin = new Login(this);
        user = daoLogin.getLogin();
        getProfile();
        btnEdit.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_profile);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT; // Đặt độ rộng là MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Đặt chiều cao là WRAP_CONTENT
            window.setAttributes(layoutParams);
            CircleImageView imageView = dialog.findViewById(R.id.imageView);
            EditText editText = dialog.findViewById(R.id.editText);
            Button button = dialog.findViewById(R.id.btnOK);
            button.setOnClickListener(view1 -> {
                String userInput = editText.getText().toString();
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    private void getProfile() {
        APINote.apiSV.getProfile(user.getIdUer()).enqueue(new Callback<ResponseProfile>() {
            @Override
            public void onResponse(Call<ResponseProfile> call, Response<ResponseProfile> response) {
                if (response.isSuccessful()){
                    ResponseProfile profile = response.body();
                    ProfileModel model = profile.getUser();
                    tvFullname.setText(model.getName());
                    tvGmail.setText(model.getGmail());
                    Glide.with(getApplicationContext()).load(model.getAvatar()).into(avtProfile);
                }
            }

            @Override
            public void onFailure(Call<ResponseProfile> call, Throwable t) {
                Log.e("onFailure profile: ", t+"");
            }
        });
    }

}