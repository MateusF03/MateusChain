package me.mateus.mateuschain;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MateusChain {

    private static final List<Block> blockchain = new ArrayList<>();
    public static final Map<String, TransactionOutput> UTXOs = new HashMap<>();
    private static final int difficulty = 3;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100.0f, null);
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        genesisTransaction.setId("0");
        genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getId()));
        UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        System.out.println("Criando e minerando o primeiro bloco");

        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block b1 = new Block(genesis.getHash());
        System.out.println("\nOs fundos da conta A: " + walletA.getBalance());
        System.out.println("\nA conta A está mandando 40 coins para a conta B");
        b1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(b1);
        System.out.println("\nOs fundos da conta A: " + walletA.getBalance());
        System.out.println("Os fundos da conta B: " + walletB.getBalance());

        Block b2 = new Block(b1.getHash());
        System.out.println("\nA conta A está mandando 1000 coins para a conta B");
        b2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        addBlock(b2);
        System.out.println("\nOs fundos da conta A: " + walletA.getBalance());
        System.out.println("Os fundos da conta B: " + walletB.getBalance());

        Block b3 = new Block(b2.getHash());
        System.out.println("\nA conta B está mandando 20 coins para a conta A");
        b3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20.0f));
        System.out.println("\nOs fundos da conta A: " + walletA.getBalance());
        System.out.println("Os fundos da conta B: " + walletB.getBalance());

        System.out.println("\n\nA chain é valida? :" + isChainValid() );
    }

    private static void addBlock(Block block) {
        block.mineBlock(difficulty);
        blockchain.add(block);
    }

    public static boolean isChainValid() {
        for (int i = 1; i < blockchain.size(); i++) {
            Block current = blockchain.get(i);
            Block previous = blockchain.get(i - 1);
            if (!current.getHash().equals(current.calculateHash())) {
                System.out.println("Hashes não são iguais");
                return false;
            }
            if (!previous.getHash().equals(current.getPreviousHash())) {
                System.out.println("Hashes passados não são iguais");
                return false;
            }
        }
        return true;
    }
}
