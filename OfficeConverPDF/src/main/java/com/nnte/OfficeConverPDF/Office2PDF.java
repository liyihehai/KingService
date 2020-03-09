package com.nnte.OfficeConverPDF;

import java.io.File;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.util.StringUtils;

/**
 * 这是一个工具类，主要是为了使Office2003-2007全部格式的文档(.doc|.docx|.xls|.xlsx|.ppt|.pptx)
 * 转化为pdf文件
 * Office2010的没测试
 * @author ZhouMengShun
 */
public class Office2PDF  {

    public static OfficeManager officeManager=null;
    /**
     * 使Office2003-2007全部格式的文档(.doc|.docx|.xls|.xlsx|.ppt|.pptx) 转化为pdf文件
     * @param inputFilePath 源文件路径,如："D:/论坛.docx"
     * @return
     */
    public static String openOfficeToPDF(String inputFilePath) {
        File pdfFile=office2pdf(inputFilePath);
        if (pdfFile!=null){
            return pdfFile.getAbsolutePath();
        }
        return null;
    }

    /**
     * 连接OpenOffice.org 并且启动OpenOffice.org
     * @return
     */
    public static OfficeManager getOfficeManager(String officeHome) {
        DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
         // 设置OpenOffice.org 4的安装目录
        config.setOfficeHome(officeHome);
        // 启动OpenOffice的服务
        OfficeManager officeManager = config.buildOfficeManager();
        officeManager.start();

        return officeManager;
    }

    /**
     * 转换文件
     * @param inputFile
     * @param outputFilePath_end
     * @param inputFilePath
     * @param converter
     */
    public static File converterFile(File inputFile,String outputFilePath_end,String inputFilePath,
            OfficeDocumentConverter converter) {
        File outputFile = new File(outputFilePath_end);
        //判断目标路径是否存在,如不存在则创建该路径
        if (!outputFile.getParentFile().exists()){
            outputFile.getParentFile().mkdirs();
        }
        converter.convert(inputFile, outputFile);//转换
        System.out.println("文件:"+inputFilePath+"转换为目标文件:"+outputFile+"成功!");
        return outputFile;
    }

    /**
     * 使Office2003-2007全部格式的文档(.doc|.docx|.xls|.xlsx|.ppt|.pptx) 转化为pdf文件
     * @param inputFilePath 源文件路径，如："D:/论坛.docx"
     * @return
     */
    public static File office2pdf(String inputFilePath) {
        if (officeManager == null){
            System.out.println("office未连接，转换终止!");
            return null;
        }
        try {
            if (StringUtils.isEmpty(inputFilePath)) {
                System.out.println("输入文件地址为空，转换终止!");
                return null;
            }
            File inputFile = new File(inputFilePath);
            //转换后的文件路径
            String outputFilePath_end=getOutputFilePath(inputFilePath);
            if (!inputFile.exists()) {
                System.out.println("输入文件不存在，转换终止!");
                return null;
            }
            //连接OpenOffice
            OfficeDocumentConverter converter=new OfficeDocumentConverter(officeManager);
            //转换并返回转换后的文件对象
            return converterFile(inputFile,outputFilePath_end,inputFilePath,converter);
        } catch (Exception e) {
            System.out.println("转化出错!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取输出文件
     * @param inputFilePath
     * @return
     */
    public static String getOutputFilePath(String inputFilePath) {
        String outputFilePath=inputFilePath.replaceAll("."+getPostfix(inputFilePath),".pdf");
        return outputFilePath;
    }

    /**
     * 获取inputFilePath的后缀名,如:"D:/论坛.docx"的后缀名为:"docx" 
     * @param inputFilePath
     * @return
     */
    public static String getPostfix(String inputFilePath) {
        return inputFilePath.substring(inputFilePath.lastIndexOf(".") + 1);
    }
}