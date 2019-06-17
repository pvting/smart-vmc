package vmc.vendor;


import java.util.Observable;

/**
 * <b>Create Date:</b> 2016/12/11<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class NetWorkObservable extends Observable {
    private static NetWorkObservable instance = new NetWorkObservable();
    private boolean isConnection = true;

    private NetWorkObservable() {}

    public static NetWorkObservable getInstance() {
        return instance;
    }

    public void setData(boolean isConnection) {
        this.isConnection = isConnection;
        setChanged();
        notifyObservers(this.isConnection);
    }
}
