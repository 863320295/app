package com.nankailiuxin.xapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.ielse.imagewatcher.ImageWatcherHelper;
import com.nankailiuxin.textx.TextXView;
import com.nankailiuxin.xapp.R;
import com.nankailiuxin.xapp.bean.Group;
import com.nankailiuxin.xapp.bean.Note;
import com.nankailiuxin.xapp.comm.GlideSimpleLoader;
import com.nankailiuxin.xapp.db.GroupDao;
import com.nankailiuxin.xapp.util.CommonUtil;
import com.nankailiuxin.xapp.util.ImageUtils;
import com.nankailiuxin.xapp.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteActivity extends BaseActivity {
    private static final String TAG = "NoteActivity";

    private TextXView tv_note_content;
    private Note note;
    private String myTitle;
    private String myContent;
    private String myGroupName;

    private ProgressDialog loadingDialog;
    private Disposable mDisposable;
    private ImageWatcherHelper iwHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_note);
        toolbar.setTitle(getResources().getString(R.string.more_food));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader());

        GroupDao groupDao = new GroupDao(this);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("加载数据中...");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

        TextView tv_note_title = findViewById(R.id.tv_note_title);
        tv_note_title.setTextIsSelectable(true);
        tv_note_content = findViewById(R.id.tv_note_content);

        TextView tv_note_time = findViewById(R.id.tv_note_time);
        TextView tv_note_group = findViewById(R.id.tv_note_group);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra("data");
            note = (Note) bundle.getSerializable("note");

            if (note != null) {
                myTitle = note.getTitle();
                myContent = note.getContent();
                Group group = groupDao.queryGroupById(note.getGroupId());
//                if (group != null) {
//                    myGroupName = group.getName();
//                    tv_note_group.setText(myGroupName);
//                }

                tv_note_title.setText(myTitle);
                tv_note_content.post(new Runnable() {
                    @Override
                    public void run() {
                        dealWithContent();
                    }
                });
                tv_note_time.setText(note.getCreateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dealWithContent(){
        tv_note_content.clearAllLayout();
        showDataSync(myContent);
        tv_note_content.setOnRtImageClickListener(new TextXView.OnRtImageClickListener() {
            @Override
            public void onRtImageClick(View view, String imagePath) {
                try {
                    ArrayList<String> imageList = StringUtils.getTextFromHtml(myContent, true);
                    int currentPosition = imageList.indexOf(imagePath);
//                    showToast("点击图片："+currentPosition+"："+imagePath);

                    List<Uri> dataList = new ArrayList<>();
                    for (int i = 0; i < imageList.size(); i++) {
                        dataList.add(ImageUtils.getUriFromPath(imageList.get(i)));
                    }
                    iwHelper.show(dataList, currentPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                mDisposable = d;
            }

            @Override
            public void onNext(String text) {
                try {
                    if (tv_note_content !=null) {
                        if (text.contains("<img") && text.contains("src=")) {
                            String imagePath = StringUtils.getImgSrc(text);
                            tv_note_content.addImageViewAtIndex(tv_note_content.getLastIndex(), imagePath);
                        } else {
                            tv_note_content.addTextViewAtIndex(tv_note_content.getLastIndex(), text);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showEditData(ObservableEmitter<String> emitter, String html) {
        try {
            List<String> textList = StringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        } catch (Exception e){
            e.printStackTrace();
            emitter.onError(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_note_edit:
                Intent intent = new Intent(NoteActivity.this, NewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("data", bundle);
                intent.putExtra("flag", 1);
                startActivity(intent);
                finish();
                break;
            case R.id.action_note_share:
                CommonUtil.shareTextAndImage(this, note.getTitle(), note.getContent(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
        finish();
    }
}
