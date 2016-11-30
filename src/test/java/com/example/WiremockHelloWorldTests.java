package com.example;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by qilei on 16/11/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiremockHelloWorldTests {
  private static final Logger log = LoggerFactory.getLogger(WiremockHelloWorldTests.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9999);

  @Test
  public void testHi() {
    stubFor(get(urlEqualTo("/api/hi"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("Hellow World")));

    String response = restTemplate.getForObject("http://localhost:9999/api/hi", String.class);
    log.info("GET RESP:" + response);

    assertThat(response).isEqualTo("Hellow World");
  }
}
