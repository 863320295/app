package com.nankailiuxin.textx;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextXView extends ScrollView {
    private static final int EDIT_PADDING = 10;
    private int viewTagIndex = 1;
    private LinearLayout allLayout;
    private LayoutInflater inflater;
    private TextView lastFocusText;
    private LayoutTransition mTransitioner;
    private int editNormalPadding = 0;
    private int disappearingImageIndex = 0;
    private OnClickListener btnListener;
    private ArrayList<String> imagePaths;
    private String keywords;
    private OnRtImageClickListener onRtImageClickListener;

    private int rtImageHeight = 0;
    private int rtImageBottom = 10;
    private String rtTextInitHint = "没有内容";
    private int rtTextSize = 16;
    private int rtTextColor = Color.parseColor("#757575");
    private int rtTextLineSpace = 8;

    public TextXView(Context context) {
        this(context, null);
    }

    public TextXView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextXView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextXView);
        rtImageHeight = ta.getInteger(R.styleable.TextXView_rt_view_image_height, 0);
        rtImageBottom = ta.getInteger(R.styleable.TextXView_rt_view_image_bottom, 10);
        rtTextSize = ta.getDimensionPixelSize(R.styleable.TextXView_rt_view_text_size, 16);
        rtTextLineSpace = ta.getDimensionPixelSize(R.styleable.TextXView_rt_view_text_line_space, 8);
        rtTextColor = ta.getColor(R.styleable.TextXView_rt_view_text_color, Color.parseColor("#757575"));
        rtTextInitHint = ta.getString(R.styleable.TextXView_rt_view_text_init_hint);

        ta.recycle();

        imagePaths = new ArrayList<>();

        inflater = LayoutInflater.from(context);

        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        allLayout.setPadding(50,15,50,15);
        addView(allLayout, layoutParams);

        btnListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v instanceof DataImageView){
                    DataImageView imageView = (DataImageView) v;
                    if (onRtImageClickListener != null){
                        onRtImageClickListener.onRtImageClick(imageView, imageView.getAbsolutePath());
                    }
                }
            }
        };

        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        TextView firstText = createTextView(rtTextInitHint, dip2px(context, EDIT_PADDING));
        allLayout.addView(firstText, firstEditParam);
        lastFocusText = firstText;
    }

    private int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    public interface OnRtImageClickListener{
        void onRtImageClick(View view, String imagePath);
    }

    public void setOnRtImageClickListener(OnRtImageClickListener onRtImageClickListener) {
        this.onRtImageClickListener = onRtImageClickListener;
    }

    public void clearAllLayout(){
        allLayout.removeAllViews();
    }

    public int getLastIndex(){
        int lastEditIndex = allLayout.getChildCount();
        return lastEditIndex;
    }

    public TextView createTextView(String hint, int paddingTop) {
        TextView textView = (TextView) inflater.inflate(R.layout.rich_textview, null);
        textView.setTag(viewTagIndex++);
        textView.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
        textView.setHint(hint);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rtTextSize);
        textView.setLineSpacing(rtTextLineSpace, 1.0f);
        textView.setTextColor(rtTextColor);
        return textView;
    }

    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.edit_imageview, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setVisibility(GONE);
        DataImageView imageView = layout.findViewById(R.id.edit_imageView);
        //imageView.setTag(layout.getTag());
		imageView.setOnClickListener(btnListener);
        return layout;
    }

    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span;
        try {
            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(text);
            while (m.find()) {
                span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));
                spannable.setSpan(span, m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannable;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void addTextViewAtIndex(final int index, CharSequence editStr) {
        try {
            TextView textView = createTextView("", EDIT_PADDING);
            if (!TextUtils.isEmpty(keywords)) {
                SpannableStringBuilder textStr = highlight(editStr.toString(), keywords);
                textView.setText(textStr);
            } else {
                textView.setText(editStr);
            }

            allLayout.addView(textView, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addImageViewAtIndex(final int index, final String imagePath) {
        if (TextUtils.isEmpty(imagePath)){
            return;
        }
        imagePaths.add(imagePath);
        RelativeLayout imageLayout = createImageLayout();
        if (imageLayout == null){
            return;
        }
        final DataImageView imageView = imageLayout.findViewById(R.id.edit_imageView);
        imageView.setAbsolutePath(imagePath);

        TextX.getInstance().loadImage(imagePath, imageView, rtImageHeight);

        allLayout.addView(imageLayout, index);
    }

    public Bitmap getScaledBitmap(String filePath, int width) {
        if (TextUtils.isEmpty(filePath)){
            return null;
        }
        BitmapFactory.Options options = null;
        try {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int sampleSize = options.outWidth > width ? options.outWidth / width
                    + 1 : 1;
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(filePath, options);
    }

    private void setupLayoutTransitions() {
        mTransitioner = new LayoutTransition();
        allLayout.setLayoutTransition(mTransitioner);
        mTransitioner.addTransitionListener(new LayoutTransition.TransitionListener() {

            @Override
            public void startTransition(LayoutTransition transition,
                                        ViewGroup container, View view, int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition,
                                      ViewGroup container, View view, int transitionType) {
                if (!transition.isRunning()
                        && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                }
            }
        });
        mTransitioner.setDuration(300);
    }

    public int getRtImageHeight() {
        return rtImageHeight;
    }

    public void setRtImageHeight(int rtImageHeight) {
        this.rtImageHeight = rtImageHeight;
    }

    public int getRtImageBottom() {
        return rtImageBottom;
    }

    public void setRtImageBottom(int rtImageBottom) {
        this.rtImageBottom = rtImageBottom;
    }

    public String getRtTextInitHint() {
        return rtTextInitHint;
    }

    public void setRtTextInitHint(String rtTextInitHint) {
        this.rtTextInitHint = rtTextInitHint;
    }

    public int getRtTextSize() {
        return rtTextSize;
    }

    public void setRtTextSize(int rtTextSize) {
        this.rtTextSize = rtTextSize;
    }

    public int getRtTextColor() {
        return rtTextColor;
    }

    public void setRtTextColor(int rtTextColor) {
        this.rtTextColor = rtTextColor;
    }

    public int getRtTextLineSpace() {
        return rtTextLineSpace;
    }

    public void setRtTextLineSpace(int rtTextLineSpace) {
        this.rtTextLineSpace = rtTextLineSpace;
    }
}
