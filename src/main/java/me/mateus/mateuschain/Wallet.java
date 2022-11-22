package me.mateus.mateuschain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {

    private final Map<String, TransactionOutput> UTXOs = new HashMap<>();
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Wallet() {
        generateKeys();
    }

    public void generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, rnd);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0.0f;
        for (Map.Entry<String, TransactionOutput> entry : MateusChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = entry.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.getId(), UTXO);
                total += UTXO.getValue();
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) {
            System.out.println("Você não tem fundos suficientes...");
            return null;
        }
        List<TransactionInput> inputs = new ArrayList<>();

        float total = 0.0f;
        for (Map.Entry<String, TransactionOutput> e : UTXOs.entrySet()) {
            TransactionOutput UTXO = e.getValue();

            total += UTXO.getValue();
            inputs.add(new TransactionInput(UTXO.getId()));
            if (total > value)
                break;
        }
        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);
        for (TransactionInput input : inputs) {
            UTXOs.remove(input.getId());
        }
        return newTransaction;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
