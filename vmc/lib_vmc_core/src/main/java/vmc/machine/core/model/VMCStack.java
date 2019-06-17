package vmc.machine.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Create Date:</b>2017/2/20 09:40<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class VMCStack implements Parcelable {
    public String box_no;

    public String stack_no;

    public int quantity;

    public VMCStack() {}

    @Override
    public String toString() {
        return "VMCStack{" +
               "box_no='" + box_no + '\'' +
               ", stack_no='" + stack_no + '\'' +
               ", quantity=" + quantity +
               '}';
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
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.box_no);
        dest.writeString(this.stack_no);
        dest.writeInt(this.quantity);
    }

    protected VMCStack(Parcel in) {
        this.box_no = in.readString();
        this.stack_no = in.readString();
        this.quantity = in.readInt();
    }

    public static final Creator<VMCStack> CREATOR = new Creator<VMCStack>() {
        @Override
        public VMCStack createFromParcel(Parcel source) {return new VMCStack(source);}

        @Override
        public VMCStack[] newArray(int size) {return new VMCStack[size];}
    };
}