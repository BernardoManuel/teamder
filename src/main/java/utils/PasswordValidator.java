package utils;

public class PasswordValidator {
    private static final int MIN_LENGTH = 8; // Longitud mínima de la contraseña
    private static final int MIN_DIGITS = 1; // Mínimo de dígitos requeridos
    private static final int MIN_UPPERCASE = 1; // Mínimo de letras mayúsculas requeridas
    private static final int MIN_LOWERCASE = 1; // Mínimo de letras minúsculas requeridas
    private static final int MIN_SPECIAL = 1; // Mínimo de caracteres especiales requeridos

    public static boolean validateLength(String password) {
        // Verificar la longitud de la contraseña
        if (password.length() < MIN_LENGTH) {
            return false;
        }else{
            return true;
        }
    }

    /**
     * Metodo que valida el numero minimo de digitos en la contraseña.
     * @param password contraseña a validar
     * @return
     */
    public static boolean validateDigits(String password) {
        // Verificar la cantidad de dígitos
        int digitCount = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                digitCount++;
            }
        }
        if (digitCount < MIN_DIGITS) {
            return false;
        }else{
            return true;
        }
    }

    /**
     * Metodo que valida el numero minimo de mayusculas en la contraseña.
     * @param password contraseña a validar
     * @return
     */
    public static boolean validateUpperCase(String password) {
        // Verificar la cantidad de letras mayúsculas
        int uppercaseCount = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                uppercaseCount++;
            }
        }
        if (uppercaseCount < MIN_UPPERCASE) {
            return false;
        }else{
            return true;
        }
    }

    /**
     * Metodo que valida el numero minimo de minusculas en la contraseña.
     * @param password contraseña a validar
     * @return
     */
    public static boolean validateLowerCase(String password) {
        // Verificar la cantidad de letras minúsculas
        int lowercaseCount = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                lowercaseCount++;
            }
        }
        if (lowercaseCount < MIN_LOWERCASE) {
            return false;
        }else{
            return true;
        }
    }

    /**
     * Metodo que valida el numero minimo de caracteres especiales en la contraseña.
     * @param password contraseña a validar
     * @return
     */
    public static boolean validateSpecialChars(String password) {
        // Verificar la cantidad de caracteres especiales
        int specialCount = 0;
        String specialChars = "!@#$%^&*()-+";
        for (int i = 0; i < password.length(); i++) {
            if (specialChars.contains(Character.toString(password.charAt(i)))) {
                specialCount++;
            }
        }
        if (specialCount < MIN_SPECIAL) {
            return false;
        }else{
            return true;
        }
    }

}

