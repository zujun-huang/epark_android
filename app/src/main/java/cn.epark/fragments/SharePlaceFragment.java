package cn.epark.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.epark.R;
import cn.epark.activitys.ShareApplyActivity;
import cn.epark.adapters.ShareApplyItemAdapter;
import cn.epark.bean.ShareParkInfo;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/9/25.
 * Describe: 发布泊位
 */
public class SharePlaceFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView shareApplyLv;
    private List<ShareParkInfo> parkInfoList;
    private ShareApplyItemAdapter shareApplyItemAdapter;
    private SharedPreferences preferences;//基类的 SharedPreferences

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_place, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        parkInfoList = new ArrayList<>();
        ShareParkInfo parkInfoError = new ShareParkInfo("00", "地面泊位", StringUtil.getCurrentTime(), StringUtil.getCurrentTime(),
                "仁和居1栋", null, StringUtil.getCurrentTime(), ShareParkInfo.AUDITED_FAILURE, null, null, 2.00);
        parkInfoError.setAuditTime(StringUtil.getCurrentTime());
        parkInfoError.setMsg("泊位材料信息有误，请核对后重试！");
        parkInfoList.add(parkInfoError);
        parkInfoList.add(new ShareParkInfo("02", "地面泊位", StringUtil.getCurrentTime(), StringUtil.getCurrentTime(),
                "仁和居1栋", null, StringUtil.getCurrentTime(), 2, null, null, 0.00));
        parkInfoList.add(new ShareParkInfo("-1", "地面泊位", StringUtil.getCurrentTime(), StringUtil.getCurrentTime(),
                "仁和居2栋", null, StringUtil.getCurrentTime(), ShareParkInfo.UNAUDITED, null, null, 0.00));
        parkInfoList.add(new ShareParkInfo("01", "地面泊位", StringUtil.getCurrentTime(), StringUtil.getCurrentTime(),
                "仁和居2栋", null, StringUtil.getCurrentTime(), 1, null, null, 0.00));
        shareApplyItemAdapter = new ShareApplyItemAdapter(parkInfoList);
        shareApplyLv.setAdapter(shareApplyItemAdapter);
        shareApplyItemAdapter.setOnItemCancelClickListener((v, position) -> {
            if (shareApplyItemAdapter.getItem(position).getState() == ShareParkInfo.UNAUDITED_LOCATION){
                preferences.edit().remove("add_apply_parkInfo").apply();
            }
            shareApplyItemAdapter.removeItem(position);
        }); //取消共享
    }

    private void initView(View view) {
        shareApplyLv = view.findViewById(R.id.share_apply_lv);
        shareApplyLv.setOnItemClickListener(this);
        view.findViewById(R.id.share_apply_add).setOnClickListener(clickListener);
    }

    /**
     * 防重复点击监听
     */
    OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.share_apply_add://添加发布共享车位申请
                    startActivity(new Intent(context, ShareApplyActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (ShareApplyActivity.isAddEdit){
            ShareApplyActivity.isAddEdit = false;
            String addApplyParkInfo = preferences.getString("add_apply_parkInfo", null);
            if (!TextUtils.isEmpty(addApplyParkInfo)){
                ShareParkInfo addParkInfo = JSON.parseObject(addApplyParkInfo, ShareParkInfo.class);
                addParkInfo.setState(ShareParkInfo.UNAUDITED_LOCATION);
                parkInfoList.add(addParkInfo);
                shareApplyItemAdapter.addItem(addParkInfo);
            }
        }
    }

    /**
     * 列表单个板块点击跳转修改共享车位申请
     *
     * @param parent   父视图
     * @param view     当前视图
     * @param position 当前下标
     * @param id       当前视图id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (shareApplyItemAdapter.getItem(position).getState() == ShareParkInfo.AUDITED_SUCCESS){
            ToastUtil.showToast(context, "审核通过的泊位不支持修改哦~");
            return;
        } else if (shareApplyItemAdapter.getItem(position).getState() == ShareParkInfo.SHARE_SUCCESS){
            ToastUtil.showToast(context, "已共享的泊位不支持修改~");
            return;
        }
        startActivity(new Intent(context, ShareApplyActivity.class)
                .putExtra("update", true)
                .putExtra("shareParkInfo", parkInfoList.get(position))
        );
    }

}
