package com.scmspain.karyon.interceptors;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import netflix.karyon.transport.interceptor.DuplexInterceptor;
import rx.Observable;

public class AccessLogInterceptor implements DuplexInterceptor<HttpServerRequest<ByteBuf>, HttpServerResponse<ByteBuf>>{

    @Override
    public Observable<Void> in(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        return null;
    }

    @Override
    public Observable<Void> out(HttpServerResponse<ByteBuf> response) {
        return null;
    }
}
