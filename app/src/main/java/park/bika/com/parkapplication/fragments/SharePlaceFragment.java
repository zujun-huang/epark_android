package park.bika.com.parkapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.activitys.ShareApplyActivity;
import park.bika.com.parkapplication.adapters.ShareApplyItemAdapter;
import park.bika.com.parkapplication.bean.ShareParkInfo;
import park.bika.com.parkapplication.utils.LogUtil;
import park.bika.com.parkapplication.utils.OnMultiClickListener;
import park.bika.com.parkapplication.utils.StringUtil;

/**
 * Created by huangzujun on 2019/9/25.
 * Describe: 发布泊位
 */
public class SharePlaceFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView shareApplyLv;
    private List<ShareParkInfo> parkInfoList;
    private ShareApplyItemAdapter shareApplyItemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_place, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        parkInfoList = new ArrayList<>();
        parkInfoList.add(new ShareParkInfo("05", "地面泊位", StringUtil.getCurrentTime(), StringUtil.getCurrentTime(),
                "仁和居", null, StringUtil.getCurrentTime(), 1, null, null, 0.00));
        shareApplyItemAdapter = new ShareApplyItemAdapter(parkInfoList);
        shareApplyLv.setAdapter(shareApplyItemAdapter);
        shareApplyItemAdapter.setOnItemCancelClickListener((v, position) -> shareApplyItemAdapter.removeItem(position)); //取消共享
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
            }
        }
    };

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
        if (parkInfoList == null){
            LogUtil.e(TAG, "parkInfoList is null");
        } else if (parkInfoList.size() > 0 && parkInfoList.get(position).getState() != 1 &&
                parkInfoList.get(position).getState() != 2){
            startActivity(new Intent(context, ShareApplyActivity.class)
                    .putExtra("update", true)
                    .putExtra("shareParkInfo", parkInfoList.get(position))
            );
        }
    }

}
