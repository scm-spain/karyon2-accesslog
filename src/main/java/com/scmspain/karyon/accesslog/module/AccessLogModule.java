package com.scmspain.karyon.accesslog.module;

import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;

public class AccessLogModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(GsonBuilder.class).asEagerSingleton();
  }
}
