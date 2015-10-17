package com.scmspain.karyon.accesslog.formatters;

import com.scmspain.karyon.accesslog.dto.AccessLog;

import java.text.SimpleDateFormat;

public class CommonApacheLog implements AccessLogFormatter{
  @Override
  public String format(AccessLog logLine) {
    return String.format(
      "%s - - [%s] \"%s %s %s\" %d %f",
      logLine.clientIp(),
      new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss.SSS Z").format(logLine.date()),
      logLine.method(),
      logLine.uri(),
      logLine.httpVersion(),
      logLine.statusCode(),
      (float) 0
    );
  }
}
