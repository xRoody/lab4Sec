package com.example.lab4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;


public class Lab4Application {
    static ObjectMapper mapper=new ObjectMapper();

    public static void main(String[] args) throws IOException, SignatureException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select option:");
            System.out.println("1 - calc and write hash to file");
            System.out.println("2 - verify hashes");
            System.out.println("3 - close");
            int input = scanner.nextInt();
            switch (input) {
                case 1 -> {
                    scanner.nextLine();
                    System.out.print("enter target file: ");
                    String path = scanner.nextLine();
                    System.out.print("enter hash location (file will be created if does not exist): ");
                    String hashPath = scanner.nextLine();
                    writeHash(path, hashPath);
                    System.out.println();
                }
                case 2 -> {
                    scanner.nextLine();
                    System.out.print("enter target file: ");
                    String path = scanner.nextLine();
                    System.out.print("enter hash location: ");
                    String hashPath = scanner.nextLine();
                    System.out.println("Result is: " + verifyHash(path, hashPath));
                    System.out.println();
                }
                case 3 -> {
                    return;
                }
            }
        }
        //writeHash("src/main/resources/test", "src/main/resources/test-hash");
        //System.out.println(verifyHash("src/main/resources/test", "src/main/resources/test-hash"));
    }

    public static void writeHash(String path, String hashPath) throws InvalidKeyException, SignatureException {
        try (FileInputStream fileInputStream = new FileInputStream(path);
             FileOutputStream fileOutputStream = new FileOutputStream(hashPath+"/sign")) {
            KeyPairGenerator generator=KeyPairGenerator.getInstance("DSA");
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            generator.initialize(1024, random);
            KeyPair keyPair=generator.generateKeyPair();
            mapper.writeValue(new File(hashPath+"/public.json"), Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            Signature signature=Signature.getInstance("SHAwithDSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(fileInputStream.readAllBytes());
            byte[] sha256 = signature.sign();
            fileOutputStream.write(sha256);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static boolean verifyHash(String path, String hashPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        File file1 = new File(path);
        if (!file1.exists() || !file1.isFile()) return false;
        File file2 = new File(hashPath+"/sign");
        if (!file2.exists() || !file2.isFile()) return false;
        try(FileInputStream fil=new FileInputStream(file1);FileInputStream sfil=new FileInputStream(file2) ){
            byte[] encodedPb = Base64.getDecoder().decode(mapper.readValue(new File(hashPath+"/public.json"), KeyDTO.class).getKey());
            KeyFactory kf = KeyFactory.getInstance("DSA");
            X509EncodedKeySpec keySpecPb = new X509EncodedKeySpec(encodedPb);
            PublicKey publicKey = kf.generatePublic(keySpecPb);
            Signature signature=Signature.getInstance("SHAwithDSA");
            signature.initVerify(publicKey);
            signature.update(fil.readAllBytes());
            byte[] bis=sfil.readAllBytes();
            return signature.verify(bis);
        }
    }
}
