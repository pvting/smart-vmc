package com.vmc.core.model.product;

import android.os.Parcel;
import android.os.Parcelable;

import vmc.core.log;

/**
 * <b>Create Date:</b>2017/2/8 13:54<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b>商品基本单元<br>
 */
public class BLLStackProduct implements Parcelable,Cloneable {
    /**
     * 商品ID
     */
    public int product_id;

    /**
     * 货柜编号
     */
    public int box_no = -1;

    /**
     * 货道编号
     */
    public int stack_no = -1;


    /**
     * 货道编号
     */
    public String origin_stack_no = "-1";


    /**
     * 商品价格, 以分为单位
     */
    public int price;
    /**
     * 商品图片链接
     */
    public String image_url;
    /**
     * 商品名称
     */
    public String name;
    /**
     * 商品库存
     */
    public int quantity;
    /**
     * 商品序列号
     */
    public String seq_no;
    /**
     * 商品类型
     */
    public String category_name;

    /**
     * 净含量
     */
    public String net_weight;

    public boolean saleTag  = true;


    /**
     * 商品详情图片链接
     */
    public String product_details_image_url;

    public BLLStackProduct() {
    }


    @Override
    public String toString() {
        return "BLLStackProduct{" +
               "product_id=" + product_id +
               ", box_no=" + box_no +
               ", stack_no=" + stack_no +
               ", origin_stack_no='" + origin_stack_no + '\'' +
               ", price=" + price +
               ", image_url='" + image_url + '\'' +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", seq_no='" + seq_no + '\'' +
               ", category_name='" + category_name + '\'' +
               ", net_weight='" + net_weight + '\'' +
               ", saleTag=" + saleTag +
               ", product_details_image_url='" + product_details_image_url + '\'' +
               '}';
    }

    /**
     * 获取货道ID
     *
     * @return 货道ID
     */
    public int getStackNoInt() {
        try{
            return Integer.valueOf(stack_no);
        }catch (Exception e){
            log.e("BLLStackProduct", "货道数据错误：" + net_weight);
            return -1;
        }

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
            log.e("BLLStackProduct", "货柜数据错误：" + net_weight);
            return -1;
        }
    }


    public double getWegiht() {
        try {
            return Double.valueOf(net_weight);
        } catch (Exception e) {
            log.e("BLLStackProduct", "净含量数据错误：" + net_weight);
            return 1.00D;
        }
    }




    @Override
    public BLLStackProduct clone() throws CloneNotSupportedException {
        return (BLLStackProduct) super.clone();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.product_id);
        dest.writeInt(this.box_no);
        dest.writeInt(this.stack_no);
        dest.writeString(this.origin_stack_no);
        dest.writeInt(this.price);
        dest.writeString(this.image_url);
        dest.writeString(this.name);
        dest.writeInt(this.quantity);
        dest.writeString(this.seq_no);
        dest.writeString(this.category_name);
        dest.writeString(this.net_weight);
        dest.writeByte(this.saleTag ? (byte) 1 : (byte) 0);
        dest.writeString(this.product_details_image_url);
    }

    protected BLLStackProduct(Parcel in) {
        this.product_id = in.readInt();
        this.box_no = in.readInt();
        this.stack_no = in.readInt();
        this.origin_stack_no = in.readString();
        this.price = in.readInt();
        this.image_url = in.readString();
        this.name = in.readString();
        this.quantity = in.readInt();
        this.seq_no = in.readString();
        this.category_name = in.readString();
        this.net_weight = in.readString();
        this.saleTag = in.readByte() != 0;
        this.product_details_image_url = in.readString();
    }

    public static final Creator<BLLStackProduct> CREATOR = new Creator<BLLStackProduct>() {
        @Override
        public BLLStackProduct createFromParcel(Parcel source) {return new BLLStackProduct(source);}

        @Override
        public BLLStackProduct[] newArray(int size) {return new BLLStackProduct[size];}
    };
}