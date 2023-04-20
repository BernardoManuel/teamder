package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {

    // Función para generar un salt aleatorio
    public static byte[] generateSalt() {
        byte[] salt = new byte[16]; // Se recomienda un tamaño de salt de al menos 16 bytes
        SecureRandom random = new SecureRandom(); //Clase que genera números aleatorios seguros, que son útiles para operaciones criptográficas como generación de claves y vectores de inicialización.
        random.nextBytes(salt);
        return salt;
    }

    // Función para obtener el hash de una contraseña usando SHA-256 y un salt
    public static byte[] getHashedPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        //Clase calcular resúmenes criptográficos (hash) de datos, como MD5 o SHA-256.
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        //para agregar la sal al proceso de hash
        md.update(salt);
        //calcula el hash de la combinación de la sal y la contraseña
        byte[] hashedPassword = md.digest(password.getBytes());
        return hashedPassword;
    }

    // Función para convertir un arreglo de bytes en una representación hexadecimal
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // Función para convertir una cadena hexadecimal en un arreglo de bytes
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
}

