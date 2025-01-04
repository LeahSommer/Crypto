import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BlowfishDecryption {

  
    public static String decryptMessage(byte[] ciphertext, String key) {
        try {
           
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
            
            
            Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            
            // Decrypt the ciphertext
            byte[] decryptedData = cipher.doFinal(ciphertext);
            
            
            return new String(decryptedData);
        } catch (Exception e) {
            System.out.println("Error during decryption: " + e.getMessage());
            return null;  // Decryption failed
        }
    }

    //  key variations
    public static List<String> generateKeyVariations(String word) {
        List<String> variations = new ArrayList<>();
        variations.add(word);  
        variations.add(word + "1"); 
        variations.add("1" + word);  
        variations.add(word.replace("i", "1"));  
        variations.add(word.replace("o", "0"));  
        return variations;
    }

    // Load dictionary 
    public static List<String> loadDictionary(String filePath) throws IOException {
        List<String> dictionary = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Error: The file " + filePath + " does not exist.");
            return dictionary;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.trim());
            }
        }
        return dictionary;
    }

   
    public static void dictionaryAttack(byte[] ciphertext, String dictionaryPath) throws IOException {
        List<String> dictionary = loadDictionary(dictionaryPath);
        long totalAttempts = 0;
        long startTime = System.nanoTime();

        for (String word : dictionary) {
            for (String key : generateKeyVariations(word)) {
                totalAttempts++;
                String plaintext = decryptMessage(ciphertext, key);
                if (plaintext != null) {
                    long endTime = System.nanoTime();
                    long elapsedTime = (endTime - startTime) / 1000000;  
                    System.out.println("Key Found: " + key);
                    System.out.println("Plaintext: " + plaintext);
                    System.out.println("Time Taken: " + elapsedTime + " milliseconds");
                    System.out.println("Total Attempts: " + totalAttempts);
                    return;
                }
            }
        }

        System.out.println("No key found.");
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000;  
        System.out.println("Total Time: " + elapsedTime + " milliseconds");
        System.out.println("Total Attempts: " + totalAttempts);
    }

    
    public static void main(String[] args) throws IOException {
        // Example ciphertext (hex-encoded for demonstration)
        String ciphertextHex = "008287041751963131C83ED6A04BAB1F6D122D943560698DE9432A29BB771BFD03F15527AD7540D00C3FE55BE53DD425";
        byte[] ciphertext = hexStringToByteArray(ciphertextHex);  

        
        String dictionaryPath = "dictionary.txt";  

        dictionaryAttack(ciphertext, dictionaryPath);
    }

    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
