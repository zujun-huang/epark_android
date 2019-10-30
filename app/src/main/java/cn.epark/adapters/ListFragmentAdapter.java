package cn.epark.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private String[] list;

    public ListFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<Fragment> fragments, String[] list) {
        this.fragments.clear();
        this.fragments.addAll(fragments);
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return  fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list != null ? list[position] : "";
    }
}
