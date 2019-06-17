package com.vmc.core.model.order;

import android.os.Parcel;

import com.vmc.core.model.Model;
import com.vmc.core.model.product.BLLStackProduct;

import org.json.JSONException;
import org.json.JSONObject;

import vmc.core.log;

/**
 * <b>Create Date:</b> 8/26/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class Order extends Model {
    private static final String TAG = "Order";

    public enum Payment {
        /** 未指定 */
        NONE("NONE"),
        /** 支付宝 */
        ALIPAY("ALIPAY"),
        /** 微信 */
        WECHATPAY("WECHATPAY"),
        /** 人民币 */
        RMB("RMB"),
        /** 旺币 */
        WANGBI("WANGBI"),
        /** 提货码 */
        CODE("CODE"),
        /** 卡支付 */
        CARD("CARD_WATERGOD");

        String payment;

        Payment(String payment) {
            this.payment = payment;
        }

        public String getPayment() {
            return this.payment;
        }

        public static Payment paymentOf(String payment) {
            if (ALIPAY.payment.equals(payment)) {
                return ALIPAY;
            } else if (WECHATPAY.payment.equals(payment)) {
                return WECHATPAY;
            } else if (RMB.payment.equals(payment)) {
                return RMB;
            } else if (WANGBI.payment.equals(payment)) {
                return WANGBI;
            } else if (CODE.payment.equals(payment)) {
                return CODE;
            }

            return NONE;
        }
    }

    public enum PayStatus {
        /** 已支付 */
        PAID("PAID"),
        /** 未支付 */
        UNPAY("UNPAY");

        private String status;

        PayStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public static PayStatus statusOf(String status) {
            if (PAID.status.equals(status)) {
                return PAID;
            } else if (UNPAY.status.equals(status)) {
                return UNPAY;
            }
            return UNPAY;
        }
    }

    public enum Status {
        /** 已创建 */
        CREATED("CREATED"),
        /** 用户取消 */
        CANCEL("CANCEL"),
        /** 已支付 */
        PAID("PAID"),
        /** 已完成 */
        FINISHED("FINISHED");

        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public static Status statusOf(String status) {
            if (CREATED.status.equals(status)) {
                return CREATED;
            } else if (CANCEL.status.equals(status)) {
                return CANCEL;
            } else if (PAID.status.equals(status)) {
                return PAID;
            } else if (FINISHED.status.equals(status)) {
                return FINISHED;
            }
            return CREATED;
        }
    }

    private BLLStackProduct product;

    public BLLStackProduct getProduct() {
        return product;
    }

    public void setProduct(BLLStackProduct product) {
        this.product = product;
    }

    public String payment_method = Payment.NONE.getPayment();
    public String payment_status = PayStatus.UNPAY.getStatus();
    public String status = Status.CREATED.getStatus();
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String id;

    //订单创建时间
    public String create_time;
    //促销规则id
    public long promotion_id;
    //促销产品促销id
    public String promotion_stack_no = "-1";
    public String promotion_box_no = "-1";

    public String shipping_status = "";

    public String promotion_shipping_status = "";

    public int error_code = 0;

    public int promotion_error_code = 0;

    public String pick_good_code ="";



    @Override
    public String toString() {
        return "Order{" +
               "product=" + product +
               ", payment_method='" + payment_method + '\'' +
               ", payment_status='" + payment_status + '\'' +
               ", status='" + status + '\'' +
               ", amount=" + amount +
               ", id='" + id + '\'' +
               ", create_time='" + create_time + '\'' +
               ", promotion_id=" + promotion_id +
               ", promotion_stack_no='" + promotion_stack_no + '\'' +
               ", promotion_box_no='" + promotion_box_no + '\'' +
               ", shipping_status='" + shipping_status + '\'' +
               ", promotion_shipping_status='" + promotion_shipping_status + '\'' +
               ", error_code=" + error_code +
               ", promotion_error_code=" + promotion_error_code +
               ", sub_product_stock='" + sub_product_stock + '\'' +
               ", sub_gift_stock='" + sub_gift_stock + '\'' +
               "} " + super.toString();
    }

    public String sub_product_stock = "";

    public String sub_gift_stock = "";


    public Order() {

    }

    public JSONObject toJson() {
        JSONObject productJson = new JSONObject();

        optput(productJson, "name", product.name);
        optput(productJson, "product_id", product.product_id);
        optput(productJson, "stack_no", product.stack_no);
        optput(productJson, "origin_stack_no", product.origin_stack_no);
        optput(productJson, "box_no", product.box_no);

        JSONObject json = new JSONObject();

        optput(json, "product", productJson);
        optput(json, "payment_method", payment_method);
        optput(json, "payment_status", payment_status);
        optput(json, "status", status);
        optput(json, "amount", amount);
        optput(json, "id", id);
        optput(json, "create_time", create_time);
        optput(json, "promotion_id", promotion_id);
        optput(json, "promotion_stack_no", promotion_stack_no);
        optput(json, "promotion_box_no", promotion_box_no);
        optput(json, "shipping_status", shipping_status);
        optput(json, "error_code", error_code);
        optput(json, "promotion_error_code", promotion_error_code);
        optput(json, "pick_good_code", pick_good_code);


        optput(json, "promotion_shipping_status", promotion_shipping_status);

        if (sub_product_stock != null) {
            optput(json, "sub_product_stock", sub_product_stock);

        }

        if (sub_gift_stock != null) {
            optput(json, "sub_gift_stock", sub_gift_stock);
        }

        return json;
    }

    private void optput(JSONObject json, String key, Object value) {
        try {
            json.putOpt(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.product, flags);
        dest.writeString(this.payment_method);
        dest.writeString(this.payment_status);
        dest.writeString(this.status);
        dest.writeInt(this.amount);
        dest.writeString(this.id);
        dest.writeString(this.create_time);
        dest.writeLong(this.promotion_id);
        dest.writeString(this.promotion_stack_no);
        dest.writeString(this.promotion_box_no);
        dest.writeString(this.shipping_status);
        dest.writeString(this.promotion_shipping_status);
        dest.writeInt(this.error_code);
        dest.writeInt(this.promotion_error_code);
        dest.writeString(this.pick_good_code);
        dest.writeString(this.sub_product_stock);
        dest.writeString(this.sub_gift_stock);
    }

    protected Order(Parcel in) {
        super(in);
        this.product = in.readParcelable(BLLStackProduct.class.getClassLoader());
        this.payment_method = in.readString();
        this.payment_status = in.readString();
        this.status = in.readString();
        this.amount = in.readInt();
        this.id = in.readString();
        this.create_time = in.readString();
        this.promotion_id = in.readLong();
        this.promotion_stack_no = in.readString();
        this.promotion_box_no = in.readString();
        this.shipping_status = in.readString();
        this.promotion_shipping_status = in.readString();
        this.error_code = in.readInt();
        this.promotion_error_code = in.readInt();
        this.pick_good_code = in.readString();
        this.sub_product_stock = in.readString();
        this.sub_gift_stock = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {return new Order(source);}

        @Override
        public Order[] newArray(int size) {return new Order[size];}
    };
}
