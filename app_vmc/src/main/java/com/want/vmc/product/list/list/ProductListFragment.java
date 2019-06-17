package com.want.vmc.product.list.list;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vmc.core.model.product.BLLProduct;

import java.util.List;

import vmc.vendor.Constants;
import vmc.vendor.VRecyclerFragment;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductListFragment extends VRecyclerFragment<BLLProduct> implements ProductListContract.View,
                                                                                  Constants {

    private int mOrientation = GridLayoutManager.VERTICAL;

    public static ProductListFragment newInstance() {
        return newInstance(GridLayoutManager.HORIZONTAL);
    }

    public static ProductListFragment newInstance(int orientation) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle bundle = getBundle(orientation);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProductListContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Override
    protected void onSetupRecyclerView(RecyclerView recyclerView) {
        super.onSetupRecyclerView(recyclerView);
        GridLayoutManager gm = new GridLayoutManager(getActivity(), 3, mOrientation, false);
        recyclerView.setLayoutManager(gm);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            mOrientation = savedInstanceState.getInt(Extras.DATA, Configuration.ORIENTATION_PORTRAIT);
        } else {
            mOrientation = getArguments().getInt(Extras.DATA, Configuration.ORIENTATION_PORTRAIT);
        }

        if (Configuration.ORIENTATION_PORTRAIT == mOrientation) {
            mOrientation = GridLayoutManager.VERTICAL;
        } else {
            mOrientation = GridLayoutManager.HORIZONTAL;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 请求商品数据
     */
    protected void onReqProducts() {
        getPresenter().reqProducts(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        onReqProducts();
    }

    @Override
    protected View onCreateItemView(LayoutInflater inflater, int viewType) {
        return com.want.vmc.databinding.VendorProductListItemLayoutBinding.inflate(inflater, null, true).getRoot();
    }

    @Override
    protected void onUpdateItemView(final View view, int pos, BLLProduct data, final int viewType) {
        final com.want.vmc.databinding.VendorProductListItemLayoutBinding
                binding = DataBindingUtil.getBinding(view);
        binding.setModel(new ProductListViewModel(getActivity(),data));
        // 固定住view的大小
        final int pWidth = view.getWidth();
        final int pHeight = view.getHeight();
        if (pWidth != pHeight || pWidth == 0) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    final View parent = getRecyclerView();
                    int size = GridLayoutManager.VERTICAL == mOrientation ?
                               parent.getWidth() : parent.getHeight();
                    size = (int) (size / 3f + 0.5f);
                    final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                    lp.width = size;
                    lp.height = size;
                    view.setLayoutParams(lp);
                }
            });
        }
    }

    @Override
    public void onProducts(int result, List<BLLProduct> products, Throwable e) {
        if (Result.OK == result) {
            updateData(products);
        }
    }
}