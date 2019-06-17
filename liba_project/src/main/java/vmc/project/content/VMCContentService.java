package vmc.project.content;

import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.vmc.core.BLLController;
import com.want.vmc.core.PService;

import java.util.ArrayList;
import java.util.List;

import vmc.core.log;
import vmc.machine.core.VMCContoller;
import vmc.project.content.bean.VMCStatus;
import vmc.project.content.bean.VmcState;

/**
 * <b>Create Date:</b> 10/9/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * VMC内容提供
 * <br>
 */
public class VMCContentService extends PService {
    private static final String TAG = "VMCContentService";
    public static final String ACTION = "vmc.project.content.ACTION_CONTENT";

    private VMCContoller mVMCContoller;

    private final IVMCContentService.Stub mBinder = new IVMCContentService.Stub() {
        @Override
        public List<VMCStatus> getStatus() throws RemoteException {
            try{
                // TODO: 10/9/16 完善VMC状态的获取
                String state = BLLController.getInstance().getVmcRunningStates();
                if(TextUtils.isEmpty(state)){
                    return null;
                }
                VmcState vmcState = new Gson().fromJson(state,VmcState.class);
                if(vmcState==null){
                    return null;
                }
                List<VMCStatus> statuses = new ArrayList<>();
                statuses.add(new VMCStatus("isDoorOpened",vmcState.isDoorOpened));
                statuses.add(new VMCStatus("isLackOf50Cent",vmcState.isLackOf50Cent));
                statuses.add(new VMCStatus("isLackOf100Cent",vmcState.isLackOf100Cent));
                statuses.add(new VMCStatus("isSoldOut",vmcState.isSoldOut));
                statuses.add(new VMCStatus("isVMCDisconnected",vmcState.isVMCDisconnected));
                log.d(TAG, "getStatus: status=" + state);
                return statuses;
            }catch (Exception e){
               e.printStackTrace();
                return null;
            }
        }

        @Override
        public VMCStatus getStatusByKey(String key) throws RemoteException {
            // TODO: 10/9/16 根据key值获取制定的状态
            return null;
        }

        @Override
        public String getFactoryCode() throws RemoteException {
            return VMCContoller.getInstance().getVendingMachineId();
        }

        @Override
        public String getMachineType() throws RemoteException {
            return VMCContoller.getInstance().getBrand();
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            // TODO: 10/9/16 做权限校验
            return super.onTransact(code, data, reply, flags);
        }





    };

    @Override
    public void onCreate() {
        super.onCreate();
        log.d(TAG, "onCreate: ");
        mVMCContoller = VMCContoller.getInstance();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        log.d(TAG, "onBind: intent=" + intent.toString());
        return mBinder;
    }
}
