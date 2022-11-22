package me.mateus.mateuschain;

import me.mateus.mateuschain.util.CryptUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String id;
    private final PublicKey sender;
    private final PublicKey recipient;
    private final float value;
    private byte[] signature;

    private final List<TransactionInput> inputs;
    private final List<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return CryptUtils.stringToSha256(CryptUtils.keyToString(sender) + CryptUtils.keyToString(recipient) + value + sequence);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = CryptUtils.keyToString(sender) + CryptUtils.keyToString(recipient) + value;
        signature = CryptUtils.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = CryptUtils.keyToString(sender) + CryptUtils.keyToString(recipient) + value;
        return CryptUtils.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("A assinatura é invalida!");
            return false;
        }
        for (TransactionInput input : inputs) {
            input.setUTXO(MateusChain.UTXOs.get(input.getId()));
        }
        if (getInputsValue() < 0.1f) {
            System.out.println("Valor muito baixo");
            return false;
        }

        float leftOver = getInputsValue() - value;
        id = calculateHash();
        outputs.add(new TransactionOutput(recipient, value, id));
        outputs.add(new TransactionOutput(sender, leftOver, id));
        for (TransactionOutput output : outputs) {
            MateusChain.UTXOs.put(output.getId(), output);
        }

        for (TransactionInput input : inputs) {
            if (input.getUTXO() == null)
                continue;
            MateusChain.UTXOs.remove(input.getUTXO().getId());
        }

        return true;
    }

    private float getInputsValue() {
        float total = 0;
        for (TransactionInput input : inputs) {
           if (input.getUTXO() == null)
               continue;
           total += input.getUTXO().getValue();
        }
        return total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public float getValue() {
        return value;
    }
}
