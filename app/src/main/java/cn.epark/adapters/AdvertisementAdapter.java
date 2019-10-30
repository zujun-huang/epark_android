package cn.epark.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.epark.R;
import cn.epark.bean.Advertisement;

/**
 * created huangzujun on 2019/9/9
 * Company:重庆帮考教育科技有限公司
 * Describe: 广告适配器
 */
public class AdvertisementAdapter extends BaseAdapter{

    private Context context;
    private List<Advertisement> list;

    public AdvertisementAdapter(Context context,List<Advertisement> list) {
        this.context = context;
        this.list = list;
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
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_advertisement_item, null);
            holder.title = convertView.findViewById(R.id.other_plate_icon);
            holder.content = convertView.findViewById(R.id.other_plate_txt);
            holder.content_title = convertView.findViewById(R.id.tv_other_plate_title);
            holder.content_tip = convertView.findViewById(R.id.tv_other_plate_tip);
            holder.img = convertView.findViewById(R.id.other_plate_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setCompoundDrawablesWithIntrinsicBounds(list.get(position).getIcon(), 0, 0, 0);
        holder.title.setText(list.get(position).getTitle());
        if (!TextUtils.isEmpty(list.get(position).getContent())){
            holder.content.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            String[] strs = list.get(position).getContent().split(",");
            holder.content_title.setText(strs[0]);
            if (strs.length > 1){
                holder.content_tip.setText(strs[1]);
            }
        } else {
            holder.content.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setImageResource(list.get(position).getImg());
//            Glide.with(context).load(list.get(position).getImg())
//                    .into(holder.img);
        }
        return convertView;
    }

    private class ViewHolder{
        private TextView title, content_title, content_tip;
        private RelativeLayout content;
        private ImageView img;
    }
}
