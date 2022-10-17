package org.secondelement.requests;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * @author 2ndElement
 * @version v1.0
 * @description
 * @date 2022/10/17 18:13
 */
class Response {
    private final StatusCode statusCode;
    private final byte[] contents;
    private final String url;
    private final Map<String, Object> headers;
    private final String encoding;
    private final String boundary;
    private final String mimeType;

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public byte[] getContents() {
        return contents;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public String getEncoding() {
        return encoding;
    }

    public Response(StatusCode statusCode, byte[] contents, String url, Map<String, Object> headers) {
        this.statusCode = statusCode;
        this.contents = contents;
        this.url = url;
        this.headers = headers;

        String charset = null;
        String boundary = null;
        String mimeType = null;
        String contentType = (String) headers.get(Constant.CONTENT_TYPE);
        if (contentType != null) {
            StringTokenizer tok = new StringTokenizer(contentType, ";");
            mimeType = tok.nextToken();
            String[] keyValue;
            while (tok.hasMoreTokens()) {
                keyValue = tok.nextToken().toLowerCase().trim().split("=");
                if (keyValue.length != 2) {
                    break;
                }
                if ("charset".equals(keyValue[0])) {
                    charset = keyValue[1];
                } else if ("boundary".equals(keyValue[0])) {
                    boundary = keyValue[1];
                }
            }
        }
        this.encoding = charset;
        this.boundary = boundary;
        this.mimeType = mimeType;
    }

    public JSONObject json() {
        return JSON.parseObject(text());
    }

    /**
     * decode the content
     *
     * @return String decoded from contents with charset in the Content-Type header if it is bot null,
     * else {@link Constant#DEFAULT_CHARSET}
     */
    public String text() {
        Charset charset;
        charset = Charset.forName(Objects.requireNonNullElse(encoding, Constant.DEFAULT_CHARSET));
        return new String(contents, charset);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Response{");
        sb.append("statusCode=").append(statusCode);
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /**
     * @param delDelayMillis delete the temp file after this delay,negative will be not deleted
     *                       open the response file if it is viewable,else throw {@link UnsupportedOperationException}
     */
    public void openFile(long delDelayMillis) {

        try {
            if (mimeType == null) {
                throw new UnsupportedOperationException();
            }
            File tmp = File.createTempFile("tmp", "." + MimeTypes.getDefaultExt(mimeType));
            FileOutputStream fos = new FileOutputStream(tmp);
            fos.write(contents);
            fos.flush();
            fos.close();
            Desktop.getDesktop().open(tmp);
            if (delDelayMillis >= 0) {
                Thread.sleep(delDelayMillis);
                tmp.deleteOnExit();
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * like {@link #openFile(long)},default set delayMill to 1000
     */
    public void openFile() {
        openFile(1000);
    }
}