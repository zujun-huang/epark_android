package park.bika.com.parkapplication.main;

import android.content.Intent;
import android.os.Bundle;

import park.bika.com.parkapplication.Constant;
import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.StatusBarUtil;

public class ChooseAreaActivity extends BaseAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        StatusBarUtil.setStatusBarDarkTheme(this,true);
    }

    @Override
    public void onBackPressed() {
        backData("秀山");
        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void backData(String data){
        Intent intent = new Intent();
        intent.putExtra(Constant.CHOOSE_AREA_KEY, data);
        setResult(RESULT_OK, intent);
    }
}
