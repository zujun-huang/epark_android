package park.bika.com.parkapplication.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baidu.mapapi.model.LatLng;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.StatusBarUtil;
import park.bika.com.parkapplication.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/10/9.
 * Describe: 在线导航
 */
public class OnlineNavigationActivity extends BaseAct implements View.OnClickListener {

    private WebView webView;
    private String toName;
    private LatLng toLatLng ;
    private LatLng fromLatLng ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_navigation);
        webView = findViewById(R.id.webView);
        findViewById(R.id.icon_back).setOnClickListener(this);
        initData(webView.getSettings());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData(WebSettings settings) {
        if (getIntent() != null){
            toName = getIntent().getStringExtra("toName");
            toLatLng = getIntent().getParcelableExtra("toLatLng");
            fromLatLng = getIntent().getParcelableExtra("fromLatLng");
        } else if (TextUtils.isEmpty(toName) || toLatLng == null || fromLatLng == null){
            ToastUtil.showLongToast(context, "位置获取失败，请稍后重试~");
            finish();
            return;
        }
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        webView.loadUrl("http://api.map.baidu.com/direction?origin=name:我的位置" +
                "|latlng:" + fromLatLng.longitude + "," + fromLatLng.longitude +
                "&destination=name:" + toName + "|latlng:" + toLatLng.longitude + "," + toLatLng.longitude +
                "&coord_type=bd09ll&output=html&src=andr.bika.epark"
        );
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            default:
        }
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }
}