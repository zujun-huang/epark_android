package cn.epark.Listeners;

import android.content.Context;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import cn.epark.utils.LogUtil;
import cn.epark.utils.ToastUtil;

/**
 * created huangzujun on 2020/2/14
 * Describe: QQ回调基类
 */
public abstract class BaseQQUiListener implements IUiListener {

    private Context mContext;

    public BaseQQUiListener(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 授权成功
     * @param o o
     */
    @Override
    public abstract void onComplete(Object o);

    @Override
    public void onError(UiError uiError) {
        LogUtil.e("okHttpUtil", "发生了错误，错误码："+ uiError.errorCode +
                "，错误信息：" + uiError.errorMessage + "，错误详细：" + uiError.errorDetail);
    }

    @Override
    public void onCancel() {
        ToastUtil.showToastInUIThread(mContext, "授权取消");
    }
}
