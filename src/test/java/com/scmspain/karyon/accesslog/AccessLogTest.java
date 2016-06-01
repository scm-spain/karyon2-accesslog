package com.scmspain.karyon.accesslog;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccessLogTest {
  private String httpVersion;
  private String method;
  private String uri;

  private String clientIp;
  private String userAgent;
  private String referer;

  private Integer statusCode;
  private Long timeTaken;
  private Long responseSize;

  private String zipkinParentId;
  private String zipkinTraceId;

  private AccessLog log;

  @Test
  public void itShouldBeBuiltRequestInformation() throws Exception {
    givenARequest();
    whenAccessLogIsCreated();
    thenLogShouldContainRequestInformation();
  }

  @Test
  public void itShouldBeBuiltWithUserInformation() throws Exception {
    givenARequestWithUserInformation();
    whenAccessLogIsCreatedWithUserInfo();
    thenLogShouldContainUserInformation();
    thenShouldContainTraceInformation();
  }

  @Test
  public void itShouldBeBuiltWithResponseInformation() throws Exception {
    givenARequest();
    statusCode = 200;
    timeTaken = (long) 123456;
    responseSize = (long) 123456;
    whenAccessLogIsCreatedWithResponseInfo();
    thenLogShouldContainResponseInformation();
    thenShouldContainTraceInformation();
  }

  @Test
  public void itShouldContainsAClientByDefault() throws Exception {
    givenARequest();
    clientIp = "";
    userAgent = "-";
    referer = "-";
    whenAccessLogIsCreated();
    thenLogShouldContainUserInformation();
  }

  @Test
  public void itShouldContainsAResponseByDefault() throws Exception {
    givenARequest();
    statusCode = 0;
    timeTaken = (long) 0;
    responseSize = (long) 0;
    whenAccessLogIsCreated();
    thenLogShouldContainResponseInformation();
  }


  private void givenARequest() {
    httpVersion = HttpVersion.HTTP_1_0.toString();
    method = HttpMethod.GET.toString();
    uri = "/status";
    zipkinParentId = UUID.randomUUID().toString();
    zipkinTraceId = UUID.randomUUID().toString();
  }

  private void givenARequestWithUserInformation() {
    givenARequest();
    clientIp = "127.0.0.1";
    userAgent = "Curl";
    referer = "/some_referer.html";
  }

  private void whenAccessLogIsCreated() {
    log = new AccessLog(httpVersion, method, uri);
  }

  private void whenAccessLogIsCreatedWithUserInfo() {
    log = new AccessLog(httpVersion, method, uri, clientIp, userAgent, referer,
        zipkinParentId, zipkinTraceId);
  }

  private void whenAccessLogIsCreatedWithResponseInfo() {
    log = new AccessLog(
        httpVersion,
        method,
        uri,
        clientIp,
        userAgent,
        referer,
        statusCode,
        timeTaken,
        responseSize,
        zipkinParentId,
        zipkinTraceId
    );
  }

  private void thenLogShouldContainRequestInformation() {
    assertThat(log.httpVersion(), is(httpVersion));
    assertThat(log.method(), is(method));
    assertThat(log.uri(), is(uri));
  }

  private void thenLogShouldContainUserInformation() {
    assertThat(log.clientIp(), is(clientIp));
    assertThat(log.userAgent(), is(userAgent));
    assertThat(log.referer(), is(referer));
  }

  private void thenLogShouldContainResponseInformation() {
    assertThat(log.statusCode(), is(statusCode));
    assertThat(log.timeTaken(), is(timeTaken));
    assertThat(log.responseSize(), is(responseSize));
  }

  private void thenShouldContainTraceInformation() {
    assertThat(log.zipkinParentId(), is(zipkinParentId));
    assertThat(log.zipkinTraceId(), is(zipkinTraceId));
  }
}
