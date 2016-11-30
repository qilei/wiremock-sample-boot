package com.example;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.net.SocketTimeoutException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by qilei on 16/11/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiremockTests {
  private static final Logger log = LoggerFactory.getLogger(WiremockTests.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9999);

  @Test
  public void response_use_file() {
    stubFor(get(urlEqualTo("/api/cinema/123"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBodyFile("cinema_123.json")));

    ResponseEntity<String> entity =
        restTemplate.getForEntity("http://localhost:9999/api/cinema/123", String.class);
    log.info("GET RESP:" + entity.getBody());

    assertThat(entity.getBody()).contains("44220801");
  }

  @Test
  public void timeout(){
    stubFor(get(urlEqualTo("/api/timeout"))
        .willReturn(aResponse()
            .withStatus(200)
            .withFixedDelay(5000)
            .withBody("api/timeout content")));

    RestTemplate template = new RestTemplate();
    SimpleClientHttpRequestFactory rf =
        (SimpleClientHttpRequestFactory) template.getRequestFactory();
    rf.setReadTimeout(1 * 1000);
    rf.setConnectTimeout(1 * 1000);

    try {
      template.getForObject("http://localhost:9999/api/timeout",String.class);
    } catch (ResourceAccessException e) {
      log.error(e.getMessage(), e);
      assertThat(e.getCause()).isInstanceOf(SocketTimeoutException.class);
    }
  }
}
