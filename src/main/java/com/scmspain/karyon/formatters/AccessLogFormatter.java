package com.scmspain.karyon.formatters;

import com.scmspain.karyon.dto.AccessLog;

public interface AccessLogFormatter {
  String format(AccessLog logLine);
}
