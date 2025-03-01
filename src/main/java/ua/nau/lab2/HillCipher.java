package ua.nau.lab2;

public class HillCipher {
    private static final String ALPHABET = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ.,_";
    private static final int MODULO = ALPHABET.length(); // 33 + 3 = 36

    private final int[][] keyMatrix;
    private final int dimension;

    public HillCipher(int[][] keyMatrix) {
        this.keyMatrix = keyMatrix;
        this.dimension = keyMatrix.length;

        // Verify if the matrix is invertible
        if (!isInvertible()) {
            System.out.println("УВАГА: Матриця ключа не має оберненої матриці по модулю " + MODULO);
        }
    }

    public String encrypt(String plaintext) {
        plaintext = plaintext.toUpperCase().replaceAll("[^" + ALPHABET + "]", "");

        // Pad plaintext if necessary
        while (plaintext.length() % dimension != 0) {
            plaintext += "_"; // Padding with underscore
        }

        StringBuilder ciphertext = new StringBuilder();

        for (int i = 0; i < plaintext.length(); i += dimension) {
            int[] vector = new int[dimension];

            // Convert characters to numbers
            for (int j = 0; j < dimension; j++) {
                char c = plaintext.charAt(i + j);
                vector[j] = ALPHABET.indexOf(c);
            }

            // Apply matrix multiplication
            int[] result = matrixMultiply(keyMatrix, vector);

            // Convert back to characters
            for (int j = 0; j < dimension; j++) {
                ciphertext.append(ALPHABET.charAt(result[j]));
            }
        }

        return ciphertext.toString();
    }

    public String decrypt(String ciphertext) {
        ciphertext = ciphertext.toUpperCase().replaceAll("[^" + ALPHABET + "]", "");

        // Ensure ciphertext length is a multiple of dimension
        while (ciphertext.length() % dimension != 0) {
            ciphertext += "_"; // Padding with underscore
        }

        // Calculate inverse key matrix
        int[][] inverseMatrix = modMatrixInverse(keyMatrix, MODULO);

        if (inverseMatrix == null) {
            return "Неможливо дешифрувати: матриця ключа не має оберненої матриці по модулю " + MODULO;
        }

        StringBuilder plaintext = new StringBuilder();

        for (int i = 0; i < ciphertext.length(); i += dimension) {
            int[] vector = new int[dimension];

            // Convert characters to numbers
            for (int j = 0; j < dimension; j++) {
                char c = ciphertext.charAt(i + j);
                vector[j] = ALPHABET.indexOf(c);
            }

            // Apply matrix multiplication with inverse matrix
            int[] result = matrixMultiply(inverseMatrix, vector);

            // Convert back to characters
            for (int j = 0; j < dimension; j++) {
                plaintext.append(ALPHABET.charAt(result[j]));
            }
        }

        return plaintext.toString();
    }

    private boolean isInvertible() {
        return gcd(determinant(keyMatrix, dimension) % MODULO, MODULO) == 1;
    }

    private int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    private int[] matrixMultiply(int[][] matrix, int[] vector) {
        int[] result = new int[dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                result[i] = (result[i] + matrix[i][j] * vector[j]) % MODULO;
            }

            // Ensure positive modulo
            if (result[i] < 0) {
                result[i] += MODULO;
            }
        }

        return result;
    }

    // Calculate determinant of a matrix
    private int determinant(int[][] matrix, int n) {
        if (n == 1) {
            return matrix[0][0];
        }

        if (n == 2) {
            return (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);
        }

        int det = 0;
        int sign = 1;

        for (int i = 0; i < n; i++) {
            det += sign * matrix[0][i] * determinant(getSubMatrix(matrix, 0, i, n), n - 1);
            sign = -sign;
        }

        return det;
    }

    private int[][] getSubMatrix(int[][] matrix, int excludeRow, int excludeCol, int n) {
        int[][] subMatrix = new int[n - 1][n - 1];
        int r = 0, c = 0;

        for (int i = 0; i < n; i++) {
            if (i == excludeRow) continue;

            c = 0;
            for (int j = 0; j < n; j++) {
                if (j == excludeCol) continue;
                subMatrix[r][c++] = matrix[i][j];
            }
            r++;
        }

        return subMatrix;
    }

    // Calculate adjoint (adjugate) of a matrix
    private int[][] adjoint(int[][] matrix) {
        int n = matrix.length;
        int[][] adj = new int[n][n];

        if (n == 1) {
            adj[0][0] = 1;
            return adj;
        }

        int sign;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sign = ((i + j) % 2 == 0) ? 1 : -1;
                adj[j][i] = sign * determinant(getSubMatrix(matrix, i, j, n), n - 1);
            }
        }

        return adj;
    }

    // Calculate modular multiplicative inverse
    private int modInverse(int a, int m) {
        a = ((a % m) + m) % m; // Ensure positive value

        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }

        return -1; // No modular inverse exists
    }

    // Calculate inverse of a matrix modulo m
    private int[][] modMatrixInverse(int[][] matrix, int m) {
        int n = matrix.length;
        int det = determinant(matrix, n);
        det = ((det % m) + m) % m; // Ensure positive value

        int detInv = modInverse(det, m);

        if (detInv == -1) {
            System.out.println("Матриця не має оберненої матриці по модулю " + m);
            return null;
        }

        int[][] adj = adjoint(matrix);
        int[][] inv = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inv[i][j] = ((adj[i][j] % m) + m) % m; // Ensure positive value
                inv[i][j] = (inv[i][j] * detInv) % m;
            }
        }

        return inv;
    }
}
