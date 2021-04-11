package util.password;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordEncrypt {
    private Random random = new SecureRandom();
    private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int numOfIterations = 10000;
    private final int keyLength = 256; 
    
    public String getSalt(int length) 
    {
       StringBuilder returnValue = new StringBuilder(length);
       for (int i = 0; i < length; i++) {
           returnValue.append(alphabet.charAt(random.nextInt(alphabet.length())));
       }
       return new String(returnValue);
    }
     
    public byte[] hash(char[] password, byte[] salt) 
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, numOfIterations, keyLength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Error while hashing a password: " + ex.getMessage());
        } finally {
            spec.clearPassword();
        }
        return null;
    }
    
    public String generateSecurePassword(String password, String salt) {
        String returnValue = null;
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
 
        returnValue = Base64.getEncoder().encodeToString(securePassword);
 
        return returnValue;
    }
    
    public boolean verifyUserPassword(String providedPassword,
            String securedPassword, String salt)
    {
        boolean returnValue = false;
        
        // Generate New secure password with the same salt
        String newSecurePassword = generateSecurePassword(providedPassword, salt).substring(1);
        
        // Check if two passwords are equal
        returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);
        return returnValue;
    }
}
