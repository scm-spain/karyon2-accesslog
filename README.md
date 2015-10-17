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

Then just include **AccessLogModule** in your AppServer as follows:

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