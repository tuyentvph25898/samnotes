package com.thinkdiffai.cloud_note;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloud_note.R;
import com.thinkdiffai.cloud_note.APIs.APINote;
import com.thinkdiffai.cloud_note.Adapter.AdapterCheckListPost;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.POST.ModelCheckListPost;
import com.thinkdiffai.cloud_note.Model.POST.ModelPostImageNote;
import com.thinkdiffai.cloud_note.Model.POST.Public.ModelCheckListPublic;
import com.thinkdiffai.cloud_note.Model.POST.Public.ModelTextNotePublic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNotePublicActivity extends AppCompatActivity {
    private Spinner spinnerTypeNote;
    private LinearLayout layoutTextNote;
    private EditText titleTextNote;
    private EditText addContentText;
    private LinearLayout layoutCheckList;
    private EditText titleChecklist;
    private RecyclerView rcvCheckList;
    private Button btnAddCheckList;
    private LinearLayout layoutNoteImage;
    private Button btnUpload;
    private ImageView imgBackground;
    private EditText titleImage;
    private EditText addContentImage;
    private ImageButton backFromCheckNote;
    private ImageButton btnChecklistDone;
    String type;
    private CardView cardView;
    String color_background = "#8FD2EF";
    private ImageButton menuTextNote;
    List<ModelCheckListPost> checklists = new ArrayList<>();
    AdapterCheckListPost adapterCheckListPost;
    String imageBase64 = "";
    KProgressHUD hud;
    private Uri mUri;
    private static final int MY_REQUEST_CODE = 10;
    private String currentPhotoPath;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_note_public);
        spinnerTypeNote = (Spinner) findViewById(R.id.spinner_typeNote);
        layoutTextNote = (LinearLayout) findViewById(R.id.layout_textNote);
        titleTextNote = (EditText) findViewById(R.id.title_text_note);
        addContentText = (EditText) findViewById(R.id.add_content_text);
        layoutCheckList = (LinearLayout) findViewById(R.id.layout_checkList);
        titleChecklist = (EditText) findViewById(R.id.title_checklist);
        rcvCheckList = (RecyclerView) findViewById(R.id.rcv_checkList);
        btnAddCheckList = (Button) findViewById(R.id.btn_addCheckList);
        layoutNoteImage = (LinearLayout) findViewById(R.id.layout_noteImage);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        imgBackground = (ImageView) findViewById(R.id.img_background);
        titleImage = (EditText) findViewById(R.id.title_image);
        addContentImage = (EditText) findViewById(R.id.add_content_image);
        menuTextNote = (ImageButton) findViewById(R.id.menu_text_note);
        cardView = findViewById(R.id.card_View);

        backFromCheckNote = (ImageButton) findViewById(R.id.back_from_check_note);
        btnChecklistDone = (ImageButton) findViewById(R.id.btn_checklist_done);
        hud = KProgressHUD.create(CreateNotePublicActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        backFromCheckNote.setOnClickListener(view -> {
            onBackPressed();
        });
        btnChecklistDone.setOnClickListener(view -> {
            hud.show();
            switch (type) {
                case "text":
                    ModelTextNotePublic text = new ModelTextNotePublic();
                    text.setType(type);
                    text.setData(addContentText.getText().toString());
                    text.setTitile(titleTextNote.getText().toString());
                    text.setNotePublic(1);
                    text.setColor(chuyenMau(color_background));
                    text.setLock("");
                    text.setPinned(false);
                    text.setShare("");
                    text.setDueAt("");
                    text.setRemindAt("");
                    text.setIdFolder("1");
                    postTextNotePublulic(text);
                    break;
                case "checklist":
                    ModelCheckListPublic checkListPublic = new ModelCheckListPublic();
                    checkListPublic.setNotePublic(1);
                    checkListPublic.setDuaAt("");
                    checkListPublic.setColor(chuyenMau(color_background));
                    checkListPublic.setReminAt("");
                    checkListPublic.setTitle(titleChecklist.getText().toString());
                    checkListPublic.setLock("");
                    checkListPublic.setType(type);
                    checkListPublic.setPinned(false);
                    checkListPublic.setShare("");
                    checkListPublic.setData(checklists);
                    checkListPublic.setIdFolder("1");
                    potsChecklistPublic(checkListPublic);
                    break;
                case "image":
                    ModelPostImageNote obj = new ModelPostImageNote();
                    obj.setTitle(titleImage.getText().toString());
                    obj.setData(addContentImage.getText().toString());
                    obj.setType(type);
                    obj.setColor(chuyenMau(color_background));
                    obj.setPinned(false);
                    obj.setLock("");
                    obj.setShare("");
                    obj.setDuaAt("");
                    obj.setReminAt("");
                    obj.setNotePublic(1);
//                    if (imageBase64 != "") {
//                        AsyncTask<Void, Void, String> async = new AsyncTask<Void, Void, String>() {
//                            @Override
//                            protected String doInBackground(Void... voids) {
//                                return img();
//                            }
//
//                            @Override
//                            protected void onPostExecute(String s) {
//                                super.onPostExecute(s);
//                                Log.e("TAG", "onPostExecute: " + s);
//                                obj.setMetaData(s);
//                                if (titleImage.getText().toString() != "" && addContentImage.getText().toString() != "") {
//                                    postImageNotePublic(obj);
//                                }
//
//                            }
//                        };
//                        async.execute();
//
//
//                    } else {
//                        obj.setMetaData("");
//                        if (titleImage.getText().toString() != "" && addContentImage.getText().toString() != "") {
//                            postImageNotePublic(obj);
//                        }
//                    }
                    break;

            }
        });
        ArrayList<String> listType = new ArrayList<>();
        listType.add("text");
        listType.add("checklist");
        listType.add("image");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeNote.setAdapter(arrayAdapter);
        spinnerTypeNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = listType.get(i);
                switch (i) {
                    case 0:
                        layoutTextNote.setVisibility(View.VISIBLE);
                        layoutNoteImage.setVisibility(View.GONE);
                        layoutCheckList.setVisibility(View.GONE);
                        break;
                    case 1:

                        layoutTextNote.setVisibility(View.GONE);
                        layoutNoteImage.setVisibility(View.GONE);
                        layoutCheckList.setVisibility(View.VISIBLE);
                        adapterCheckListPost = new AdapterCheckListPost(checklists);
                        rcvCheckList.setAdapter(adapterCheckListPost);
                        btnAddCheckList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogAddCheckList(CreateNotePublicActivity.this);
                            }
                        });

                        break;
                    case 2:

                        layoutTextNote.setVisibility(View.GONE);
                        layoutNoteImage.setVisibility(View.VISIBLE);
                        layoutCheckList.setVisibility(View.GONE);
                        btnUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                opendDialogImage();
                            }
                        });
                        break;
//                    default:
//                        ModelTextNotePost text1 = new ModelTextNotePost();
//                        layoutTextNote.setVisibility(View.VISIBLE);
//                        layoutNoteImage.setVisibility(View.GONE);
//                        layoutCheckList.setVisibility(View.GONE);
//                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        menuTextNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu_Dialog(Gravity.BOTTOM);
            }
        });


    }

    private void postTextNotePublulic(ModelTextNotePublic obj) {

        APINote.apiService.post_text_note_public(obj).enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                if (response.isSuccessful() & response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        hud.dismiss();
                        onBackPressed();
                        Toast.makeText(CreateNotePublicActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        hud.dismiss();
                        onBackPressed();
                        Toast.makeText(CreateNotePublicActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                hud.dismiss();
                Log.e("TAGdsdsdsdsdsds", "onFailure: " + t);
            }
        });
    }

    private void potsChecklistPublic(ModelCheckListPublic checklist) {
        APINote.apiService.post_checklist_public(checklist).enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        hud.dismiss();
                        onBackPressed();
                        Toast.makeText(CreateNotePublicActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                hud.dismiss();
                Log.e("TAG", "onFailure: " + t);
            }
        });
    }

    private void postImageNotePublic(ModelPostImageNote imageNote) {
        APINote.apiService.post_image_note_public(imageNote).enqueue(new Callback<ModelReturn>() {
            @Override
            public void onResponse(Call<ModelReturn> call, Response<ModelReturn> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        hud.dismiss();
                        onBackPressed();
                        Toast.makeText(CreateNotePublicActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelReturn> call, Throwable t) {
                hud.dismiss();
                Log.e("TAG", "onFailure: "+t );
            }
        });
    }

    public void dialogAddCheckList(Context context) {
        final Dialog dialog = new Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.setContentView(R.layout.dialog_add_check_list);
        TextInputLayout inputCheckList = (TextInputLayout) dialog.findViewById(R.id.input_checkList);
        Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
        CheckBox cbStatus = dialog.findViewById(R.id.cb_status);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputCheckList.getEditText().getText().toString() != "") {
                    ModelCheckListPost obj = new ModelCheckListPost();
                    obj.setContent(inputCheckList.getEditText().getText().toString());
                    obj.setStatus(cbStatus.isChecked());
                    checklists.add(obj);
                    adapterCheckListPost = new AdapterCheckListPost(checklists);
                    rcvCheckList.setAdapter(adapterCheckListPost);
                    inputCheckList.setError("");
                    dialog.dismiss();
                } else {
                    inputCheckList.setError("Không được để trống");
                }

            }
        });
        dialog.show();
    }

    private void opendDialogImage() {
        final Dialog dialog = new Dialog(CreateNotePublicActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
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
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FFBC7D";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#FAE28C";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        green1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#D3EF82";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        green2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#A5EF82";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        mint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#82EFBB";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#82C8EF";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_background = "#8293EF";

                cardView.setCardBackgroundColor(android.graphics.Color.parseColor(color_background));
                dialog.cancel();
            }
        });
        dialog.show();
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

}