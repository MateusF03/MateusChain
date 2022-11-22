package me.mateus.mateuschain;

import me.mateus.mateuschain.util.CryptUtils;

import java.security.PublicKey;

public class TransactionOutput {
    private final String id;
    private final PublicKey recipient;
    private final float value;

    public TransactionOutput(PublicKey recipient, float value, String parentId) {
        this.recipient = recipient;
        this.value = value;
        this.id = CryptUtils.stringToSha256(CryptUtils.keyToString(recipient) + value + parentId);
    }

    public String getId() {
        return id;
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey == recipient;
    }

    public float getValue() {
        return value;
    }
}
