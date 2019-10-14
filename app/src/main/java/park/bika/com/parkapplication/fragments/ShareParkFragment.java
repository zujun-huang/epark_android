package park.bika.com.parkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.OnMultiClickListener;


/**
 * Created by huangzujun on 2019/9/25.
 * Describe: 预约停车
 */
public class ShareParkFragment extends BaseFragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_park, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }

    /**
     * 防重复点击监听
     */
    OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.share_park_location:
                    break;
                default:
            }
        }
    };

}
