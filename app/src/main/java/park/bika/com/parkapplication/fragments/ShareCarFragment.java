package park.bika.com.parkapplication.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import park.bika.com.parkapplication.R;

/**
 * @作者 hzj
 * @日期 2019/7/16
 * @描述 社交 Fragment
 */
public class ShareCarFragment extends Fragment {


    public ShareCarFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_car, container, false);
    }

}
