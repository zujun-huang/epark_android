package cn.epark.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.epark.R;

/**
 * create by huangzujun on 2020-07-15
 * describe：上传图片适配器
 */
public class UploadPicAdapter extends BaseRecyclerViewAdapter<String, UploadPicAdapter.ViewHolder> {

    public final int MAX_IMG_NUMBER = 9;

    public UploadPicAdapter(List<String> imgPaths) {
        if (imgPaths != null && imgPaths.size() > 0) {
            if (imgPaths.size() > MAX_IMG_NUMBER) {
                imgPaths = imgPaths.subList(0, MAX_IMG_NUMBER);
            }
            setList(imgPaths);
        }
    }

    @Override
    public int getItemCount() {
        //小于最大图片数量时多个添加按钮
        return super.getItemCount() < MAX_IMG_NUMBER ? super.getItemCount() + 1 : super.getItemCount();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getView(parent, R.layout.adapter_upload_img_item));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (position + 1 == getItemCount() && super.getItemCount() < MAX_IMG_NUMBER) {
            viewHolder.close_btn.setVisibility(View.GONE);
            viewHolder.img.setImageResource(R.drawable.ic_add_pic);
        } else {
            viewHolder.close_btn.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(getItem(position))
                    .error(ContextCompat.getDrawable(context, R.mipmap.app_icon))
                    .into(viewHolder.img);
        }
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder implements View.OnClickListener {

        private ImageView img, close_btn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_add_pic);
            close_btn = itemView.findViewById(R.id.close_btn);
            close_btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close_btn:
                    int position = getAdapterPosition();
                    if (position != -1) {
                        getList().remove(position);
                        notifyItemRemoved(position);

                    }
                    break;
                default: super.onClick(v);
            }
        }
    }
}
