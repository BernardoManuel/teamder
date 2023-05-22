package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator {
    private static final int MIN_LENGTH = 6; // Longitud mínima del nombre de usuario
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]+$"; // Patrón de expresión regular para el nombre de usuario

    /**
     * Metodo que valida contiene al menos 6 caracteres. Y si cumple con el patron de expresion regular.
     * @param username
     * @return
     */
    public static boolean validate(String username) {
        // Verificar la longitud del nombre de usuario
        if (username.length() < MIN_LENGTH) {
            return false;
        }

        // Verificar si el nombre de usuario cumple con el patrón de expresión regular
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
