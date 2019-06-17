package vmc.machine.core.model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 9/2/16<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */
public class VmcState {
    //纸币器是否异常
    public boolean paperCashM;
    //硬币器状态是否异常
    public boolean coinCashM;

    //缺少5角
    public boolean isLeakChange5jiao;
    //缺少1元
    public boolean isLeakChange1yuan;


    public VmcState(
                    boolean paperCashM,
                    boolean coinCashM,
                    boolean isLeakChange5jiao,
                    boolean isLeakChange1yuan) {
        this.paperCashM = paperCashM;
        this.coinCashM = coinCashM;
        this.isLeakChange5jiao = isLeakChange5jiao;
        this.isLeakChange1yuan = isLeakChange1yuan;
    }
}
