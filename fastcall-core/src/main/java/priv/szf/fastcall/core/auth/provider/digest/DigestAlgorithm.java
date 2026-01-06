package priv.szf.fastcall.core.auth.provider.digest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DigestAlgorithm {

    MD2("MD2"),

    MD2_SESS("MD2-sess"),

    MD5("MD5"),

    MD5_SESS("MD5-sess"),

    SHA_1("SHA-1"),

    SHA_1_SESS("SHA-1-sess"),

    SHA_256("SHA-256"),

    SHA_256_SESS("SHA-256-sess"),

    SHA_384("SHA-384"),

    SHA_384_SESS("SHA-384-sess"),

    SHA_512("SHA-512"),

    SHA_512_SESS("SHA-512-sess"),

    SHA_512_256("SHA-512-256"),

    SHA_512_256_SESS("SHA-512-256-sess");

    private final String value;

    public static DigestAlgorithm fromString(String text) {
        DigestAlgorithm[] values = DigestAlgorithm.values();
        for (DigestAlgorithm algo : values) {
            if (algo.value.equalsIgnoreCase(text)) {
                return algo;
            }
        }
        throw new IllegalArgumentException(String.format("暂不支持的Digest加密算法: %s", text));
    }

    public String getHashAlgorithm() {
        return this.value.replace("-sess", "").replace("-", "");
    }

    public boolean isSessionAlgorithm() {
        return this.value.endsWith("-sess");
    }

}
