package com.vmc.core.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 8/31/16<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */
public class Weather implements Parcelable {
    public String status;
    public Result result;

    protected Weather(Parcel in) {
        status = in.readString();
        result = in.readParcelable(Result.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeParcelable(result, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    public static class Result implements Parcelable {
        public float temperature;
        public String skycon;
        public float humidity;
        public float pm25;

        protected Result(Parcel in) {
            temperature = in.readFloat();
            skycon = in.readString();
            humidity = in.readFloat();
            pm25 = in.readFloat();
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel in) {
                return new Result(in);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(temperature);
            dest.writeString(skycon);
            dest.writeFloat(humidity);
            dest.writeFloat(pm25);
        }
    }
}



/*
    CLEAR_DAY：晴
    CLEAR_NIGHT：晴
    PARTLY_CLOUDY_DAY：多云
    PARTLY_CLOUDY_NIGHT：多云
    CLOUDY：阴
    RAIN： 雨
    SLEET：冻雨
    SNOW：雪
    WIND：风
    FOG：雾
    HAZE：霾
*/