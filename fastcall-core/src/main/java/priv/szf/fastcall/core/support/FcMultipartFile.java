package priv.szf.fastcall.core.support;

import lombok.Getter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A {@link MultipartFile} implementation backed by a byte array.
 * <p>
 * It wraps content already fully loaded into memory (such as an HTTP response body or a {@code byte[]}) as a Spring file object,
 * so that it can take part in file uploads, Spring Web file argument handling, and similar scenarios.
 * <p>
 * Content never changes after an instance is created: {@link #getBytes()} returns the internal array directly,
 * {@link #getInputStream()} returns a new byte stream on every call, so it can be read repeatedly.
 */
@Getter
public class FcMultipartFile implements MultipartFile {

    /**
     * Form field name.
     */
    private final String name;

    /**
     * Original file name, possibly {@code null}.
     */
    private final String originalFilename;

    /**
     * Content type, possibly {@code null}.
     */
    private final String contentType;

    private final byte[] bytes;

    public FcMultipartFile(String name, String originalFilename, String contentType, byte[] bytes) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.bytes = Objects.isNull(bytes) ? new byte[0] : bytes;
    }

    @Override
    public boolean isEmpty() {
        return this.bytes.length == 0;
    }

    @Override
    public long getSize() {
        return this.bytes.length;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (Objects.isNull(dest)) {
            throw new IllegalArgumentException("Destination file must not be null");
        }
        FileCopyUtils.copy(this.bytes, dest);
    }
}
