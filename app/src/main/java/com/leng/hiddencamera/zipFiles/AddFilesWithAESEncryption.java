package com.leng.hiddencamera.zipFiles;

import java.io.IOException;
import java.io.RandomAccessFile;

public class AddFilesWithAESEncryption {


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