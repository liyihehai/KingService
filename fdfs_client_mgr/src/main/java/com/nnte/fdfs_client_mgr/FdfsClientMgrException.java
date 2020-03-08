package com.nnte.fdfs_client_mgr;

public class FdfsClientMgrException extends Exception{
    public static String Fdfs_Client_Mgr_Exception = "Fdfs-Client-Mgr异常";
    public FdfsClientMgrException(){
        super(Fdfs_Client_Mgr_Exception);
    }
    public FdfsClientMgrException(String errMsg){
        super(Fdfs_Client_Mgr_Exception+":"+errMsg);
    }
}
