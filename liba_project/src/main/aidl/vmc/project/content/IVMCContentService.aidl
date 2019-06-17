// IVMCContentService.aidl
package vmc.project.content;

import vmc.project.content.bean.VMCStatus;

// Declare any non-default types here with import statements

interface IVMCContentService {

    /**
     * 获取VMC的所有状态
     */
    List<VMCStatus> getStatus();

    /**
     * 获取指定Key值的状态
     */
    VMCStatus getStatusByKey(in String key);



     /**
      * 获取机器编号
      */
    String getFactoryCode();


     /**
      * 获取机器类型
      */
    String getMachineType();

}
