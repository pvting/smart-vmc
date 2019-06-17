package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class Product extends Model implements Cloneable {
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    /**
     * 商品ID
     */
    public int id;
    /**
     * 货柜编号
     */
    public String box_no = "-1";
    /**
     * 货道编号
     */
    public String stack_no = "-1";
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
     * 商品库存
     */
    public int stock;
    /**
     * 商品序列号
     */
    public String seq_no;

    /**
     * 商品类型
     */
    public String product_type;
    /**
     * 商品促销信息
     */
    public PromotionDetails mPromotionDetail;

    public String net_weight;



    public Product() {
    }

    protected Product(Parcel in) {
        super(in);
        this.box_no = in.readString();
        this.stack_no = in.readString();
//        this.channel_no = in.readString();
        this.id = in.readInt();
        this.price = in.readInt();
        this.image_url = in.readString();
        this.name = in.readString();
        this.stock = in.readInt();
        this.seq_no = in.readString();
//        this.sales_promotion=in.readString();
        this.product_type = in.readString();
        this.product_details_image_url = in.readString();
        this.net_weight = in.readString();
        this.mPromotionDetail = in.readParcelable(PromotionDetails.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.box_no);
        dest.writeString(this.stack_no);
//        dest.writeString(this.channel_no);
        dest.writeInt(this.id);
        dest.writeInt(this.price);
        dest.writeString(this.image_url);
        dest.writeString(this.name);
        dest.writeInt(this.stock);
        dest.writeString(this.seq_no);
//        dest.writeString(this.sales_promotion);
        dest.writeString(this.product_type);
        dest.writeString(this.product_details_image_url);
        dest.writeString(this.net_weight);
        dest.writeParcelable(this.mPromotionDetail, 0);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", box_no='" + box_no + '\'' +
                ", stack_no='" + stack_no + '\'' +
                ", price=" + price +
                ", image_url='" + image_url + '\'' +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", seq_no='" + seq_no + '\'' +
                ", category_name='" + product_type + '\'' +
                ", mPromotionDetail=" + mPromotionDetail +
                ", net_weight='" + net_weight + '\'' +
                ", product_details_image_url='" + product_details_image_url + '\'' +
                "} " + super.toString();
    }

    /**
     * 获取货道ID
     *
     * @return 货道ID
     */
    public int getStackNoInt() {
        return Integer.valueOf(stack_no);
    }

    /**
     * 获取货柜ID
     *
     * @return 货柜ID
     */
    public int getBoxNoInt() {
        try {
            return Integer.valueOf(box_no);
        } catch (Exception e) {
            return -1;
        }
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
