package org.secondelement.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 2ndElement
 * @version v1.0
 * @description
 * @date 2022/10/17 17:25
 */
class RequestsTest {

    @Test
    void get() {
        String url1 = "http://www.2ndelement.space";
        String url2 = "http://www.2ndelement.space/test.png";
        Response response = Requests.get(url1, null, null);
        assert response != null;
        response.openFile();
        response = Requests.get(url2, null, null);
        assert response != null;
        response.openFile();
    }
}