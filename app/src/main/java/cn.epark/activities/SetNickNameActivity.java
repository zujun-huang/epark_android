package cn.epark.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.epark.R;
import cn.epark.adapters.LoginTextWatcher;
import cn.epark.utils.OnMultiClickListener;

/**
 * Created by huangzujun on 2020/3/17.
 * Describe: 修改昵称
 */
public class SetNickNameActivity extends BaseAct {

    private TextView title_tv;
    private EditText et_nickname;
    private ImageView clear_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);
        initViews();
    }

    private void initViews() {
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.set_user_nickname);
        et_nickname = findViewById(R.id.et_nickname);
        clear_iv = findViewById(R.id.clear_iv);
        clear_iv.setOnClickListener(clickListener);
        et_nickname.addTextChangedListener(new LoginTextWatcher(clear_iv));
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.clear_iv:
                    et_nickname.setText("");
                    et_nickname.requestFocus();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void finish() {
        String nickName = et_nickname.getText().toString().trim();
        if(!TextUtils.isEmpty(nickName)){
            setResult(Activity.RESULT_OK,
                    getIntent().putExtra("name", nickName));
        }
        super.finish();
    }
}
