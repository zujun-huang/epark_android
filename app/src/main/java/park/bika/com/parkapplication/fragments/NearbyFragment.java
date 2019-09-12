package park.bika.com.parkapplication.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.main.MainActivity;
import park.bika.com.parkapplication.utils.CalcUtil;
import park.bika.com.parkapplication.utils.InputMethodUtils;
import park.bika.com.parkapplication.utils.ToastUtil;
import park.bika.com.parkapplication.view.SearchView;

/**
 * @作者 hzj
 * @日期 2019/7/16
 * @描述 附近 Fragment
 */
public class NearbyFragment extends BaseFragment implements View.OnClickListener, OnGetSuggestionResultListener {

    private MainActivity mainAct;
    private TextView tv_near_area;
    private AutoCompleteTextView keyWorldsView;
    private SearchView search_content;

    private ArrayAdapter<String> sugAdapter;
    private SuggestionSearch mSuggestionSearch;
    private List<SuggestionResult.SuggestionInfo> suggestionInfos;
    private List<HotCity> hotCities = new ArrayList<>();
    private City chooseCity;
    private LocatedCity locatedCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        init(view);
        initData();
        initListener();
        return view;
    }

    private void initData() {
        //地址选择
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("重庆", "重庆", "101040100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        chooseCity = new City(mainAct != null ? mainAct.currentDistrict : "重庆", null, null, null);
        List<City> cities = new DBManager(context).searchCity(chooseCity.getName());
        locatedCity = new LocatedCity(chooseCity.getName(), "重庆", "101040100");
        if (cities != null && cities.size() > 0) {
            locatedCity = new LocatedCity(cities.get(0).getName(),
                    cities.get(0).getProvince(),
                    cities.get(0).getCode());
        }

        //搜索候选词
        mSuggestionSearch = SuggestionSearch.newInstance();
        keyWorldsView = search_content.getAet_search();
        sugAdapter = new ArrayAdapter<>(mainAct, android.R.layout.simple_list_item_1);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        keyWorldsView.setDropDownVerticalOffset(CalcUtil.dp2px(context, 3));
        keyWorldsView.setDropDownAnchor(R.id.search_content);

        //
    }

    private void initListener() {
        tv_near_area.setOnClickListener(this);
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        //文本输入提示
        search_content.setSetTxtOnChangedListener(searchTxt ->
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().city(chooseCity.getName()).keyword(searchTxt))
        );

        search_content.setSearchIconClickListener(searchTxt -> {
            InputMethodUtils.hide(context);
            ToastUtil.showToast(getContext(), "搜索：" + searchTxt);
        });

    }

    private void init(View view) {
        tv_near_area = view.findViewById(R.id.tv_near_area);
        search_content = view.findViewById(R.id.search_content);

        if (mainAct != null) {
            tv_near_area.setText(mainAct.currentDistrict);
        }
    }

    private LatLng getLatLngByCityName(String cityName) {

        return null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_near_area://选择地区
                CityPicker.from(this).enableAnimation(true).setAnimationStyle(R.style.animAct)
                        .setLocatedCity(locatedCity).setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                chooseCity = data;
                                if (chooseCity != null) {
                                    tv_near_area.setText(chooseCity.getName());
                                }
                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onLocate() {

                            }
                        })
                        .show();
                break;
        }
    }

    //获取搜索结果数据
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggestionInfos = res.getAllSuggestions();
        List<String> suggests = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggests.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<>(mainAct, android.R.layout.simple_list_item_1, suggests);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSuggestionSearch != null){
            mSuggestionSearch.destroy();
        }
    }
}
