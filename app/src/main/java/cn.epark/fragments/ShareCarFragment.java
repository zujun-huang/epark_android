package cn.epark.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.epark.R;
import cn.epark.adapters.ListFragmentAdapter;

/**
 * Created by huangzujun on 2019/9/24.
 * Describe: 共享车位 Fragment
 */
public class ShareCarFragment extends BaseFragment {

    private TabLayout tab;
    private ViewPager vp;

    private final String[] shareTitles = {"预约停车","发布泊位"};
    private List<Fragment> fragments = new ArrayList<>();
    private ListFragmentAdapter fragmentAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_car, container, false);
        init(view);
        initData();
        return view;
    }

    private void init(View view) {
        tab = view.findViewById(R.id.share_park_tab);
        vp = view.findViewById(R.id.share_park_vp);
        fragmentAdapter = new ListFragmentAdapter(getChildFragmentManager());
    }

    private void initData() {
        if (fragments.size() > 0 ) {
            fragments.clear();
        }
        fragments.add(new ShareParkFragment());
        fragments.add(new SharePlaceFragment());
        for (String shareTitle : shareTitles){
            tab.addTab(tab.newTab().setText(shareTitle));
        }
        fragmentAdapter.setData(fragments, shareTitles);
        vp.setAdapter(fragmentAdapter);
        tab.setupWithViewPager(vp);
    }

}
