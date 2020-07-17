package cn.epark.adapters;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;

/**
 * create by huangzujun on 2020-07-16
 * describe：RecyclerView拖拽
 */
public class CommonItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private BaseRecyclerViewAdapter mAdapter;
    private boolean isLongPressDrag = true;

    public CommonItemTouchHelperCallback(BaseRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public CommonItemTouchHelperCallback(BaseRecyclerViewAdapter adapter, boolean longPressDrag) {
        mAdapter = adapter;
        isLongPressDrag = longPressDrag;
    }

    public void setLongPressDrag(boolean longPressDrag) {
        isLongPressDrag = longPressDrag;
    }

    /** 决定拖拽/滑动的方向 */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager ||
                recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, 0);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        }
    }

    /** 和位置交换有关 */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (mAdapter.touchHelperCallback != null) {
            return mAdapter.touchHelperCallback.onItemMove(source, target);
        }
        if (mAdapter.getItemCount() < 1) {
            return false;
        }
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();//当前拖拽到的item的viewHolder
        if (fromPosition < mAdapter.getList().size() && toPosition < mAdapter.getList().size()) {
            //交换位置
            Collections.swap(mAdapter.getList(), fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isLongPressDrag;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /** 和滑动有关,可用于实现swipe功能 */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /** 长按选中时回调 */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (mAdapter.touchHelperCallback != null) {
                mAdapter.touchHelperCallback.onItemSelect(viewHolder);
            }
            if (viewHolder != null && ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
                viewHolder.itemView.setScaleX(1.1f);
                viewHolder.itemView.setScaleY(1.1f);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /** 手指松开的时回调 */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (!recyclerView.isComputingLayout()) {
            if (mAdapter.touchHelperCallback != null) {
                mAdapter.touchHelperCallback.onItemClear(viewHolder);
            }
            viewHolder.itemView.setScaleX(1.0f);
            viewHolder.itemView.setScaleY(1.0f);
        }
        super.clearView(recyclerView, viewHolder);
    }

    public interface Callback {
        /** 移动回调 */
        boolean onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target);

        /** 状态清除 */
        default void onItemClear(RecyclerView.ViewHolder source){}

        //drag或者swipe选中
        default void onItemSelect(RecyclerView.ViewHolder source){}
    }
}
