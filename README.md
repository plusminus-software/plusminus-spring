# Plusminus Spring

Set of Spring-related extensions.

A small library built on `spring-context` that provides static access to the
`ApplicationContext` and helpers for resolving and grouping beans by their
generic type parameters.

## Static ApplicationContext access

`SpringUtil` is a `@Component` that captures the `ApplicationContext` on
startup and exposes it statically:

```java
ApplicationContext context = SpringUtil.getApplicationContext();
boolean inSpring = SpringUtil.isRunningInSpring();
```

## Generic type resolution for beans

Resolve the generic type parameter of a bean (falling back to the bean
definition's `ResolvableType` when the class itself does not declare it),
or organize a list of beans by their generic type:

```java
Class<?> type = SpringUtil.resolveGenericType(bean, Handler.class);

Map<Class<?>, List<Handler>> grouped = SpringUtil.groupBeansByGenericType(handlers, Handler.class);
Map<Class<?>, Handler> byType = SpringUtil.beansToMapByGenericType(handlers, Handler.class);
Map<Class<?>, Handler> concurrent = SpringUtil.beansToConcurrentMapByGenericType(handlers, Handler.class);
```

## Auto-configuration

The library registers `SpringAutoconfig` via `META-INF/spring.factories`, so
in a Spring Boot application the `software.plusminus.spring` package is
component-scanned automatically — no manual configuration needed.

## Getting started

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>software.plusminus</groupId>
    <artifactId>plusminus-spring</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Building

Requires JDK 8.

```bash
./mvnw clean install
```

The build enforces Checkstyle, PMD, SpotBugs and JaCoCo coverage checks.

## License

[Apache License 2.0](LICENSE)
