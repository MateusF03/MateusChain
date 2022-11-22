package me.mateus.mateuschain;

import me.mateus.mateuschain.util.CryptUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Block {

    private final String previousHash;
    private final long timestamp;
    private String merkleRoot;
    private final List<Transaction> transactions = new ArrayList<>();
    private String hash;

    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return CryptUtils.stringToSha256(previousHash + timestamp + nonce + merkleRoot);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = CryptUtils.getMerkleRoot(transactions);
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

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null)
            return false;
        if (!Objects.equals(previousHash, "0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transação falhou ao processar");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transação adicionada ao bloco!");
        return true;
    }
    public String getPreviousHash() {
        return previousHash;
    }
}
