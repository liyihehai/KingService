package com.nnte.fdfs_client_mgr;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.SpringContextHolder;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.NumberUtil;
import com.nnte.framework.utils.PropertiesUtil;
import com.nnte.framework.utils.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.pool.Connection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FdfsClientMgrComponent {
    private static TrackerServer trackerServer = null;
    private static Map<String,String> typeGroupMap = new HashMap<>();
    private static String DEFAULT_GROUP_NAME="group1";
    /*
    * 启动管理初始化
    * */
    public void runFdfsClientMgr(String[] args){
        if (trackerServer == null) {
            initGroupMap();
            reConnServer();
        }
    }
    private void initGroupMap(){
        try {
            FdfsClientMgrConfig fdfsClientMgrConf= SpringContextHolder.getBean("fdfsClientMgrConfig");
            if (fdfsClientMgrConf == null)
                throw new FdfsClientMgrException("未取得FdfsClientMgrConf实例");
            Integer gc=NumberUtil.getDefaultInteger(fdfsClientMgrConf.getLocalConfig(null,"groupcount"));
            if (gc<=0)
                throw new FdfsClientMgrException("没有配置文件上传的group的数量");
            Map<String,String> propMap=PropertiesUtil.getPropertyMap("application.properties");
            if (propMap==null || propMap.size()<=0)
                throw new FdfsClientMgrException("没有配置文件的group的配置项");
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
        } catch (FdfsClientMgrException fe) {
            BaseNnte.outConsoleLog(fe.getMessage());
        }
    }
    /*
    * 重连FastDFS服务器
    * */
    private static void reConnServer(){
        try {
            FdfsClientMgrConfig fdfsClientMgrConf= SpringContextHolder.getBean("fdfsClientMgrConfig");;
            if (fdfsClientMgrConf == null)
                throw new FdfsClientMgrException("未取得FdfsClientMgrConf实例");
            try {
                ClientGlobal.initByProperties(fdfsClientMgrConf.getLocalConfig("", "propertiesFile"));
                TrackerClient trackerClient = new TrackerClient();
                trackerServer = trackerClient.getTrackerServer();
                if (trackerServer == null)
                    throw new FdfsClientMgrException("连接FastDFS服务端失败!");
                BaseNnte.outConsoleLog("连接FastDFS服务端......成功!");
            } catch (MyException me) {
                trackerServer = null;
                throw new FdfsClientMgrException(me.getMessage());
            } catch (IOException e) {
                trackerServer = null;
                throw new FdfsClientMgrException(e.getMessage());
            }
        } catch (FdfsClientMgrException fe) {
            BaseNnte.outConsoleLog(fe.getMessage());
        }
    }
    /*
    * 重载析构
    * */
    protected void finalize(){
        stopFdfsClientMgr();
    }
    /*
    * 关闭服务连接
    * */
    public synchronized static void stopFdfsClientMgr(){
        if (trackerServer!=null){
            try {
                Connection conn=trackerServer.getConnection();
                conn.close();
                trackerServer=null;
            }catch (MyException me){
                trackerServer=null;
            }catch (IOException e){
                trackerServer=null;
            }
        }
        BaseNnte.outConsoleLog("关闭FastDFS服务端连接......");
    }
    /*判断连接是否可用，如果不可用，则尝试重连服务端*/
    public synchronized boolean isConnect(){
        if (trackerServer==null){
            reConnServer();
        }
        return trackerServer!=null;
    }
    public static String getTypeGroupName(String type){
        if (typeGroupMap==null)
            return DEFAULT_GROUP_NAME;
        String groupname=typeGroupMap.get(type);
        if (StringUtils.isEmpty(groupname))
            return DEFAULT_GROUP_NAME;
        return groupname;
    }
    /*上传文件*/
    public String uploadFile(String type,String fileName){
        if (!isConnect())
            return null;
        try {
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String[] files=storageClient.upload_file(getTypeGroupName(type),FileUtil.getContent(fileName),
                    null,null);
            if (files!=null && files.length>1)
                return files[1];
        }catch (MyException me){
            stopFdfsClientMgr();
        }catch (IOException e){
            stopFdfsClientMgr();
        }
        return null;
    }
    public String uploadFile(String type,byte[] file){
        if (!isConnect())
            return null;
        try {
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String[] files=storageClient.upload_file(getTypeGroupName(type),file,null,null);
            if (files!=null && files.length>1)
                return files[1];
        }catch (MyException me){
            stopFdfsClientMgr();
        }catch (IOException e){
            stopFdfsClientMgr();
        }
        return null;
    }

    public int deleteFile(String type,String fileName){
        if (!isConnect())
            return 0;
        try {
            StorageClient storageClient = new StorageClient(trackerServer, null);
            return storageClient.delete_file(getTypeGroupName(type),fileName);
        }catch (MyException me){
            stopFdfsClientMgr();
        }catch (IOException e){
            stopFdfsClientMgr();
        }
        return 0;
    }
}
