package com.scmspain.karyon.accesslog.formatters;

import com.scmspain.karyon.accesslog.AccessLog;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommonApacheLogTest {

  static final String DATE_REGEX =
      ".*\\[(\\d{2})/(\\d{2})/(\\d{4}):(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3}) (Z|[+-]\\d{4})\\].*";

  @Test
  public void itShouldFormatDateAsApacheCommonLog() throws Exception {
    CommonApacheLog formatter = new CommonApacheLog();
    AccessLog accessLog = new AccessLog(
            "1.1",
            "GET",
            "/something",
            "123.123.123.123",
            "Chrome",
            null,
            200,
            1L,
            1L
    );
    System.out.println(formatter.format(accessLog));
    assertTrue(
            "Date should have the format: 15/01/2016:13:35:54.987 +0200",
            formatter.format(accessLog).matches(DATE_REGEX)
    );
  }
}
