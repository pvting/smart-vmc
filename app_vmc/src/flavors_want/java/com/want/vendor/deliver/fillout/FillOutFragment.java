package com.want.vendor.deliver.fillout;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.repacked.google.common.eventbus.Subscribe;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.want.base.sdk.framework.app.MFragment;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.vendor.deliver.DeliverActivity;
import com.want.vendor.deliver.fai.ClearEditTextEventBus;
import com.want.vmc.databinding.VendorDeliverFilloutLayoutYichuBinding;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * View stub.
 */
public class FillOutFragment extends MFragment implements FillOutContract.View {

    private FillOutViewModel mFillOutViewModel;
    private FillOutViewModel.PositionChangeListener positionChangeListener;

    public static FillOutFragment newInstance() {
        FillOutFragment fragment = new FillOutFragment();
        return fragment;
    }

    public FillOutViewModel.PositionChangeListener getPositionChangeListener() {
        return positionChangeListener;
    }

    public void setPositionChangeListener(FillOutViewModel.PositionChangeListener positionChangeListener) {
        this.positionChangeListener = positionChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorDeliverFilloutLayoutYichuBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FillOutViewModel.DEFAULT_TAG_RESULT) {
            getActivity().finish();
        }
    }

    private int editTextCursorPosition = 0;
    private String beforeStr = "";
    private String aferStr = "";

    private VendorDeliverFilloutLayoutYichuBinding binding = null;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MEventBus.getDefault().register(this);
        binding = DataBindingUtil.getBinding(view);

        mFillOutViewModel = new FillOutViewModel(getActivity(), this);

        binding.setModel(mFillOutViewModel);
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            binding.vendorDeliverOrderEdittext.setInputType(InputType.TYPE_NULL);
        } else {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(
                        "setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(binding.vendorDeliverOrderEdittext, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        binding.vendorDeliverOrderEdittext.setInputType(InputType.TYPE_NULL);
        binding.vendorDeliverOrderEdittext.setFocusable(true);
        binding.vendorDeliverOrderEdittext.performClick();
        binding.vendorDeliverOrderEdittext.requestFocus();

//        binding.vendorDeliverOrderEdittext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //获取光标的位置;
//                editTextCursorPosition = binding.vendorDeliverOrderEdittext.getSelectionStart();
//                positionChangeListener.onPositionChanged(editTextCursorPosition);
//            }
//        });
        binding.vendorDeliverOrderEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //获取光标的位置;
                editTextCursorPosition = binding.vendorDeliverOrderEdittext.getSelectionStart();
                positionChangeListener.onPositionChanged(editTextCursorPosition);
                beforeStr = binding.vendorDeliverOrderEdittext.getText().toString();
            }
//
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
//
            @Override
            public void afterTextChanged(Editable s) {
            }

        });


//        binding.vendorDeliverOrderEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    return;
//                }
//                //获取光标的位置;
//                editTextCursorPosition = binding.vendorDeliverOrderEdittext.getSelectionStart();
//                positionChangeListener.onPositionChanged(editTextCursorPosition);
//            }
//        });

//        binding.etInputParent.hideSoftInputMethod(binding.vendorDeliverOrderEdittext, getActivity());


        binding.etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!TextUtils.isEmpty(v.getText().toString())){
                    /** 去除回车 */
                    ((DeliverActivity)getActivity()).restTime();
                    Pattern p = Pattern.compile("\n");
                    Matcher m = p.matcher(v.getText().toString());
                    String str = m.replaceAll("");
                    if (mFillOutViewModel.isClickable) {
//                        mFillOutViewModel.setInput(str);
//                        binding.etInput.setText("");
                        Log.i("扫码获取到的值binding.etInput:",binding.etInput+"");
                        //调用出货的方法
                        mFillOutViewModel.configGoodsOut(v);
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        if (mFillOutViewModel.isClickable) {
            mFillOutViewModel.setInput("", binding.vendorDeliverOrderEdittext);
        }
    }


    @Subscribe
    public void onEvent(ClearEditTextEventBus editTextEventBus) {
        if (null != binding) {
            binding.vendorDeliverOrderEdittext.setText("");
        }
        mFillOutViewModel.clearInputString();
    }


    @Override
    public void onDestroy() {
        MEventBus.getDefault().unregister(this);
        if (mFillOutViewModel != null) {
            mFillOutViewModel.onDestroy();
        }
        super.onDestroy();
    }


}