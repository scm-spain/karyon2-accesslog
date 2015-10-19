package com.scmspain.karyon.accesslog.formatters;

import com.scmspain.karyon.accesslog.AccessLog;

public interface AccessLogFormatter {
  String format(AccessLog logLine);
}
