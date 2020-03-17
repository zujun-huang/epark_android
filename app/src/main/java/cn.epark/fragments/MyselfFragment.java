package cn.epark.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.epark.App;
import cn.epark.R;
import cn.epark.activities.NoticeActivity;
import cn.epark.activities.SMSLoginActivity;
import cn.epark.activities.SetUserInfoActivity;
import cn.epark.activities.SettingsActivity;
import cn.epark.view.CircleImageView;

/**
 * @作者 hzj
 * @日期 2019/7/16
 * @描述 我的 Fragment
 */
public class MyselfFragment extends BaseFragment implements View.OnClickListener {

    public static final int REQUEST_LOGIN = 0x000791;

    private CircleImageView head_img;//头像
    private TextView userNameTv;

    public static MyselfFragment newInstance() {
        MyselfFragment fragment = new MyselfFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        head_img = view.findViewById(R.id.head_img);
        head_img.setOnClickListener(this);
        view.findViewById(R.id.notify_my).setOnClickListener(this);
        userNameTv = view.findViewById(R.id.nick_name);
        userNameTv.setOnClickListener(this);
        view.findViewById(R.id.rl_qd).setOnClickListener(this);
        view.findViewById(R.id.tv_wallet).setOnClickListener(this);
        view.findViewById(R.id.rl_zd).setOnClickListener(this);
        view.findViewById(R.id.rl_car).setOnClickListener(this);
        view.findViewById(R.id.tv_coupon).setOnClickListener(this);
        view.findViewById(R.id.tv_collection).setOnClickListener(this);
        view.findViewById(R.id.tv_set).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.nick_name) {
//            if (isLogin(false)) {
//                startActivity(new Intent(context, SMSLoginActivity.class));
//            }
//        } else if (isLogin()) {
//            return;
//        }
        switch (v.getId()) {
            case R.id.notify_my://通知
                startActivity(new Intent(context, NoticeActivity.class));
                break;
            case R.id.head_img://头像
                startActivity(new Intent(context, SetUserInfoActivity.class));
                break;
            case R.id.rl_qd://每日签到
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.daily_attendance))
                );
                break;
            case R.id.tv_wallet://我的钱包
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_wallet))
                );
                break;
            case R.id.rl_zd://停车记录
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.parking_record))
                );
                break;
            case R.id.rl_car://车辆管理
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.vehicle_management))
                );
                break;
            case R.id.tv_coupon://优惠券
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_coupon))
                );
                break;
            case R.id.tv_collection://我的收藏
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_collection))
                );
                break;
            case R.id.tv_set://设置
                startActivity(new Intent(context, SettingsActivity.class));
                break;
            default:
        }
    }

    private void updateUserInfo() {
//        Glide.with(context)
//                .load(App.getAccount().getHead())
//                .error(ContextCompat.getDrawable(context, R.mipmap.default_icon))
//                .into(head_img);
        head_img.setImageUrl(App.getAccount().getHead());
        if (!App.getAccount().isEmptyNickName()) {
            userNameTv.setText(App.getAccount().getNickName());
        } else {
            userNameTv.setText(R.string.default_user_name);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }
}
