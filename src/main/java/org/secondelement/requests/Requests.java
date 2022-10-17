package org.secondelement.requests;

import org.secondelement.requests.io.StreamReader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author 2ndElement
 * @version v1.0
 * @description provide all methods of requests
 * @date 2022/10/16 01:59
 */
public class Requests {


    public static Response get(String url, Map<String, Object> params, Map<String, Object> requestHeaders) {
        Socket socket = null;
        StatusCode statusCode;
        String transferEncoding;
        String contentEncoding;
        ByteArrayOutputStream contents = new ByteArrayOutputStream();
        Map<String, Object> responseHeaders;
        try {
            URL urlObj = new URL(url);
            int port = urlObj.getPort();
            if (port == -1) {
                port = urlObj.getDefaultPort();
            }

            socket = new Socket(urlObj.getHost(), port);

            StreamReader in = new StreamReader(new BufferedInputStream(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            StringBuilder request = new StringBuilder(128);
            String path;
            if ("".equals(urlObj.getPath())) {
                path = "/";
            } else {
                path = urlObj.getPath();
            }

            request.append("GET").append(' ').append(path);

            if (params != null) {
                request.append("?");
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String mapKey = entry.getKey();
                    Object mapValue = entry.getValue();
                    request.append(mapKey).append('=').append(mapValue).append("&");
                }
            }

            request.append(' ').append(Constant.HTTP_VERSION).append(Constant.CRLF);
            request.append("Host: ").append(urlObj.getHost()).append(Constant.CRLF);

            if (requestHeaders == null) {
                requestHeaders = Constant.DEFAULT_HEADERS;
            }
            for (Map.Entry<String, Object> entry : requestHeaders.entrySet()) {
                String mapKey = entry.getKey();
                Object mapValue = entry.getValue();
                request.append(mapKey).append(": ").append(mapValue).append(Constant.CRLF);
            }

            request.append(Constant.CRLF);
            out.print(request);
            out.flush();


            String responseLine = in.readLine();
            String headerLine;
            String[] kw;
            statusCode = StatusCode.getStatusCode(Integer.parseInt(responseLine.substring(9, 12)));
            assert statusCode != null;
            responseHeaders = new HashMap<>(16);
            while (!"".equals(headerLine = in.readLine())) {
                kw = headerLine.split(": ");
                responseHeaders.put(kw[0].toLowerCase(), kw[1]);
            }
            transferEncoding = (String) responseHeaders.get("transfer-encoding");
            contentEncoding = (String) responseHeaders.get("content-encoding");
            if (statusCode == StatusCode.CONTINUE || statusCode == StatusCode.SWITCHING_PROTOCOL || statusCode == StatusCode.NO_CONTENT) {
                return new Response(statusCode, null, url, responseHeaders);
            }
            if (Constant.CHUNKED_MARK.equals(transferEncoding)) {
                while (true) {
                    int chunkSize = Integer.parseInt(in.readLine(), 16);
                    if (chunkSize == 0) {
                        in.readLine();
                        break;
                    }
                    contents.write(in.readBytes(chunkSize));
                    in.readLine();
                }
            } else {
                int contentLength = Integer.parseInt((String) responseHeaders.get("content-length"));
                contents.write(in.readBytes(contentLength));
            }
            if (Constant.GZIP_HEAD.equals(contentEncoding)) {
                GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(contents.toByteArray()));
                contents.reset();
                byte[] buffer = new byte[Constant.BUFFER_SIZE];
                int readSize;
                while ((readSize = gzip.read(buffer)) != -1) {
                    contents.write(buffer, 0, readSize);
                }
            }
            return new Response(statusCode, contents.toByteArray(), url, responseHeaders);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }
    }
}
