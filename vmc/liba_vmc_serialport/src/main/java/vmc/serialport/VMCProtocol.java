package vmc.serialport;

import java.util.LinkedList;

/**
 * <b>Create Date:</b> 9/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public abstract class VMCProtocol implements IVMCProtocol {

    private LinkedList<String> mSendDataList;
    private ISerialPortController mController;


    public VMCProtocol() {
        this.mSendDataList = new LinkedList<>();
    }

    @Override
    public final ISerialPortController getSerialPortController() {
        return this.mController;
    }

    @Override
    public final void setSerialPortController(ISerialPortController controller) {
        this.mController = controller;
    }

}
