package com.abcmeasure.andyzhou.ruler;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abcmeasure.andyzhou.ruler.databinding.CmGridBinding;
import com.abcmeasure.andyzhou.ruler.databinding.InchGridBinding;

public class RulerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int length = 0;
    float dpi = 0;

    int currentViewType;

    public RulerRecyclerAdapter(int length, float dpi, int currentViewType) {
        this.length = length;
        this.dpi = dpi;
        this.currentViewType = currentViewType;
    }

    @Override
    public int getItemViewType(int position) {
        return currentViewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding;
        if (viewType == Constants.INCH_VIEWTYPE) {
            binding = InchGridBinding.inflate(inflater, parent, false);
        } else {
            binding = CmGridBinding.inflate(inflater, parent, false);
        }

        return new RulerGridViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewDataBinding binding = ((RulerGridViewHolder) holder).getBinding();
        if (binding instanceof InchGridBinding)
            ((InchGridBinding) binding).setGridModel(new RulerGridViewModel(String.valueOf(position), dpi, Constants.INCH_VIEWTYPE));
        if (binding instanceof CmGridBinding)
            ((CmGridBinding) binding).setGridModel(new RulerGridViewModel(String.valueOf(position), dpi, Constants.CM_VIEWTYPE));
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public int getCurrentViewType() {
        return currentViewType;
    }

    public void changeCurrentViewType() {
        this.currentViewType = -currentViewType;
        notifyDataSetChanged();
    }

    public class RulerGridViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;


        public RulerGridViewHolder(View itemView) {
            super(itemView);
        }

        public RulerGridViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
