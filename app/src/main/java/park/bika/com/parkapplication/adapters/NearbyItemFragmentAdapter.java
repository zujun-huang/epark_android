package park.bika.com.parkapplication.adapters;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.Photo;
import com.amap.api.services.poisearch.PoiItemExtension;
import com.bumptech.glide.Glide;

import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.view.CircleImageView;

/**
 * created huangzujun on 2019/9/13
 * Describe: 附近item适配器
 */
public class NearbyItemFragmentAdapter extends BaseAdapter {

    private List<PoiItem> list;

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    public NearbyItemFragmentAdapter(List<PoiItem> list) {
        this.list = list;
        notifyDataSetChanged();
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
            holder.tell = convertView.findViewById(R.id.tv_tell);
            holder.address = convertView.findViewById(R.id.tv_address);
            holder.star1 = convertView.findViewById(R.id.id_check_one);
            holder.star2 = convertView.findViewById(R.id.id_check_two);
            holder.star3 = convertView.findViewById(R.id.id_check_three);
            holder.star4 = convertView.findViewById(R.id.id_check_four);
            holder.star5 = convertView.findViewById(R.id.id_check_five);
            holder.openTime = convertView.findViewById(R.id.tv_tip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        List<Photo> photos = list.get(position).getPhotos();
        Glide.with(parent.getContext())
                .load( photos!= null && photos.size() > 0 ? photos.get(0).getUrl() : null)
                .error(ContextCompat.getDrawable(parent.getContext(), R.mipmap.nearby_jt))
                .into(holder.img);
        holder.title.setText(list.get(position).getTitle());
        PoiItemExtension poiExtension = list.get(position).getPoiExtension();
        if (poiExtension == null) {
            poiExtension = new PoiItemExtension(null, null);
        }

        if (TextUtils.isEmpty(poiExtension.getOpentime())) {
            holder.openTime.setVisibility(View.GONE);
        } else {
            holder.openTime.setText(String.format("营业时间：%s", poiExtension.getOpentime()));
        }

        if (TextUtils.isEmpty(list.get(position).getTel())) {
            holder.tell.setVisibility(View.GONE);
        } else {
            holder.tell.setText(String.format("电话：%s", list.get(position).getTel()));
        }

        if (TextUtils.isEmpty(list.get(position).getSnippet())) {
            holder.address.setVisibility(View.GONE);
        } else {
            holder.address.setText(String.format("地址：%s", list.get(position).getSnippet()));
        }

        int star = (int) Math.ceil(Double.valueOf(TextUtils.isEmpty(poiExtension.getmRating()) ? "5" : poiExtension.getmRating()));
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
        }
        return convertView;
    }

    private class ViewHolder {
        private CircleImageView img;
        private TextView title, openTime, tell, address, tv_discount;
        private LinearLayout ll_two;
        private CheckBox star1, star2, star3, star4, star5;
    }
}
