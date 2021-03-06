package cn.epark.fragments;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

import cn.epark.R;
import cn.epark.adapters.NearbyItemFragmentAdapter;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/9/13.
 * Describe: 附近item页面
 */
public class NearbyItemFragment extends BaseFragment implements PoiSearch.OnPoiSearchListener {

    public static final int REFRESH_ADAPTER = 0x000001;
    private static final String CURRENT_TAB_POSITION = "current_key_word";

    private ListView listView;

    private String ctgr; //ctgr POI 类型的组合:餐馆|景点
    private int tabPosition = 0;
    private PoiSearch mPoiSearch;
    private Integer pageNum = 1, pageSize = 15;//第1页，每页15条
    private List<PoiItem> poiItems;
    private View view;

    public NearbyItemFragment() {
    }

    public static NearbyItemFragment newInstance(int tabPosition) {
        NearbyItemFragment fragment = new NearbyItemFragment();
        Bundle args = new Bundle();
        args.putInt(CURRENT_TAB_POSITION, tabPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabPosition = getArguments().getInt(CURRENT_TAB_POSITION);
            switch (tabPosition){
                case 0:
                    ctgr = "餐饮服务";
                    break;
                case 1:
                    ctgr = "住宿服务";
                    break;
                case 2:
                    ctgr = "风景名胜|医疗保健服务|节日庆典|公众活动|体育休闲服务";
                    break;
                case 3:
                    ctgr = "生活服务|购物服务";
                    break;
                default:
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_item, container, false);
        initData();
        showNearbyByKeyWord();
        initView();
        return view;
    }

    private void initData() {
        PoiSearch.Query query = new PoiSearch.Query(null, ctgr, NearbyFragment.chooseCity.getName());
        query.setPageNum(pageNum); query.setPageSize(pageSize);
        mPoiSearch = new PoiSearch(context, query);
        mPoiSearch.setOnPoiSearchListener(this);
    }

    //根据kwyWord显示数据
    private void showNearbyByKeyWord() {
        if (mPoiSearch != null){
            showLoadingDialog();
            mPoiSearch.searchPOIAsyn();
        }
    }

    private void initView() {
        listView = view.findViewById(R.id.nearby_item_lv);
        listView.setEmptyView(view.findViewById(R.id.no_data));
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case REFRESH_ADAPTER:
                listView.setAdapter(new NearbyItemFragmentAdapter(poiItems));
                dismissLoadingDialog();
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int errorCode) {
        dismissLoadingDialog();
        if (errorCode == 1000){
            poiItems = poiResult.getPois();
            handler.sendEmptyMessage(REFRESH_ADAPTER);
        } else if (errorCode == 1804) {
            showNetworkErrorToast();
        }
        else {
            ToastUtil.showToast(context, context.getResources().getString(R.string.nearby_no_found_msg));
        }
    }

    //poi id搜索的结果回调
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
