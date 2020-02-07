package cn.epark.adapters;

import android.view.View;
import android.widget.EditText;

/**
 * created huangzujun on 2020/2/7
 * Describe: EditText电话号码监听
 */
public abstract class BasePhoneLoginTextWatcher extends LoginTextWatcher {

    private EditText editText;

    public BasePhoneLoginTextWatcher(EditText editText, View clearBtn) {
        super(clearBtn);
        this.editText = editText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || s.length() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9)
                        && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        //防止多次设置值
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if (sb.charAt(start) == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }
            editText.setText(sb.toString());
            editText.setSelection(index);
        } else {
            String line = s.subSequence(s.length() - 1, s.length()).toString();
            if (line.equals(String.valueOf(' '))) {
                sb.deleteCharAt(s.subSequence(0, s.length() - 1).length());
                editText.setText(sb.toString());
                editText.setSelection(sb.length());
            }
        }
    }
}
