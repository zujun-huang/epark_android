package cn.epark.adapters;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ScaleXSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.epark.R;
import cn.epark.bean.ShareParkInfo;
import cn.epark.utils.StringUtil;
import cn.epark.view.CircleImageView;

/**
 * created huangzujun on 2019/9/13
 * Describe: 发布共享车位item适配器
 */
public class ShareApplyItemAdapter extends BaseAdapter {

    private List<ShareParkInfo> list;
    private ItemCancelClickListener onItemCancelClickListener;

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    public ShareApplyItemAdapter(List<ShareParkInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addItem(ShareParkInfo shareParkInfo) {
        if (this.list != null && this.list.size() > 0 && shareParkInfo != null) {
            this.list.add(shareParkInfo);
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        if (position > -1 && position < this.list.size()) {
            this.list.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public ShareParkInfo getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_share_apply_item, null);
            holder.containerLL = convertView.findViewById(R.id.ll_container);
            holder.createTimeTv = convertView.findViewById(R.id.tv_create);
            holder.stateTv = convertView.findViewById(R.id.tv_state);
            holder.imgCiv = convertView.findViewById(R.id.civ_img);
            holder.titleTv = convertView.findViewById(R.id.tv_title);
            holder.numberTv = convertView.findViewById(R.id.tv_number);
            holder.timeTv = convertView.findViewById(R.id.tv_time);
            holder.totalPriceTv = convertView.findViewById(R.id.tv_total_price);
            holder.updateTv = convertView.findViewById(R.id.tv_update);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.createTimeTv.setText(list.get(position).getCreateTime());
        Glide.with(parent.getContext())
                .load(list.get(position).getParkImg())
                .error(ContextCompat.getDrawable(parent.getContext(), R.mipmap.default_share_park))
                .into(holder.imgCiv);
        holder.titleTv.setText(list.get(position).getAddress());

        if (!TextUtils.isEmpty(list.get(position).getFloor()) && !TextUtils.isEmpty(list.get(position).getNumber())){
            holder.numberTv.setText("车位号：" + list.get(position).getFloor() + " - " + list.get(position).getNumber());
        } else if (!TextUtils.isEmpty(list.get(position).getFloor())){
            holder.numberTv.setText("车位号：" + list.get(position).getFloor());
        } else if (!TextUtils.isEmpty(list.get(position).getNumber())){
            holder.numberTv.setText("车位号：" + list.get(position).getNumber());
        }

        if (!TextUtils.isEmpty(list.get(position).getFromTime()) && !TextUtils.isEmpty(list.get(position).getToTime())){
            holder.timeTv.setText("共享时段：" + list.get(position).getFromTime() + " - " + list.get(position).getToTime());
        } else {
            holder.timeTv.setText("共享时段：");
        }
        int state = list.get(position).getState();
        holder.updateTv.setOnClickListener(v -> {
            if (onItemCancelClickListener != null) {
                onItemCancelClickListener.itemCancelClick(v, position);
            }
        });
        switch (state) {
            case -2:
                holder.containerLL.setBackgroundResource(R.drawable.shape_item_bg_z);
                holder.stateTv.setText("本地申请历史");
                holder.stateTv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.g333333));
                holder.totalPriceTv.setText("本地申请历史需要确认才能审核哦~");
                break;
            case 0:
                holder.containerLL.setBackgroundResource(R.drawable.shape_item_bg_r);
                holder.stateTv.setText("审核失败");
                holder.stateTv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.gf35334));
                break;
            case 1:
                holder.containerLL.setBackgroundResource(R.drawable.shape_item_bg_g);
                holder.stateTv.setText("审核通过");
                holder.stateTv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.modal_nav_tencent));
                holder.totalPriceTv.setText("此车位正在接单中，不要灰心~");
                break;
            case 2:
                holder.containerLL.setBackgroundResource(R.drawable.shape_item_bg_g);
                holder.stateTv.setText("已共享");
                holder.stateTv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.modal_nav_tencent));
                SpannableString priceSs = new SpannableString("合计收益：￥" + StringUtil.formatAmount(list.get(position).getPrice()));
                priceSs.setSpan(new ScaleXSpan(1.6f), 5, priceSs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                priceSs.setSpan(new ForegroundColorSpan(ContextCompat.getColor(parent.getContext(), R.color.gf35334))
                        , 5, priceSs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.totalPriceTv.setText(priceSs);
                break;
            default:
                holder.containerLL.setBackgroundResource(R.drawable.shape_item_bg_b);
                holder.stateTv.setText("审核中");
                holder.stateTv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.theme_color));
                holder.totalPriceTv.setText("信息的完整度可以加速审核速度~");
                break;
        }
        return convertView;
    }

    private class ViewHolder {
        private CircleImageView imgCiv;
        private LinearLayout containerLL;
        private TextView titleTv, createTimeTv, stateTv,
                numberTv, timeTv, totalPriceTv, updateTv;
    }

    public void setOnItemCancelClickListener(ItemCancelClickListener onItemCancelClickListener) {
        this.onItemCancelClickListener = onItemCancelClickListener;
    }

    public interface ItemCancelClickListener {
        /**
         * Item取消共享点击事件
         * @param view 当前视图
         * @param position 当前item下标
         */
        void itemCancelClick(View view, int position);
    }
}
