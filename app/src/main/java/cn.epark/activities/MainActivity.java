package cn.epark.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.baidu.mapapi.SDKInitializer;

import cn.epark.App;
import cn.epark.BuildConfig;
import cn.epark.MainBar;
import cn.epark.PackageType;
import cn.epark.R;
import cn.epark.fragments.MainFragment;
import cn.epark.fragments.MyselfFragment;
import cn.epark.fragments.NearbyFragment;
import cn.epark.fragments.ShareCarFragment;
import cn.epark.utils.NetWorkReceiver;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.ToastUtil;
import cn.epark.utils.ToolBarUtil;

public class MainActivity extends BaseAct {

    private LinearLayout mLLToolBar;
    private String[] TOOLBAR_TITLES;
    private int[] iconArr;

    public String currentDistrict = "重庆";

    private ToolBarUtil toolBarUtil;
    private int curViewId = -1;
    public MainFragment mainFragment;
    public NearbyFragment nearbyFragment;
    public ShareCarFragment shareCarFragment;
    public MyselfFragment myselfFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
        showPreferential();
    }

    private void showPreferential() {
        if (App.getAccount().getPwdIsNull() && ShareUtil.newInstance().isNewUser(context)) {
            //新用户首次登录优惠
            Dialog dialog = new Dialog(context);
            dialog.setCanceledOnTouchOutside(false);
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setGravity(Gravity.CENTER);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogWindow.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
            }
            View view = View.inflate(context, R.layout.layout_dialog_new_user_preferential, null);
            view.findViewById(R.id.close_btn).setOnClickListener((v)-> dialog.dismiss());
            view.findViewById(R.id.submit_btn).setOnClickListener((v)-> {
                // TODO: 2020/7/17 优惠券领取
                dialog.dismiss();
            });
            dialog.setContentView(view);
            dialog.show();
            ShareUtil.newInstance().setNewUserLogin(context);
        }
    }

    private void initListener() {
        toolBarUtil.setOnToolBarClickListener(this::getContentView);
        if (App.getAccount().isEmptyId()) {
            App.getInstance().startSessionTimer();
        }
    }

    private void initData() {
        netWorkReceiver = new NetWorkReceiver(context, this);
        //底部按钮
        switch (BuildConfig.type) {
            case PackageType.EP_PUBLIC:
                TOOLBAR_TITLES = new String[]{"首页", "附近", "我的"};
                iconArr = new int[]{
                        R.drawable.selector_toolbar_index,
                        R.drawable.selector_toolbar_nearby,
                        R.drawable.selector_toolbar_myself};
                getContentView(MainBar.HOME_PAGE);
                break;
            case PackageType.EP_SHARE:
                TOOLBAR_TITLES = new String[]{"享停", "附近", "我的"};
                iconArr = new int[]{
                        R.drawable.selector_toolbar_share,
                        R.drawable.selector_toolbar_nearby,
                        R.drawable.selector_toolbar_myself};
                getContentView(MainBar.SHARE_PAGE);
                break;
            default:
                TOOLBAR_TITLES = new String[]{"首页", "附近", "享停", "我的"};
                iconArr = new int[]{
                        R.drawable.selector_toolbar_index,
                        R.drawable.selector_toolbar_nearby,
                        R.drawable.selector_toolbar_share,
                        R.drawable.selector_toolbar_myself};
                getContentView(MainBar.HOME_PAGE);
                break;
        }
        if (toolBarUtil == null) {
            toolBarUtil = new ToolBarUtil();
        }
        toolBarUtil.createToolBar(mLLToolBar, TOOLBAR_TITLES, iconArr);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        int barTag = intent.getIntExtra("showBar", MainBar.HOME_PAGE);
        if (barTag == MainBar.HOME_PAGE || barTag == MainBar.NEARBY_PAGE ||
                barTag == MainBar.MYSELF_PAGE || barTag == MainBar.SHARE_PAGE) {
            getContentView(barTag);
        }
    }

    /**
     * 切换fragment
     *
     * @param position 下标
     */
    private void getContentView(int position) {
        if (curViewId == position) {
            return;
        }
        curViewId = position;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case MainBar.HOME_PAGE:
                mainFragment = new MainFragment();
                ft.replace(R.id.fl_main_content, mainFragment);
                break;
            case MainBar.NEARBY_PAGE:
                nearbyFragment = new NearbyFragment();
                ft.replace(R.id.fl_main_content, nearbyFragment);
                break;
            case MainBar.SHARE_PAGE:
                shareCarFragment = new ShareCarFragment();
                ft.replace(R.id.fl_main_content, shareCarFragment);
                break;
            case MainBar.MYSELF_PAGE:
                myselfFragment = MyselfFragment.newInstance();
                ft.replace(R.id.fl_main_content, myselfFragment);
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void initView() {
        mLLToolBar = findViewById(R.id.ll_toolbar_main);
        //初始化地图
        SDKInitializer.initialize(this.getApplicationContext());
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showLongToast(this, "再按一次退出易派克");
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            netWorkReceiver.unregisterReceiver();
            App.getInstance().cancelSessionTimer();
            App.getInstance().closeAllActivity();
            System.exit(0);
        }
    }

    public static void actShowBar(Context context, int bar) {
        context.startActivity(new Intent(context, MainActivity.class)
                .putExtra("showBar", bar)
        );
    }

}
