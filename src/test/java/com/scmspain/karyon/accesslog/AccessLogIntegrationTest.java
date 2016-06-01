package com.scmspain.karyon.accesslog;

import com.netflix.governator.guice.BootstrapModule;
import com.scmspain.karyon.accesslog.helpers.AppServerForTesting;
import com.scmspain.karyon.accesslog.formatters.AccessLogFormatter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import netflix.karyon.Karyon;
import netflix.karyon.KaryonServer;
import org.hamcrest.CustomMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccessLogIntegrationTest {
  private static KaryonServer server;
  private static AccessLogFormatter mockedFormatter;
  private static final  String IP_FOR_TEST = "8.8.8.8";
  private String zipkinParentId  = "666";
  private String zipkinTraceId   = "777";

  @BeforeClass
  public static void setUpBefore() throws Exception {
    mockedFormatter = mock(AccessLogFormatter.class);

    BootstrapModule[] bootstrapModules = {
      (binder) -> binder.bind(AccessLogFormatter.class).toInstance(mockedFormatter)
    };

    server = Karyon.forApplication(AppServerForTesting.class, bootstrapModules);
    server.start();
  }

  @AfterClass
  public  static void cleanUpAfter() throws Exception {
    server.shutdown();
  }


  @After
  public void cleanUp() throws Exception {
    Mockito.reset(mockedFormatter);
  }

  @Test
  public void itShouldReturnSuccessCodeStatus() throws Exception {

    givenAnIncomingRequest();
    thenRequestIsLogged();
  }

  @Test
  public void itShouldLogIPfromHttpHeaderIfExist() {
    givenAnIncominRequestWithX_Forwarded_For();
    thenRequestIsLoggedWithIpFromXForwarded();
  }

  @Test
  public void itShouldLogIpFromRequestIfExist() {
    givenAnIncomingRequest();
    thenRequestIsLoggedWithIpFromRequest();
  }


  private void thenRequestIsLoggedWithIpFromXForwarded() {
    verify(mockedFormatter).format(argThat(new CustomMatcher<AccessLog>("") {
      @Override
      public boolean matches(Object item) {
        AccessLog logLine = (AccessLog) item;
        return logLine.clientIp().equals(IP_FOR_TEST);
      }
    }));
  }

  private void thenRequestIsLoggedWithIpFromRequest() {
    verify(mockedFormatter).format(argThat(new CustomMatcher<AccessLog>("") {
      @Override
      public boolean matches(Object item) {
        AccessLog logLine = (AccessLog) item;
        return logLine.clientIp().equals("127.0.0.1");
      }
    }));
  }

  private void givenAnIncominRequestWithX_Forwarded_For() {
    Observable<HttpClientResponse<ByteBuf>> clientResponse =
        RxNetty.createHttpClient("localhost", AppServerForTesting.AppServer.DEFAULT_PORT)
          .submit(HttpClientRequest.createGet("/sample").withHeader("X-FORWARDED-FOR",IP_FOR_TEST));

    processRequest(clientResponse);
  }

  private void thenRequestIsLogged() {
    verify(mockedFormatter).format(argThat(new CustomMatcher<AccessLog>("") {
      @Override
      public boolean matches(Object item) {
        AccessLog logLine = (AccessLog) item;
        return logLine.method().equals("GET")
          && logLine.statusCode().equals(200)
          && logLine.uri().equals("/sample")
          && logLine.zipkinParentId().equals(zipkinParentId)
          && logLine.zipkinTraceId().equals(zipkinTraceId);
      }
    }));
  }

  private void processRequest(Observable<HttpClientResponse<ByteBuf>> clientResponse) {
    clientResponse.doOnNext(response -> assertThat(response.getStatus(), is(HttpResponseStatus.OK)))
      .flatMap(HttpClientResponse::getContent)
      .map(content -> content.toString(Charset.defaultCharset()))
      .timeout(5, TimeUnit.SECONDS)
      .toBlocking()
      .single();
  }

  private void givenAnIncomingRequest() {

    Observable<HttpClientResponse<ByteBuf>> clientResponse =
        RxNetty.createHttpClient("localhost", AppServerForTesting.AppServer.DEFAULT_PORT)
          .submit(HttpClientRequest.createGet("/sample")
          .withHeader("X-Parent-Id", "666")
          .withHeader("X-Trace-Id", "777"));
    processRequest(clientResponse);
  }


}
