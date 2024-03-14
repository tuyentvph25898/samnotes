package com.thinkdiffai.cloud_note;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.CommentAdapter;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.CommentModel;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetNoteText;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.GET.ResponseComment;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.Model.PATCH.ChangPublicNote;
import com.thinkdiffai.cloud_note.Model.PATCH.ModelPutTextNote;

import com.thinkdiffai.cloud_note.Model.Model_List_Note;
import com.thinkdiffai.cloud_note.Model.PATCH.ModelPutTextNote;
import com.thinkdiffai.cloud_note.Model.POST.CommentPostModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detail_Note extends AppCompatActivity {
    private RecyclerView rcv_comment;
    private ImageButton back;
    private ImageButton done;
    private EditText title;
    private EditText content, comment;
    private ImageButton menu;
    private CardView cardView;
    private String color_background;
    private TextView tvDateCreate;
    private ImageView imgDateCreate;
    private TextView tvTimeCreate;
    private ImageView imgTimeCreate, imgSend;
    List<Model_List_Note> list;
    int idNote;
    float colorA;
    int colorR;
    int colorG;
    int colorB;
    int notePublic;
    KProgressHUD isloading;
    Login daoLogin;
    Model_State_Login user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);
        Intent intent = getIntent();
        //Ánh xạ
        cardView = findViewById(R.id.cardview_detail_note);
        back = findViewById(R.id.back_from_detail_text_note);
        done = findViewById(R.id.btn_done_update_note);
        title = findViewById(R.id.detail_title_name);
        content = findViewById(R.id.detail_content_text);
        menu = findViewById(R.id.menu_detail_text_note);
        comment = findViewById(R.id.ed_content);
        imgSend = findViewById(R.id.sendComment);

        tvDateCreate = (TextView) findViewById(R.id.tv_dateCreate);
        imgDateCreate = (ImageView) findViewById(R.id.img_dateCreate);
        tvTimeCreate = (TextView) findViewById(R.id.tv_timeCreate);
        imgTimeCreate = (ImageView) findViewById(R.id.img_timeCreate);
        rcv_comment = findViewById(R.id.rcv_comment);
        daoLogin = new Login(Detail_Note.this);
        user = daoLogin.getLogin();

        getData(intent);
        getComment();

        if(notePublic==0){
            done.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
            title.setEnabled(true);
            content.setEnabled(true);
        }else{
            done.setVisibility(View.INVISIBLE);
            title.setEnabled(false);
            content.setEnabled(false);
            menu.setVisibility(View.VISIBLE);
        }
        isloading= new KProgressHUD(Detail_Note.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

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
        APINote.apiService.getNoteByIdTypeText(idNote).enqueue(new Callback<ModelGetNoteText>() {
            @Override
            public void onResponse(Call<ModelGetNoteText> call, Response<ModelGetNoteText> response) {
                isloading.show();
                if(response.isSuccessful()&response.body()!=null){
                    isloading.dismiss();
                    ModelGetNoteText obj = response.body();
                    ModelPutTextNote update = new ModelPutTextNote();
                    title.setText(obj.getModelTextNote().getTitle());
                    content.setText(obj.getModelTextNote().getData());
                    String hex = ChuyenMau(colorA, colorR, colorG, colorB);
                    cardView.setCardBackgroundColor(Color.parseColor(hex));
                    done.setOnClickListener(view -> {
                       // ModelTextNotePost update = new ModelTextNotePost();
                        update.setData(content.getText().toString());
                        update.setTitle(title.getText().toString());
                        update.setType(obj.getModelTextNote().getType());
                        if(cardView.getCardBackgroundColor().getDefaultColor()==Color.parseColor(hex)){
                            Log.d("TAG", "onCreate:Color1: k thay đổi màu  ");
                            update.setColor(new com.thinkdiffai.cloud_note.Model.Color(colorA, colorB, colorG, colorR));
                        }else{
                            Log.d("TAG", "onCreate:Color2: thay đổi màu  ");
                            update.setColor(ChuyenMauARGB(color_background));
                        }
                        update.setLock("");
                        update.setReminAt("");
                        update.setPinned(0);
                        if(tvDateCreate.getText().toString()==""&&tvTimeCreate.getText().toString()==""){
                            if(obj.getModelTextNote().getDuaAt()==""){
                                update.setDuaAt("");
                            }else{
                                update.setDuaAt(obj.getModelTextNote().getDuaAt());
                            }
                        }else{
                            update.setDuaAt(tvDateCreate.getText().toString()+" "+tvTimeCreate.getText().toString());
                        }
                        updateNodeTextNote(update, idNote);
                    });
                }
            }
            @Override
            public void onFailure(Call<ModelGetNoteText> call, Throwable t) {
                Log.e("TAG", "onFailure: "+t.getMessage() );
                isloading.dismiss();
            }
        });
        Back();
        OpenMenu();
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
                Toast.makeText(Detail_Note.this, "ok roi!", Toast.LENGTH_SHORT).show();
                comment.setText("");
                getComment();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("onFailuressss: ", t+"");
            }
        });
    }

    private void getData(Intent intent){
        idNote = intent.getIntExtra("id", -1);
        colorA = intent.getFloatExtra("colorA",0);
        colorR = intent.getIntExtra("colorR", 0);
        colorG = intent.getIntExtra("colorG", 0);
        colorB = intent.getIntExtra("colorB", 0);
        notePublic = intent.getIntExtra("notePublic",0);
    }
    private void updateNodeTextNote(ModelPutTextNote obj, int id) {
        isloading.show();
        ModelReturn modelR = new ModelReturn();
        APINote.apiService.patch_text_note(id, obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ModelReturn>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ModelReturn modelReturn) {
                        modelR.setMessage(modelReturn.getMessage());
                        modelR.setStatus(modelR.getStatus());
                    }
                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e("TAG", "onError: " + e.getMessage());
                    }
                    @Override
                    public void onComplete() {
                        isloading.dismiss();
                        Toast.makeText(Detail_Note.this, modelR.getMessage() , Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        if (modelR.getStatus() == 200) {
                        }
                    }
                });
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
    public com.thinkdiffai.cloud_note.Model.Color ChuyenMauARGB (String hexColor){
        int red = Integer.parseInt(hexColor.substring(1, 3), 16);
        int green = Integer.parseInt(hexColor.substring(3, 5), 16);
        int blue = Integer.parseInt(hexColor.substring(5, 7), 16);
        com.thinkdiffai.cloud_note.Model.Color color = new com.thinkdiffai.cloud_note.Model.Color();
        color.setA((float) 0.87);
        color.setB(blue);
        color.setG(green);
        color.setR(red);
        return color;
    }
    public void Back(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void OpenMenu(){
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu_Dialog(Gravity.BOTTOM);
            }
        });
    }
    public void Menu_Dialog(int gravity){
        final Dialog dialog = new Dialog(this);
        //Truyền layout cho dialog.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_select_color);
        Window window = dialog.getWindow();
        if(window == null){
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }else {
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

        Rl_deletenote.setOnClickListener(v -> dialogDelete(idNote));
        Rl_lock.setOnClickListener(view -> changePublicNote());

        red.setOnClickListener(view -> {
            color_background = "#FF7D7D";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        orange.setOnClickListener(view -> {
            color_background = "#FFBC7D";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        yellow.setOnClickListener(view -> {
            color_background = "#FAE28C";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        green1.setOnClickListener(view -> {
            color_background = "#D3EF82";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        green2.setOnClickListener(view -> {
            color_background = "#A5EF82";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        mint.setOnClickListener(view -> {
            color_background = "#82EFBB";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        blue.setOnClickListener(view -> {
            color_background = "#82C8EF";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
        purple.setOnClickListener(view -> {
            color_background = "#8293EF";
            cardView.setCardBackgroundColor(Color.parseColor(color_background));
            dialog.cancel();
        });
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
                Log.e( "onResponse: ", response.body().getMessage());
            }
            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                Log.e( "onFailure: ", t+"");
            }
        });
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
        btn_move_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        dialog1.show();
    }
    public void dialogDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(Detail_Note.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(Detail_Note.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hour = i;
                int minute = i1;
                tvTimeCreate.setText(hour + ":" + minute);
            }
        }, hourOfDay, minute, false);
        timePickerDialog.show();
    }
    public void dialogArchived(){
        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialog_confirm);
        Button btn_cancel = dialog1.findViewById(R.id.btn_cancle);
        Button btn_confirm = dialog1.findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
//        btn_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                APINote.apiService.deleteNote().enqueue(new Callback<ModelReturn>() {
//                    @Override
//                    public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
//                        if (response.isSuccessful() & response.body() != null) {
//                            ModelReturn r = response.body();
//                            if (r.getStatus() == 200) {
//                                Toast.makeText(getApplicationContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
//                                dialog1.dismiss();
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ModelReturn> call, Throwable t) {
//                        Log.e("TAG", "onFailure: " + t.getMessage());
//                    }
//                });
//                onBackPressed();
//            }
//        });
    }

}
