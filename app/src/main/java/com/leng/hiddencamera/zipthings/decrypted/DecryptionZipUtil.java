package com.leng.hiddencamera.zipthings.decrypted;

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
 * 鍘嬬缉鎸囧畾鏂囦欢鎴栫洰褰曚负ZIP鏍煎紡鍘嬬缉鏂囦欢
 * 鏀寔涓枃(淇敼婧愮爜鍚?)
 * 鏀寔瀵嗙爜(浠呮敮鎸?256bit鐨凙ES鍔犲瘑瑙ｅ瘑)
 * 渚濊禆bcprov椤圭洰(bcprov-jdk16-140.jar)
 * 
 * @author zyh
 */
public class DecryptionZipUtil {
	
	/**
	 * 浣跨敤鎸囧畾瀵嗙爜灏嗙粰瀹氭枃浠舵垨鏂囦欢澶瑰帇缂╂垚鎸囧畾鐨勮緭鍑篫IP鏂囦欢
	 * @param srcFile 闇?瑕佸帇缂╃殑鏂囦欢鎴栨枃浠跺す
	 * @param destPath 杈撳嚭璺緞
	 * @param passwd 鍘嬬缉鏂囦欢浣跨敤鐨勫瘑鐮?
	 */
	public static void zip(Context context,String srcFile,String destPath,String passwd){
		AESEncrypter encrypter = new AESEncrypterBC();
		AesZipFileEncrypter zipFileEncrypter = null;
		try {
			zipFileEncrypter = new AesZipFileEncrypter(destPath, encrypter);
			/**
			 * 姝ゆ柟娉曟槸淇敼婧愮爜鍚庢坊鍔?,鐢ㄤ互鏀寔涓枃鏂囦欢鍚?
			 */
			zipFileEncrypter.setEncoding("utf8");
			File sFile = new File(srcFile);
			/**
			 * AesZipFileEncrypter鎻愪緵浜嗛噸杞界殑娣诲姞Entry鐨勬柟娉?,鍏朵腑:
			 * add(File f, String passwd) 
			 * 			鏂规硶鏄皢鏂囦欢鐩存帴娣诲姞杩涘帇缂╂枃浠?
			 * 
			 * add(File f,  String pathForEntry, String passwd)
			 * 			鏂规硶鏄寜鎸囧畾璺緞灏嗘枃浠舵坊鍔犺繘鍘嬬缉鏂囦欢
			 * pathForEntry - to be used for addition of the file (path within zip file)
			 */
			doZip(sFile, zipFileEncrypter, "", passwd);
			zipFileEncrypter.close();
//			Toast.makeText(context, "鍘嬬缉鎴愬姛锛?", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 鍏蜂綋鍘嬬缉鏂规硶,灏嗙粰瀹氭枃浠舵坊鍔犺繘鍘嬬缉鏂囦欢涓?,骞跺鐞嗗帇缂╂枃浠朵腑鐨勮矾寰?
	 * @param file 缁欏畾纾佺洏鏂囦欢(鏄枃浠剁洿鎺ユ坊鍔?,鏄洰褰曢?掑綊璋冪敤娣诲姞)
	 * @param encrypter AesZipFileEncrypter瀹炰緥,鐢ㄤ簬杈撳嚭鍔犲瘑ZIP鏂囦欢
	 * @param pathForEntry ZIP鏂囦欢涓殑璺緞
	 * @param passwd 鍘嬬缉瀵嗙爜
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
	 * 娴嬭瘯
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * 鍘嬬缉娴嬭瘯
		 * 鍙互浼犳枃浠舵垨鑰呯洰褰?
		 */
//		zip("M:\\ZIP\\test\\bb\\a\\t.txt", "M:\\ZIP\\test\\temp1.zip", "zyh");
//		zip("M:\\ZIP\\test\\bb", "M:\\ZIP\\test\\temp2.zip", "zyh");
		
		//unzip("M:\\ZIP\\test\\temp2.zip", "M:\\ZIP\\test\\temp", "zyh");
	}
}
