package JWT.JAVA.PROJECT.security.config;

import java.util.regex.Pattern;


public interface PasswordConfig {

    public default String passwordConfig(String password){

        if (password.length() < 8) {
            return "A senha deve conter pelo menos 8 caracteres!";
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "A senha deve conter pelo menos uma letra maiúscula!";
        }

        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return "A senha deve conter pelo menos uma letra minúscula!";
        }
        
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return "A senha deve conter pelo menos um número!";
        }

        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            return "A senha deve conter pelo menos um caractere especial!";
        }

        return null;
    }
    
}   
