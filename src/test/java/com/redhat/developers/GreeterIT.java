package com.redhat.developers;

import com.jayway.restassured.RestAssured;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.OpenShiftClient;
import org.arquillian.cube.kubernetes.api.Session;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class GreeterIT extends AbstractGreeterTests {

    @ArquillianResource
    OpenShiftClient openShiftClient;

    @ArquillianResource
    Session session;

    private String applicationName = "spring-boot-arq-demo";

    @Before
    public void setup() {
        final Route route = this.openShiftClient.adapt(OpenShiftClient.class).routes()
                .inNamespace(this.session.getNamespace()).withName(applicationName).get();

        assertThat(route).isNotNull();

        RestAssured.baseURI = String.format("http://%s/api/greet", Objects.requireNonNull(route).getSpec().getHost());

    }
}
