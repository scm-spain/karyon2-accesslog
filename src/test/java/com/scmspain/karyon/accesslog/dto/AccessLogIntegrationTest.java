package com.scmspain.karyon.accesslog.dto;

import com.netflix.governator.guice.BootstrapModule;
import com.scmspain.karyon.accesslog.dto.helpers.AppServerForTesting;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import netflix.karyon.Karyon;
import netflix.karyon.KaryonServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccessLogIntegrationTest {
  private static KaryonServer server;

  @BeforeClass
  public static void setUpBefore() throws Exception {
    server = Karyon.forApplication(AppServerForTesting.class, (BootstrapModule[]) null);
    server.start();
  }

  @AfterClass
  public static void cleanUpAfter() throws Exception {
    server.shutdown();
  }

  @Test
  public void itShouldReturnSuccessCodeStatus() throws Exception {

    String body = RxNetty.createHttpClient("localhost", AppServerForTesting.AppServer.DEFAULT_PORT)
        .submit(HttpClientRequest.createGet("/sample"))
        .doOnNext(response -> assertThat(response.getStatus(), is(HttpResponseStatus.OK)))
        .flatMap(HttpClientResponse::getContent)
        .map(content -> content.toString(Charset.defaultCharset()))
        .timeout(5, TimeUnit.SECONDS)
        .toBlocking()
        .single();

    assertThat(body, is("Hello!"));
  }
}
