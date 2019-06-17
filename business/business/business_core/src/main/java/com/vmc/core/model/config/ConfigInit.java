package com.vmc.core.model.config;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ConfigInit extends Model {
    /**安全距离*/
    public int check_distance;
    /**客服电话*/
    public String customer_phone;
    /**帮助二维码*/
    public String img_url;
    /**
     * 支付宝自动退款
     */
    public boolean alipay_refund;
    /**
     * 微信自动退款
     */
    public boolean weixinpay_refund;
    /**
     * 出货失败：shipment_fail_one 光感没有检测到
     */
    public boolean shipment_fail_one;
    /**
     * 出货失败：shipment_fail_three 料道故障或无货
     */
    public boolean shipment_fail_three;
    /**
     * 出货超时：shipment _fail_two 出货超时
     */
    public boolean shipment_fail_two;
    /**
     * 退款上限
     */
    public String refund_amount_ceiling;
    /**倒计时*/
    public TimeSetting vmc_count_down_time_settings;
    /**重启时间*/
    public ReBootTime vmc_reset_time_settings;
    /**
     * 是否有硬币器
     */
    public boolean hasCoinM;
    /**
     * 是否有纸币器
     */
    public boolean hasPaperM;

    /**
     * 后台是否在升级
     */
    public boolean is_upgrade;

    public PayMentWay payment_way;

    public OutWaterConfig outputwater;


    public ConfigInit() {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.check_distance);
        dest.writeString(this.customer_phone);
        dest.writeParcelable(this.vmc_count_down_time_settings, flags);
        dest.writeParcelable(this.vmc_reset_time_settings, flags);
        dest.writeParcelable(this.payment_way, flags);
        dest.writeParcelable(this.outputwater, flags);
        dest.writeString(this.img_url);
        dest.writeByte((byte)(alipay_refund ? 1 : 0));
        dest.writeByte((byte)(weixinpay_refund ? 1:0));
        dest.writeByte((byte)(shipment_fail_one ? 1:0));
        dest.writeByte((byte)(shipment_fail_two ? 1 : 0));
        dest.writeByte((byte)(shipment_fail_three ? 1 : 0));
        dest.writeString(this.refund_amount_ceiling);
        dest.writeByte((byte)(is_upgrade ? 1 : 0));
        dest.writeByte((byte) (hasCoinM ? 1: 0));
        dest.writeByte((byte)(hasPaperM ? 1: 0));
    }

    protected ConfigInit(Parcel in) {
        super(in);
        this.check_distance = in.readInt();
        this.customer_phone = in.readString();
        this.vmc_count_down_time_settings = in.readParcelable(TimeSetting.class.getClassLoader());
        this.vmc_reset_time_settings = in.readParcelable(ReBootTime.class.getClassLoader());
        this.payment_way = in.readParcelable(PayMentWay.class.getClassLoader());
        this.outputwater = in.readParcelable(OutWaterConfig.class.getClassLoader());
        this.img_url=in.readString();
        this.alipay_refund=in.readByte()!=0;
        this.weixinpay_refund=in.readByte()!=0;
        this.shipment_fail_one=in.readByte()!=0;
        this.shipment_fail_two=in.readByte()!=0;
        this.shipment_fail_three=in.readByte()!=0;
        this.refund_amount_ceiling=in.readString();
        this.is_upgrade = in.readByte() != 0;
        this.hasCoinM=in.readByte() != 0;
    }

    public static final Creator<ConfigInit> CREATOR = new Creator<ConfigInit>() {
        @Override
        public ConfigInit createFromParcel(Parcel source) {return new ConfigInit(source);}

        @Override
        public ConfigInit[] newArray(int size) {return new ConfigInit[size];}
    };
}