package ua.nau.lab2;

public class PlayfairCipher {
    private static final String ALPHABET = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ.,_";
    private static final int SQUARE_SIZE = 6; // 6x6 for Ukrainian alphabet + 3 punctuation marks
    private static final char FILLER = 'Х';

    private char[][] matrix;

    public PlayfairCipher(String key) {
        buildMatrix(key);
    }

    private void buildMatrix(String key) {
        matrix = new char[SQUARE_SIZE][SQUARE_SIZE];
        String keyWithoutDuplicates = removeDuplicates(key.toUpperCase());
        String matrixContent = keyWithoutDuplicates + ALPHABET;
        matrixContent = removeDuplicates(matrixContent);

        int k = 0;
        for (int i = 0; i < SQUARE_SIZE; i++) {
            for (int j = 0; j < SQUARE_SIZE; j++) {
                if (k < matrixContent.length()) {
                    matrix[i][j] = matrixContent.charAt(k++);
                }
            }
        }

        // Print the matrix for debugging
        System.out.println("Матриця Playfair:");
        for (int i = 0; i < SQUARE_SIZE; i++) {
            for (int j = 0; j < SQUARE_SIZE; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private String removeDuplicates(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (result.indexOf(String.valueOf(current)) == -1) {
                result.append(current);
            }
        }
        return result.toString();
    }

    public String encrypt(String plaintext) {
        String preparedText = prepareText(plaintext);
        StringBuilder ciphertext = new StringBuilder();

        for (int i = 0; i < preparedText.length(); i += 2) {
            char a = preparedText.charAt(i);
            char b = preparedText.charAt(i + 1);

            int[] posA = findPosition(a);
            int[] posB = findPosition(b);

            char encryptedA;
            char encryptedB;

            if (posA[0] == posB[0]) { // Same row
                encryptedA = matrix[posA[0]][(posA[1] + 1) % SQUARE_SIZE];
                encryptedB = matrix[posB[0]][(posB[1] + 1) % SQUARE_SIZE];
            } else if (posA[1] == posB[1]) { // Same column
                encryptedA = matrix[(posA[0] + 1) % SQUARE_SIZE][posA[1]];
                encryptedB = matrix[(posB[0] + 1) % SQUARE_SIZE][posB[1]];
            } else { // Rectangle
                encryptedA = matrix[posA[0]][posB[1]];
                encryptedB = matrix[posB[0]][posA[1]];
            }

            ciphertext.append(encryptedA).append(encryptedB);
        }

        return ciphertext.toString();
    }

    public String decrypt(String ciphertext) {
        StringBuilder plaintext = new StringBuilder();

        for (int i = 0; i < ciphertext.length(); i += 2) {
            if (i + 1 >= ciphertext.length()) break;

            char a = ciphertext.charAt(i);
            char b = ciphertext.charAt(i + 1);

            int[] posA = findPosition(a);
            int[] posB = findPosition(b);

            char decryptedA;
            char decryptedB;

            if (posA[0] == posB[0]) { // Same row
                decryptedA = matrix[posA[0]][(posA[1] - 1 + SQUARE_SIZE) % SQUARE_SIZE];
                decryptedB = matrix[posB[0]][(posB[1] - 1 + SQUARE_SIZE) % SQUARE_SIZE];
            } else if (posA[1] == posB[1]) { // Same column
                decryptedA = matrix[(posA[0] - 1 + SQUARE_SIZE) % SQUARE_SIZE][posA[1]];
                decryptedB = matrix[(posB[0] - 1 + SQUARE_SIZE) % SQUARE_SIZE][posB[1]];
            } else { // Rectangle
                decryptedA = matrix[posA[0]][posB[1]];
                decryptedB = matrix[posB[0]][posA[1]];
            }

            plaintext.append(decryptedA).append(decryptedB);
        }

        return plaintext.toString();
    }

    private String prepareText(String text) {
        text = text.toUpperCase().replaceAll("[^" + ALPHABET + "]", "");
        StringBuilder prepared = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            prepared.append(text.charAt(i));

            // Add filler character between duplicate letters in a bigram
            if (i + 1 < text.length() && text.charAt(i) == text.charAt(i + 1)) {
                prepared.append(FILLER);
            }
        }

        // Add filler if the length is odd
        if (prepared.length() % 2 != 0) {
            prepared.append(FILLER);
        }

        // Print bigrams for clarity
        System.out.println("Підготовлений текст у біграмах:");
        for (int i = 0; i < prepared.length(); i += 2) {
            if (i + 1 < prepared.length()) {
                System.out.print(prepared.charAt(i) + "" + prepared.charAt(i + 1) + " ");
            }
        }
        System.out.println();

        return prepared.toString();
    }

    private int[] findPosition(char c) {
        for (int i = 0; i < SQUARE_SIZE; i++) {
            for (int j = 0; j < SQUARE_SIZE; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1}; // Character not found
    }
}
