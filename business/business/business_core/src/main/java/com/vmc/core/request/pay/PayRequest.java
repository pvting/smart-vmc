package com.vmc.core.request.pay;

import android.os.Parcel;
import android.os.Parcelable;

import com.vmc.core.request.BaseRequest;

/**
 * <b>Create Date:</b> 8/26/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class PayRequest extends BaseRequest implements Parcelable {

    public String payment_type = Payment.NONE.getPayment();
    public String order_id;
    public double total_amount;
    public String  card_number;



    public enum Payment {
        NONE(""),
        /** 支付宝*/
        ALIPAY("alipay"),
        /** 微信*/
        WECHATPAY("weixinpay"),
        /** 旺币*/
        WANGBI("wangbipay"),

        RMB("RMB"),

        CARD_WATERGOD("card_watergod");


        String payment;

        Payment(String payment) {
            this.payment = payment;
        }

        public String getPayment() {
            return this.payment;
        }


    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.payment_type);
        dest.writeString(this.order_id);
        dest.writeDouble(this.total_amount);
        dest.writeString(this.card_number);
    }

    public PayRequest() {}

    protected PayRequest(Parcel in) {
        this.payment_type = in.readString();
        this.order_id = in.readString();
        this.total_amount = in.readDouble();
        this.card_number = in.readString();
    }

    public static final Parcelable.Creator<PayRequest> CREATOR = new Parcelable.Creator<PayRequest>() {
        @Override
        public PayRequest createFromParcel(Parcel source) {return new PayRequest(source);}

        @Override
        public PayRequest[] newArray(int size) {return new PayRequest[size];}
    };
}
