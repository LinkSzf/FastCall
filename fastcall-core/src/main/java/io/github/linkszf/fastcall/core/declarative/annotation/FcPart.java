package io.github.linkszf.fastcall.core.declarative.annotation;

import io.github.linkszf.fastcall.common.FcMediaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method argument to a multipart form part; declaring any part makes the whole request multipart.
 * Values may be {@code MultipartFile}, {@code File}, {@code byte[]}, {@code InputStream}, {@code RequestBody} or a scalar.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcPart {

    /**
     * Part name in the multipart form, required and not allowed to be blank.
     * An iterable or array argument emits one part per element under this name.
     */
    String value();

    /**
     * File name reported for the part, blank by default.
     * When blank, files use their own name while byte arrays, streams and request bodies fall back to the part name.
     */
    String fileName() default "";

    /**
     * Content type of binary or object parts, {@code application/octet-stream} by default.
     * For a {@code MultipartFile} the default lets the file's own content type be used instead.
     */
    FcMediaType mediaType() default FcMediaType.APPLICATION_OCTET_STREAM;
}
