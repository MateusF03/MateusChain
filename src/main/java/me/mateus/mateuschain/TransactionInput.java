package me.mateus.mateuschain;

public class TransactionInput {
    private final String id;
    private TransactionOutput UTXO;

    public TransactionInput(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }

    public void setUTXO(TransactionOutput UTXO) {
        this.UTXO = UTXO;
    }
}
