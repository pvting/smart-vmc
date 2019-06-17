package vmc.machine.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/25<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */

public class VmcRunStates implements Parcelable {


    //温度
    public List<Integer> temperature;

    //灯状态
    public String lightState;

    //门状态
    public boolean isDoorOpened;

    //缺币(5角)
    public boolean isLackOf50Cent;

    //缺币(1元)
    public boolean isLackOf100Cent;

    //停止销售
    public boolean isSaleStop;

    //主控和工控的通信异常
    public boolean isVMCDisconnected;

    //硬币器异常
    public boolean isCoinMError;

    //纸币器异常
    public boolean isPaperMError;

    //主控机异常
    public boolean isMasterError;

    //工控机运行异常
    public boolean isVmcError;



    //货道为空
    public boolean isSoldOut;

    //主控版本
    public String  majorVersionId;

    //售卖版本号
    public String  saleAppVersionId;


    //补货管理版本号
    public String maintainAppVersionId;

    //IMEI
    public String vmc_code;

    //sim卡
    public String sim_code ;


    public String soldOutStockId;

    public String breakdownStockId;





    public VmcRunStates() {

    }

    @Override
    public String toString() {
        return "VmcRunStates{" +
               "temperature=" + temperature +
               ", lightState='" + lightState + '\'' +
               ", isDoorOpened=" + isDoorOpened +
               ", isLackOf50Cent=" + isLackOf50Cent +
               ", isLackOf100Cent=" + isLackOf100Cent +
               ", isSaleStop=" + isSaleStop +
               ", isVMCDisconnected=" + isVMCDisconnected +
               ", isCoinMError=" + isCoinMError +
               ", isPaperMError=" + isPaperMError +
               ", isMasterError=" + isMasterError +
               ", isVmcError=" + isVmcError +
               ", isSoldOut=" + isSoldOut +
               ", majorVersionId='" + majorVersionId + '\'' +
               ", saleAppVersionId='" + saleAppVersionId + '\'' +
               ", maintainAppVersionId='" + maintainAppVersionId + '\'' +
               ", soldOutStockId=" + soldOutStockId +
               ", breakdownStockId=" + breakdownStockId +
               '}';
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.temperature);
        dest.writeString(this.lightState);
        dest.writeByte(this.isDoorOpened ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLackOf50Cent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLackOf100Cent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSaleStop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVMCDisconnected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCoinMError ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPaperMError ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMasterError ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVmcError ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSoldOut ? (byte) 1 : (byte) 0);
        dest.writeString(this.majorVersionId);
        dest.writeString(this.saleAppVersionId);
        dest.writeString(this.maintainAppVersionId);
        dest.writeString(this.vmc_code);
        dest.writeString(this.sim_code);
        dest.writeString(this.soldOutStockId);
        dest.writeString(this.breakdownStockId);
    }

    protected VmcRunStates(Parcel in) {
        this.temperature = new ArrayList<Integer>();
        in.readList(this.temperature, Integer.class.getClassLoader());
        this.lightState = in.readString();
        this.isDoorOpened = in.readByte() != 0;
        this.isLackOf50Cent = in.readByte() != 0;
        this.isLackOf100Cent = in.readByte() != 0;
        this.isSaleStop = in.readByte() != 0;
        this.isVMCDisconnected = in.readByte() != 0;
        this.isCoinMError = in.readByte() != 0;
        this.isPaperMError = in.readByte() != 0;
        this.isMasterError = in.readByte() != 0;
        this.isVmcError = in.readByte() != 0;
        this.isSoldOut = in.readByte() != 0;
        this.majorVersionId = in.readString();
        this.saleAppVersionId = in.readString();
        this.maintainAppVersionId = in.readString();
        this.vmc_code = in.readString();
        this.sim_code = in.readString();
        this.soldOutStockId = in.readString();
        this.breakdownStockId = in.readString();
    }

    public static final Creator<VmcRunStates> CREATOR = new Creator<VmcRunStates>() {
        @Override
        public VmcRunStates createFromParcel(Parcel source) {return new VmcRunStates(source);}

        @Override
        public VmcRunStates[] newArray(int size) {return new VmcRunStates[size];}
    };
}
