package com.leo.device.base.viewbox;


import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Leo90
 */
public abstract class BaseRecyclerViewBox<B> extends BaseViewBox<B> {

    private ViewHolder viewHolder;

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public ViewHolder createViewHolder() {
        viewHolder = new ViewHolder();
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder() {
            super(BaseRecyclerViewBox.this.getRootView());
        }

        public ViewBox<B> getViewBox() {
            return BaseRecyclerViewBox.this;
        }
    }
}
