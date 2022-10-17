package org.secondelement.requests.io;

import org.secondelement.requests.Constant;

import java.io.*;

/**
 * @author 2ndElement
 * @version v1.0
 * @description
 * @date 2022/10/17 15:23
 */
public class StreamReader extends FilterInputStream {

    public StreamReader(InputStream in) {
        super(in);
    }

    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder(64);
        int ch;
        while ((ch = read()) != -1) {
            switch (ch) {
                case '\r':
                    break;
                case '\n':
                    return line.toString();
                default:
                    line.append((char) ch);
            }
        }
        return line.toString();
    }

    public int readBytes(byte[] bytes) throws IOException {
        byte[] buffer = new byte[Constant.BUFFER_SIZE];
        int length = bytes.length;
        int remainSize = length;
        int readSize;
        while (remainSize > 0) {
            readSize = read(buffer, 0, Math.min(remainSize, Constant.BUFFER_SIZE));
            if (readSize != -1) {
                System.arraycopy(buffer, 0, bytes, length - remainSize, readSize);
                remainSize -= readSize;
            } else if (remainSize == length) {
                return -1;
            }
        }
        return length - remainSize;
    }

    public int readBytes(byte[] bytes, int off, int len) throws IOException {
        byte[] buffer = new byte[Constant.BUFFER_SIZE];
        int length = Math.min(len, bytes.length);
        int remainSize = len;
        int readSize;
        while (remainSize > 0) {
            readSize = read(buffer, 0, Math.min(remainSize, Constant.BUFFER_SIZE));
            if (readSize != -1) {
                System.arraycopy(buffer, 0, bytes, off, readSize);
                remainSize -= readSize;
            } else if (remainSize == length) {
                return -1;
            }
        }
        return len - remainSize;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] buffer = new byte[Constant.BUFFER_SIZE];
        byte[] bytes = new byte[length];
        int remainSize = length;
        int readSize;
        while (remainSize > 0) {
            readSize = read(buffer, 0, Math.min(remainSize, Constant.BUFFER_SIZE));
            if (readSize == -1) {
                break;
            }
            System.arraycopy(buffer, 0, bytes, length - remainSize, readSize);
            remainSize -= readSize;
        }
        return bytes;
    }
}
