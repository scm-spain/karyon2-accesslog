package com.scmspain.karyon.accesslog.dto;

import com.scmspain.karyon.accesslog.formatters.AccessLogFormatter;

import java.util.Date;

public class AccessLog {
  private final Date timestamp;

  private final String httpVersion;
  private final String method;
  private final String uri;

  private String clientIp;
  private String userAgent;
  private String referer;

  private Integer statusCode;
  private Long timeTaken;
  private Long responseSize;

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
      String userAgent,
      String referer
  ) {
    this(httpVersion, method, uri);
    this.clientIp = clientIp;
    this.userAgent = userAgent;
    this.referer = referer;
  }

  public AccessLog(
          String httpVersion,
          String method,
          String uri,
          String clientIp,
          String userAgent,
          String referer,
          Integer statusCode,
          Long timeTaken,
          Long responseSize
  ) {
    this(httpVersion, method, uri, clientIp, userAgent, referer);
    this.statusCode = statusCode;
    this.timeTaken = timeTaken;
    this.responseSize = responseSize;
  }

  public Date date() {
    return timestamp;
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

  public String referer() {
    return referer;
  }

  public Integer statusCode() {
    return statusCode;
  }

  public Long timeTaken() {
    return timeTaken;
  }

  public Long responseSize() {
    return responseSize;
  }

  public String format(AccessLogFormatter logFormatter) {
    return logFormatter.format(this);
  }
}
