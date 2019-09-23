package park.bika.com.parkapplication.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.baidu.mapapi.SDKInitializer;

import park.bika.com.parkapplication.MainBar;
import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.fragments.MainFragment;
import park.bika.com.parkapplication.fragments.MyselfFragment;
import park.bika.com.parkapplication.fragments.NearbyFragment;
import park.bika.com.parkapplication.fragments.ShareCarFragment;
import park.bika.com.parkapplication.utils.ToastUtil;
import park.bika.com.parkapplication.utils.ToolBarUtil;

public class MainActivity extends BaseAct {

    private LinearLayout mLLToolBar;
    //    private final String[] TOOLBAR_TITLES = new String[]{"首页", "附近", "共享", "我的"};
    private final String[] TOOLBAR_TITLES = new String[]{"首页", "附近", "我的"};
    private final int[] iconArr = {
            R.drawable.selector_toolbar_index,
            R.drawable.selector_toolbar_nearby,
//            R.drawable.selector_toolbar_social,
            R.drawable.selector_toolbar_myself};

    public String currentDistrict;

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
    }

    private void initListener() {
        toolBarUtil.setOnToolBarClickListener(position -> {
            //改变底部按钮状态
            toolBarUtil.changeChoosedState(position);
            //选择内容页面
            getContentView(position);
        });
    }

    private void initData() {
        getContentView(MainBar.HOME_PAGE);
        //底部按钮
        if (toolBarUtil == null) {
            toolBarUtil = new ToolBarUtil();
        }
        toolBarUtil.createToolBar(mLLToolBar, TOOLBAR_TITLES, iconArr);
        toolBarUtil.changeChoosedState(0);    //设置底部菜单默认选中
    }

    @Override
    protected void onNewIntent(Intent intent) {
        int barTag = intent.getIntExtra("show", MainBar.HOME_PAGE);
        if (barTag == MainBar.HOME_PAGE || barTag == MainBar.NEARBY_PAGE ||
                barTag == MainBar.MYSELF_PAGE || barTag == MainBar.SOCIAL_PAGE) {
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
            case MainBar.SOCIAL_PAGE:
//                socialFragment = new SocialFragment();
//                ft.replace(R.id.fl_main_content, socialFragment);
//                break;
            case MainBar.MYSELF_PAGE:
                myselfFragment = MyselfFragment.newInstance();
                ft.replace(R.id.fl_main_content, myselfFragment);
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
            closeAllActivity();
            System.exit(0);
        }
    }
}
