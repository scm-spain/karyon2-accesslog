package com.scmspain.karyon.dto;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import java.util.Date;

public class AccessLog {
  private final Date timestamp;

  private final String httpVersion;
  private final String method;
  private final String uri;

  private String clientIp;
  private String userAgent;

  public AccessLog(String httpVersion, String method, String uri) {
    this.timestamp = new Date();
    this.httpVersion = httpVersion;
    this.method = method;
    this.uri = uri;
  }

  public AccessLog(
      String httpVersion,
      String method,
      String uri,
      String clientIp,
      String userAgent
  ) {
    this(httpVersion, method, uri);
    this.clientIp = clientIp;
    this.userAgent = userAgent;
  }

  public String httpVersion() {
    return this.httpVersion;
  }

  public String method() {
    return this.method;
  }

  public String uri() {
    return this.uri;
  }

  public String clientIp() {
    return clientIp;
  }

  public String userAgent() {
    return userAgent;
  }
}
