package com.scmspain.karyon.accesslog.formatters;

import com.scmspain.karyon.accesslog.dto.AccessLog;

public interface AccessLogFormatter {
  String format(AccessLog logLine);
}
