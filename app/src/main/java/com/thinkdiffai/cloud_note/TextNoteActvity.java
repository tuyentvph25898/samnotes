package com.thinkdiffai.cloud_note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cloud_note.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.Model.POST.ModelTextNotePost;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TextNoteActvity extends AppCompatActivity {
    private CardView cardView;
    private ImageButton back;
    private Button btnTest;
    private ImageButton done;
    private EditText title;
    private EditText content;
    private ImageButton menu;
    private TextView tvDateCreate;
    private ImageView imgDateCreate;
    private TextView tvTimeCreate;
    private ImageView imgTimeCreate;
    private String color_background = "#8FD2EF";
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    ModelReturn MmodelReturn;

    com.thinkdiffai.cloud_note.Model.Color objColor;
    Login daoLogin;
    Model_State_Login user;
    KProgressHUD isloading;
    private int jam, menit, day1, month1, year1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note_actvity);
        init();
        daoLogin = new Login(TextNoteActvity.this);
        user = daoLogin.getLogin();
        isloading = new KProgressHUD(TextNoteActvity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        imgDateCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDate();
            }
        });
        imgTimeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        btnTest = findViewById(R.id.test);
        btnTest.setOnClickListener(view -> {
            creatNotificationChannel();
            setAlarm();
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        });
        Save();
        Back();
        OpenMenu();
    }

    private void init() {
        cardView = findViewById(R.id.cardView_textnote);
        back = findViewById(R.id.back_from_text_note);
        done = findViewById(R.id.btn_done);
        title = findViewById(R.id.title_name);
        content = findViewById(R.id.add_content_text);
        menu = findViewById(R.id.menu_text_note);
        tvDateCreate = (TextView) findViewById(R.id.tv_dateCreate);
        imgDateCreate = (ImageView) findViewById(R.id.img_dateCreate);
        tvTimeCreate = (TextView) findViewById(R.id.tv_timeCreate);
        imgTimeCreate = (ImageView) findViewById(R.id.img_timeCreate);
    }

    public void Save() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title_values = title.getText().toString();
                String content_values = content.getText().toString();
                ModelTextNotePost obj = new ModelTextNotePost();
                obj.setColor(chuyenMau(color_background));
                Log.e("TAG", "onClick:Color: "+chuyenMau(color_background).getA()+":"+chuyenMau(color_background).getR()+":"+chuyenMau(color_background).getG()+":"+chuyenMau(color_background).getB()+":");
                obj.setTitle(title_values);
                obj.setData(content_values);
                obj.setType("text");
                obj.setPinned(false);
                obj.setDuaAt(tvDateCreate.getText().toString());
                obj.setLock("");
                obj.setReminAt("");
                obj.setShare("");
                obj.setIdFolder("1");
                if (obj.getTitle() != null && obj.getData() != null) {
                    postTextNote(obj);
                } else {
                    if (obj.getTitle() == null) {
                        Toast.makeText(TextNoteActvity.this, "Title không được để trống", Toast.LENGTH_SHORT).show();
                    }
                    if (obj.getData() == null) {

                        Toast.makeText(TextNoteActvity.this, "Content không được để trống", Toast.LENGTH_SHORT).show();
                    }

                }
                creatNotificationChannel();
                setAlarm();
            }
        });
    }

    private void creatNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = title.getText().toString();
            String description = content.getText().toString();
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel  = new NotificationChannel("Notify", name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void setAlarm(){
        AlarmManager alarmManager  = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Date date = new Date();

        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_now.setTime(date);
        cal_alarm.setTime(date);

        cal_alarm.set(Calendar.DAY_OF_MONTH, day1);
        cal_alarm.set(Calendar.MONTH, month1);
        cal_alarm.set(Calendar.YEAR, year1);
        cal_alarm.set(Calendar.HOUR_OF_DAY, jam);
        cal_alarm.set(Calendar.MINUTE, menit);
        cal_alarm.set(Calendar.SECOND, 0);

        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE, 1);
        }
        Intent i = new Intent(TextNoteActvity.this, AlarmReceiver.class);
        i.putExtra("notification_title", title.getText().toString());
        i.putExtra("notification_content", content.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TextNoteActvity.this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),pendingIntent);
    }

//    private void cancelAlarm(){
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        if (alarmManager == null){
//            alarmManager  = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        }
//        alarmManager.cancel(pendingIntent);
//    }
    private void showTimePicker(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay > 12) {
                        tvTimeCreate.setText(
                                String.format("%02d", (hourOfDay - 12)) + " : " + String.format("%02d", minute) + " PM"
                        );
                    } else {
                        tvTimeCreate.setText(hourOfDay + " : " + minute + " AM");
                    }
                    jam = hourOfDay;
                    menit = minute;
                },
                12, // Default hour
                0, // Default minute
                false // 24-hour format
        );

        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }


    public void dialogDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(TextNoteActvity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
                day1 = dayOfMonth;
                month1 = month;
                year1 = year;

                // Gọi phương thức dialogTime() sau khi chọn ngày
                dialogTime();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(TextNoteActvity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                selectedHour = i;
                selectedMinute = i1;
                jam = i;
                menit = i1;
                combineAndFormatDateTime();
            }
        }, hourOfDay, minute, false);
        timePickerDialog.show();
    }

    private void combineAndFormatDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);

        String dateFormat = "dd/MM/yyyy hh:mm a Z";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        String formattedTime = sdf.format(calendar.getTime());
        tvDateCreate.setText(formattedTime);
    }


    public void postTextNote(ModelTextNotePost obj) {
isloading.show();
        APINote.apiService.post_text_note(user.getIdUer(), obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ModelReturn>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ModelReturn modelReturn) {
                        MmodelReturn = modelReturn;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: " + e);
                        isloading.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        if (MmodelReturn.getStatus() == 200) {
                            isloading.dismiss();
                            Toast.makeText(TextNoteActvity.this, "Success", Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        }
                    }
                });
    }

    public void Back() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void OpenMenu() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu_Dialog(Gravity.BOTTOM);
            }
        });
    }

    public void Menu_Dialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        //Truyền layout cho dialog.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);

        dialog.setContentView(R.layout.custom_select_color);

        //Xác định vị trí cho dialog

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
        ImageButton red = dialog.findViewById(R.id.color_red);
        ImageButton orange = dialog.findViewById(R.id.color_orange);
        ImageButton yellow = dialog.findViewById(R.id.color_yellow);
        ImageButton green1 = dialog.findViewById(R.id.color_green1);
        ImageButton green2 = dialog.findViewById(R.id.color_green2);
        ImageButton mint = dialog.findViewById(R.id.color_mint);
        ImageButton blue = dialog.findViewById(R.id.color_blue);
        ImageButton purple = dialog.findViewById(R.id.color_purple);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FF7D7D";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FFBC7D";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FAE28C";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        green1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#D3EF82";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        green2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#A5EF82";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        mint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#82EFBB";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#82C8EF";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#8293EF";

                cardView.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        dialog.show();
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
}