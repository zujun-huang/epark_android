package park.bika.com.parkapplication.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.poi.PoiDetailResult;

import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.StringUtil;
import park.bika.com.parkapplication.view.CircleImageView;

/**
 * created huangzujun on 2019/9/13
 * Describe: 附近item适配器
 */
public class NearbyItemFragmentAdapter extends BaseAdapter {

    private List<PoiDetailResult> list;

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    public NearbyItemFragmentAdapter(List<PoiDetailResult> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public NearbyItemFragmentAdapter() {
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nearby_list_fragment, null);
            holder.img = convertView.findViewById(R.id.civ_img);
            holder.title = convertView.findViewById(R.id.tv_title);
            holder.tv_pl = convertView.findViewById(R.id.tv_pl);
            holder.tv_discount = convertView.findViewById(R.id.tv_discount);
            holder.price = convertView.findViewById(R.id.tv_price);
            holder.star1 = convertView.findViewById(R.id.id_check_one);
            holder.star2 = convertView.findViewById(R.id.id_check_two);
            holder.star3 = convertView.findViewById(R.id.id_check_three);
            holder.star4 = convertView.findViewById(R.id.id_check_four);
            holder.star5 = convertView.findViewById(R.id.id_check_five);
            holder.tip = convertView.findViewById(R.id.tv_tip);
            holder.ll_two = convertView.findViewById(R.id.ll_two);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        list.get(position).getLocation();
        int imgRound = (int) (Math.random() * 10 % 4);
        switch (imgRound){
            case 0:
                holder.img.setImageResource(R.mipmap.nearby_jt);
                break;
            case 1:
                holder.img.setImageResource(R.mipmap.nearby_jt2);
                break;
            case 2:
                holder.img.setImageResource(R.mipmap.nearby_ms);
                break;
            case 3:
                holder.img.setImageResource(R.mipmap.nearby_sh);
                break;
        }

        holder.title.setText(list.get(position).getName());
        holder.tip.setText(String.format("营业时间：%s", list.get(position).getShopHours()));
        holder.tv_pl.setText(String.format("评论人数：%s", list.get(position).getCommentNum()));
        holder.tv_discount.setText(String.format("优惠数：%s", list.get(position).getDiscountNum()));
        holder.price.setText(String.format("￥%s", StringUtil.formatAmount(list.get(position).getPrice())));
        int star = (int) Math.ceil(list.get(position).getOverallRating());
        switch (star) {
            case 5:
                holder.star5.setChecked(true);
            case 4:
                holder.star4.setChecked(true);
            case 3:
                holder.star3.setChecked(true);
            case 2:
                holder.star2.setChecked(true);
            case 1:
                holder.star1.setChecked(true);
                break;
            default:
                holder.star1.setChecked(true);
                holder.star2.setChecked(true);
                holder.star3.setChecked(true);
                holder.star4.setChecked(true);
                holder.star5.setChecked(true);
        }
//        holder.ll_two.removeAllViews();
//        TextView tv1 = new TextView(parent.getContext());
//        tv1.setText("服务评价" + list.get(position).getServiceRating());
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tv);
        return convertView;
    }

    private class ViewHolder {
        private CircleImageView img;
        private TextView title, tip, price, tv_pl, tv_discount;
        private LinearLayout ll_two;
        private CheckBox star1, star2, star3, star4, star5;
    }
}
