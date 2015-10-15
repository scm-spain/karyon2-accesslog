package com.scmspain.karyon.interceptors;

import com.scmspain.karyon.dto.AccessLog;
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
import java.util.Map;

public class AccessLogInterceptor
    implements DuplexInterceptor<HttpServerRequest<ByteBuf>, HttpServerResponse<ByteBuf>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogInterceptor.class);
  public static AttributeKey<AccessLog> accessAttribute = AttributeKey.valueOf("accessAttribute");

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
    AccessLog logLine = response.getChannel().attr(accessAttribute).get();

    LOGGER.info(logLine.toString());

    return Observable.empty();
  }

  private AccessLog buildAccessLogWith(HttpServerRequest<ByteBuf> request) {
    return new AccessLog(
      request.getHttpVersion().toString(),
      request.getHttpMethod().toString(),
      request.getUri(),
      ((InetSocketAddress) request.getNettyChannel().remoteAddress()).getAddress().getHostAddress(),
      request.getHeaders().getHeader(HttpHeaders.Names.USER_AGENT)
    );
  }
}
