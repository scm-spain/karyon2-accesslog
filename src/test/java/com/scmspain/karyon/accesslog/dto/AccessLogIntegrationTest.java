package com.scmspain.karyon.accesslog.dto;

import com.netflix.governator.guice.BootstrapModule;
import com.scmspain.karyon.accesslog.dto.helpers.AppServerForTesting;
import com.scmspain.karyon.accesslog.formatters.AccessLogFormatter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import netflix.karyon.Karyon;
import netflix.karyon.KaryonServer;
import org.hamcrest.CustomMatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccessLogIntegrationTest {
  private static KaryonServer server;
  private static AccessLogFormatter mockedFormatter;

  @BeforeClass
  public static void setUpBefore() throws Exception {
    mockedFormatter = mock(AccessLogFormatter.class);

    BootstrapModule[] bootstrapModules = {(binder) -> {
      binder.bind(AccessLogFormatter.class).toInstance(mockedFormatter);
    }};

    server = Karyon.forApplication(AppServerForTesting.class, bootstrapModules);
    server.start();
  }

  @AfterClass
  public static void cleanUpAfter() throws Exception {
    server.shutdown();
  }

  @Test
  public void itShouldReturnSuccessCodeStatus() throws Exception {

    RxNetty.createHttpClient("localhost", AppServerForTesting.AppServer.DEFAULT_PORT)
        .submit(HttpClientRequest.createGet("/sample"))
        .doOnNext(response -> assertThat(response.getStatus(), is(HttpResponseStatus.OK)))
        .flatMap(HttpClientResponse::getContent)
        .map(content -> content.toString(Charset.defaultCharset()))
        .timeout(5, TimeUnit.SECONDS)
        .toBlocking()
        .single();

    verify(mockedFormatter).format(argThat(new CustomMatcher<AccessLog>("") {
      @Override
      public boolean matches(Object item) {
        AccessLog logLine = (AccessLog) item;
        return logLine.method().equals("GET")
          && logLine.statusCode().equals(200)
          && logLine.uri().equals("/sample");
      }
    }));
  }
}
