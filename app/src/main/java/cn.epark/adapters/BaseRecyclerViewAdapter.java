package cn.epark.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * create by huangzujun on 2020-07-15
 * describe：BaseRecyclerViewAdapter
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewAdapter.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private List<T> list;
    public Context context;
    /** 拖拽效果 */
    public CommonItemTouchHelperCallback.Callback touchHelperCallback;

    public T getItem(int position) {
        return list.get(position);
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }

    public View getView(@NonNull ViewGroup parent, int layoutId) {
        this.context = parent.getContext();
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {

        void OnItemClick(RecyclerView.ViewHolder holder);

        default boolean OnItemLongClick(RecyclerView.ViewHolder holder) {
            return false;
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemClickListener != null) {
                return onItemClickListener.OnItemLongClick(this);
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(this);
            }
        }
    }
}
