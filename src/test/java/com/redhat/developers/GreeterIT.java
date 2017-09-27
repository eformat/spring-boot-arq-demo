package com.redhat.developers;

import com.jayway.restassured.RestAssured;
import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.extern.slf4j.Slf4j;
import org.arquillian.cube.kubernetes.api.Session;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@Slf4j
public class GreeterIT extends AbstractGreeterTests {

    @ArquillianResource
    KubernetesClient client;

    @ArquillianResource
    Session session;


    //both application name and service name are same, if you use different modify it
    private String applicationName = "spring-boot-arq-demo";

    @Before
    public void setup() {

        String baseURI;

        /**
         * Checking whether the host is OpenShift Cluster or Raw Kubernetes Cluster and
         * based on that we can use {@link Route} or NodePort
         */
        if (KubernetesHelper.isOpenShift(client)) {
            final Route route = this.client.adapt(OpenShiftClient.class).routes()
                .inNamespace(this.session.getNamespace()).withName(applicationName).get();
            assertThat(route).isNotNull();
            baseURI = String.format("http://%s/api/greet", Objects.requireNonNull(route).getSpec().getHost());

        } else {

            //Construct NodePortIP url
            final String nodeIp = client.getMasterUrl().getHost();

            final Service service = client.services()
                .inNamespace(this.session.getNamespace())
                .withName(applicationName)
                .get();

            Optional<ServicePort> servicePort = service.getSpec().getPorts().stream()
                .filter(sp -> sp.getPort() == 8080)
                .findFirst();

            assertThat(servicePort.isPresent()).isTrue();

            int nodePort = servicePort.get().getNodePort();
            assertThat(nodePort).isBetween(30000, 32767); // this is NodePort Range

            baseURI = String.format("http://%s:%d/api/greet", Objects.requireNonNull(nodeIp), Objects.requireNonNull(nodePort));
        }

        log.info("Using {} service  URL:{}", applicationName, baseURI);
        RestAssured.baseURI = baseURI;
    }
}
