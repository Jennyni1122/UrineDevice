package com.leo.device.base;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.leo.device.base.viewbox.BaseRecyclerViewBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Leo90
 */
public abstract class BaseAdapter<B> extends RecyclerView.Adapter<BaseRecyclerViewBox<B>.ViewHolder> {

    public static final int TYPE_HEAD = -7;

    private List<B> data = new ArrayList<>(20);
    private List<BaseRecyclerViewBox<B>> headBoxList = new ArrayList<>(1);

    @NonNull
    @Override
    public BaseRecyclerViewBox<B>.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type < 0 && type % TYPE_HEAD == 0) {
            int headPosition = (type / TYPE_HEAD) - 1;
            return headBoxList.get(headPosition).createViewHolder();
        }
        BaseRecyclerViewBox<B> viewBox = createViewBox(type);
        viewBox.createView(viewGroup);
        viewBox.bindView();
        return viewBox.createViewHolder();
    }

    /**
     * 创建ViewBox
     *
     * @param type 类型
     * @return ViewBox
     */
    protected abstract BaseRecyclerViewBox<B> createViewBox(int type);

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewBox<B>.ViewHolder viewHolder, int position) {
        viewHolder.getViewBox().bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return headBoxList.size() + data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < headBoxList.size()) {
            return TYPE_HEAD * (position + 1);
        }
        return super.getItemViewType(position);
    }

    public void addHeadView(BaseRecyclerViewBox<B> headBox) {
        headBoxList.add(headBox);
        notifyDataSetChanged();
    }

    protected List<B> getDisplayList() {
        return getSourceList();
    }

    public List<B> getSourceList() {
        return data;
    }

    public void add(B item) {
        if (item != null) {
            int start = getSourceList().size();
            getSourceList().add(item);
            notifyItemInserted(start);
            notifyItemRangeChanged(start, 1);
        }
    }

    public void add(Collection<B> collection) {
        if (collection != null && collection.size() > 0) {
            int start = getSourceList().size();
            data.addAll(collection);
            notifyItemRangeInserted(start, collection.size() - start);
            notifyItemRangeChanged(start, collection.size() - start);
        }
    }

    public void remove(B item) {
        int position = getSourceList().indexOf(item);
        remove(position);
    }

    public void remove(int position) {
        if (position >= 0 && position < getSourceList().size()) {
            getSourceList().remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, 1);
        }
    }

    public void replace(Collection<B> collection) {
        getSourceList().clear();
        if (collection != null) {
            getSourceList().addAll(collection);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        getSourceList().clear();
        notifyDataSetChanged();
    }
}
