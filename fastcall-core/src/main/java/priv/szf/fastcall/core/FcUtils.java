package priv.szf.fastcall.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FcUtils {

    public static byte[] readRequestBody(Request request) {
        RequestBody body = request.body();
        if (Objects.isNull(body)) {
            return new byte[0];
        }

        try (Buffer buffer = new Buffer()) {
            body.writeTo(buffer);
            return buffer.readByteArray();
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occurred when reading request body");
        }

    }

    public static void cacheRequestBodySnapshot(Request request, byte[] bodyBytes) {
        FcRequestContext context = request.tag(FcRequestContext.class);
        if (Objects.isNull(context)) {
            return;
        }
        context.getInterceptorContext().setRequestBodySnapshot(bodyBytes);
    }

    public static Request rebuildRequestWithBodySnapshot(Request request) {
        RequestBody body = request.body();
        if (Objects.isNull(body)) {
            return request;
        }

        FcRequestContext context = request.tag(FcRequestContext.class);
        if (Objects.isNull(context)) {
            return request;
        }

        byte[] snapshot = context.getInterceptorContext().getRequestBodySnapshot();
        if (Objects.isNull(snapshot)) {
            return request;
        }

        RequestBody clonedBody = RequestBody.create(snapshot, body.contentType());
        return request.newBuilder()
                .method(request.method(), clonedBody)
                .build();
    }

    public static int encodeId(@NonNull String id) {
        return id.hashCode();
    }


}
