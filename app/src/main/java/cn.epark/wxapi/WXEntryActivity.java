package cn.epark.wxapi;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import cn.epark.activitys.BaseAct;
import cn.epark.utils.ToastUtil;

/**
 * created huangzujun on 2020/2/9
 * Describe: 微信回调
 */
public class WXEntryActivity extends BaseAct implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp != null) {
            wxResp = resp;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK://用户确认授权
                    if (wxHandler != null) {
                        ToastUtil.showToast(context, "页面刷新中，请稍候..");
                        handler.obtainMessage(WX_ONRESP, true).sendToTarget();
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消授权
                    if (wxHandler != null) {
                        handler.obtainMessage(WX_ONRESP, false).sendToTarget();
                    }
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户授权被拒
                    ToastUtil.showToast(context, "授权被拒，" + resp.errStr);
                    break;
                default: break;
            }
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }
}
