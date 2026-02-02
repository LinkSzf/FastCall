package priv.szf.fastcall.core;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import priv.szf.fastcall.common.exception.FcUnexpectedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

public final class FcUtils {

    private FcUtils() {}

    public static byte[] readRequestBody(Request request) {
        RequestBody body = request.body();
        if (Objects.isNull(body)) {
            return new byte[0];
        }

        try (Buffer buffer = new Buffer()) {
            body.writeTo(buffer);

            RequestBody cloneRequestBody = RequestBody.create(buffer.clone().readByteArray(), body.contentType());
            Field f = Request.class.getDeclaredField("body");
            f.setAccessible(true);
            f.set(request, cloneRequestBody);

            return buffer.readByteArray();
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            throw new FcUnexpectedException(e, "IO exception occurred when reading request body");
        }

    }




}
