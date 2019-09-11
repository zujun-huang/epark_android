package park.bika.com.parkapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.ToastUtil;

/**
 * @作者 huangzujun
 * @日期 2019-07-26
 * @描述 顶部搜索视图
 */
public class SearchView extends LinearLayout implements View.OnClickListener {

    private ImageView iv_search, iv_voice, iv_remove;
    private AutoCompleteTextView aet_search;
    private SearchIconClickListener searchIconClickListener;
    private VoiceIconTouchListener voiceIconTouchListener;
    private String searchTxt, defHintTxt;
    private boolean isVoice;
    private setTxtOnChangedListener mSetTxtOnChangedListener;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        initListener();
    }

    public interface setTxtOnChangedListener {
        void TxtChanged(String searchTxt);
    }

    public void setSetTxtOnChangedListener(setTxtOnChangedListener setTxtOnChangedListener) {
        this.mSetTxtOnChangedListener = setTxtOnChangedListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        iv_search.setOnClickListener(this);
        iv_remove.setOnClickListener(this);
        iv_voice.setOnTouchListener((v, event) -> {
            if (voiceIconTouchListener != null) {
                voiceIconTouchListener.voiceIconTouch(aet_search);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    aet_search.setHint("语音读取中……");
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    aet_search.setText("语音读取功能还未完善，敬请期待~");
                    break;
            }
            return false;
        });
        aet_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() <= 0) {
                    return;
                }
                if (mSetTxtOnChangedListener != null) {
                    mSetTxtOnChangedListener.TxtChanged(aet_search.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //清除图标的显示隐藏
                if (editable.toString().length() > 0) {
                    iv_remove.setVisibility(View.VISIBLE);
                } else {
                    iv_remove.setVisibility(View.INVISIBLE);
                    iv_voice.setSelected(false);
                }
                searchTxt = editable.toString().trim();
            }
        });
        //设置输入法回车搜索
        aet_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                if (searchIconClickListener != null) {
                    searchIconClickListener.searchIconClick(searchTxt);
                }
                return true;
            }
            return false;
        });

    }


    private void init(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_search, this, true);
        iv_search = view.findViewById(R.id.iv_search);
        iv_remove = view.findViewById(R.id.iv_remove);
        iv_voice = view.findViewById(R.id.iv_voice);
        aet_search = view.findViewById(R.id.aet_search);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SearchView);
        setVoice(a.getBoolean(R.styleable.SearchView_isVoice, true));
        searchHintTxt(a.getString(R.styleable.SearchView_hint_txt));
        a.recycle();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                if (searchIconClickListener != null && aet_search.getText().length() > 0) {
                    searchIconClickListener.searchIconClick(searchTxt);
                } else if (aet_search.getText().length() <= 0) {
                    ToastUtil.showToast(getContext(),"请先输入搜索内容后重试！");
                }
                break;
            case R.id.iv_remove:
                aet_search.setText("");
                aet_search.setHint(defHintTxt);
                break;
        }
    }


    /**
     * 搜索图标点击回调搜索内容
     */
    public interface SearchIconClickListener {
        void searchIconClick(String searchTxt);
    }

    /**
     * 语音图标触摸回调\n
     * 搜索框
     */
    public interface VoiceIconTouchListener {
        void voiceIconTouch(EditText editText);
    }

    public void setSearchIconClickListener(SearchIconClickListener searchIconClickListener) {
        this.searchIconClickListener = searchIconClickListener;
    }

    public void setVoiceIconTouchListener(VoiceIconTouchListener voiceIconTouchListener) {
        this.voiceIconTouchListener = voiceIconTouchListener;
    }

    private void searchHintTxt(String searchHintTxt) {
        this.defHintTxt = searchHintTxt;
        aet_search.setHint(searchHintTxt);
    }

    public void setVoice(boolean voice) {
        isVoice = voice;
        if (isVoice) {
            iv_voice.setVisibility(View.VISIBLE);
        } else {
            iv_voice.setVisibility(View.GONE);
        }
    }

    public AutoCompleteTextView getAet_search() {
        return aet_search;
    }
}
