package vmc.machine.core.model;


/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 8/30/16<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */
public class VMCProductStock {
    //料道号
    public int roadId;
    //库存
    public int stock;

    public VMCProductStock(int roadId, int stock) {
        this.stock = stock;
        this.roadId = roadId;
    }
}
