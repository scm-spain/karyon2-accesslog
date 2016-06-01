package com.scmspain.karyon.accesslog;

import com.scmspain.karyon.accesslog.formatters.AccessLogFormatter;

import java.time.Instant;

public class AccessLog {
  private Long timeTaken = 0L;
  private final Instant timestamp = Instant.now();
  private RequestBag request;
  private ResponseBag response;
  private ClientBag client;
  private TraceBag traceBag;

  public AccessLog(String httpVersion, String method, String uri) {
    this.request = new RequestBag(httpVersion, method, uri);
    this.response = new ResponseBag(0, (long) 0);
    this.client = new ClientBag("", "-", "-");
  }

  public AccessLog(
      String httpVersion,
      String method,
      String uri,
      String clientIp,
      String userAgent,
      String referer,
      String zipkinParentId,
      String zipkinTraceId
  ) {
    this(httpVersion, method, uri);
    this.client = new ClientBag(clientIp, userAgent, referer);
    this.traceBag = new TraceBag(zipkinParentId, zipkinTraceId);
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
          Long responseSize,
          String zipkinParentId,
          String zipkinTraceId
  ) {
    this(httpVersion, method, uri, clientIp, userAgent, referer, zipkinParentId, zipkinTraceId);
    this.response = new ResponseBag(statusCode, responseSize);
    this.timeTaken = timeTaken;
  }

  public Instant date() {
    return timestamp;
  }

  public String httpVersion() {
    return this.request.httpVersion;
  }

  public String method() {
    return this.request.method;
  }

  public String uri() {
    return this.request.uri;
  }

  public String clientIp() {
    return this.client.ip;
  }

  public String userAgent() {
    return this.client.userAgent;
  }

  public String referer() {
    return this.client.referer;
  }

  public Integer statusCode() {
    return this.response.statusCode;
  }

  public Long timeTaken() {
    return timeTaken;
  }

  public Long responseSize() {
    return this.response.responseSize;
  }

  public String zipkinParentId() {
    return this.traceBag.getxParentId();
  }

  public String zipkinTraceId() {
    return this.traceBag.getxTraceId();
  }

  public String format(AccessLogFormatter logFormatter) {
    return logFormatter.format(this);
  }

  private class RequestBag {
    private final String httpVersion;
    private final String method;
    private final String uri;

    public RequestBag(String httpVersion, String method, String uri) {
      this.httpVersion = httpVersion;
      this.method = method;
      this.uri = uri;
    }
  }

  private class ResponseBag {

    private Long responseSize = 0L;
    private final Integer statusCode;

    public ResponseBag(Integer statusCode, Long responseSize) {
      this.statusCode = statusCode;
      this.responseSize = responseSize;
    }
  }

  private class ClientBag {

    private String ip;
    private String userAgent;
    private String referer;

    public ClientBag(String clientIp, String userAgent, String referer) {
      this.ip = clientIp;
      this.userAgent = userAgent;
      this.referer = referer;
    }
  }

  private class TraceBag {
    private String zipkinParentId;
    private String zipkinTraceId;

    public TraceBag(String zipkinParentId, String zipkinTraceId) {
      this.zipkinParentId = zipkinParentId;
      this.zipkinTraceId = zipkinTraceId;
    }

    public String getxParentId() {
      return zipkinParentId;
    }

    public String getxTraceId() {
      return zipkinTraceId;
    }
  }
}
