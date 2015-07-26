package com.wizarpos.atool.net.volley2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.os.Environment;

/**
 * 文件操作
 * 
 * @author wu
 * 
 */
public class FileUtils {

	private final static int FILESIZE = 4 * 1024;

	/**
	 * 将string 写入到cache
	 * 
	 * @param result
	 */
	public static void saveResp2Cache(String result) {
		try {
			String cacheFile = VolleyApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator + "netresp.txt";
//			String cacheFile = Environment.getExternalStorageDirectory() + File.separator + "netresp.txt";
			saveStringToFile(cacheFile, result);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向sdcard中写入文件
	 * 
	 * @param pathAndName
	 *            文件名
	 * @param content
	 *            文件内容
	 */
	public static void saveStringToFile(String pathAndName, String content) throws Exception {
		File file = new File(pathAndName);
		if (file.exists() == false) {
			file.createNewFile();
		}
		OutputStream out = new FileOutputStream(file);
		out.write(content.getBytes());
		out.close();
	}
}
