package com.nankailiuxin.textx;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({ "NewApi", "InflateParams" })
public class TextXEditor extends ScrollView {
	private static final int EDIT_PADDING = 10;

	private int viewTagIndex = 1;
	private LinearLayout allLayout;
	private LayoutInflater inflater;
	private OnKeyListener keyListener;
	private OnClickListener btnListener;
	private OnFocusChangeListener focusListener;
	private EditText lastFocusEdit;
	private LayoutTransition mTransitioner;
	private int editNormalPadding = 0;
	private int disappearingImageIndex = 0;

	private ArrayList<String> imagePaths;
	private String keywords;

	private int rtImageHeight = 500;
    private int rtImageBottom = 10;
    private String rtTextInitHint = "请输入内容";
    private int rtTextSize = 16;
    private int rtTextColor = Color.parseColor("#757575");
	private int rtTextLineSpace = 8;

	private OnRtImageDeleteListener onRtImageDeleteListener;
	private OnRtImageClickListener onRtImageClickListener;

	public TextXEditor(Context context) {
		this(context, null);
	}

	public TextXEditor(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextXEditor(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextXEditor);
		rtImageHeight = ta.getInteger(R.styleable.TextXEditor_rt_editor_image_height, 500);
        rtImageBottom = ta.getInteger(R.styleable.TextXEditor_rt_editor_image_bottom, 10);
        rtTextSize = ta.getDimensionPixelSize(R.styleable.TextXEditor_rt_editor_text_size, 16);
		rtTextLineSpace = ta.getDimensionPixelSize(R.styleable.TextXEditor_rt_editor_text_line_space, 8);
		rtTextColor = ta.getColor(R.styleable.TextXEditor_rt_editor_text_color, Color.parseColor("#757575"));
        rtTextInitHint = ta.getString(R.styleable.TextXEditor_rt_editor_text_init_hint);

		ta.recycle();

		imagePaths = new ArrayList<>();

		inflater = LayoutInflater.from(context);

		allLayout = new LinearLayout(context);
		allLayout.setOrientation(LinearLayout.VERTICAL);
		setupLayoutTransitions();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		allLayout.setPadding(50,15,50,15);
		addView(allLayout, layoutParams);

		keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
					EditText edit = (EditText) v;
					onBackspacePress(edit);
				}
				return false;
			}
		};

		btnListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v instanceof DataImageView){
					DataImageView imageView = (DataImageView)v;
					if (onRtImageClickListener != null){
						onRtImageClickListener.onRtImageClick(imageView, imageView.getAbsolutePath());
					}
				} else if (v instanceof ImageView){
					RelativeLayout parentView = (RelativeLayout) v.getParent();
					onImageCloseClick(parentView);
				}
			}
		};

		focusListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lastFocusEdit = (EditText) v;
				}
			}
		};

		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		EditText firstEdit = createEditText(rtTextInitHint, dip2px(context, EDIT_PADDING));
		allLayout.addView(firstEdit, firstEditParam);
		lastFocusEdit = firstEdit;
	}

    private int dip2px(Context context, float dipValue) {
		float m = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * m + 0.5f);
	}

	private void onBackspacePress(EditText editTxt) {
		try {
			int startSelection = editTxt.getSelectionStart();
			if (startSelection == 0) {
				int editIndex = allLayout.indexOfChild(editTxt);
				View preView = allLayout.getChildAt(editIndex - 1);
				if (null != preView) {
					if (preView instanceof RelativeLayout) {
						onImageCloseClick(preView);
					} else if (preView instanceof EditText) {
						String str1 = editTxt.getText().toString();
						EditText preEdit = (EditText) preView;
						String str2 = preEdit.getText().toString();

						allLayout.setLayoutTransition(null);
						allLayout.removeView(editTxt);
						allLayout.setLayoutTransition(mTransitioner);

						preEdit.setText(String.valueOf(str2 + str1));
						preEdit.requestFocus();
						preEdit.setSelection(str2.length(), str2.length());
						lastFocusEdit = preEdit;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface OnRtImageDeleteListener{
		void onRtImageDelete(String imagePath);
	}

	public void setOnRtImageDeleteListener(OnRtImageDeleteListener onRtImageDeleteListener) {
		this.onRtImageDeleteListener = onRtImageDeleteListener;
	}

	public interface OnRtImageClickListener{
		void onRtImageClick(View view, String imagePath);
	}

	public void setOnRtImageClickListener(OnRtImageClickListener onRtImageClickListener) {
		this.onRtImageClickListener = onRtImageClickListener;
	}

	private void onImageCloseClick(View view) {
		try {
			if (!mTransitioner.isRunning()) {
				disappearingImageIndex = allLayout.indexOfChild(view);

				List<EditData> dataList = buildEditData();
				EditData editData = dataList.get(disappearingImageIndex);
				if (editData.imagePath != null){
					if (onRtImageDeleteListener != null){
						onRtImageDeleteListener.onRtImageDelete(editData.imagePath);
					}
					imagePaths.remove(editData.imagePath);
				}
				allLayout.removeView(view);
				mergeEditText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearAllLayout(){
		allLayout.removeAllViews();
	}

	public int getLastIndex(){
		int childCount = allLayout.getChildCount();
		return childCount;
	}

	public EditText createEditText(String hint, int paddingTop) {
		EditText editText = (EditText) inflater.inflate(R.layout.rich_edittext, null);
		editText.setOnKeyListener(keyListener);
		editText.setTag(viewTagIndex++);
		editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
		editText.setHint(hint);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rtTextSize);
		editText.setTextColor(rtTextColor);
		editText.setLineSpacing(rtTextLineSpace, 1.0f);
		editText.setOnFocusChangeListener(focusListener);
		return editText;
	}

	private RelativeLayout createImageLayout() {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.edit_imageview, null);
		layout.setTag(viewTagIndex++);
		View closeView = layout.findViewById(R.id.image_close);
		closeView.setTag(layout.getTag());
		closeView.setOnClickListener(btnListener);
		DataImageView imageView = layout.findViewById(R.id.edit_imageView);
		imageView.setOnClickListener(btnListener);
		return layout;
	}

	public void insertImage(String imagePath) {
		if (TextUtils.isEmpty(imagePath)){
			return;
		}
		try {
			String lastEditStr = lastFocusEdit.getText().toString();
			int cursorIndex = lastFocusEdit.getSelectionStart();
			String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
			String editStr2 = lastEditStr.substring(cursorIndex).trim();
			int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);

			if (lastEditStr.length() == 0) {
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, imagePath);
			} else if (editStr1.length() == 0) {
				addImageViewAtIndex(lastEditIndex, imagePath);
				addEditTextAtIndex(lastEditIndex + 1, "");
			} else if (editStr2.length() == 0) {
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, imagePath);
			} else {
				lastFocusEdit.setText(editStr1);
				addEditTextAtIndex(lastEditIndex + 1, editStr2);
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, imagePath);
			}
			hideKeyBoard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && lastFocusEdit != null) {
			imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
		}
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

	public void addEditTextAtIndex(final int index, CharSequence editStr) {
		try {
			EditText editText2 = createEditText("输入内容", EDIT_PADDING);
			if (!TextUtils.isEmpty(keywords)) {
				SpannableStringBuilder textStr = highlight(editStr.toString(), keywords);
				editText2.setText(textStr);
			} else if (!TextUtils.isEmpty(editStr)) {
				editText2.setText(editStr);
			}
			editText2.setOnFocusChangeListener(focusListener);

			allLayout.setLayoutTransition(null);
			allLayout.addView(editText2, index);
			allLayout.setLayoutTransition(mTransitioner);
			lastFocusEdit = editText2;
			lastFocusEdit.requestFocus();
			lastFocusEdit.setSelection(editStr.length(), editStr.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addImageViewAtIndex(final int index, final String imagePath) {
		if (TextUtils.isEmpty(imagePath)){
			return;
		}
		try {
			imagePaths.add(imagePath);
			final RelativeLayout imageLayout = createImageLayout();
			DataImageView imageView = imageLayout.findViewById(R.id.edit_imageView);
			imageView.setAbsolutePath(imagePath);
			TextX.getInstance().loadImage(imagePath, imageView, rtImageHeight);
			allLayout.addView(imageLayout, index);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Bitmap getScaledBitmap(String filePath, int width) {
		if (TextUtils.isEmpty(filePath)){
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
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
					 mergeEditText();
				}
			}
		});
		mTransitioner.setDuration(300);
	}

	private void mergeEditText() {
		try {
			View preView = allLayout.getChildAt(disappearingImageIndex - 1);
			View nextView = allLayout.getChildAt(disappearingImageIndex);
			if (preView instanceof EditText && nextView instanceof EditText) {

				EditText preEdit = (EditText) preView;
				EditText nextEdit = (EditText) nextView;
				String str1 = preEdit.getText().toString();
				String str2 = nextEdit.getText().toString();
				String mergeText = "";
				if (str2.length() > 0) {
					mergeText = str1 + "\n" + str2;
				} else {
					mergeText = str1;
				}

				allLayout.setLayoutTransition(null);
				allLayout.removeView(nextEdit);
				preEdit.setText(mergeText);
				preEdit.requestFocus();
				preEdit.setSelection(str1.length(), str1.length());
				allLayout.setLayoutTransition(mTransitioner);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<EditData> buildEditData() {
		List<EditData> dataList = new ArrayList<EditData>();
		try {
			int num = allLayout.getChildCount();
			for (int index = 0; index < num; index++) {
				View itemView = allLayout.getChildAt(index);
				EditData itemData = new EditData();
				if (itemView instanceof EditText) {
					EditText item = (EditText) itemView;
					itemData.inputStr = item.getText().toString();
				} else if (itemView instanceof RelativeLayout) {
					DataImageView item = (DataImageView) itemView.findViewById(R.id.edit_imageView);
					itemData.imagePath = item.getAbsolutePath();
				}
				dataList.add(itemData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataList;
	}

	public class EditData {
		public String inputStr;
		public String imagePath;
		public Bitmap bitmap;
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
