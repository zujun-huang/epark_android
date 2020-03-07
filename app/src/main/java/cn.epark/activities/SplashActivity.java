package cn.epark.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.epark.App;
import cn.epark.R;
import cn.epark.utils.ShareUtil;

/**
 * Created by huangzujun on 2019/10/9.
 * Describe: 启动页
 */
public class SplashActivity extends BaseAct {

    private Context context;
    private ViewPager mVPCarousel;
    private Button mBtnIn;
    private List<ImageView> carouselImgList;//图片数据
    @DrawableRes
    private int[] splashResIds = {R.mipmap.splash_1, R.mipmap.splash_2, R.mipmap.splash_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initImages();
        initListener();
    }

    private void initImages() {
        App.isFirstRun = ShareUtil.newInstance().isFirstRun(context);
        carouselImgList = new ArrayList<>();
        if (App.isFirstRun) {
            for (int resId : splashResIds) {
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(resId);
                carouselImgList.add(imageView);
            }
            if (mVPCarousel.getAdapter() != null) {
                mVPCarousel.getAdapter().notifyDataSetChanged();
            }
            ShareUtil.newInstance().setNotFirstRun(context);
        } else {
            mBtnIn.setVisibility(View.GONE);
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.mipmap.app_index);
            carouselImgList.add(imageView);
            if (mVPCarousel.getAdapter() != null) {
                mVPCarousel.getAdapter().notifyDataSetChanged();
            }
            //延时1.5s跳转
            handler.postDelayed(this::autoLogin, 1500);
        }
    }

    private void autoLogin() {
        if (!ShareUtil.newInstance().isEmptyLoginUser(context)) {
            App.getInstance().refreshSessionId();
        }
        clickToMain();
    }

    private void clickToMain() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void initListener() {
        mVPCarousel.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //按钮
                mBtnIn.setVisibility(View.GONE);
                if (position == carouselImgList.size() - 1) {
                    mBtnIn.setVisibility(View.VISIBLE);
                }
            }
        });
        mBtnIn.setOnClickListener(v -> clickToMain());
    }

    private void initView() {
        context = this;
        mVPCarousel = findViewById(R.id.vp_carousel);
        mBtnIn = findViewById(R.id.btn_carousel_in);
        mVPCarousel.setAdapter(new CarouselPagerAdapter());
    }

    class CarouselPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return carouselImgList != null ? carouselImgList.size() : 0;
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
