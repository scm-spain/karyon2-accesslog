package com.scmspain.karyon.dto;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccessLogTest {
  @Test
  public void testItShouldBeBuiltRequestInformation() throws Exception {
    final String httpVersion = HttpVersion.HTTP_1_0.toString();
    final String method = HttpMethod.GET.toString();
    final String uri = "/status";

    AccessLog log = new AccessLog(httpVersion, method, uri);

    assertThat(log.httpVersion(), is(httpVersion));
    assertThat(log.method(), is(method));
    assertThat(log.uri(), is(uri));
  }
}
