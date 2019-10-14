package park.bika.com.parkapplication.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.bean.ModalBean;
import park.bika.com.parkapplication.bean.ShareParkInfo;
import park.bika.com.parkapplication.utils.StatusBarUtil;
import park.bika.com.parkapplication.utils.StringUtil;
import park.bika.com.parkapplication.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/10/9.
 * Describe: 共享车位发布/修改
 */
public class ShareApplyActivity extends BaseAct implements View.OnClickListener {

    private TextView title_tv, share_apply_address_tv, share_apply_floor_tv,
            share_apply_data_tv, share_apply_time_tv, share_apply_no_time_tv;
    private EditText share_apply_number_et, et_mobile_phone, et_price;

    /**
     * 是否为车位发布修改
     */
    private boolean isUpdate = false;
    /**
     * 车位修改的信息
     */
    private ShareParkInfo shareParkInfo;

    /**
     * 模态框列表数据
     */
    private List<ModalBean> modalBeanList = new ArrayList<>();
    private String chooseFloor = "地面泊位";//选择的楼层
    private Double hintPrice = 2d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_apply);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.icon_back).setOnClickListener(this);
        findViewById(R.id.share_apply_address).setOnClickListener(this);
        findViewById(R.id.share_apply_floor).setOnClickListener(this);
        findViewById(R.id.share_apply_data).setOnClickListener(this);
        findViewById(R.id.share_apply_time).setOnClickListener(this);
        findViewById(R.id.share_apply_no_time).setOnClickListener(this);
        findViewById(R.id.share_apply_btn).setOnClickListener(this);
        title_tv = findViewById(R.id.title_tv);
        share_apply_address_tv = findViewById(R.id.share_apply_address_tv);
        share_apply_floor_tv = findViewById(R.id.share_apply_floor_tv);
        share_apply_data_tv = findViewById(R.id.share_apply_data_tv);
        share_apply_number_et = findViewById(R.id.share_apply_number_et);
        share_apply_time_tv = findViewById(R.id.share_apply_time_tv);
        share_apply_no_time_tv = findViewById(R.id.share_apply_no_time_tv);
        et_mobile_phone = findViewById(R.id.et_mobile_phone);
        et_price = findViewById(R.id.et_price);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        if (getIntent() != null) {
            isUpdate = getIntent().getBooleanExtra("update", false);
            shareParkInfo = getIntent().getParcelableExtra("shareParkInfo");
        }
        if (isUpdate) {
            title_tv.setText("泊位发布修改");
            share_apply_address_tv.setText(shareParkInfo.getAddress());
            share_apply_floor_tv.setText(shareParkInfo.getFloor());
            share_apply_number_et.setText(shareParkInfo.getNumber());
            et_mobile_phone.setText(shareParkInfo.getPhone());
            if (!TextUtils.isEmpty(shareParkInfo.getParkInfoId())){
                share_apply_data_tv.setText("已上传");
            }
            share_apply_time_tv.setText(shareParkInfo.getFromTime() + " - " + shareParkInfo.getToTime());
            if (TextUtils.isEmpty(shareParkInfo.getNoTime())){
                share_apply_no_time_tv.setText("全共享");
            } else {
                share_apply_no_time_tv.setText(shareParkInfo.getNoTime());
            }
            if (shareParkInfo.getApplyPrice() < 0){
                et_price.setHint("当前区域参考价格 ￥" + StringUtil.formatAmount(hintPrice));
            } else {
                et_price.setText(StringUtil.formatAmount(shareParkInfo.getApplyPrice()));
            }
        } else {
            title_tv.setText("泊位发布申请");
        }
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_apply_btn://确认按钮
                saveShareApply();
                break;
            case R.id.share_apply_no_time://限享时段

                break;
            case R.id.share_apply_time://共享时段

                break;
            case R.id.share_apply_data://材料上传

                break;
            case R.id.share_apply_floor://楼层选择
                modalBeanList.clear();
                modalBeanList.add(new ModalBean("地面泊位", R.color.colorAccent));
                modalBeanList.add(new ModalBean("地下一层"));
                modalBeanList.add(new ModalBean("地下二层"));
                modalBeanList.add(new ModalBean("地下三层"));
                modalBeanList.add(new ModalBean("地下四层"));
                modalBeanList.add(new ModalBean("地下五层"));
                modalBeanList.add(new ModalBean("其他泊位"));
                showModal(modalBeanList).setOnItemClickListener((parent, view, position, id) -> {
                    dismissModal();
                    share_apply_floor_tv.setText(modalBeanList.get(position).getContentTxt());
                    chooseFloor = modalBeanList.get(position).getContentTxt();
                });
                break;
            case R.id.share_apply_address://选择地址

                break;
            case R.id.icon_back:
                finish();
                break;
            default:
        }
    }

    /**
     * 存储用户输入的共享泊位
     * <p>默认待审核状态</p>
     */
    private void saveShareApply() {
        String applyAddress = share_apply_address_tv.getText().toString();
        String applyParkNum = share_apply_number_et.getText().toString();
        String applyPhone = et_mobile_phone.getText().toString();
        String applyParkData = share_apply_data_tv.getText().toString();
        String applyShareTime = share_apply_time_tv.getText().toString();
        Double applyPrice = Double.valueOf(et_price.getText().toString());
        if (TextUtils.isEmpty(applyAddress)){
            ToastUtil.showToast(context, "请选择泊位地址后重试~");
            return;
        }else if (TextUtils.isEmpty(applyParkNum)){
            ToastUtil.showToast(context, "请输入需要共享的泊位号~");
            return;
        }else if (!StringUtil.isPhoneNumber(applyPhone)){
            ToastUtil.showToast(context, "联系电话格式有误，请输入正确的手机号码~");
            return;
        }else if (TextUtils.isEmpty(applyParkData)){
            ToastUtil.showToast(context, "请先上传泊位相关证明后重试~");
            return;
        }else if (TextUtils.isEmpty(applyShareTime)){
            ToastUtil.showToast(context, "请选择共享泊位时段后重试~");
            return;
        } else if (applyPrice < 0){
            applyPrice = hintPrice;
        }

        //价格超出区域价格太多提示
        if (applyPrice >= hintPrice * 2){
            showAlertDialog("建议您的共享价格在区域参考价格 ￥" + hintPrice + "2倍以下~", "仍然提交", v -> {
                ToastUtil.showLongToast(context, "泊位申请成功~");
                finish();
            }, v -> dismiss());
        } else {
            ToastUtil.showLongToast(context, "泊位申请成功~");
            finish();
        }

        //模拟数据
//        ShareParkInfo shareParkInfo = new ShareParkInfo(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
//                String.valueOf((Math.random() + 1) * 10), applyPhone, chooseFloor, );


    }
}
