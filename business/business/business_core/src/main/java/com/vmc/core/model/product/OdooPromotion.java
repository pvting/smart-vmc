package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2017/2/8 14:19<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class OdooPromotion extends Model {

    /**
     * id : 34190
     * name : 180ml火咖
     * mPromotionDetail : {"promotional_image_links":"http://gweiudkg/jdkhk/hjkgdf","name":"fbg","promotion_id":1,"promotion_time_type":"every_day","start_date":"2016-11-19","end_date":"2016-11-23","start_time":"14:00:00","end_time":"15:00:00","time_period_start":"","time_period_end":"","payment_way":"0000","payment_option":["ALIPAY","WECHATPAY","RMB","WANGBI"],"promotion_type":"one_more","promotion_price":0,"freebie":[{"name":"180ml火咖","quantity":1,"id":34192},{"name":"180g 大礼包","quantity":1,"id":34193}]}
     */

    public int product_id;
    public String name;
    public PromotionDetails promotion_details;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.product_id);
        dest.writeString(this.name);
        dest.writeParcelable(this.promotion_details, flags);
    }

    public OdooPromotion() {}

    protected OdooPromotion(Parcel in) {
        super(in);
        this.product_id = in.readInt();
        this.name = in.readString();
        this.promotion_details = in.readParcelable(PromotionDetails.class.getClassLoader());
    }

    public static final Creator<OdooPromotion> CREATOR = new Creator<OdooPromotion>() {
        @Override
        public OdooPromotion createFromParcel(Parcel source) {return new OdooPromotion(source);}

        @Override
        public OdooPromotion[] newArray(int size) {return new OdooPromotion[size];}
    };

    @Override
    public String toString() {
        return "OdooPromotion{" +
               "product_id=" + product_id +
               ", name='" + name + '\'' +
               ", promotion_details=" + promotion_details +
               "} " + super.toString();
    }
}