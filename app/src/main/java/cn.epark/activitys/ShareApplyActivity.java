package cn.epark.activitys;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.epark.R;
import cn.epark.bean.ModalBean;
import cn.epark.bean.ShareParkInfo;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/10/9.
 * Describe: 共享车位发布/修改
 */
public class ShareApplyActivity extends BaseAct implements View.OnClickListener {

    private TextView title_tv, share_apply_address_tv, share_apply_floor_tv, share_apply_error_msg,
            share_apply_data_tv, share_apply_time_tv, share_apply_no_time_tv;
    private EditText share_apply_number_et, et_mobile_phone, et_price;

    /**
     * 是否为车位发布修改
     */
    private boolean isUpdate = false;
    /**
     * 车位修改的信息 / 用户填写的车位信息
     */
    private ShareParkInfo shareParkInfo;

    /**
     * 车位信息是否有编辑/修改
     */
    public static boolean isAddEdit = false;

    private List<ModalBean> modalBeanList = new ArrayList<>();//模态框列表数据
    private String chooseFloor = "地面泊位";//选择的楼层
    private Double hintPrice = 2D;
    private SharedPreferences preferences;//基类的 SharedPreferences


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
        share_apply_error_msg = findViewById(R.id.share_apply_error_msg);
        et_mobile_phone = findViewById(R.id.et_mobile_phone);
        et_price = findViewById(R.id.et_price);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (getIntent() != null) {
            isUpdate = getIntent().getBooleanExtra("update", false);
            shareParkInfo = getIntent().getParcelableExtra("shareParkInfo");
        }
        if (isUpdate) {
            title_tv.setText("泊位发布修改");
            if (!TextUtils.isEmpty(shareParkInfo.getAddress())){
                share_apply_address_tv.setText(shareParkInfo.getAddress());
            }
            if (!TextUtils.isEmpty(shareParkInfo.getFloor())){
                share_apply_floor_tv.setText(shareParkInfo.getFloor());
            }
            if (!TextUtils.isEmpty(shareParkInfo.getNumber())){
                share_apply_number_et.setText(shareParkInfo.getNumber());
            }
            if (!TextUtils.isEmpty(shareParkInfo.getPhone())){
                et_mobile_phone.setText(shareParkInfo.getPhone());
            }
            if (!TextUtils.isEmpty(shareParkInfo.getParkInfoId())){
                share_apply_data_tv.setText("已上传");
            }
            if (!TextUtils.isEmpty(shareParkInfo.getFromTime()) && !TextUtils.isEmpty(shareParkInfo.getToTime())){
                share_apply_time_tv.setText(shareParkInfo.getFromTime() + " - " + shareParkInfo.getToTime());
            }
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
            if (!TextUtils.isEmpty(shareParkInfo.getAuditTime())) {
                share_apply_error_msg.setVisibility(View.VISIBLE);
                share_apply_error_msg.setText(StringUtil.getFormatTime(shareParkInfo.getAuditTime())
                        + " \n失败原因：" + shareParkInfo.getMsg());
            } else {
                share_apply_error_msg.setVisibility(View.GONE);
            }
        } else {
            title_tv.setText("泊位发布申请");
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
                modalBeanList.add(new ModalBean("地面泊位", R.color.theme_color));
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
            case R.id.icon_back://用户返回时存储填写历史
                shareParkInfo = getUserApplyParkInfo();
                if (!isUpdate){
                    if (!TextUtils.isEmpty(shareParkInfo.getAddress()) || !TextUtils.isEmpty(shareParkInfo.getParkInfoId()) || !TextUtils.isEmpty(shareParkInfo.getNumber()) ||
                            !TextUtils.isEmpty(shareParkInfo.getPhone()) || !TextUtils.isEmpty(shareParkInfo.getFromTime()) || shareParkInfo.getPrice() > 0){
                        isAddEdit = true;
                        shareParkInfo.setState(ShareParkInfo.UNAUDITED_LOCATION);
                        shareParkInfo.setFloor(chooseFloor);
                        preferences.edit().putString("add_apply_parkInfo", JSON.toJSONString(shareParkInfo)).apply();
                    }
                }
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
        shareParkInfo = getUserApplyParkInfo();
        if (TextUtils.isEmpty(shareParkInfo.getAddress())){
            ToastUtil.showToast(context, "请选择泊位地址后重试~");
            return;
        }else if (TextUtils.isEmpty(shareParkInfo.getNumber())){
            ToastUtil.showToast(context, "请输入需要共享的泊位号~");
            return;
        }else if (!StringUtil.isPhoneNumber(shareParkInfo.getPhone())){
            ToastUtil.showToast(context, "联系电话格式有误，请输入正确的手机号码~");
            return;
        }else if (TextUtils.isEmpty(share_apply_data_tv.getText().toString())){
            ToastUtil.showToast(context, "请先上传泊位相关材料后重试~");
            return;
        }else if (TextUtils.isEmpty(shareParkInfo.getFromTime()) || TextUtils.isEmpty(shareParkInfo.getToTime())){
            ToastUtil.showToast(context, "请选择共享泊位时段后重试~");
            return;
        } else if (shareParkInfo.getApplyPrice() <= 0){
            shareParkInfo.setApplyPrice(hintPrice);
        }

        //价格超出区域价格太多提示
        if (shareParkInfo.getApplyPrice() >= hintPrice * 2){
            showAlertDialog("建议您的共享价格在区域参考价格 ￥" + hintPrice + "2倍以下~", "仍然提交", v -> {
                isAddEdit = true;
                ToastUtil.showLongToast(context, "泊位申请成功~");
                finish();
            }, v -> dismiss());
        } else {
            isAddEdit = true;
            ToastUtil.showLongToast(context, "泊位申请成功~");
            finish();
        }

    }

    private ShareParkInfo getUserApplyParkInfo() {
        ShareParkInfo parkInfo = new ShareParkInfo();
        parkInfo.setAddress(share_apply_address_tv.getText().toString());
        parkInfo.setNumber(share_apply_number_et.getText().toString());
        parkInfo.setPhone(et_mobile_phone.getText().toString());
        String[] applyTimes = share_apply_time_tv.getText().toString().split("-");
        // FIXME: 2019/10/17 时间拆分 null
        if (applyTimes.length == 2){
            parkInfo.setFromTime(applyTimes[0]);
            parkInfo.setToTime(applyTimes[1]);
        }
        parkInfo.setApplyPrice(!TextUtils.isEmpty(et_price.getText().toString()) ? Double.valueOf(et_price.getText().toString()) : 0D);
        return parkInfo;
    }
}
