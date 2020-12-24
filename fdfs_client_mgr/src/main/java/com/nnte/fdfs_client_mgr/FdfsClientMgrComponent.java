package com.nnte.fdfs_client_mgr;

import com.nnte.basebusi.base.BaseBusiComponent;
import com.nnte.basebusi.excption.BusiException;
import com.nnte.basebusi.excption.ExpLogInterface;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.SpringContextHolder;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.PropertiesUtil;
import com.nnte.framework.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FdfsClientMgrComponent extends BaseBusiComponent {

    private static Map<String,String> typeGroupMap = new HashMap<>();
    private static String DEFAULT_GROUP_NAME="group1";
    private static final String RETRY_EXCEPTION_MSG = "recv package size -1";

    private static FdfsConns g_FdfsConns;

    @Getter @Setter
    public static class FdfsConns{
        private String confFile;
        private TrackerClient trackerClient=null;
        private TrackerServer trackerServer=null;

        /**
         * 创建StorageClient
         */
        public synchronized StorageClient createStorageClient() throws Exception{
            if (!isConnect())
                throw new Exception("trackerServer is invalid!");
            StorageClient storageClient = new StorageClient(trackerServer, null);
            if (storageClient.isAvaliable() && storageClient.isConnected())
                return storageClient;
            throw new Exception("storageClient is invalid!");
        }
        /**
         * 断开StorageClient
         * */
        private static void closeStorageClient(StorageClient storageClient){
            if (storageClient!=null) {
                try {
                    storageClient.close();
                } catch (IOException e) { }
            }
        }
        /**
         * 服务器端连接是否正常
         * */
        public boolean isConnect() throws Exception{
            if (trackerServer!=null && trackerServer.getConnection().isAvaliable()
                    && trackerServer.getConnection().isConnected()) {
                return true;
            }
            return false;
        }

        public void closeConn(ExpLogInterface log){
            try {
                if (trackerServer!=null) {
                    trackerServer.getConnection().close();
                    trackerServer=null;
                }
                if (trackerClient!=null)
                    trackerClient=null;
            } catch (Exception e) {
                log.logException(new BusiException(e,3999, BusiException.ExpLevel.ERROR));
            }
        }

        public static String[] uploadFile(StorageClient storageClient, String group, byte[] content,
                                         String extName, NameValuePair[] meta_list) throws IOException, MyException {
            try {
                return storageClient.upload_file(group, content, extName, meta_list);
            }catch (IOException ie){
                if (BaseNnte.getExpMsg(ie).indexOf(RETRY_EXCEPTION_MSG)>=0)
                    return storageClient.upload_file(group, content, extName, meta_list);
            }
            return null;
        }
        public static int deleteFile(StorageClient storageClient,String group,String fileName)
                throws IOException, MyException{
            try {
                return storageClient.delete_file(group,fileName);
            }catch (IOException ie){
                if (BaseNnte.getExpMsg(ie).indexOf(RETRY_EXCEPTION_MSG)>=0)
                    return storageClient.delete_file(group,fileName);
            }
            return 0;
        }
    }

    /*判断连接是否可用，如果不可用，则尝试重连服务端*/
    public synchronized boolean isServerConnect() throws Exception{
        if (g_FdfsConns==null)
            g_FdfsConns = connectServer();
        if (activeTest())
            return true;
        reConnServer(g_FdfsConns);
        return g_FdfsConns.isConnect();
    }

    /*
    * 启动管理初始化
    * */
    public void runFdfsClientMgr(String[] args){
        initGroupMap();
        g_FdfsConns = connectServer();
    }
    private void initGroupMap(){
        try {
            FdfsClientMgrConfig fdfsClientMgrConf= SpringContextHolder.getBean("fdfsClientMgrConfig");
            if (fdfsClientMgrConf == null)
                throw new BusiException("未取得FdfsClientMgrConf实例");
            Integer gc=NumberUtil.getDefaultInteger(fdfsClientMgrConf.getGroupcount());
            if (gc<=0)
                throw new BusiException("没有配置文件上传的group的数量");
            Map<String,String> propMap=PropertiesUtil.getPropertyMap("application.properties");
            if (propMap==null || propMap.size()<=0)
                throw new BusiException("没有配置文件的group的配置项");
            for(Integer i=1;i<=gc;i++){
                String groupName="group"+i;
                String propName="nnte.ks.fdfsclientmgr."+groupName;
                String propVal=propMap.get(propName);
                String[] types=propVal.split(",");
                if (types!=null && types.length>0){
                    for(String type:types)
                        typeGroupMap.put(type,groupName);
                }
            }
        } catch (BusiException be) {
            this.logException(be);
        } catch (Exception e){
            BusiException be = new BusiException(e);
            this.logException(be);
        }
    }
    /*
    * 重连FastDFS服务器
    * */
    private void reConnServer(FdfsConns fdfsConns){
        try {
            ClientGlobal.initByProperties(fdfsConns.getConfFile());
            fdfsConns.setTrackerClient(new TrackerClient());
            TrackerServer tServer = fdfsConns.getTrackerClient().getTrackerServer();
            if (tServer == null)
                throw new BusiException("连接FastDFS TrackerServer失败!");
            fdfsConns.setTrackerServer(tServer);
            logFileMsg("连接FastDFS服务端......成功!");
        } catch (Exception e){
            if (fdfsConns!=null)
                fdfsConns.closeConn(this);
            logException(new BusiException(e,3999,BusiException.ExpLevel.ERROR));
        }
    }

    private FdfsConns connectServer(){
        FdfsClientMgrConfig fdfsClientMgrConf= SpringContextHolder.getBean("fdfsClientMgrConfig");;
        if (fdfsClientMgrConf == null) {
            logException(new BusiException(new Exception("未取得FdfsClientMgrConf实例"),3999,
                    BusiException.ExpLevel.ERROR));
            return null;
        }
        FdfsConns retObj=new FdfsConns();
        retObj.setConfFile(fdfsClientMgrConf.getPropertiesFile());
        reConnServer(retObj);
        return retObj;
    }

    /**
    * 重载析构
    * */
    protected void finalize(){
        logFileMsg("stopFdfsClientMgr by finalize");
        stopFdfsClientMgr();
    }
    /**
     * 执行一次活跃检测
     * */
    public boolean activeTest(){
        if (g_FdfsConns!=null){
            try {
                return g_FdfsConns.getTrackerServer().getConnection().activeTest();
            }catch (Exception e){
                logFileMsg("activeTest error:"+ BaseNnte.getExpMsg(e));
            }
        }
        return false;
    }

    /**
    * 关闭服务连接
    * */
    public synchronized void stopFdfsClientMgr(){
        if (g_FdfsConns != null) {
            g_FdfsConns.closeConn(this);
        }
        logException(new BusiException("关闭FastDFS服务端连接......"));
    }

    public String getTypeGroupName(String type){
        if (typeGroupMap==null)
            return DEFAULT_GROUP_NAME;
        String groupname=typeGroupMap.get(type);
        if (StringUtils.isEmpty(groupname))
            return DEFAULT_GROUP_NAME;
        return groupname;
    }
    /**
     * 上传文件:按文件
     * */
    public String uploadFile(String type,String fileName){
        StorageClient storageClient=null;
        try {
            if (!isServerConnect())
                return null;
            storageClient = g_FdfsConns.createStorageClient();
            String[] files = FdfsConns.uploadFile(storageClient,getTypeGroupName(type), FileUtil.getContent(fileName),
                    FileUtil.getExtention(fileName), null);
            if (files != null && files.length > 1)
                return files[0] + ":" + files[1];
        } catch (Exception e) {
            logException(new BusiException(e));
        }finally {
            FdfsConns.closeStorageClient(storageClient);
        }
        return null;
    }
    /**
     * 上传文件*按内容
     * */
    public String uploadFile(String type,byte[] file,String extName){
        StorageClient storageClient=null;
        try {
            if (!isServerConnect())
                return null;
            storageClient = g_FdfsConns.createStorageClient();
            String[] files=FdfsConns.uploadFile(storageClient,getTypeGroupName(type),file,extName,null);
            if (files!=null && files.length>1)
                return files[0]+":"+files[1];
        }catch (Exception e){
            logException(new BusiException(e));
        }finally {
            FdfsConns.closeStorageClient(storageClient);
        }
        return null;
    }

    public int deleteFile(String type,String submitName){
        StorageClient storageClient=null;
        try {
            if (!isServerConnect())
                return 0;
            storageClient = g_FdfsConns.createStorageClient();
            String group=getTypeGroupName(type);
            String fileName=submitName.replaceFirst(group+":","");
            return  FdfsConns.deleteFile(storageClient,group,fileName);
        }catch (Exception e){
            logException(new BusiException(e));
        }finally {
            FdfsConns.closeStorageClient(storageClient);
        }
        return 0;
    }

    public byte[] downloadFile(String type,String submitName){
        StorageClient storageClient=null;
        try {
            if (!isServerConnect())
                return null;
            storageClient = g_FdfsConns.createStorageClient();
            byte[] contents=storageClient.download_file(getTypeGroupName(type),submitName);
            if (contents==null || contents.length<=0)
                return null;
            return contents;
        }catch (Exception e){
            logException(new BusiException(e));
        }finally {
            FdfsConns.closeStorageClient(storageClient);
        }
        return null;
    }
}
