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
 * 以字节数组承载的 {@link MultipartFile} 实现。
 * <p>
 * 用于把已完整载入内存的内容（例如 HTTP 响应体、{@code byte[]}）包装成 Spring 的文件对象，
 * 便于继续参与文件上传、Spring Web 的文件参数处理等场景。
 * <p>
 * 实例创建后内容不再变化：{@link #getBytes()} 直接返回内部数组，
 * {@link #getInputStream()} 每次返回一个新的字节流，可重复读取。
 */
@Getter
public class FcMultipartFile implements MultipartFile {

    /**
     * 表单字段名。
     */
    private final String name;

    /**
     * 原始文件名，可能为 {@code null}。
     */
    private final String originalFilename;

    /**
     * 内容类型，可能为 {@code null}。
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
