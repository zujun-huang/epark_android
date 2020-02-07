package cn.epark.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * created huangzujun on 2020/2/7
 * Describe: EditText监听
 */
public class LoginTextWatcher implements TextWatcher {

    public View clearBtn;

    public LoginTextWatcher(View clearBtn) {
        this.clearBtn = clearBtn;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            clearBtn.setVisibility(View.VISIBLE);
        } else {
            clearBtn.setVisibility(View.GONE);
        }
    }
}
