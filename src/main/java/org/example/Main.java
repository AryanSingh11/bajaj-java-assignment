import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException {
        // Parse command line arguments
        if (args.length != 2) {
            System.out.println("Usage: java Main <roll_number> <file_location>");
            return;
        }
        String rollNumber = args[0];
        String fileLocation = args[1];

        // Read JSON file
        JSONParser parser = new JSONParser();
        JSONObject jsonData = (JSONObject) parser.parse(new FileReader(fileLocation));

        // Traverse JSON to find the first instance of the key "destination"
        String destination = getDestination(jsonData);
        if (destination == null) {
            throw new IllegalArgumentException("No 'destination' key found in the JSON data.");
        }

        // Generate a random alphanumeric string of 8 characters
        String randomString = generateRandomString(8);

        // Generate the hash
        String toHash = rollNumber + destination + randomString;
        String hashValue = generateHash(toHash);

        // Append the random string with a ";" after the hash value
        String output = hashValue + ";" + randomString;
        System.out.println(output);
    }

    // Function to traverse JSON and get the value associated with the first instance of the key "destination"
    private static String getDestination(JSONObject jsonObject) {
        for (Object key : jsonObject.keySet()) {
            if (key.equals("destination")) {
                return (String) jsonObject.get(key);
            } else if (jsonObject.get(key) instanceof JSONObject) {
                String result = getDestination((JSONObject) jsonObject.get(key));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Function to generate a random alphanumeric string
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    // Function to generate the hash
    private static String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}