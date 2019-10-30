package cn.epark.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.epark.R;
import cn.epark.bean.ModalBean;

/**
 * created huangzujun on 2019/9/7
 * Describe: 模态框适配器
 */
public class ModalAdapter extends BaseAdapter {

    private List<ModalBean> list;
    private Context context;

    public ModalAdapter(Context context, List<ModalBean> list) {
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_modal_content_item, null);
            holder.item_tv = convertView.findViewById(R.id.item_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_tv.setText(list.get(position).getContentTxt());
        holder.item_tv.setTextColor(ContextCompat.getColor(context, list.get(position).getColor()));
        return convertView;
    }

    private class ViewHolder {
        private TextView item_tv;
    }
}
