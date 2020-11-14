package com.leng.hiddencamera;

import android.content.Context;


import net.lingala.zip4j.crypto.AESDecrypter;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import de.idyl.winzipaes.AesZipFileDecrypter;
import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESDecrypterBC;
import de.idyl.winzipaes.impl.AESEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterBC;
import de.idyl.winzipaes.impl.ExtZipEntry;


/**
 * 压缩指定文件或目录为ZIP格式压缩文件
 * 支持中文(修改源码�?)
 * 支持密码(仅支�?256bit的AES加密解密)
 * 依赖bcprov项目(bcprov-jdk16-140.jar)
 * 
 * @author zyh
 */
public class DecryptionZipUtil {
	
	/**
	 * 使用指定密码将给定文件或文件夹压缩成指定的输出ZIP文件
	 * @param srcFile �?要压缩的文件或文件夹
	 * @param destPath 输出路径
	 * @param passwd 压缩文件使用的密�?
	 */
	public static void zip(Context context,String srcFile,String destPath,String passwd){
		AESEncrypter encrypter = new AESEncrypterBC();
		AesZipFileEncrypter zipFileEncrypter = null;
		try {
			zipFileEncrypter = new AesZipFileEncrypter(destPath, encrypter);
			/**
			 * 此方法是修改源码后添�?,用以支持中文文件�?
			 */
			zipFileEncrypter.setEncoding("utf8");
			File sFile = new File(srcFile);
			/**
			 * AesZipFileEncrypter提供了重载的添加Entry的方�?,其中:
			 * add(File f, String passwd) 
			 * 			方法是将文件直接添加进压缩文�?
			 * 
			 * add(File f,  String pathForEntry, String passwd)
			 * 			方法是按指定路径将文件添加进压缩文件
			 * pathForEntry - to be used for addition of the file (path within zip file)
			 */
			doZip(sFile, zipFileEncrypter, "", passwd);
			zipFileEncrypter.close();
//			Toast.makeText(context, "压缩成功�?", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 具体压缩方法,将给定文件添加进压缩文件�?,并处理压缩文件中的路�?
	 * @param file 给定磁盘文件(是文件直接添�?,是目录�?�归调用添加)
	 * @param encrypter AesZipFileEncrypter实例,用于输出加密ZIP文件
	 * @param pathForEntry ZIP文件中的路径
	 * @param passwd 压缩密码
	 * @throws IOException
	 */
	private static void doZip(File file, AesZipFileEncrypter encrypter,
			String pathForEntry, String passwd) throws IOException {
		if (file.isFile()) {
			pathForEntry += file.getName();
			encrypter.add(file, pathForEntry, passwd);
			return;
		}
		pathForEntry += file.getName() + File.separator;
		for(File subFile : file.listFiles()) {
			doZip(subFile, encrypter, pathForEntry, passwd);
		}
	}
	

	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * 压缩测试
		 * 可以传文件或者目�?
		 */
//		zip("M:\\ZIP\\test\\bb\\a\\t.txt", "M:\\ZIP\\test\\temp1.zip", "zyh");
//		zip("M:\\ZIP\\test\\bb", "M:\\ZIP\\test\\temp2.zip", "zyh");
		
		//unzip("M:\\ZIP\\test\\temp2.zip", "M:\\ZIP\\test\\temp", "zyh");
	}
}
