package com.scmspain.karyon.accesslog.dto.helpers;

import com.netflix.governator.annotations.Modules;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import netflix.karyon.KaryonBootstrap;
import netflix.karyon.transport.http.KaryonHttpModule;
import netflix.karyon.transport.http.SimpleUriRouter;
import rx.Observable;

@KaryonBootstrap(name = "AppServer")
@Modules(
    include = {
        AppServerForTesting.AppServer.class
    })
public interface AppServerForTesting {
  class AppServer extends KaryonHttpModule<ByteBuf, ByteBuf> {
    public static final int DEFAULT_PORT = 8000;
    public static final int DEFAULT_THREADS_POOL_SIZE = 20;

    protected AppServer() {
      super("AppServer", ByteBuf.class, ByteBuf.class);
    }

    @Override
    protected void configureServer() {
      server()
        .port(DEFAULT_PORT)
        .threadPoolSize(DEFAULT_THREADS_POOL_SIZE);
    }

    @Override
    protected void configure() {
      bindRouter().to(Router.class);
      super.configure();
    }

    private static class Router implements RequestHandler<ByteBuf, ByteBuf> {

      @Override
      public Observable<Void> handle(
          HttpServerRequest<ByteBuf> request,
          HttpServerResponse<ByteBuf> response
      ) {

        SimpleUriRouter router = new SimpleUriRouter<ByteBuf, ByteBuf>()
            .addUri("/sample", (req, res) -> res.writeStringAndFlush("Hello!"));

        return router.handle(request, response);
      }
    }
  }

}
