package com.want.vendor.tips.cancelorder;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorContinueButtonLayoutBinding;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 2017/1/7<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class CancelOrderFragment extends VFragment implements CancelOrderContract.View{
    
    private CancelOrderViewModel cancelOrderViewModel;
    
    public static CancelOrderFragment newInstance( ){
        CancelOrderFragment fragment = new CancelOrderFragment( );
        return fragment;
    }
    
    public CancelOrderFragment( ){
    
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected CancelOrderContract.Presenter getPresenter( ){
        return super.getPresenter( );
    }
    
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState ){
        return VendorContinueButtonLayoutBinding.inflate(inflater, container, false).getRoot( );
    }
    
    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState ){
        super.onViewCreated(view, savedInstanceState);
        VendorContinueButtonLayoutBinding binding = DataBindingUtil.getBinding(getView( ));
        cancelOrderViewModel = new CancelOrderViewModel(getPresenter( ));
        binding.setModel(cancelOrderViewModel);
    }
    
    @Override
    public void onBack( ){
    
    }
    
    @Override
    public void onPause( ){
        super.onPause( );
        if (null != cancelOrderViewModel) {
            cancelOrderViewModel.pause( );
        }
    }
    
    @Override
    public void onResume( ){
        super.onResume( );
        if (null != cancelOrderViewModel) {
            cancelOrderViewModel.resume( );
        }
    }
    
    @Override
    public void onDestroy( ){
        super.onDestroy( );
        if (null != cancelOrderViewModel) {
            cancelOrderViewModel.destroy( );
        }
    }
    
}