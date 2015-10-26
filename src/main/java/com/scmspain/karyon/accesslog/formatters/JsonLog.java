package com.scmspain.karyon.accesslog.formatters;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.scmspain.karyon.accesslog.AccessLog;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class JsonLog implements AccessLogFormatter {

  private GsonBuilder gson;

  @Inject
  JsonLog(GsonBuilder gson) {
    this.gson = gson;
  }

  @Override
  public String format(AccessLog logLine) {

    return gson.create().toJson(new HashMap<String, String>() {
      {
        put("clientIp", logLine.clientIp());
        put("date",
            new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss.SSS Z").format(Date.from(logLine.date())));
        put("method", logLine.method());
        put("uri", logLine.uri());
        put("httpVersion", logLine.httpVersion());
        put("referrer", String.valueOf(logLine.referer()));
        put("userAgent", logLine.userAgent());
        put("responseSize", String.valueOf(logLine.responseSize()));
        put("timeTaken", String.valueOf(logLine.timeTaken()));
      }
    });
  }
}
