package com.cyb.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileChannelTest {
	public static void main(String[] args) throws IOException {
		RandomAccessFile fromFile = new RandomAccessFile("d://20150907.html", "rw");
		FileChannel fromChannel = fromFile.getChannel();

		RandomAccessFile toFile = new RandomAccessFile("d://cyb.txt", "rw");
		FileChannel toChannel = toFile.getChannel();
		long position = 0;
		long count = fromChannel.size();
		toChannel.transferFrom(fromChannel, position, count);
		toChannel.close();
		fromChannel.close();
	}
}
