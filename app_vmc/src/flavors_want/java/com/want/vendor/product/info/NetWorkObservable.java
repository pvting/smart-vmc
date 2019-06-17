package com.want.vendor.product.info;


import java.util.Observable;

/**
 * <b>Create Date:</b> 2016/12/11<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class NetWorkObservable extends Observable {
    private static volatile NetWorkObservable mInstance;
    private boolean isConnection = true;

    private NetWorkObservable() {}

    public static NetWorkObservable getInstance() {
        if (mInstance == null) {
            synchronized (NetWorkObservable.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkObservable();
                }
            }
        }
        return mInstance;
    }


    public void setData(boolean isConnection) {
        this.isConnection = isConnection;
        setChanged();
        notifyObservers(this.isConnection);
    }
}
