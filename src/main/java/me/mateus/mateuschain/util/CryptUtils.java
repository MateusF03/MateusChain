package me.mateus.mateuschain.util;

import me.mateus.mateuschain.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CryptUtils {

    public static String stringToSha256(String str) {
        try {
            MessageDigest msg = MessageDigest.getInstance("SHA-256");
            byte[] hash = msg.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexStr = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1)
                    hexStr.append(0);
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;

        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousLayer.add(transaction.getId());
        }
        List<String> treeLayer = previousLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousLayer.size(); i++) {
                treeLayer.add(stringToSha256(previousLayer.get(i - 1) + previousLayer.get(i)));
            }
            count = treeLayer.size();
            previousLayer = treeLayer;
        }
        return treeLayer.size() == 1 ? treeLayer.get(0) : "";
    }
}
