package park.bika.com.parkapplication.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.CalcUtil;
import park.bika.com.parkapplication.utils.ShareUtil;
import park.bika.com.parkapplication.utils.StatusBarUtil;
import park.bika.com.parkapplication.utils.ThreadUtil;
import park.bika.com.parkapplication.view.StatusBarHeightView;

public class SplashActivity extends BaseAct {

    private static final int DELAYED_MAIN = 0x000700;
    private Context context;
    private ViewPager mVPCarousel;
    private LinearLayout mLLCarouselPointGroup;
    private Button mBtnIn;
    private List<ImageView> carouselImgList = new ArrayList<>();//图片数据
    private int prePosition = 0; //记录上次圆点位置
    public boolean firstRun;
    private StatusBarHeightView status_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initImages();
        initListener();
    }

    private void initImages() {
        firstRun = ShareUtil.newInstance().getShared(getApplicationContext()).getBoolean("first_run" ,true);
        if (firstRun){
            carouselImgList.clear();
            mLLCarouselPointGroup.removeAllViews();
            for (int i = 0; i < 3; i++) {
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                switch (i) {
                    case 0:
                        imageView.setBackgroundResource(R.mipmap.splash_1);
                        break;
                    case 1:
                        imageView.setImageResource(R.mipmap.splash_2);
                        break;
                    case 2:
                        imageView.setBackgroundResource(R.mipmap.splash_3);
                        break;
                }
                carouselImgList.add(imageView);
                //添加轮播点
                ImageView pointIV = new ImageView(this);
                pointIV.setBackgroundResource(R.drawable.selector_point);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CalcUtil.dp2px(this, 10), CalcUtil.dp2px(this, 10));
                if (i == 0) {
                    pointIV.setEnabled(true);
                } else {
                    pointIV.setEnabled(false);
                    params.leftMargin = 15;
                }
                pointIV.setLayoutParams(params);
                mLLCarouselPointGroup.setVisibility(View.VISIBLE);
                mLLCarouselPointGroup.addView(pointIV);
            }
            if (mVPCarousel.getAdapter() != null){
                mVPCarousel.getAdapter().notifyDataSetChanged();
            }
            setStatusBarBlack(true);
            ShareUtil.newInstance().getShared(getApplicationContext()).edit()
                    .putBoolean("first_run", false).apply();
        } else {
            status_bar.setVisibility(View.GONE);
            mBtnIn.setVisibility(View.GONE);
            mLLCarouselPointGroup.setVisibility(View.GONE);
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(R.mipmap.app_index);
            carouselImgList.add(imageView);
            if (mVPCarousel.getAdapter() != null){
                mVPCarousel.getAdapter().notifyDataSetChanged();
            }
            //延时1s跳转
            ThreadUtil.runInThread(()->{
                try {
                    Thread.sleep(1000);
                    handler.obtainMessage(DELAYED_MAIN).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case DELAYED_MAIN:
                startActivity(new Intent(context, MainActivity.class));
                finish();
                break;
            default:
                super.handleMessage(msg);
        }
    }

    private void initListener() {
        mVPCarousel.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //设置轮播点状态及文本
                mLLCarouselPointGroup.getChildAt(prePosition).setEnabled(false);
                mLLCarouselPointGroup.getChildAt(position).setEnabled(true);
                prePosition = position;
                //按钮
                mBtnIn.setVisibility(View.GONE);
                if (position == carouselImgList.size() - 1) {
                    mBtnIn.setVisibility(View.VISIBLE);
                }
                //状态栏主题切换
                switch (position) {
                    case 0:
                    case 2:
                        setStatusBarBlack(true);
                        break;
                    default:
                        setStatusBarBlack(false);
                        break;
                }
            }
        });
        mBtnIn.setOnClickListener( view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initView() {
        context = this;
        mVPCarousel = findViewById(R.id.vp_carousel);
        mLLCarouselPointGroup = findViewById(R.id.ll_carousel_point_group);
        mBtnIn = findViewById(R.id.btn_carousel_in);
        mVPCarousel.setAdapter(new CarouselPagerAdapter());
        status_bar = findViewById(R.id.status_bar);
    }

    private void setStatusBarBlack(boolean isBlack){
        super.setStatusBar();
        status_bar.setBackgroundResource(R.color.colorParkBluePressed);
        if (isBlack){
            status_bar.setBackgroundResource(R.color.colorWhite);
            if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
                StatusBarUtil.setStatusBarColor(this, 0x55000000);
            }
        }
    }

    class CarouselPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return carouselImgList.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = carouselImgList.get(position);
            if (container.getParent() != null) {
                container.removeView(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
