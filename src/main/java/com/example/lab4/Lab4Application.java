package com.example.lab4;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;


public class Lab4Application {

    public static void main(String[] args) throws IOException {
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

    public static void writeHash(String path, String hashPath) {
        try (FileInputStream fileInputStream = new FileInputStream(path);
             FileOutputStream fileOutputStream = new FileOutputStream(hashPath)) {
            byte[] sha256 = DigestUtils.sha256(fileInputStream.readAllBytes());
            fileOutputStream.write(sha256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean verifyHash(String path, String hashPath) throws IOException {
        File file1 = new File(path);
        if (!file1.exists() || !file1.isFile()) return false;
        File file2 = new File(hashPath);
        if (!file2.exists() || !file2.isFile()) return false;
        FileInputStream fileInputStream = new FileInputStream(file1);
        FileInputStream fileInputStream1 = new FileInputStream(file2);
        byte[] sha256 = DigestUtils.sha256(fileInputStream.readAllBytes());
        byte[] orig = fileInputStream1.readAllBytes();
        return Arrays.equals(sha256, orig);
    }
}
