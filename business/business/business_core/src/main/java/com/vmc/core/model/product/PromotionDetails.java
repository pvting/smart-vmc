package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

import java.util.ArrayList;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */

public class PromotionDetails extends Model {
    public int promotion_id;
    public String name;
    public String end_date;
    public String start_time;
    public String payment_way;
    public ArrayList<String> payment_option;
    public String promotion_type;
    public String start_date;
    public int promotion_price;
    public String end_time;
    public ArrayList<FreeBie> freebie;
    /** 商品促销弹窗图片链接*/
    public String promotional_image_links;
    public String promotion_time_type;
    public  String time_period_start;
    public  String time_period_end;



    public PromotionDetails(){

   }

    public int getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(int promotion_id) {
        this.promotion_id = promotion_id;
    }

    @Override
    public String toString() {
        return "PromotionDetails{" +
               "promotion_id=" + promotion_id +
               ", name='" + name + '\'' +
               ", end_date='" + end_date + '\'' +
               ", start_time='" + start_time + '\'' +
               ", payment_way='" + payment_way + '\'' +
               ", payment_option=" + payment_option +
               ", promotion_type='" + promotion_type + '\'' +
               ", start_date='" + start_date + '\'' +
               ", promotion_price=" + promotion_price +
               ", end_time='" + end_time + '\'' +
               ", freebie=" + freebie +
               ", promotional_image_links='" + promotional_image_links + '\'' +
               ", promotion_time_type='" + promotion_time_type + '\'' +
               ", time_period_start='" + time_period_start + '\'' +
               ", time_period_end='" + time_period_end + '\'' +
               "} " + super.toString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.promotion_id);
        dest.writeString(this.name);
        dest.writeString(this.end_date);
        dest.writeString(this.start_time);
        dest.writeString(this.payment_way);
        dest.writeStringList(this.payment_option);
        dest.writeString(this.promotion_type);
        dest.writeString(this.start_date);
        dest.writeInt(this.promotion_price);
        dest.writeString(this.end_time);
        dest.writeTypedList(this.freebie);
        dest.writeString(this.promotional_image_links);
        dest.writeString(this.promotion_time_type);
        dest.writeString(this.time_period_start);
        dest.writeString(this.time_period_end);
    }

    protected PromotionDetails(Parcel in) {
        super(in);
        this.promotion_id = in.readInt();
        this.name = in.readString();
        this.end_date = in.readString();
        this.start_time = in.readString();
        this.payment_way = in.readString();
        this.payment_option = in.createStringArrayList();
        this.promotion_type = in.readString();
        this.start_date = in.readString();
        this.promotion_price = in.readInt();
        this.end_time = in.readString();
        this.freebie = in.createTypedArrayList(FreeBie.CREATOR);
        this.promotional_image_links = in.readString();
        this.promotion_time_type = in.readString();
        this.time_period_start = in.readString();
        this.time_period_end = in.readString();
    }

    public static final Creator<PromotionDetails> CREATOR = new Creator<PromotionDetails>() {
        @Override
        public PromotionDetails createFromParcel(Parcel source) {return new PromotionDetails(source);}

        @Override
        public PromotionDetails[] newArray(int size) {return new PromotionDetails[size];}
    };
}
