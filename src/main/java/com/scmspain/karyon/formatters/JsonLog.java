package com.scmspain.karyon.formatters;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.scmspain.karyon.dto.AccessLog;

public class JsonLog implements AccessLogFormatter {

  private GsonBuilder gson;

  @Inject
  JsonLog(GsonBuilder gson) {
    this.gson = gson;
  }

  @Override
  public String format(AccessLog logLine) {
    return gson.create().toJson(logLine);
  }
}
