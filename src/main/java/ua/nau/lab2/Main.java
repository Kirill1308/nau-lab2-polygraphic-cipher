package ua.nau.lab2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("ПОЛІГРАМНІ ШИФРИ");
        System.out.println("1. Шифр Playfair");
        System.out.println("2. Шифр Хілла");
        System.out.print("Оберіть тип шифру (1-2): ");
        int cipherType = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введіть ім'я вхідного файлу (або залиште порожнім для використання текста за замовчуванням): ");
        String inputFileName = scanner.nextLine();

        System.out.print("Введіть ім'я вихідного файлу: ");
        String outputFileName = scanner.nextLine();

        String plaintext;
        if (inputFileName.isEmpty()) {
            System.out.print("Введіть ваше ПІБ: ");
            String pib = scanner.nextLine();
            plaintext = "Я, " + pib + ", студент університету";
        } else {
            try {
                plaintext = readFromFile(inputFileName);
            } catch (IOException e) {
                System.out.println("Помилка читання файлу: " + e.getMessage());
                return;
            }
        }

        System.out.println("Відкритий текст: " + plaintext);

        String result = "";

        switch (cipherType) {
            case 1: // Playfair
                System.out.print("Введіть ключове слово для шифру Playfair: ");
                String playfairKey = scanner.nextLine().toUpperCase();

                System.out.println("1. Шифрування");
                System.out.println("2. Дешифрування");
                System.out.print("Оберіть операцію (1-2): ");
                int operationPlayfair = scanner.nextInt();
                scanner.nextLine(); // consume newline

                PlayfairCipher playfairCipher = new PlayfairCipher(playfairKey);

                if (operationPlayfair == 1) {
                    result = playfairCipher.encrypt(plaintext.toUpperCase());
                } else {
                    result = playfairCipher.decrypt(plaintext.toUpperCase());
                }
                break;

            case 2: // Hill
                System.out.println("Введіть розмірність матриці для шифру Хілла (m x m): ");
                int m = scanner.nextInt();
                scanner.nextLine();

                int[][] keyMatrix = new int[m][m];
                System.out.println("Введіть елементи матриці ключа (цілі числа):");
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < m; j++) {
                        System.out.print("k[" + (i + 1) + "][" + (j + 1) + "]: ");
                        keyMatrix[i][j] = scanner.nextInt();
                    }
                }
                scanner.nextLine();

                System.out.println("1. Шифрування");
                System.out.println("2. Дешифрування");
                System.out.print("Оберіть операцію (1-2): ");
                int operationHill = scanner.nextInt();
                scanner.nextLine();

                HillCipher hillCipher = new HillCipher(keyMatrix);

                if (operationHill == 1) {
                    result = hillCipher.encrypt(plaintext.toUpperCase());
                } else {
                    result = hillCipher.decrypt(plaintext.toUpperCase());
                }
                break;

            default:
                System.out.println("Невірний вибір шифру.");
                return;
        }

        System.out.println("Результат: " + result);

        try {
            writeToFile(outputFileName, result);
            System.out.println("Результат збережено у файл: " + outputFileName);
        } catch (IOException e) {
            System.out.println("Помилка запису у файл: " + e.getMessage());
        }
    }

    private static String readFromFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().trim();
    }

    private static void writeToFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }
}
