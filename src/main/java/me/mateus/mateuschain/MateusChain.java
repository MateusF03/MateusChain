package me.mateus.mateuschain;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class MateusChain {

    private static List<Block> blockchain = new ArrayList<>();
    private static final int difficulty = 6;

    public static void main(String[] args) {
        blockchain.add(new Block("Eu sou o bloco 1", "0"));
        System.out.println("Tentando minerar o bloco 1...");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Eu sou o bloco 2", blockchain.get(blockchain.size() - 1).getHash()));
        System.out.println("Tentando minerar o bloco 2...");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Eu sou o bloco 3", blockchain.get(blockchain.size() - 1).getHash()));
        System.out.println("Tentando minerar o bloco 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("A chain é valida? :" + isChainValid());

        String jsonStr = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nA blockchain:\n" + jsonStr);
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
