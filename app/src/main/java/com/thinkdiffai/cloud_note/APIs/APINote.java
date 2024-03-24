package com.thinkdiffai.cloud_note.APIs;

import com.thinkdiffai.cloud_note.Model.GET.FolderModel;
import com.thinkdiffai.cloud_note.Model.GET.GroupModel;
import com.thinkdiffai.cloud_note.Model.GET.MessageModel;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetImageNote;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetScreenShots;
import com.thinkdiffai.cloud_note.Model.GET.ResponseComment;
import com.thinkdiffai.cloud_note.Model.GET.ResponseFolder;
import com.thinkdiffai.cloud_note.Model.GET.ResponseGroup;
import com.thinkdiffai.cloud_note.Model.GET.ResponseMessage;
import com.thinkdiffai.cloud_note.Model.GET.ResponseProfile;
import com.thinkdiffai.cloud_note.Model.GET.ResponseUser;
import com.thinkdiffai.cloud_note.Model.LoginModel;
import com.thinkdiffai.cloud_note.Model.ModelListLastUser;
import com.thinkdiffai.cloud_note.Model.PATCH.ChangPublicNote;
import com.thinkdiffai.cloud_note.Model.PATCH.ModelPutCheckList;
import com.thinkdiffai.cloud_note.Model.PATCH.ModelPutTextNote;
import com.thinkdiffai.cloud_note.Model.POST.CommentPostModel;
import com.thinkdiffai.cloud_note.Model.POST.FolderPostModel;
import com.thinkdiffai.cloud_note.Model.POST.GroupPostModel;
import com.thinkdiffai.cloud_note.Model.POST.LoginReq;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetCheckList;
import com.thinkdiffai.cloud_note.Model.GET.ModelGetNoteText;
import com.thinkdiffai.cloud_note.Model.GET.ModelReturn;
import com.thinkdiffai.cloud_note.Model.POST.ModelPostImageNote;
import com.thinkdiffai.cloud_note.Model.POST.ModelPostImageNote1;
import com.thinkdiffai.cloud_note.Model.POST.ModelPostScreenShot;
import com.thinkdiffai.cloud_note.Model.POST.ModelTextNoteCheckListPost;
import com.thinkdiffai.cloud_note.Model.POST.ModelTextNotePost;
import com.thinkdiffai.cloud_note.Model.GET.Model_Notes;
import com.thinkdiffai.cloud_note.Model.POST.Public.ModelCheckListPublic;
import com.thinkdiffai.cloud_note.Model.POST.Public.ModelTextNotePublic;
import com.thinkdiffai.cloud_note.Model.POST.RegiterReq;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thinkdiffai.cloud_note.Model.POST.ResponsePostMessageUK;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APINote {
    // khởi tạo Gson
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    // khởi tạo retrofit
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder okbilder = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(httpLoggingInterceptor);
    APINote apiService = new Retrofit.Builder()
            // là dumain cảu api
            .baseUrl("https://samnote.mangasocial.online/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okbilder.build())
            .build().create(APINote.class);

    APINote apiSV = new Retrofit.Builder().
            baseUrl("https://samnote.mangasocial.online/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APINote.class);
    @Multipart
    @POST("new-note/{id}")
    Call<ModelPostImageNote1> postImageNote(@Path("id") int id,
                                            @Part("type")RequestBody type,
                                            @Part("title")RequestBody title,
                                            @Part("content")RequestBody content,
                                            @Part("r")RequestBody r,
                                            @Part("g")RequestBody g,
                                            @Part("b")RequestBody b,
                                            @Part("a")RequestBody a,
                                            @Part MultipartBody.Part image_note,
                                            @Part("remind")RequestBody remind);


    @POST("login")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<LoginModel> login(@Body LoginReq loginReq);


    @POST("register")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<ModelReturn> Regiter(@Body RegiterReq regiterReq);

    @GET("notes/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<Model_Notes> getListNoteByUser(@Path("id") int id);


    @GET("notes/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Call<Objects> getListNoteByUser2(@Path("id") int id);
    @POST("notes/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<ModelReturn> post_text_note(@Path("id") int id, @Body ModelTextNotePost modelTextNotePost);

    @POST("notes/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<ModelReturn> post_Check_list(@Path("id") int id, @Body ModelTextNoteCheckListPost modelTextNoteCheckList);

    @POST("new-note/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<ModelReturn> post_image_note(@Path("id") int id , @Body ModelPostImageNote modelPostImageNote);

    @PATCH("notes/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<ModelReturn> patch_text_note(@Path("id") int id, @Body ModelPutTextNote modelPutTextNote);

    @PATCH("notes/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Observable<ModelReturn> patch_Check_list(@Path("id") int id, @Body ModelPutCheckList modelPutCheckList);

    @GET("only/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelGetNoteText> getNoteByIdTypeText(@Path("id") int id);

    @GET("only/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelGetCheckList> getNoteByIdTypeCheckList(@Path("id") int id);
    @GET("only/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelGetImageNote> getNoteByIdTypeImage(@Path("id") int id);
    @GET("only/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelGetScreenShots>getNoteByIdTypeScreenshot(@Path("id") int id);

    @DELETE("trunc-notes/{id}")
    Call<ModelReturn> deleteNote(@Path("id") int id);

    @DELETE("notes/{id}")
    Call<ModelReturn> moveToTrash(@Path("id") int id);

    @GET("trash/{id}")
    Observable<Model_Notes> getListTrash(@Path("id") int id);
    @POST("trash-res/{id}")
    Call<ModelReturn> restore(@Path("id") int id);

    @POST("notes/{id}")
    Call<ModelReturn> postScreenShot(@Path("id") int id,@Body ModelPostScreenShot image);
    @GET("lastUser")
    Call<ModelListLastUser> getListLastUser();
    @GET("notes/10")
    Call<Model_Notes> getNotePublic();

    @POST("notes/10")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelReturn> post_text_note_public( @Body ModelTextNotePublic modelTextNotePost);
    @POST("notes/10")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelReturn> post_checklist_public( @Body ModelCheckListPublic modelCheckListPublic);
    @POST("notes/10")
    @Headers({
            "Content-type: Application/json"
    })
    Call<ModelReturn> post_image_note_public( @Body ModelPostImageNote modelPostImageNote);

    @GET("group/all/{id}")
    Call<ResponseGroup> getAllGroup(@Path("id") int id);

    @PATCH("notes_public/{id}")
    Call<ModelReturn> changePublicNote(@Path("id") int id, @Body ChangPublicNote changPublicNote);
    @GET("notes/notes-comment/{id}")
    Call<ResponseComment> getComment(@Path("id") int id);
    @POST("notes/notes-comment/{id}")
    @Headers({
            "Content-type: Application/json"
    })
    Call<Void> postComment(@Path("id") int id, @Body CommentPostModel commentPostModel);

    @GET("message/chat-unknown/{idSend}")
    Call<ResponseMessage> getMessageData(@Path("idSend") int id);
    @POST("message/chat-unknown/{idReceive}")
    Call<ResponsePostMessageUK> postMessageUK(@Path("idReceive") int id, @Body MessageModel model);
    @GET("folder/{id}")
    Call<ResponseFolder> getFolder(@Path("id") int id);
    @POST("folder/{id}")
    Call<ModelReturn> postFolder(@Path("id") int id, @Body FolderPostModel model);
    @PATCH("changefolder/{idFolder}")
    Call<ModelReturn> patchFolder(@Path("idFolder") int idFolder, @Body FolderPostModel model);
    @DELETE("changefolder/{idFolder}")
    Call<ModelReturn> deleteFolder(@Path("idFolder") int idFolder);
    @GET("profile/{id}")
    Call<ResponseProfile> getProfile(@Path("id") int id);
    @GET("allUsers/10")
    Call<ResponseUser> getAllUser();
    @POST("group/create/{id}")
    Call<Void> postGroup(@Path("id") int id, @Body GroupPostModel groupPostModel);

}
