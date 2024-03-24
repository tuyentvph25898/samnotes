package com.thinkdiffai.cloud_note;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.DAO.Login;
import com.thinkdiffai.cloud_note.Model.Model_State_Login;
import com.thinkdiffai.cloud_note.Model.POST.ModelPostImageNote1;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteImageActivity1 extends AppCompatActivity {
    public static final String TAG = NoteImageActivity1.class.getName();
    private static final int MY_REQUEST_CODE = 10;
    private ImageButton backFromTextNote;
    private ImageButton btnDone;
    private CardView cardViewTextnote;
    private Button btnUpload;
    private ImageView imgBackground;
    private ImageView imgDateCreate;
    private EditText titleName;
    private EditText addContentText;
    private TextView tvTimeCreate;
    private TextView tvDateCreate;
    private ImageButton menuTextNote;
    String color_background = "#FF7D7D";
    private Uri mUri;
    Login daoUser;
    Model_State_Login user;
    private String currentPhotoPath;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private int jam, menit, day1, month1, year1;

    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("TAG", "onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data == null){
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgBackground.setImageBitmap(bitmap);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        if (result) {
            // Khi chụp ảnh thành công, hiển thị ảnh trên ImageView
            imgBackground.setImageURI(Uri.parse(currentPhotoPath));
        } else {
            // Xử lý nếu có lỗi xảy ra
            Log.e("MainActivity", "Failed to take picture");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_image);
        backFromTextNote = (ImageButton) findViewById(R.id.back_from_text_note);
        btnDone = (ImageButton) findViewById(R.id.btn_done);
        cardViewTextnote = (CardView) findViewById(R.id.cardView_textnote);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        imgBackground = (ImageView) findViewById(R.id.img_background);
        titleName = (EditText) findViewById(R.id.title_name);
        addContentText = (EditText) findViewById(R.id.add_content_text);
        imgDateCreate = (ImageView) findViewById(R.id.img_dateCreate);
        tvTimeCreate = (TextView) findViewById(R.id.tv_timeCreate);
        tvDateCreate = (TextView) findViewById(R.id.tv_dateCreate);
        menuTextNote = (ImageButton) findViewById(R.id.menu_text_note);
        daoUser = new Login(NoteImageActivity1.this);
        user = daoUser.getLogin();
        backFromTextNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgDateCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDate();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opendDialogImage();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUri != null){
                    creatNotificationChannel();
                    setAlarm();
                    postImagePost();
                }
            }
        });



        menuTextNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu_Dialog(Gravity.BOTTOM);
            }
        });
    }

    private void creatNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = titleName.getText().toString();
            String description = addContentText.getText().toString();
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
        Intent i = new Intent(NoteImageActivity1.this, AlarmReceiver.class);
        i.putExtra("notification_title", titleName.getText().toString());
        i.putExtra("notification_content", addContentText.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NoteImageActivity1.this, 0, i, PendingIntent.FLAG_IMMUTABLE);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(NoteImageActivity1.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(NoteImageActivity1.this, new TimePickerDialog.OnTimeSetListener() {
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
    private void postImagePost() {
        String title = titleName.getText().toString();
        String content = addContentText.getText().toString();
        String type = "image";
        String remind = "";
        com.thinkdiffai.cloud_note.Model.Color color = chuyenMau(color_background);
        int r = color.getR();
        int g = color.getG();
        int b = color.getB();
        float a = color.getA();
        int id = user.getIdUer();
        RequestBody requestBodyR = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(r));
        RequestBody requestBodyG = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(g));
        RequestBody requestBodyB = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(b));
        RequestBody requestBodyA = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(a));
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title);
        RequestBody requestBodyContent = RequestBody.create(MediaType.parse("multipart/form-data"), content);
        RequestBody requestBodyType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
        RequestBody requestBodyRemind = RequestBody.create(MediaType.parse("multipart/form-data"), remind);

        String strRealPath = RealPathUtil.getRealPath(this, mUri);
        File file = new File(strRealPath);
        RequestBody requestBodyImgNote = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multiPartBodyImgNote = MultipartBody.Part.createFormData("image_note", file.getName(), requestBodyImgNote);
        APINote.apiSV.postImageNote(id,requestBodyType,requestBodyTitle, requestBodyContent, requestBodyR, requestBodyG, requestBodyB, requestBodyA, multiPartBodyImgNote, requestBodyRemind).enqueue(
                new Callback<ModelPostImageNote1>() {
                    @Override
                    public void onResponse(Call<ModelPostImageNote1> call, Response<ModelPostImageNote1> response) {
                        ModelPostImageNote1 modelPostImageNote1 = response.body();
                        if (modelPostImageNote1 != null){
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelPostImageNote1> call, Throwable t) {
                        Log.e( "onFailure: ", t+"");
                    }
                }
        );
    }

    public com.thinkdiffai.cloud_note.Model.Color chuyenMau(String hexColor) {
        Log.e("TAG", "chuyenMau: " + hexColor);
        int red = Integer.parseInt(hexColor.substring(1, 3), 16);
        int green = Integer.parseInt(hexColor.substring(3, 5), 16);
        int blue = Integer.parseInt(hexColor.substring(5, 7), 16);
        Log.e("TAG", "chuyenMau:R " + red);
        Log.e("TAG", "chuyenMau: G" + green);
        Log.e("TAG", "chuyenMau: B" + blue);
        com.thinkdiffai.cloud_note.Model.Color color = new com.thinkdiffai.cloud_note.Model.Color();
        color.setA((float) 0.87);
        color.setB(blue);
        color.setG(green);
        color.setR(red);
        return color;
    }

    private void opendDialogImage() {
        final Dialog dialog = new Dialog(NoteImageActivity1.this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.setContentView(R.layout.dialog_menu_image);
        TextView tv_camera = dialog.findViewById(R.id.tv_camera);
        TextView tv_gallery = dialog.findViewById(R.id.tv_gallery);
        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                dialog.dismiss();
            }
        });
        tv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }
    public void Menu_Dialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        //Truyền layout cho dialog.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FFBC7D";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FAE28C";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        green1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#D3EF82";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        green2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#A5EF82";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        mint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#82EFBB";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#82C8EF";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#8293EF";

                cardViewTextnote.setCardBackgroundColor(Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        dialog.show();
    }
    private void openCamera() {
        // Kiểm tra quyền CAMERA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
        }

        // Tạo intent để mở camera và chụp ảnh
            Uri photoUri = null;
            try {
                photoUri = createImageFile();
            } catch (Exception ex) {
                Log.e("MainActivity", "Error creating image file", ex);
            }
            if (photoUri != null) {
                cameraLauncher.launch(photoUri);
            }

    }

    // Tạo tệp tin ảnh tạm thời
    private Uri createImageFile() throws Exception {
        String fileName = "photo";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        mUri = imageUri;
        currentPhotoPath = imageUri.toString();
        return imageUri;
    }
}
