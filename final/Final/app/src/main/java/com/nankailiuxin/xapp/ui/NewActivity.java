package com.nankailiuxin.xapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ielse.imagewatcher.ImageWatcherHelper;
import com.nankailiuxin.textx.TextXEditor;
import com.nankailiuxin.xapp.R;
import com.nankailiuxin.xapp.bean.Group;
import com.nankailiuxin.xapp.bean.Note;
import com.nankailiuxin.xapp.comm.GlideSimpleLoader;
import com.nankailiuxin.xapp.db.GroupDao;
import com.nankailiuxin.xapp.db.NoteDao;
import com.nankailiuxin.xapp.util.CommonUtil;
import com.nankailiuxin.xapp.util.ImageUtils;
import com.nankailiuxin.xapp.comm.MyGlideEngine;
import com.nankailiuxin.xapp.util.SDCardUtil;
import com.nankailiuxin.xapp.util.StringUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewActivity extends BaseActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;

    private EditText et_new_title;
    private TextXEditor et_new_content;
    private TextView tv_new_time;
    private TextView tv_new_group;

    private GroupDao groupDao;
    private NoteDao noteDao;
    private Note note;
    private String myTitle;
    private String myContent;
    private String myGroupName;
    private String myNoteTime;
    private int flag;

    private static final int cutTitleLength = 20;

    private ProgressDialog loadingDialog;
    private ProgressDialog insertDialog;
    private int screenWidth;
    private int screenHeight;
    private Disposable subsLoading;
    private Disposable subsInsert;
    private ImageWatcherHelper iwHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        verifyStoragePermissions(this);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_new);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealwithExit();
            }
        });

        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader());

        groupDao = new GroupDao(this);
        noteDao = new NoteDao(this);
        note = new Note();

        screenWidth = CommonUtil.getScreenWidth(this);
        screenHeight = CommonUtil.getScreenHeight(this);

        insertDialog = new ProgressDialog(this);
        insertDialog.setMessage("传输图片中...");
        insertDialog.setCanceledOnTouchOutside(false);

        et_new_title = findViewById(R.id.et_new_title);
        et_new_content = findViewById(R.id.et_new_content);
        tv_new_time = findViewById(R.id.tv_new_time);
        tv_new_group = findViewById(R.id.tv_new_group);

        openSoftKeyInput();

        try {
            Intent intent = getIntent();
            flag = intent.getIntExtra("flag", 0);

            if (flag == 1){
                setTitle(getResources().getString(R.string.edit_food));
                Bundle bundle = intent.getBundleExtra("data");
                note = (Note) bundle.getSerializable("note");

                if (note != null) {
                    myTitle = note.getTitle();
                    myContent = note.getContent();
                    myNoteTime = note.getCreateTime();
                    Group group = groupDao.queryGroupById(note.getGroupId());
                    if (group != null){
                        myGroupName = group.getName();
                        tv_new_group.setText(myGroupName);
                    }

                    loadingDialog = new ProgressDialog(this);
                    loadingDialog.setMessage("加载数据中...");
                    loadingDialog.setCanceledOnTouchOutside(false);
                    loadingDialog.show();

                    tv_new_time.setText(note.getCreateTime());
                    et_new_title.setText(note.getTitle());
                    et_new_content.post(new Runnable() {
                        @Override
                        public void run() {
                            dealWithContent();
                        }
                    });
                }
            } else {
                setTitle(getResources().getString(R.string.create_food));

                if (myGroupName == null || "全部笔记".equals(myGroupName)) {
                    myGroupName = "默认笔记";
                }

                tv_new_group.setText(myGroupName);
                myNoteTime = CommonUtil.date2string(new Date());
                tv_new_time.setText(myNoteTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealWithContent(){
        et_new_content.clearAllLayout();
        showDataSync(note.getContent());

        et_new_content.setOnRtImageDeleteListener(new TextXEditor.OnRtImageDeleteListener() {

            @Override
            public void onRtImageDelete(String imagePath) {
                if (!TextUtils.isEmpty(imagePath)) {
                    boolean isOK = SDCardUtil.deleteFile(imagePath);
                    if (isOK) {
//                        showToast("删除成功：" + imagePath);
                    }
                }
            }
        });

        et_new_content.setOnRtImageClickListener(new TextXEditor.OnRtImageClickListener() {
            @Override
            public void onRtImageClick(View view, String imagePath) {
                try {
                    myContent = getEditData();
                    if (!TextUtils.isEmpty(myContent)){
                        List<String> imageList = StringUtils.getTextFromHtml(myContent, true);
                        if (!TextUtils.isEmpty(imagePath)) {
                            int currentPosition = imageList.indexOf(imagePath);
//                            showToast("点击图片：" + currentPosition + "：" + imagePath);

                            List<Uri> dataList = new ArrayList<>();
                            for (int i = 0; i < imageList.size(); i++) {
                                dataList.add(ImageUtils.getUriFromPath(imageList.get(i)));
                            }
                            iwHelper.show(dataList, currentPosition);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void closeSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        if (imm != null && imm.isActive() && getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void openSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && !imm.isActive() && et_new_content != null){
            et_new_content.requestFocus();
            imm.showSoftInputFromInputMethod(et_new_content.getWindowToken(),
                    InputMethodManager.SHOW_FORCED);
        }
    }

    private void showDataSync(final String html){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                showEditData(emitter, html);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onComplete() {
                if (loadingDialog != null){
                    loadingDialog.dismiss();
                }
                if (et_new_content != null) {
                    et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), "");
                }
            }

            @Override
            public void onError(Throwable e) {
                if (loadingDialog != null){
                    loadingDialog.dismiss();
                }
                showToast("错误");
            }

            @Override
            public void onSubscribe(Disposable d) {
                subsLoading = d;
            }

            @Override
            public void onNext(String text) {
                try {
                    if (et_new_content != null) {
                        if (text.contains("<img") && text.contains("src=")) {
                            String imagePath = StringUtils.getImgSrc(text);
                            et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), "");
                            et_new_content.addImageViewAtIndex(et_new_content.getLastIndex(), imagePath);
                        } else {
                            et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), text);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void showEditData(ObservableEmitter<String> emitter, String html) {
        try{
            List<String> textList = StringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        }catch (Exception e){
            e.printStackTrace();
            emitter.onError(e);
        }
    }

    private String getEditData() {
        StringBuilder content = new StringBuilder();
        try {
            List<TextXEditor.EditData> editList = et_new_content.buildEditData();
            for (TextXEditor.EditData itemData : editList) {
                if (itemData.inputStr != null) {
                    content.append(itemData.inputStr);
                } else if (itemData.imagePath != null) {
                    content.append("<img src=\"").append(itemData.imagePath).append("\"/>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void saveNoteData(boolean isBackground) {
        String noteTitle = et_new_title.getText().toString();
        String noteContent = getEditData();
        String groupName = tv_new_group.getText().toString();
        String noteTime = tv_new_time.getText().toString();

        try {
            Group group = groupDao.queryGroupByName("默认笔记");
            if (group != null) {
                if (noteTitle.length() == 0 ){
                    if (noteContent.length() > cutTitleLength){
                        noteTitle = noteContent.substring(0,cutTitleLength);
                    } else if (noteContent.length() > 0){
                        noteTitle = noteContent;
                    }
                }
                int groupId = group.getId();
                note.setTitle(noteTitle);
                note.setContent(noteContent);
                note.setGroupId(groupId);
                note.setGroupName(groupName);
                note.setType(2);
                note.setBgColor("#FFFFFF");
                note.setIsEncrypt(0);
                note.setCreateTime(CommonUtil.date2string(new Date()));
                if (flag == 0 ) {
                    if (noteTitle.length() == 0 && noteContent.length() == 0) {
                        if (!isBackground){
                            Toast.makeText(NewActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        long noteId = noteDao.insertNote(note);
                        note.setId((int) noteId);
                        flag = 1;
                        if (!isBackground){
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }else if (flag == 1) {
                    if (!noteTitle.equals(myTitle) || !noteContent.equals(myContent)
                            || !groupName.equals(myGroupName) || !noteTime.equals(myNoteTime)) {
                        noteDao.updateNote(note);
                    }
                    if (!isBackground){
                        finish();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_image:
                closeSoftKeyInput();
                callGallery();
                break;
            case R.id.action_new_save:
                try {
                    saveNoteData(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callGallery(){
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))
                .countable(true)
                .maxSelectable(3)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .theme(R.style.Matisse_Zhihu)
                .imageEngine(new MyGlideEngine())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true,"com.nankailiuxin.matisse.fileprovider"))
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == 1){
                } else if (requestCode == REQUEST_CODE_CHOOSE){
                    insertImagesSync(data);
                }
            }
        }
    }

    private void insertImagesSync(final Intent data){
        insertDialog.show();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                try{
                    et_new_content.measure(0, 0);
                    List<Uri> mSelected = Matisse.obtainResult(data);
                    for (Uri imageUri : mSelected) {
                        String imagePath = SDCardUtil.getFilePathFromUri(NewActivity.this,  imageUri);
                        Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath, screenWidth, screenHeight);
                        imagePath = SDCardUtil.saveToSdCard(bitmap);
                        emitter.onNext(imagePath);
                    }

                    emitter.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onComplete() {
                if (insertDialog != null && insertDialog.isShowing()) {
                    insertDialog.dismiss();
                }
//                showToast("图片插入成功");
            }

            @Override
            public void onError(Throwable e) {
                if (insertDialog != null && insertDialog.isShowing()) {
                    insertDialog.dismiss();
                }
//                showToast("图片插入失败");
            }

            @Override
            public void onSubscribe(Disposable d) {
                subsInsert = d;
            }

            @Override
            public void onNext(String imagePath) {
                et_new_content.insertImage(imagePath);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (CommonUtil.isAppOnBackground(getApplicationContext()) ||
                    CommonUtil.isLockScreeen(getApplicationContext())){
                saveNoteData(true);
            }

            if (subsLoading != null && subsLoading.isDisposed()){
                subsLoading.dispose();
            }
            if (subsInsert != null && subsInsert.isDisposed()){
                subsInsert.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealwithExit(){
        try {
            String noteTitle = et_new_title.getText().toString();
            String noteContent = getEditData();
            String groupName = tv_new_group.getText().toString();
            String noteTime = tv_new_time.getText().toString();
            if (flag == 0) {
                if (noteTitle.length() > 0 || noteContent.length() > 0) {
                    saveNoteData(false);
                }
            }else if (flag == 1) {
                if (!noteTitle.equals(myTitle) || !noteContent.equals(myContent)
                        || !groupName.equals(myGroupName) || !noteTime.equals(myNoteTime)) {
                    saveNoteData(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
        dealwithExit();
    }
}
