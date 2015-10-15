package com.scmspain.karyon.formatters;

import com.google.inject.Inject;
import com.scmspain.karyon.dto.AccessLog;

public class CombinedApacheLog implements AccessLogFormatter {

  private CommonApacheLog commonApacheLogFormatter;

  @Inject
  CombinedApacheLog(CommonApacheLog commonApacheLogFormatter) {
    this.commonApacheLogFormatter = commonApacheLogFormatter;
  }

  @Override
  public String format(AccessLog logLine) {
    return String.format("%s - %s", commonApacheLogFormatter.format(logLine), logLine.userAgent());
  }
}
