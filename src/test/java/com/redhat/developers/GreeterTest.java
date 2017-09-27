package com.redhat.developers;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreeterTest extends AbstractGreeterTests {

    @LocalServerPort
    int port;

    @Before
    public void setup() {
        RestAssured.baseURI = String.format("http://localhost:%d/api/greet", port);
    }

}
