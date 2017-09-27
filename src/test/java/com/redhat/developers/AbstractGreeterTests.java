package com.redhat.developers;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

public class AbstractGreeterTests {

    @Test
    public void greet_should_return_hello_with_status_http_OK() throws Exception {
        when().get().then().statusCode(200).header("Content-Type", "text/plain;charset=UTF-8").body(is("Hello"));
    }
}
