package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private Pattern pattern;
    private Matcher matcher;

    // Este patrón de expresión regular valida correos electrónicos según las especificaciones de RFC 5322
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Metodo que valida si el email es valido segun el patron de expresion regular.
     * @param email
     * @return
     */
    public boolean validate(final String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
