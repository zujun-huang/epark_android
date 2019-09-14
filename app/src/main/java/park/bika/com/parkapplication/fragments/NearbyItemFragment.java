package park.bika.com.parkapplication.fragments;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.adapters.NearbyItemFragmentAdapter;
import park.bika.com.parkapplication.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/9/13.
 * Describe: 附近item页面
 */
public class NearbyItemFragment extends BaseFragment {

    public static final int REFRESH_ADAPTER = 0x000001;
    private static final String CURRENT_KEY_WORD = "current_key_word";
    private static final String CHOOSE_CITY = "choose_city";

    private ListView listView;

    private String currentKey, city;
    private PoiSearch mPoiSearch;
    private List<PoiDetailResult> poiDetailResults;
    private View view;
    private boolean isUIVisiable = false,//页面是否可见
            isInitView = false;//初始化view是否完成

    public NearbyItemFragment() {
    }

    public static NearbyItemFragment newInstance(String currentKey, String chooseCity) {
        NearbyItemFragment fragment = new NearbyItemFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_KEY_WORD, currentKey);
        args.putString(CHOOSE_CITY, chooseCity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentKey = getArguments().getString(CURRENT_KEY_WORD);
            if ("玩乐".equals(currentKey)){
                currentKey = "游";
            }
            city = getArguments().getString(CHOOSE_CITY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_nearby_item, container, false);
            isInitView = true;
            setParams();
        }
        initView();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isUIVisiable = isVisibleToUser;
        setParams();
    }

    private void setParams() {
        if (isInitView && isUIVisiable) {
            initData();
            showNearbyByKeyWord(currentKey);
        }
    }

    private OnGetPoiSearchResultListener searchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                dismissProgressDialog();
                ToastUtil.showToastInUIThread(context, context.getResources().getString(R.string.nearby_no_found_msg));
            } else if (poiResult.error == SearchResult.ERRORNO.NO_ERROR && mPoiSearch != null) {
                poiDetailResults = new ArrayList<>();
                for(PoiInfo poiInfo: poiResult.getAllPoi()){
                    mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poiInfo.getUid()));
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            poiDetailResults.add(poiDetailResult);
            if (poiDetailResults.size() == 15){
                handler.sendEmptyMessage(REFRESH_ADAPTER);
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void initData() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(searchResultListener);
    }

    //根据kwyWord显示数据
    private void showNearbyByKeyWord(String kewWord) {
        if (mPoiSearch != null){
            if (TextUtils.isEmpty(kewWord)) kewWord = "美食";
            showProgressDialog(null, "正在加载...");
            mPoiSearch.searchInCity(new PoiCitySearchOption()
                    .city(city)
                    .keyword(kewWord)
                    .pageCapacity(15)//每页数据
            );
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
                listView.setAdapter(new NearbyItemFragmentAdapter(poiDetailResults));
                dismissProgressDialog();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        dismissProgressDialog();
    }
}
