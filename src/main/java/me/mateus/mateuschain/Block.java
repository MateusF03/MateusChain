package me.mateus.mateuschain;

import me.mateus.mateuschain.util.CryptUtils;

public class Block {

    private final String previousHash;
    private final String data;
    private final long timestamp;
    private String hash;

    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return CryptUtils.stringToSha256(previousHash + timestamp + nonce + data);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0','0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Bloco minerado: " + hash);
    }
    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}
