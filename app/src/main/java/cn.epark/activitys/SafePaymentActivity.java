package cn.epark.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.epark.R;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.StatusBarUtil;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2019/9/10.
 * Describe: 支付页面
 */
public class SafePaymentActivity extends BaseAct implements View.OnClickListener {

    public static final int ALIPAY_METHOD = 0;//支付宝
    public static final int WXPAY_METHOD = 1;//微信
    public static final int BALANCEPAY_METHOD = 2;//余额支付
    private int currentPayMethod = ALIPAY_METHOD; //当前支付方式

    private TextView curLearnCoinTv, payPriceBtn,
            orderPriceTV, orderNumTV;
    private CheckBox payBalanceCB, payWxCB, payAliCB, preChoosePayCB;
    private RelativeLayout balanceContainerRL;
    private LinearLayout surePayLL;
    private ImageView icon_back;

    private Double orderPrice = 0.00;
    private boolean isBalanceEnough = false;    //余额是否充足
    private boolean needSuccessReturn = false;//订单支付成功后是否需要返回上一界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addActivity(this);
        setContentView(R.layout.activity_safe_payment);
        needSuccessReturn = getIntent().getBooleanExtra("needSuccessReturn", false);
        orderPrice = getIntent().getDoubleExtra("orderPrice", 0.00);
        initView();
        initData();
    }

    private void initView() {
        curLearnCoinTv = findViewById(R.id.pay_safe_tv_learn_coin);
        payPriceBtn = findViewById(R.id.pay_price_tv);
        payBalanceCB = findViewById(R.id.pay_safe_cb_balance);
        payWxCB = findViewById(R.id.pay_safe_cb_wxpay);
        payAliCB = findViewById(R.id.pay_safe_cb_alipay);
        orderPriceTV = findViewById(R.id.pay_safe_tv_order_price);
        orderNumTV = findViewById(R.id.pay_safe_tv_order_number);
        balanceContainerRL = findViewById(R.id.pay_safe_cb_balance_container);
        surePayLL = findViewById(R.id.pay_safe_ll_sure_pay);
        icon_back = findViewById(R.id.icon_back);
        icon_back.setOnClickListener(this);
        currentPayMethod = ALIPAY_METHOD;
        payBalanceCB.setChecked(true);
        preChoosePayCB = payBalanceCB;
        balanceContainerRL.setOnClickListener(this);
        findViewById(R.id.pay_safe_cb_wxpay_container).setOnClickListener(this);
        findViewById(R.id.pay_safe_cb_alipay_container).setOnClickListener(this);
        surePayLL.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                //确认支付
                ShareUtil.newInstance().getShared(context).edit() //支付完成清除标识
                        .putString("parkingStartTime", "")
                        .apply();
                ToastUtil.showLongToast(context,"支付成功~");
                finish();
            }
        });
    }

    private void initData() {
        initBalance();
        orderNumTV.setText(String.format(getString(R.string.pay_safe_orderid),
                StringUtil.getCurrentTime("yyyyddMMHHmmss") + (int) (Math.random() * 9 + 1) * 100000)
        );
        orderPriceTV.setText(StringUtil.formatAmount(orderPrice));
    }

    /**
     * 订单支付结果提示
     *
     * @param isok
     */
    private void showPayResultDialog(boolean isok) {

    }

    /**
     * 初始化金额
     */
    private void initBalance() {
        curLearnCoinTv.setText("0.00");
        if (isBalanceEnough) {
            setIsClick(true);
        } else {
            setIsClick(false);
        }
    }

    /**
     * 设置是否支持点击并更新按钮显示效果
     *
     * @param click
     */
    private void setIsClick(boolean click) {
        if (click) {
            surePayLL.setSelected(true);
            surePayLL.setClickable(true);
        } else {
            surePayLL.setClickable(false);
            surePayLL.setSelected(false);
        }
    }

    /**
     * @param bar MainBar选项
     */
    private void goToHome(int bar) {
        Intent intent = new Intent(SafePaymentActivity.this, MainActivity.class);
        intent.putExtra("showtag", bar);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_safe_cb_balance_container://余额支付
                if (preChoosePayCB != payBalanceCB) {
                    preChoosePayCB.setChecked(false);
                    payBalanceCB.setChecked(true);
                    setIsClick(false);
                    preChoosePayCB = payBalanceCB;
                    currentPayMethod = BALANCEPAY_METHOD;
                }
                break;
            case R.id.pay_safe_cb_wxpay_container://微信支付
                if (preChoosePayCB != payWxCB) {
                    preChoosePayCB.setChecked(false);
                    payWxCB.setChecked(true);
                    setIsClick(true);
                    preChoosePayCB = payWxCB;
                    currentPayMethod = WXPAY_METHOD;
                    payPriceBtn.setText(String.format(getString(R.string.pay_sure_btn),
                            StringUtil.formatAmount(orderPrice)));
                }
                break;
            case R.id.pay_safe_cb_alipay_container://支付宝支付
                if (preChoosePayCB != payAliCB) {
                    preChoosePayCB.setChecked(false);
                    payAliCB.setChecked(true);
                    setIsClick(true);
                    preChoosePayCB = payAliCB;
                    currentPayMethod = ALIPAY_METHOD;
                    payPriceBtn.setText(String.format(getString(R.string.pay_sure_btn),
                            StringUtil.formatAmount(orderPrice)));
                }
                break;
            case R.id.icon_back:
                finish();
                break;
        }
    }

    @Override
    public void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }
}
