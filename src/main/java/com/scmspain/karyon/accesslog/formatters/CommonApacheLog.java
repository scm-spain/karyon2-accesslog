package com.scmspain.karyon.accesslog.formatters;

import com.scmspain.karyon.accesslog.AccessLog;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class CommonApacheLog implements AccessLogFormatter{
  @Override
  public String format(AccessLog logLine) {
    return String.format(
      "%s - - [%s] \"%s %s %s\" %d %d",
      logLine.clientIp(),
      new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z").format(Date.from(logLine.date())),
      logLine.method(),
      logLine.uri(),
      logLine.httpVersion(),
      logLine.statusCode(),
      logLine.responseSize()
    );
  }
}
