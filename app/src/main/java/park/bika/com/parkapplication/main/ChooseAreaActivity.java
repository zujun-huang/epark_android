package park.bika.com.parkapplication.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.widget.Toast;

import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.Constant;
import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.StatusBarUtil;

public class ChooseAreaActivity extends BaseAct {


    private String chooseArea, chooseCity, curCityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        initData();
    }

    private void initData() {
        if (getIntent() != null){
            chooseArea = getIntent().getStringExtra("chooseArea");
            chooseCity = getIntent().getStringExtra("chooseCity");
            curCityCode = getIntent().getStringExtra("curCityCode");
        }
    }

    @Override
    public void onBackPressed() {
        backData(chooseCity);
        super.onBackPressed();
    }

    private void backData(String data){
        Intent intent = new Intent();
        intent.putExtra(Constant.CHOOSE_AREA_KEY, data);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }
}
