package com.scmspain.karyon.dto;

import java.util.Date;

public class AccessLog {
  private final Date timestamp;
  private final String httpVersion;
  private final String method;
  private final String uri;

  public AccessLog(String httpVersion, String method, String uri) {
    this.timestamp = new Date();
    this.httpVersion = httpVersion;
    this.method = method;
    this.uri = uri;
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
}
