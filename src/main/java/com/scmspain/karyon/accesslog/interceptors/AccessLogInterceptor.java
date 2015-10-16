package com.scmspain.karyon.accesslog.interceptors;

import com.google.inject.Inject;
import com.scmspain.karyon.accesslog.dto.AccessLog;
import com.scmspain.karyon.accesslog.formatters.AccessLogFormatter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import netflix.karyon.transport.interceptor.DuplexInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class AccessLogInterceptor
    implements DuplexInterceptor<HttpServerRequest<ByteBuf>, HttpServerResponse<ByteBuf>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogInterceptor.class);
  public static AttributeKey<AccessLog> accessAttribute = AttributeKey.valueOf("accessAttribute");
  private final AccessLogFormatter logFormatter;

  @Inject
  AccessLogInterceptor(AccessLogFormatter logFormatter) {
    this.logFormatter = logFormatter;
  }

  @Override
  public Observable<Void> in(
      HttpServerRequest<ByteBuf> request,
      HttpServerResponse<ByteBuf> response
  ) {

    AccessLog logLine = buildAccessLogWith(request);
    response.getChannel().attr(accessAttribute).setIfAbsent(logLine);

    return Observable.empty();
  }

  @Override
  public Observable<Void> out(HttpServerResponse<ByteBuf> response) {
    AccessLog logLine = buildAccessLogWith(
        response, response.getChannel().attr(accessAttribute).get());

    // TODO: gather and log response bytes.
    LOGGER.info(logLine.format(logFormatter));

    return Observable.empty();
  }

  private AccessLog buildAccessLogWith(HttpServerRequest<ByteBuf> request) {
    return new AccessLog(
      request.getHttpVersion().toString(),
      request.getHttpMethod().toString(),
      request.getUri(),
      ((InetSocketAddress) request.getNettyChannel().remoteAddress()).getAddress().getHostAddress(),
      request.getHeaders().getHeader(HttpHeaders.Names.USER_AGENT),
      request.getHeaders().getHeader(HttpHeaders.Names.REFERER)
    );
  }

  private AccessLog buildAccessLogWith(HttpServerResponse<ByteBuf> response, AccessLog accessLog) {
    return new AccessLog(
      accessLog.httpVersion(),
      accessLog.method(),
      accessLog.uri(),
      accessLog.clientIp(),
      accessLog.userAgent(),
      accessLog.referer(),
      response.getStatus().code(),
      Duration.between(accessLog.date().toInstant(), Instant.now()).toMillis(),
      (long) 0
    );
  }
}
