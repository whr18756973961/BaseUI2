package com.whr.baseui.baserv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dafan on 2015/11/14 0014.
 */
public abstract class BaseRvAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements IListDataOperation<T> {

    /**
     * 数据源
     */
    public List<T> listData = new ArrayList<>();
    /**
     * 收集所有在屏幕内的ViewHolder
     */
    private List<VH> mViewHolderInWindow = new ArrayList<>();

    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickLitener;
    private OnItemLongClickListener mOnItemLongClickLitener;

    @Override
    public void addData(List<T> data) {
        if (data == null) return;
        if (data.isEmpty()) return;

        this.listData.addAll(data);
        this.notifyItemRangeInserted(this.getItemCount(), data.size());
    }

    @Override
    public void addAllData(int index, List<T> data) {
        if (data == null) return;
        if (data.isEmpty()) return;
        this.listData.addAll(index, data);
        this.notifyItemRangeInserted(index, data.size());
    }

    @Override
    public void addData(int index, T bean) {
        if (bean == null) return;
        if (index < 0 || index > this.getItemCount()) return;

        this.listData.add(index, bean);
        this.notifyItemInserted(index);
    }

    @Override
    public void addData(T data) {
        if (data == null) return;
        this.listData.add(data);
        this.notifyItemInserted(this.listData.size());
    }

    @Override
    public void changeData(T bean, int index) {
        if (bean == null) return;
        if (index < 0 || index > this.getItemCount()) return;

        this.listData.set(index, bean);
        /*this.notifyItemChanged(index);*/
        this.notifyDataSetChanged();
    }

    @Override
    public void removeData(int index) {
        if (index < 0 || index > this.listData.size()) return;
        this.listData.remove(index);
        /*this.notifyItemRemoved(index);*/
        this.notifyDataSetChanged();
    }

    /**
     * 批量移除数据
     *
     * @param list
     */
    public void remove(List<T> list) {
        if (list == null || list.isEmpty() || this.listData == null || this.listData.isEmpty())
            return;
        if (this.listData.remove(list)) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public List<T> getData() {
        return this.listData;
    }

    @Override
    public void setData(List<T> data) {
        if (data == null) return;
        this.listData = data;
        this.notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (this.listData == null || this.listData.isEmpty())
            return;
        this.listData.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.listData == null ? 0 : this.listData.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null)
            mInflater = LayoutInflater.from(parent.getContext());
        return onCreateViewHolder(mInflater, parent, viewType);
    }

    /**
     * @param inflater
     * @param parent
     * @param viewType
     * @return
     */
    public abstract VH onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);


    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setTag(holder);

        // 点击事件回调
        if (mOnItemClickLitener != null)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });

        // 长按事件回调
        if (mOnItemLongClickLitener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickLitener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });
        }

        if (mViewHolderInWindow == null)
            mViewHolderInWindow = new ArrayList<>();
        if (!mViewHolderInWindow.contains(holder))
            mViewHolderInWindow.add(holder);

        onBindViewHolder(holder, position, (listData == null || listData.isEmpty()) ? null : listData.get(position));
    }

    /**
     * @param holder
     * @param position
     * @param t
     */
    public abstract void onBindViewHolder(final VH holder, final int position, T t);

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        if (mViewHolderInWindow == null)
            mViewHolderInWindow = new ArrayList<>();
        if (!mViewHolderInWindow.contains(holder))
            mViewHolderInWindow.add(holder);
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        if (mViewHolderInWindow != null && mViewHolderInWindow.contains(holder))
            mViewHolderInWindow.remove(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);
        if (mViewHolderInWindow != null && mViewHolderInWindow.contains(holder))
            mViewHolderInWindow.remove(holder);
    }

    /**
     * 清理系统未来得及清理的ViewHolder
     *
     * @return
     */
    public List<VH> getViewHolderInWindow() {
        List<VH> vhs = new ArrayList<>();
        for (VH vh : mViewHolderInWindow) {
            if (vh.getAdapterPosition() != -1)
                vhs.add(vh);
        }
        mViewHolderInWindow.clear();
        mViewHolderInWindow.addAll(vhs);
        vhs.clear();
        vhs = null;
        return mViewHolderInWindow;
    }

    /**
     * 点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    /**
     * 长按事件
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    /**
     * Item的点击事件监听
     *
     * @param litener
     */
    public void setOnItemClickListener(OnItemClickListener litener) {
        mOnItemClickLitener = litener;
    }

    /**
     * Item的长按事件监听
     *
     * @param litener
     */
    public void setOnItemLongClickLitener(OnItemLongClickListener litener) {
        mOnItemLongClickLitener = litener;
    }
}
