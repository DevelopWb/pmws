package com.leng.hiddencamera.zipthings;

import java.io.IOException;
import java.io.RandomAccessFile;

public class AddFilesWithAESEncryption {

//	public static void AddFilesWithpassword(String desFileName,
//			String sourceFileName, String password)
//			throws net.lingala.zip4j.exception.ZipException {
//
//		ZipFile zipFile = new ZipFile(desFileName);
//
//		ArrayList<File> filesToAdd = new ArrayList<File>();
//		filesToAdd.add(new File(sourceFileName));
//		// filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
//		// filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));
//
//		ZipParameters parameters = new ZipParameters();
//		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//
//		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST); // ?????????????????
//																				// DEFLATE_LEVEL_NORMAL
//		parameters.setEncryptFiles(true);
//
//		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
//
//		parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
//		parameters.setPassword(password);
//
//		zipFile.addFiles(filesToAdd, parameters);
//	}
//
//	public static void unZipFilesWithPassword(String sourceZipFileName,
//			String desFoler, String password) {
//		try {
//			// 解压有密码的zip文件
//			File src = new File(sourceZipFileName);
//			ZipFile zipFile = new ZipFile(src);
//			if (zipFile.isEncrypted()) {
//				zipFile.setPassword(password);
//			}
//			String dest = new String(desFoler);
//			zipFile.extractAll(dest);
//		} catch (net.lingala.zip4j.exception.ZipException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/**
	 * 使用RandomAccessFile 修复文件
	 * 
	 * @throws IOException
	 */
	public static void repairFile(String sourceFileName,
			String destinationFileName) throws IOException {

		RandomAccessFile read = new RandomAccessFile(sourceFileName, "r");
		RandomAccessFile writer = new RandomAccessFile(destinationFileName, "rw");
		byte[] byteBuffer = new byte[20 * 1024 * 1024];
		boolean reversalFirstByte = true;

		while (read.read(byteBuffer) != -1) {
			if (reversalFirstByte) {
				writer.write(backByte(byteBuffer));
				reversalFirstByte = false;
			} else {
				writer.write(byteBuffer);
			}

		}
		writer.close();
		read.close();

	}

	/**
	 * 使用 RandomAccessFile 损坏文件
	 * 
	 * @throws IOException
	 */
	public static void damageFile(String desFileName,
			String sourceFileName) throws IOException {

		RandomAccessFile read = new RandomAccessFile(sourceFileName, "r");
		RandomAccessFile writer = new RandomAccessFile(desFileName, "rw");

		byte[] byteBuffer = new byte[20 * 1024 * 1024];

		boolean reversalFirstByte = true;

		while (read.read(byteBuffer) != -1) {
			if (reversalFirstByte) {
				writer.write(backByte(byteBuffer));
				reversalFirstByte = false;
			} else {
				writer.write(byteBuffer);
			}

		}
		writer.close();
		read.close();

	}

	/**
	 * @param buff
	 * @return 数组取反
	 */
	public static byte[] backByte(byte[] buff) {
		for (int i = 0; i < buff.length; i++) {
			int b = 0;
			for (int j = 0; j < 8; j++) {
				int bit = (buff[i] >> j & 1) == 0 ? 1 : 0;
				b += (1 << j) * bit;
			}
			buff[i] = (byte) b;
		}
		return buff;
	}

}