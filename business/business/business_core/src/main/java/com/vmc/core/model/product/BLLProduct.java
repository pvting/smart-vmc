package com.vmc.core.model.product;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import vmc.core.log;

/**
 * <b>Create Date:</b>2017/2/8 13:56<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class BLLProduct implements Parcelable ,Cloneable{

    /**
     * 商品ID
     */
    public int product_id;

    /**
     * 商品价格, 以分为单位
     */
    public int price;
    /**
     * 商品图片链接
     */
    public String image_url;

    /**
     * 商品详情图片链接
     */
    public String product_details_image_url;

    /**
     * 商品名称
     */
    public String name;

    /**
     * 净含量
     */
    public String net_weight;

    /**
     * 商品类型
     */
    public String category_name;

    /**
     * 多个料道商品
     */
    public ArrayList<BLLStackProduct> mBLLStackProducts = new ArrayList<>();

    /**
     * 促销信息
     */
    public PromotionDetails mPromotionDetail;

    /**
     * 最小货柜号
     */
    public int fristBoxNo;

    /**
     * 最小料道号
     */
    public int fristStackNo;


    /**
     * 通过支付方式获取促销价格
     * @param payMethod
     * @return
     */
    public int getPromotionPirce(String payMethod) {
        int price = this.price;
        if (mPromotionDetail != null) {//如果是促销
            if (mPromotionDetail.promotion_type.equals("discount") ||
                mPromotionDetail.promotion_type.equals("unchange_count")) {
                if (mPromotionDetail.payment_option.contains(payMethod)) {
                    price = mPromotionDetail.promotion_price;
                }
            }
        }
        return price;
    }


    /**
     * 通过支付方式获取促销ID
     * @param payMethod
     * @return
     */
    public int getPromotionId(String payMethod) {
        int promotion_id = 0;
        if (mPromotionDetail != null) {//如果是促销
            if (mPromotionDetail.payment_option.contains(payMethod)) {
                promotion_id = mPromotionDetail.promotion_id;
            }
        }
        return promotion_id;
    }

    /**
     * 通过支付方式获取促销类型
     * @param payMethod
     * @return
     */
    public String getPromotionTypeByPayment(String payMethod) {
        String promotion_type = null;
        if (mPromotionDetail != null) {//如果是促销
            if (mPromotionDetail.payment_option.contains(payMethod)) {
                promotion_type = mPromotionDetail.promotion_type;
            }
        }
        return promotion_type;
    }



    public BLLProduct() {}

    @Override
    public String toString() {
        return "BLLProduct{" +
               "product_id=" + product_id +
               ", price=" + price +
               ", image_url='" + image_url + '\'' +
               ", product_details_image_url='" + product_details_image_url + '\'' +
               ", name='" + name + '\'' +
//               ", quantity=" + quantity +
               ", net_weight='" + net_weight + '\'' +
               ", category_name='" + category_name + '\'' +
               ", mBLLStackProducts=" + mBLLStackProducts +
               ", mPromotionDetail=" + mPromotionDetail +
               ", fristBoxNo='" + fristBoxNo + '\'' +
               ", fristStackNo='" + fristStackNo + '\'' +
               '}';
    }


    /**
     * 获取最小料道号
     * @return
     */
    public int getFristStackNoInt() {
        String str = ("00" + fristStackNo);
        return Integer.parseInt(fristBoxNo + str.substring(str.length() - 2, str.length()));

    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.product_id);
        dest.writeInt(this.price);
        dest.writeString(this.image_url);
        dest.writeString(this.product_details_image_url);
        dest.writeString(this.name);
        dest.writeString(this.net_weight);
        dest.writeString(this.category_name);
        dest.writeTypedList(this.mBLLStackProducts);
        dest.writeParcelable(this.mPromotionDetail, flags);
        dest.writeInt(this.fristBoxNo);
        dest.writeInt(this.fristStackNo);
    }

    protected BLLProduct(Parcel in) {
        this.product_id = in.readInt();
        this.price = in.readInt();
        this.image_url = in.readString();
        this.product_details_image_url = in.readString();
        this.name = in.readString();
        this.net_weight = in.readString();
        this.category_name = in.readString();
        this.mBLLStackProducts = in.createTypedArrayList(BLLStackProduct.CREATOR);
        this.mPromotionDetail = in.readParcelable(PromotionDetails.class.getClassLoader());
        this.fristBoxNo = in.readInt();
        this.fristStackNo = in.readInt();
    }

    public static final Creator<BLLProduct> CREATOR = new Creator<BLLProduct>() {
        @Override
        public BLLProduct createFromParcel(Parcel source) {return new BLLProduct(source);}

        @Override
        public BLLProduct[] newArray(int size) {return new BLLProduct[size];}
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    /**
     * 获取净含量
     * @return
     */
    public double getWegiht() {
        try {
            return Double.valueOf(net_weight);
        } catch (Exception e) {
            log.e("BLLStackProduct", "净含量数据错误：" + net_weight);
            return 1.00D;
        }
    }
}