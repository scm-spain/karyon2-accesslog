# Karyon Access Log Interceptor Module

This module add an Interceptor to [Karyon](https://github.com/Netflix/karyon) so that log all requests.
 
## How to use

First of all, add to your **build.gradle** the dependency:
```
dependencies {
  ...
  compile 'com.scmspain.karyon:ms-accesslog-interceptor-module:0.1'
}
```

Then include **AccessLogModule** in your AppServer as follows:

```java
...
@Modules(
    include = {
        ...
        AccessLogModule.class
    })
public interface AppServer {
  ...
}
```

And finally intercept all urls:

```java
@Override
protected void configureServer() {
  ...
  interceptorSupport().forUri("/*").intercept(AccessLogInterceptor.class);
}
```

### Log Formatter
By default **CombinedApacheLog** format is bind, but you can override this in your module as follows:

```java
@Override
protected void configure() {
  ...
  binder.bind(AccessLogFormatter.class).to(JsonLog.class);
}
```

#### Available formatter classes:
* **JsonLog** print the Access log as a Json.
* **CommonApacheLog** print a default Apache Access log.
* **CombinedApacheLog** print a Combined Apache Access log (common plus referrer and user agent).