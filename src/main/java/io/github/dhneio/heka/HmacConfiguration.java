package io.github.dhneio.heka;

public class HmacConfiguration {
    public enum HashFunction {
        MD5,
        SHA1
    }

    public final String signer;
    public final int keyVersion;
    public final HashFunction hashFunction;
    public final String key;

    public HmacConfiguration(String signer, int keyVersion, HashFunction hashFunction, String key) {
        this.signer = signer;
        this.keyVersion = keyVersion;
        this.hashFunction = hashFunction;
        this.key = key;
    }
}
