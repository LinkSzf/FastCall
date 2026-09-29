/**
 * FastCall Spring Boot starter.
 *
 * <p>This module intentionally contains no code: it is a dependency aggregator that pulls in
 * {@code fastcall-common}, {@code fastcall-data}, {@code fastcall-core} and {@code fastcall-api}
 * so that a Spring Boot application only has to declare a single dependency:</p>
 *
 * <pre>{@code
 * <dependency>
 *     <groupId>io.github.linkszf</groupId>
 *     <artifactId>fastcall-spring-boot-starter</artifactId>
 *     <version>1.0.0</version>
 * </dependency>
 * }</pre>
 *
 * <p>Auto-configuration is provided by the transitively included modules through
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports},
 * so no extra annotation is required.</p>
 */
package io.github.linkszf.fastcall.starter;
