package com.whr.baseui.baserv;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by dafan on 2017/4/22 0022.
 */

public class BaseRvViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;

    public BaseRvViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
    }

    /**
     * 控件注册
     *
     * @param viewId 控件ID
     * @param <E>
     * @return
     */
    public <E extends View> E findView(@IdRes int viewId) {
        return itemView.findViewById(viewId);
    }

    /**
     * same as {@link RecyclerView.ViewHolder#getAdapterPosition()}
     *
     * @return
     */
    public int position() {
        return getAdapterPosition();
    }

    public Context getContext() {
        return mContext;
    }
}
