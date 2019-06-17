package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/13<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 货道同步数据模型
 */

public class StackSyncList extends OdooList<OdooStackSync> {

    public int sync_id;

    public StackSyncList() {
    }

    protected StackSyncList(Parcel in) {
        super(in);
        this.records = in.createTypedArrayList(OdooStackSync.CREATOR);
        this.sync_id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(sync_id);
    }

    public static final Creator<StackSyncList> CREATOR = new Creator<StackSyncList>() {
        @Override
        public StackSyncList createFromParcel(Parcel source) {
            return new StackSyncList(source);
        }

        @Override
        public StackSyncList[] newArray(int size) {
            return new StackSyncList[size];
        }
    };
}
