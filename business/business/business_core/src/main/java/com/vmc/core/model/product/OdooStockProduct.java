package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class OdooStockProduct extends Model{


    /**
     * seq_no : 1F
     * id : 34056
     * stock : 10
     * stock_supplied : 10
     * supply_type : 换货
     * price : 0.10
     * image_url : http://vmc.hollywant.com:88/web/image/product.template/6777/image/300x300?unique=93e808c
     * name : 0泡果奶草莓味1.5L
     * net_weight :
     * product_details_image_url : https://127.0.0.1:8069/web/image/470
     * product_type : 缤纷小食
     */

    public String seq_no;
    public int id;
    public int stock;
    public int stock_supplied;
    public String supply_type;
    public int price;
    public String image_url;
    public String name;
    public String net_weight;
    public String product_details_image_url;
    public String product_type;

    public String getSeq_no() { return seq_no;}

    public void setSeq_no(String seq_no) { this.seq_no = seq_no;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public int getStock() { return stock;}

    public void setStock(int stock) { this.stock = stock;}

    public int getStock_supplied() { return stock_supplied;}

    public void setStock_supplied(int stock_supplied) { this.stock_supplied = stock_supplied;}

    public String getSupply_type() { return supply_type;}

    public void setSupply_type(String supply_type) { this.supply_type = supply_type;}


    public String getImage_url() { return image_url;}

    public void setImage_url(String image_url) { this.image_url = image_url;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getNet_weight() { return net_weight;}

    public void setNet_weight(String net_weight) { this.net_weight = net_weight;}

    public String getProduct_details_image_url() { return product_details_image_url;}

    public void setProduct_details_image_url(String product_details_image_url) {
        this.product_details_image_url =
                product_details_image_url;
    }

    public String getProduct_type() { return product_type;}

    public void setProduct_type(String product_type) { this.product_type = product_type;}

    @Override
    public String toString() {
        return "OdooStockProduct{" +
               "seq_no='" + seq_no + '\'' +
               ", id=" + id +
               ", stock=" + stock +
               ", stock_supplied=" + stock_supplied +
               ", supply_type='" + supply_type + '\'' +
               ", price='" + price + '\'' +
               ", image_url='" + image_url + '\'' +
               ", name='" + name + '\'' +
               ", net_weight='" + net_weight + '\'' +
               ", product_details_image_url='" + product_details_image_url + '\'' +
               ", product_type='" + product_type + '\'' +
               "} " + super.toString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.seq_no);
        dest.writeInt(this.id);
        dest.writeInt(this.stock);
        dest.writeInt(this.stock_supplied);
        dest.writeString(this.supply_type);
        dest.writeInt(this.price);
        dest.writeString(this.image_url);
        dest.writeString(this.name);
        dest.writeString(this.net_weight);
        dest.writeString(this.product_details_image_url);
        dest.writeString(this.product_type);
    }

    public OdooStockProduct() {}

    protected OdooStockProduct(Parcel in) {
        super(in);
        this.seq_no = in.readString();
        this.id = in.readInt();
        this.stock = in.readInt();
        this.stock_supplied = in.readInt();
        this.supply_type = in.readString();
        this.price = in.readInt();
        this.image_url = in.readString();
        this.name = in.readString();
        this.net_weight = in.readString();
        this.product_details_image_url = in.readString();
        this.product_type = in.readString();
    }

    public static final Creator<OdooStockProduct> CREATOR = new Creator<OdooStockProduct>() {
        @Override
        public OdooStockProduct createFromParcel(Parcel source) {return new OdooStockProduct(source);}

        @Override
        public OdooStockProduct[] newArray(int size) {return new OdooStockProduct[size];}
    };
}