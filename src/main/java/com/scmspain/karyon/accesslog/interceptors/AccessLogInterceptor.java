package com.scmspain.karyon.accesslog.interceptors;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.scmspain.karyon.accesslog.AccessLog;
import com.scmspain.karyon.accesslog.formatters.AccessLogFormatter;
import com.scmspain.karyon.accesslog.formatters.CombinedApacheLog;
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
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class AccessLogInterceptor
    implements DuplexInterceptor<HttpServerRequest<ByteBuf>, HttpServerResponse<ByteBuf>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogInterceptor.class);
  private static final Class<? extends AccessLogFormatter>
      DEFAULT_FORMATTER = CombinedApacheLog.class;

  public static AttributeKey<AccessLog> accessAttribute =
      AttributeKey.newInstance("accessAttribute");
  private final AccessLogFormatter logFormatter;

  @Inject
  AccessLogInterceptor(Injector injector, AccessLogFormatterHolder logFormatterHolder) {
    if (null == logFormatterHolder.value) {
      logFormatterHolder.value = injector.getInstance(DEFAULT_FORMATTER);
    }

    this.logFormatter = logFormatterHolder.value;
  }

  @Override
  public Observable<Void> in(
      HttpServerRequest<ByteBuf> request,
      HttpServerResponse<ByteBuf> response
  ) {

    AccessLog logLine = buildAccessLogWith(request);
    response.getChannel().attr(accessAttribute).set(logLine);

    return Observable.empty();
  }

  @Override
  public Observable<Void> out(HttpServerResponse<ByteBuf> response) {
    AccessLog logLine = buildAccessLogWith(
        response, response.getChannel().attr(accessAttribute).getAndRemove());

    // TODO: gather and log response bytes.
    LOGGER.info(logLine.format(logFormatter));

    return Observable.empty();
  }

  private AccessLog buildAccessLogWith(HttpServerRequest<ByteBuf> request) {
    return new AccessLog(
      request.getHttpVersion().toString(),
      request.getHttpMethod().toString(),
      request.getUri(),
      getIpAddressFromProxy(request).orElse(getIpAddressFromSocket(request)),
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
      Duration.between(accessLog.date(), Instant.now()).toMillis(),
      (long) 0
    );
  }


  private Optional<String> getIpAddressFromProxy(HttpServerRequest<ByteBuf> request) {
    return Optional.ofNullable(request.getHeaders().get("X-FORWARDED-FOR"))
      .map(ipValuesFromProxy -> ipValuesFromProxy.split(","))
      .filter(ipsAsArray -> ipsAsArray.length > 0)
      .map(ipsAsArray -> ipsAsArray[0].trim());
  }

  private String getIpAddressFromSocket(HttpServerRequest<ByteBuf> request) {

    String ipAddress = null;
    SocketAddress remoteAddr = request.getNettyChannel().remoteAddress();

    if (remoteAddr instanceof InetSocketAddress) {
      InetSocketAddress inetSock = (InetSocketAddress) remoteAddr;
      ipAddress = inetSock.getHostString(); // Don't use hostname that does a DNS lookup.
    }

    return ipAddress;
  }

  private static class AccessLogFormatterHolder {
    @Inject(optional = true) AccessLogFormatter value;
  }
}
