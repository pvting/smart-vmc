package com.want.vmc;

import android.os.Bundle;

import com.want.vmc.R;
import vmc.vendor.VActivity;

/**
 * <b>Create Date:</b> 2016/12/8<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class SerialActivity extends VActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContentView();
        setContentView(R.layout.vendor_serial_error_activity);
    }
}
