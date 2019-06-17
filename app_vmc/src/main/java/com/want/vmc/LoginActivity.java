package com.want.vmc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.user.UserInfo;
import com.want.base.http.error.HttpError;

import vmc.core.log;
import vmc.machine.core.VMCContoller;

/**
 * <b>Create Date:</b> 2016/11/28<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class LoginActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private static final String TAG = "init";
    public static final int RESULTCODE = 1100;
    private EditText mEt_name;
    private EditText mEt_pwd;
    private TextView mTv_pro;
    private static TextView tvToastText;
    private static Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_home_login_activity);

        mEt_name = (EditText) findViewById(R.id.vendor_login_username);
        mEt_pwd = (EditText) findViewById(R.id.vendor_login_password);
        mTv_pro = (TextView) findViewById(R.id.vendor_login_promt);
        mEt_pwd.setOnEditorActionListener(this);
    }


    public void onClickLogin(View view) {
        login();
    }


    private void login() {
        final String name = mEt_name.getText().toString();
        final String pwd = mEt_pwd.getText().toString();
        final String factory_code = VMCContoller.getInstance().getVendingMachineId();

        if (!BLLController.getInstance().isNetState(this)) {

            toast(getApplicationContext(), "请检查网络", mEt_pwd);
            return;
        }
        if (TextUtils.isEmpty(factory_code) || TextUtils.equals(factory_code, "00000000")) {

            log.w(TAG, "onError: 无法获取机器号");

            toast(this, "机器号为空", mEt_pwd);

            return;
        }

        if (!name.isEmpty() && !pwd.isEmpty()) {

            mTv_pro.setText("");


            Odoo.getInstance(this).authenticate(this,
                              name,
                              pwd,
                              factory_code,
                              new OdooHttpCallback<UserInfo>(this) {

                                  @Override
                                  public void onSuccess(UserInfo result) {
                                      log.d(TAG, "onSuccess: 用户登录成功!");
                                      SharedPreferences sp = this.getContext().getSharedPreferences("user", MODE_PRIVATE);
                                      SharedPreferences.Editor editor = sp.edit();
                                      editor.putString("name", name);
                                      editor.putString("password", pwd);
                                      editor.putString("factory_code", factory_code);
                                      editor.apply();

                                      Intent intent = new Intent();
                                      setResult(RESULTCODE, intent);
                                      finish();
                                  }

                                  @Override
                                  public void onError(HttpError error) {
                                      super.onError(error);
                                      log.w(TAG, "onError: 用户登录失败");
//
                                      toast(this.getContext(), error == null ||
                                                               TextUtils.isEmpty(error.getMessage())
                                                               ? "链接服务器失败"
                                                               : (error.getMessage() + ""), mEt_pwd);
                                  }
                              }
            );
        } else {
            toast(this, "用户名/密码不能为空", mEt_pwd);
        }

    }

    public void toast(Context context, String data, View v) {
        if (mToast == null) {
            mToast = Toast.makeText(context, data, Toast.LENGTH_LONG);
            View view = View.inflate(context, R.layout.vendor_deliver_custom_toast_layout, null);
            mToast.setView(view);
            tvToastText = (TextView) view.findViewById(R.id.tvToastText);
            tvToastText.setText(data);
        } else {
            tvToastText.setText(data);
        }

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        mToast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, location[1]);
        mToast.show();

    }

    public void onClickReset(View view) {
        mEt_name.setText("");
        mEt_pwd.setText("");
        mTv_pro.setText("");
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            login();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }


        return super.onKeyDown(keyCode, event);
    }
}
