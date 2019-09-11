package park.bika.com.parkapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import park.bika.com.parkapplication.Constant;
import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.main.ChooseAreaActivity;
import park.bika.com.parkapplication.utils.ToastUtil;
import park.bika.com.parkapplication.view.SearchView;

import static android.app.Activity.RESULT_OK;

/**
 * @作者 hzj
 * @日期 2019/7/16
 * @描述 附近 Fragment
 */
public class NearbyFragment extends Fragment implements View.OnClickListener {

    private TextView tv_near_area;
    private SearchView search_content;
    private String chooseArea;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        init(view);
        initListener();
        return view;
    }

    private void initListener() {
        tv_near_area.setOnClickListener(this);
        search_content.setSearchIconClickListener(searchTxt ->
                ToastUtil.showToast(getContext(), "搜索：" + searchTxt));

    }

    private void init(View view) {
        tv_near_area = view.findViewById(R.id.tv_near_area);
        search_content = view.findViewById(R.id.search_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_near_area://选择地区
                Intent intent = new Intent(getContext(), ChooseAreaActivity.class);
                startActivityForResult(intent, Constant.CHOOSE_AREA_CODE);
//                getActivity().overridePendingTransition(R.anim.slide_in_right,
//                        R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //返回成功且为地区选择
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.CHOOSE_AREA_CODE:
                    String result = data.getStringExtra(Constant.CHOOSE_AREA_KEY);
                    if (!result.equals("null")) {
                        chooseArea = result;
                        ToastUtil.showToast(getContext(), chooseArea);
                    }
                    break;
            }
        }
    }

}
