package com.scmspain.karyon.accesslog.formatters;

import com.google.inject.Inject;
import com.scmspain.karyon.accesslog.AccessLog;

public class CombinedApacheLog implements AccessLogFormatter {

  private CommonApacheLog commonApacheLogFormatter;

  @Inject
  CombinedApacheLog(CommonApacheLog commonApacheLogFormatter) {
    this.commonApacheLogFormatter = commonApacheLogFormatter;
  }

  @Override
  public String format(AccessLog logLine) {
    return String.format(
      "%s \"%s\" \"%s\"",
      commonApacheLogFormatter.format(logLine),
      valueOrDefault(logLine.referer()),
      valueOrDefault(logLine.userAgent())
    );
  }

  private String valueOrDefault(String value) {
    return (null != value && !value.isEmpty()) ? value : "-";
  }
}
