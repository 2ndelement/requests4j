package org.secondelement.requests;

import java.util.HashMap;

/**
 * @author 2ndElement
 * @version v1.0
 * @description
 * @date 2022/10/17 16:25
 */
public interface Constant {
    int BUFFER_SIZE = 1024;
    String CRLF = "\r\n";
    String CHUNKED_MARK = "chunked";
    String CONTENT_TYPE = "content-type";
    String HTTP_VERSION = "HTTP/1.1";
    String GZIP_HEAD = "gzip";
    String MODULE_VERSION = "1.0";
    String DEFAULT_CHARSET = "utf-8";
    HashMap<String, Object> DEFAULT_HEADERS = new HashMap<>() {
        {
            put("User-Agent", "java-requests4j/" + MODULE_VERSION);
            put("Accept-Encoding", "gzip, deflate, br");
            put("Accept", "*/*");
            put("Connection", "keep-alive");
        }
    };
}
