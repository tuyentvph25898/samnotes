package com.thinkdiffai.cloud_note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.CommentAdapter;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.CommentModel;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetImageNote;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetScreenShots;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.GET.ResponseComment;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.Model.PATCH.ChangPublicNote;
import com.thinkdiffai.cloud_note.Model.POST.CommentPostModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detail_Note_ImageActivity extends AppCompatActivity {
    private ImageButton backFromTextNote;
    private ImageButton btnDone;
    private CardView cardViewTextnote;
    private Button btnUpload;
    private ImageView imgBackground, imgSend;
    private RecyclerView  rcv_comment;
    private EditText titleName, comment;
    private TextView tvDateCreate;
    private ImageView imgDateCreate;
    private TextView tvTimeCreate;
    private ImageView imgTimeCreate;
    private EditText addContentText;
    private ImageButton menuTextNote;
    String color_background;
    String imageBase64;
    int idNote;
    float colorA;
    int colorR;
    int colorG;
    int colorB;
    String type;
    KProgressHUD isloading;
    int notePublic;
    Login daoLogin;
    Model_State_Login user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note_image);
        Intent intentData = getIntent();
        backFromTextNote = (ImageButton) findViewById(R.id.back_from_text_note);
        btnDone = (ImageButton) findViewById(R.id.btn_done);
        cardViewTextnote = (CardView) findViewById(R.id.cardView_textnote);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        imgBackground = (ImageView) findViewById(R.id.img_background);
        titleName = (EditText) findViewById(R.id.title_name);
        tvDateCreate = (TextView) findViewById(R.id.tv_dateCreate);
        imgDateCreate = (ImageView) findViewById(R.id.img_dateCreate);
        tvTimeCreate = (TextView) findViewById(R.id.tv_timeCreate);
        imgTimeCreate = (ImageView) findViewById(R.id.img_timeCreate);
        addContentText = (EditText) findViewById(R.id.add_content_text);
        menuTextNote = (ImageButton) findViewById(R.id.menu_text_note);
        comment = findViewById(R.id.ed_content);
        imgSend = findViewById(R.id.sendComment);
        rcv_comment = findViewById(R.id.rcv_comment);
        daoLogin = new Login(Detail_Note_ImageActivity.this);
        user = daoLogin.getLogin();
        btnDone.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.INVISIBLE);
        isloading = new KProgressHUD(Detail_Note_ImageActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        getData(intentData);
        getComment();
        String hex = ChuyenMau(colorA, colorR, colorB, colorB);
        if(!hex.equalsIgnoreCase("#000")){
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(hex+""));
        }
        backFromTextNote.setOnClickListener(view -> onBackPressed());
        btnUpload.setOnClickListener(view -> {
            if(ActivityCompat.checkSelfPermission(Detail_Note_ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requesPermisstion();
            }else{
                pickImage();
            }
        });
        menuTextNote.setVisibility(View.VISIBLE);
        menuTextNote.setOnClickListener(view -> Menu_Dialog(Gravity.BOTTOM));
        imgSend.setOnClickListener(view -> {
            Date date = new Date();
            String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            String formattedDate = sdf.format(date);
            CommentPostModel model = new CommentPostModel();
            model.setContent(comment.getText().toString());
            model.setIdNote(idNote);
            model.setIdUser(user.getIdUer());
            model.setParent_id(0);
            model.setSendAt(formattedDate);
            postComment(model);
        });
        if(type.equalsIgnoreCase("image")){
            APINote.apiService.getNoteByIdTypeImage(idNote).enqueue(new Callback<ModelGetImageNote>() {
                @Override
                public void onResponse(Call<ModelGetImageNote> call, Response<ModelGetImageNote> response) {
                    isloading.show();
                    if(response.isSuccessful()&response.body()!=null){
                        isloading.dismiss();
                        ModelGetImageNote obj = response.body();
                        titleName.setText(obj.getNote().getTitle());
                        addContentText.setText(obj.getNote().getData());
                        if(obj.getNote().getMetaData()!=null){
                            imgBackground.setVisibility(View.VISIBLE);
                            Glide.with(imgBackground).load(obj.getNote().getMetaData()).into(imgBackground);
                        }else{
                            imgBackground.setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onFailure(Call<ModelGetImageNote> call, Throwable t) {
                    Log.e("TAG", "onFailure: "+t.getMessage() );
                    isloading.dismiss();
                }
            });
        }else{
            APINote.apiService.getNoteByIdTypeScreenshot(idNote).enqueue(new Callback<ModelGetScreenShots>() {
                @Override
                public void onResponse(Call<ModelGetScreenShots> call, Response<ModelGetScreenShots> response) {
                    isloading.show();
                    if(response.isSuccessful()&response.body()!=null){
                        isloading.dismiss();
                        ModelGetScreenShots item = response.body();
                        titleName.setText(item.getNotes().getTitle());
                        addContentText.setText(item.getNotes().getData());
                        Glide.with(imgBackground).load(item.getNotes().getMetaData()).into(imgBackground);
                    }
                }
                @Override
                public void onFailure(Call<ModelGetScreenShots> call, Throwable t) {
                    Log.e("TAG", "onFailure: "+t.getMessage() );
                    isloading.dismiss();
                }
            });
        }
    }
    private void getData(Intent intent){
        idNote = intent.getIntExtra("id", -1);
        colorA = intent.getFloatExtra("colorA",0);
        colorR = intent.getIntExtra("colorR", 0);
        colorG = intent.getIntExtra("colorG", 0);
        colorB = intent.getIntExtra("colorB", 0);
        type = intent.getStringExtra("type");
        notePublic= intent.getIntExtra("notePublic", 0);
    }
    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),999);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==999&&resultCode ==RESULT_OK&& data!=null&&data.getData()!=null){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgBackground.setVisibility(View.VISIBLE);
                imgBackground.setImageBitmap(decodedByte);
            }catch (IOException e){
                Log.e("TAG", "onActivityResult: "+e.getMessage() );
            }
        }
    }
    private void requesPermisstion(){
        ActivityCompat.requestPermissions(Detail_Note_ImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999 & grantResults[0] == 0) {

        } else {
            Toast.makeText(Detail_Note_ImageActivity.this, "Do bạn không đồng ý !!!", Toast.LENGTH_SHORT).show();
        }
    }
    public void dialogDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(Detail_Note_ImageActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int days = dayOfMonth;
                int months = month;
                int years = year;
                tvDateCreate.setText(days + "-" + (months + 1) + "-" + years);
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    public void dialogTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Detail_Note_ImageActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hour = i;
                int minute = i1;
                tvTimeCreate.setText(hour + ":" + minute);
            }
        }, hourOfDay, minute, false);
        timePickerDialog.show();
    }
    public void Menu_Dialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        //Truyền layout cho dialog.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_select_color);
        Window window = dialog.getWindow();
        if (window == null) {

        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        //Ánh xạ
        RelativeLayout Rl_reminder,Rl_share,Rl_lock,Rl_archive,Rl_deletenote;
        Rl_reminder = dialog.findViewById(R.id.Rl_Reminder);
        Rl_share = dialog.findViewById(R.id.Rl_share);
        Rl_lock = dialog.findViewById(R.id.Rl_lock);
        Rl_archive = dialog.findViewById(R.id.Rl_archive);
        Rl_deletenote = dialog.findViewById(R.id.Rl_deletenote);
        ImageButton red = dialog.findViewById(R.id.color_red);
        ImageButton orange = dialog.findViewById(R.id.color_orange);
        ImageButton yellow = dialog.findViewById(R.id.color_yellow);
        ImageButton green1 = dialog.findViewById(R.id.color_green1);
        ImageButton green2 = dialog.findViewById(R.id.color_green2);
        ImageButton mint = dialog.findViewById(R.id.color_mint);
        ImageButton blue = dialog.findViewById(R.id.color_blue);
        ImageButton purple = dialog.findViewById(R.id.color_purple);
        red.setOnClickListener(view -> {
            color_background = "#FF7D7D";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        orange.setOnClickListener(view -> {
            color_background = "#FFBC7D";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        yellow.setOnClickListener(view -> {
            color_background = "#FAE28C";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        green1.setOnClickListener(view -> {
            color_background = "#D3EF82";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        green2.setOnClickListener(view -> {
            color_background = "#A5EF82";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        mint.setOnClickListener(view -> {
            color_background = "#82EFBB";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        blue.setOnClickListener(view -> {
            color_background = "#82C8EF";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        purple.setOnClickListener(view -> {
            color_background = "#8293EF";
            cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        Rl_deletenote.setOnClickListener(view -> dialogDelete(idNote));
        Rl_share.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chia sẻ");
            final EditText editText = new EditText(this);
            editText.setText("https://samnote.mangasocial.online/note/" + idNote);
            builder.setView(editText);
            builder.setPositiveButton("Copy link", (dialogInterface, i) -> {
                String content = editText.getText().toString().trim();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Link", content);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            builder.setNegativeButton("Hủy", (dialogInterface, i) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        Rl_lock.setOnClickListener(view -> changePublicNote());
        dialog.show();
    }
    private void changePublicNote() {
        ChangPublicNote publicNote = new ChangPublicNote(1);
        Log.e( "changePublicNote: ", publicNote.getPublicNote()+"");
        Log.e( "changePublicNote: ", idNote+"");
        Call<ModelReturn> call = APINote.apiService.changePublicNote(idNote, publicNote);
        call.enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                onBackPressed();
            }
            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                Log.e( "onFailure: ", t+"");
            }
        });
    }

    public com.thinkdiffai.cloud_note.Model.Color chuyenMau(String hexColor) {
        Log.e("TAG", "chuyenMau: "+hexColor);
        int red = Integer.parseInt(hexColor.substring(1, 3), 16);
        int green = Integer.parseInt(hexColor.substring(3, 5), 16);
        int blue = Integer.parseInt(hexColor.substring(5, 7), 16);
        Log.e("TAG", "chuyenMau:R "+red);
        Log.e("TAG", "chuyenMau: G"+green);
        Log.e("TAG", "chuyenMau: B"+blue);
        com.thinkdiffai.cloud_note.Model.Color color = new com.thinkdiffai.cloud_note.Model.Color();
        color.setA((float) 0.87);
        color.setB(blue);
        color.setG(green);
        color.setR(red);
        return color;
    }
    private String ChuyenMau(float alpha, float red, float green, float blue) {
        // chuyển đổi giá trị của từng kênh màu sang giá trị thập lục phân
        String alphaHex = Integer.toHexString((int) alpha);
        String redHex = Integer.toHexString((int) red);
        String greenHex = Integer.toHexString((int) green);
        String blueHex = Integer.toHexString((int) blue);
// ghép các giá trị thập lục phân lại với nhau theo thứ tự ARGB
        String hex = "#" + redHex + greenHex + blueHex;
        Log.d("TAG", "ChuyenMau: " + hex);
        return hex;
    }
    private void dialogDelete( int id) {
        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialog_delete_note);
        Button btn_cancel = dialog1.findViewById(R.id.btn_cancel);
        Button btn_delete = dialog1.findViewById(R.id.btn_delete);
        Button btn_move_trash = dialog1.findViewById(R.id.btn_move_trash);
        btn_cancel.setOnClickListener(view -> dialog1.dismiss());
        btn_delete.setOnClickListener(view -> {
            APINote.apiService.deleteNote(id).enqueue(new Callback<ModelReturn>() {
                @Override
                public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                    if (response.isSuccessful() & response.body() != null) {
                        ModelReturn r = response.body();
                        if (r.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ModelReturn> call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.getMessage());
                }
            });
            onBackPressed();
        });
        btn_move_trash.setOnClickListener(view -> {
            APINote.apiService.moveToTrash(id).enqueue(new Callback<ModelReturn>() {
                @Override
                public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                    if (response.isSuccessful() & response.body() != null) {
                        ModelReturn r = response.body();
                        if (r.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ModelReturn> call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.getMessage());
                }
            });
            onBackPressed();
        });
        dialog1.show();
    }
    private void getComment() {
        APINote.apiSV.getComment(idNote).enqueue(new Callback<ResponseComment>() {
            @Override
            public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                if (response.isSuccessful()){
                    ResponseComment responseComment = response.body();
                    if (responseComment != null){
                        List<CommentModel> list1 = responseComment.getComments();
                        CommentAdapter adapter = new CommentAdapter(list1);
                        rcv_comment.setAdapter(adapter);
                    }else {
                        Log.e("Error", "Null comment");
                    }
                } else {
                    Log.e("Error", "Response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseComment> call, Throwable t) {
                Log.e("Error", "Response unsuccessful: " + t);
            }
        });
    }
    private void postComment(CommentPostModel model){
        APINote.apiSV.postComment(idNote, model).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(Detail_Note_ImageActivity.this, "ok roi!", Toast.LENGTH_SHORT).show();
                comment.setText("");
                getComment();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("onFailuressss: ", t+"");
            }
        });
    }
}