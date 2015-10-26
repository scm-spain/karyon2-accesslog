package com.scmspain.karyon.accesslog.formatters;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.scmspain.karyon.accesslog.AccessLog;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class JsonLog implements AccessLogFormatter {

  private GsonBuilder gson;

  @Inject
  JsonLog(GsonBuilder gson) {
    this.gson = gson;
  }

  @Override
  public String format(AccessLog logLine) {

    Map<String, String> accessLogMapped = new HashMap<>();
    accessLogMapped.put("clientIp", logLine.clientIp());
    accessLogMapped.put("date",
        new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss.SSS Z").format(Date.from(logLine.date())));
    accessLogMapped.put("method", logLine.method());
    accessLogMapped.put("uri", logLine.uri());
    accessLogMapped.put("httpVersion", logLine.httpVersion());
    accessLogMapped.put("referrer", String.valueOf(logLine.referer()));
    accessLogMapped.put("userAgent", logLine.userAgent());
    accessLogMapped.put("responseSize", String.valueOf(logLine.responseSize()));
    accessLogMapped.put("timeTaken", String.valueOf(logLine.timeTaken()));

    return gson.create().toJson(accessLogMapped);
  }
}
