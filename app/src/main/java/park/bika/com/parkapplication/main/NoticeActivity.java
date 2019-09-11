package park.bika.com.parkapplication.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.StatusBarUtil;
/**
 * Created by huangzujun on 2019/9/4.
 * Describe: 通知
 */
public class NoticeActivity extends BaseAct implements View.OnClickListener {

    private ImageView icon_back;
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initView();
        initData();
    }

    private void initData() {
        if (getIntent() != null){
            String title = getIntent().getStringExtra("title");
            if (!TextUtils.isEmpty(title)) title_tv.setText(title);
        }
    }

    private void initView() {
        icon_back = findViewById(R.id.icon_back);
        icon_back.setOnClickListener(this);
        title_tv = findViewById(R.id.title_tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
        }
    }


    /**
     * 设置状态栏颜色
     */
    @Override
    public void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

}
