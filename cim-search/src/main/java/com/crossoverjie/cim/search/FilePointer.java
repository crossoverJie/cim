package com.crossoverjie.cim.search;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020/8/16 20:06
 * @since JDK 11
 */
public class FilePointer {
    public static void main(String[] args) throws Exception {
        // 所要写入的文件内容
        String s1 = "first";
        String s2 = "second";
        String s3 = "third";
        String s4 = "four";
        String s5 = "five";

        // 利用多线程同时写入一个文件
        new FileWriteThread(0,s1.getBytes()).run(); // 从文件的1024字节之后开始写入数据
        new FileWriteThread(512*1,s2.getBytes()).run(); // 从文件的2048字节之后开始写入数据
        new FileWriteThread(512*2,s3.getBytes()).run(); // 从文件的3072字节之后开始写入数据
        new FileWriteThread(512*3,s4.getBytes()).run(); // 从文件的4096字节之后开始写入数据
//        new FileWriteThread(521*5,s5.getBytes()).start(); // 从文件的5120字节之后开始写入数据
//
        TimeUnit.SECONDS.sleep(1);
        RandomAccessFile raf = new RandomAccessFile("/Users/chenjie/Documents/test.log", "r");
        System.out.println(raf.length());
//        byte buff[] = new byte[512];
//        int read = raf.read(buff, 0, 512);
//        System.out.println(new String(buff));

        for (int i = 0; i <= 4; i++) {
            int off = i*512;
            byte buff[] = new byte[512];
            int read = raf.read(buff, 0, 512);
//            System.out.println(read);
            System.out.println(new String(buff));
        }

        raf.seek(512*3);
        byte buff[] = new byte[512];
        raf.read(buff, 0, 512);
        System.out.println(new String(buff));
    }

    // 利用线程在文件的指定位置写入指定数据
    static class FileWriteThread{
        private int skip;
        private byte[] content;

        public FileWriteThread(int skip,byte[] content){
            this.skip = skip;
            this.content = content;
        }

        public void run(){
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile("/Users/chenjie/Documents/test.log", "rw");
                raf.seek(skip);
                raf.write(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                } catch (Exception e) {
                }
            }
        }
    }

}
