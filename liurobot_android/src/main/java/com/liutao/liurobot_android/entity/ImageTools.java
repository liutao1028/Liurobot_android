/** 
 *
 */
package com.liutao.liurobot_android.entity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

/**
 * @author Mr Liu
 * @data  2016-9-19 --- 上午9:30:53
 */
public class ImageTools {
	public static Bitmap readImage(String path){
		Bitmap bitmap=null;
		bitmap = BitmapFactory.decodeFile(path);
		return bitmap;
	}
	public static Boolean saveImage(Context context,Bitmap bitmap,String path){
		Boolean isSucceed=false;
		FileOutputStream fos = null;
		String statue = Environment.getExternalStorageState();
		if (!statue.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, "SD_卡 未准备就绪！", Toast.LENGTH_SHORT).show();
		return isSucceed;
		}
		try {
			 fos = new FileOutputStream(path);
			 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			 Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
			 return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (fos != null) {
                try {
                    fos.close();
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
		return isSucceed;
	}
}
