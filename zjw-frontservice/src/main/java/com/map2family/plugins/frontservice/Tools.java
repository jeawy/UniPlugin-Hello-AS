package com.map2family.plugins.frontservice;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Tools {
    //把Base64字符串转换成bitmap
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = Base64.decode(base64String.toString().trim(), Base64.URL_SAFE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }
    /**
     * Save Bitmap
     *
     * @param name file name
     * @param bm  picture to save
     */
    public static void saveBitmap(String name, Bitmap bm, Context mContext, String abspath) {

        //指定我们想要存储文件的地址
        String TargetPath = mContext.getFilesDir() + abspath;
        //判断指定文件夹的路径是否存在
        if (!FileUtils.fileIsExist(TargetPath)) {

        } else {
            //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
            File saveFile = new File(TargetPath, name);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                // compress - 压缩的意思
                bm.compress(Bitmap.CompressFormat.PNG, 80, saveImgOut);
                //存储完成后需要清除相关的进程
                saveImgOut.flush();
                saveImgOut.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
class FileUtils {
 /**
    * 判断指定目录的文件夹是否存在，如果不存在则需要创建新的文件夹
    * @param fileName 指定目录
    * @return 返回创建结果 TRUE or FALSE
    */
         static boolean fileIsExist(String fileName)
        {
            //传入指定的路径，然后判断路径是否存在
            File file=new File(fileName);
            if (file.exists())
                return true;
            else{
                //file.mkdirs() 创建文件夹的意思
                return file.mkdirs();
                }
            }
       }
