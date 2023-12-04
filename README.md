When using virtual threads all `@Scheduled` executors stop working after first exception on any `@Scheduled` executor and no error is logged. With disabled virtual threads `@Scheduled` executors continue working and exception is logged with `TaskUtils$LoggingErrorHandler` and `SimpleAsyncUncaughtExceptionHandler`.

In this demo `DemoEvent` is generated every 5 seconds with `@Scheduled` task, and there are two listeners, one is `@Async` and other is synchronous and both of them are throwing exception on every second invocation. Also, there is one `@Scheduled` task which throws exception on every second invocation.

With disabled virtual threads, all `@Scheduled` tasks continue working after exception.

Run application with virtual threads disabled and after that with virtual threads enabled in `application.yml`.