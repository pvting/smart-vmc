package com.vmc.core.model.pay;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class PayStatusResult extends Model {
    public int order_status;
    public String payment_type;

    @Override
    public String toString() {
        return "PayStatusResult{" +
               "order_status=" + order_status +
               ", payment_type='" + payment_type + '\'' +
               "} " + super.toString();
    }

    public PayStatusResult() {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.order_status);
        dest.writeString(this.payment_type);
    }

    protected PayStatusResult(Parcel in) {
        super(in);
        this.order_status = in.readInt();
        this.payment_type = in.readString();
    }

    public static final Creator<PayStatusResult> CREATOR = new Creator<PayStatusResult>() {
        @Override
        public PayStatusResult createFromParcel(Parcel source) {return new PayStatusResult(source);}

        @Override
        public PayStatusResult[] newArray(int size) {return new PayStatusResult[size];}
    };
}