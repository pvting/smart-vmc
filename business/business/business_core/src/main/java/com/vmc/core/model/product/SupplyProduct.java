package com.vmc.core.model.product;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Create Date:</b>2017/6/5 09:12<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class SupplyProduct implements Parcelable {


    /**
     * price : 400
     * net_weight :
     * product_id : 6
     * category_name : 4
     * image_url : https://svmdemo03.hollywant.com/web/image/product.template/323/image/300x300?unique=da8547b
     * product_details_image_url :
     * box_no : 9
     * quantity : 12
     * stack_no : 29
     * type : 0
     */

    private  String name;



    private int price;
    private String net_weight;
    private int product_id;
    private String category_name;
    private String image_url;
    private String product_details_image_url;
    private String box_no;
    private int quantity;
    private String stack_no;
    private int type;
    private String  seq_no ;

    public String getSeq_no() {return seq_no;}

    public void setSeq_no(String seq_no) {this.seq_no = seq_no;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() { return price;}

    public void setPrice(int price) { this.price = price;}

    public String getNet_weight() { return net_weight;}

    public void setNet_weight(String net_weight) { this.net_weight = net_weight;}

    public int getProduct_id() { return product_id;}

    public void setProduct_id(int product_id) { this.product_id = product_id;}

    public String getCategory_name() { return category_name;}

    public void setCategory_name(String category_name) { this.category_name = category_name;}

    public String getImage_url() { return image_url;}

    public void setImage_url(String image_url) { this.image_url = image_url;}

    public String getProduct_details_image_url() { return product_details_image_url;}

    public void setProduct_details_image_url(String product_details_image_url) {
        this.product_details_image_url =
                product_details_image_url;
    }

    public String getBox_no() { return box_no;}

    public void setBox_no(String box_no) { this.box_no = box_no;}

    public int getQuantity() { return quantity;}

    public void setQuantity(int quantity) { this.quantity = quantity;}

    public String getStack_no() { return stack_no;}

    public void setStack_no(String stack_no) { this.stack_no = stack_no;}

    public int getType() { return type;}

    public void setType(int type) { this.type = type;}

    public SupplyProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.price);
        dest.writeString(this.net_weight);
        dest.writeInt(this.product_id);
        dest.writeString(this.category_name);
        dest.writeString(this.image_url);
        dest.writeString(this.product_details_image_url);
        dest.writeString(this.box_no);
        dest.writeInt(this.quantity);
        dest.writeString(this.stack_no);
        dest.writeInt(this.type);
        dest.writeString(this.seq_no);
    }

    protected SupplyProduct(Parcel in) {
        this.name = in.readString();
        this.price = in.readInt();
        this.net_weight = in.readString();
        this.product_id = in.readInt();
        this.category_name = in.readString();
        this.image_url = in.readString();
        this.product_details_image_url = in.readString();
        this.box_no = in.readString();
        this.quantity = in.readInt();
        this.stack_no = in.readString();
        this.type = in.readInt();
        this.seq_no = in.readString();
    }

    public static final Creator<SupplyProduct> CREATOR = new Creator<SupplyProduct>() {
        @Override
        public SupplyProduct createFromParcel(Parcel source) {return new SupplyProduct(source);}

        @Override
        public SupplyProduct[] newArray(int size) {return new SupplyProduct[size];}
    };
}