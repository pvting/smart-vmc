package com.vmc.core.model.ads;

import android.os.Parcel;

import com.vmc.core.model.Model;

import org.json.JSONObject;

/**
 * <b>Create Date:</b> 8/25/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class Ads extends Model implements Comparable<Ads> {

    public enum AdType {

        IMAGE("IMAGE"),
        VIDEO("VIDEO");

        private String adType;

        AdType(String adtype) {
            this.adType = adtype;
        }

        public static AdType adTypeOf(String adType) {
            if (IMAGE.adType.equals(adType)) {
                return IMAGE;
            } else if (VIDEO.adType.equals(adType)) {
                return VIDEO;
            }
            return IMAGE;
        }

        public String getAdType() {
            return adType;
        }

    }
    public String ad_detail;
    public String ad_url;
    public String ad_type;
    /** 广告次序, 顺序排序 */
    public int ad_order;



    public Ads() {}

    protected Ads(Parcel in) {
        super(in);
        this.ad_detail =in.readString();
        this.ad_url = in.readString();
        this.ad_type = in.readString();
        this.ad_order = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.ad_detail);
        dest.writeString(this.ad_url);
        dest.writeString(this.ad_type);
        dest.writeInt(this.ad_order);
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();

        putopt(object,"ad_detail",ad_detail);

        putopt(object, "ad_type", ad_type);
        putopt(object, "ad_url", ad_url);
        putopt(object, "ad_order", ad_order);
        return object;
    }

    @Override
    public int compareTo(Ads another) {
        return this.ad_order - another.ad_order;
    }

    public static final Creator<Ads> CREATOR = new Creator<Ads>() {
        @Override
        public Ads createFromParcel(Parcel source) {return new Ads(source);}

        @Override
        public Ads[] newArray(int size) {return new Ads[size];}
    };

    @Override
    public String toString() {
        return "Ads{" +
               "ad_detail='" + ad_detail + '\'' +
               ", ad_url='" + ad_url + '\'' +
               ", ad_type='" + ad_type + '\'' +
               ", ad_order=" + ad_order +
               "} " + super.toString();
    }
}
