import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipCodeProcessor {
    private static final String API_URL = "https://api.zippopotam.us/us/";
    private static final String CSV_FILE = System.getProperty("user.dir") + File.separator + "zip_data.csv";
    private static final String INVALID_ZIP_FILE = System.getProperty("user.dir") + File.separator + "invalid_zipcodes.txt";
    private static final String ZIP_FILE = System.getProperty("user.dir") + File.separator + "zipcodes.txt";

    public static void main(String[] args) {
        ZipCodeProcessor processor = new ZipCodeProcessor();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:");
        System.out.println("1. Enter a single ZIP code");
        System.out.println("2. Process ZIP codes from file (zipcodes.txt)");
        System.out.print("Enter your choice (1 or 2): ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter the ZIP code: ");
            String zipCode = scanner.nextLine();
            processor.processZipCode(zipCode);
        } else if (choice.equals("2")) {
            processor.processZipCodesFromFile();
        } else {
            System.out.println("Invalid choice. Exiting program.");
        }

        scanner.close();
    }

    private void processZipCodesFromFile() {
        List<String> zipCodes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ZIP_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                zipCodes.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading ZIP codes file: " + e.getMessage());
            return;
        }

        // Create CSV file with headers
        createCsvWithHeaders();

        // Process each ZIP code
        for (String zipCode : zipCodes) {
            processZipCode(zipCode);
        }

        System.out.println("Processing complete. Check " + CSV_FILE + " for valid ZIP data and " + 
                          INVALID_ZIP_FILE + " for invalid ZIP codes.");
    }

    private void processZipCode(String zipCode) {
        try {
            String jsonResponse = fetchDataFromApi(zipCode);
            if (jsonResponse != null) {
                parseJsonAndSaveToCsv(zipCode, jsonResponse);
            }
        } catch (Exception e) {
            System.out.println("Error processing ZIP code " + zipCode + ": " + e.getMessage());
            logInvalidZipCode(zipCode, "Error: " + e.getMessage());
        }
    }

    private String fetchDataFromApi(String zipCode) throws IOException {
        URL url = new URL(API_URL + zipCode);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } else if (responseCode == 404) {
            System.out.println("ZIP code " + zipCode + " not found");
            logInvalidZipCode(zipCode, "Not found (404)");
            return null;
        } else {
            System.out.println("API request failed with response code: " + responseCode);
            logInvalidZipCode(zipCode, "API error: " + responseCode);
            return null;
        }
    }

    private void parseJsonAndSaveToCsv(String zipCode, String jsonResponse) {
        try {
            // Extract country
            Pattern countryPattern = Pattern.compile("\"country\"\\s*:\\s*\"([^\"]+)\"");
            Matcher countryMatcher = countryPattern.matcher(jsonResponse);
            if (!countryMatcher.find()) {
                System.out.println("Country not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - country not found");
                return;
            }
            String country = countryMatcher.group(1);

            // Check if places array exists and has elements
            if (!jsonResponse.contains("\"places\"") || !jsonResponse.contains("\"place name\"")) {
                System.out.println("Places data not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - places data not found");
                return;
            }

            // Extract place name
            Pattern placeNamePattern = Pattern.compile("\"place name\"\\s*:\\s*\"([^\"]+)\"");
            Matcher placeNameMatcher = placeNamePattern.matcher(jsonResponse);
            if (!placeNameMatcher.find()) {
                System.out.println("Place name not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - place name not found");
                return;
            }
            String placeName = placeNameMatcher.group(1);

            // Extract state
            Pattern statePattern = Pattern.compile("\"state\"\\s*:\\s*\"([^\"]+)\"");
            Matcher stateMatcher = statePattern.matcher(jsonResponse);
            if (!stateMatcher.find()) {
                System.out.println("State not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - state not found");
                return;
            }
            String state = stateMatcher.group(1);

            // Extract state abbreviation
            Pattern stateAbbrPattern = Pattern.compile("\"state abbreviation\"\\s*:\\s*\"([^\"]+)\"");
            Matcher stateAbbrMatcher = stateAbbrPattern.matcher(jsonResponse);
            if (!stateAbbrMatcher.find()) {
                System.out.println("State abbreviation not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - state abbreviation not found");
                return;
            }
            String stateAbbreviation = stateAbbrMatcher.group(1);

            // Extract latitude
            Pattern latitudePattern = Pattern.compile("\"latitude\"\\s*:\\s*\"([^\"]+)\"");
            Matcher latitudeMatcher = latitudePattern.matcher(jsonResponse);
            if (!latitudeMatcher.find()) {
                System.out.println("Latitude not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - latitude not found");
                return;
            }
            String latitude = latitudeMatcher.group(1);

            // Extract longitude
            Pattern longitudePattern = Pattern.compile("\"longitude\"\\s*:\\s*\"([^\"]+)\"");
            Matcher longitudeMatcher = longitudePattern.matcher(jsonResponse);
            if (!longitudeMatcher.find()) {
                System.out.println("Longitude not found in response for ZIP code " + zipCode);
                logInvalidZipCode(zipCode, "Invalid JSON response - longitude not found");
                return;
            }
            String longitude = longitudeMatcher.group(1);

            // Append data to CSV
            appendToCsv(zipCode, country, placeName, state, stateAbbreviation, latitude, longitude);

            System.out.println("Successfully processed ZIP code: " + zipCode);
        } catch (Exception e) {
            System.out.println("Error parsing JSON for ZIP code " + zipCode + ": " + e.getMessage());
            logInvalidZipCode(zipCode, "Error parsing response: " + e.getMessage());
        }
    }

    private void createCsvWithHeaders() {
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            writer.append("ZIP Code,Country,Place Name,State,State Abbreviation,Latitude,Longitude\n");
        } catch (IOException e) {
            System.out.println("Error creating CSV file: " + e.getMessage());
        }
    }

    private void appendToCsv(String zipCode, String country, String placeName, String state, 
                            String stateAbbreviation, String latitude, String longitude) throws IOException {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            writer.append(zipCode).append(",");
            writer.append(country).append(",");
            writer.append(escapeCsvField(placeName)).append(",");
            writer.append(escapeCsvField(state)).append(",");
            writer.append(stateAbbreviation).append(",");
            writer.append(latitude).append(",");
            writer.append(longitude).append("\n");
        }
    }

    private String escapeCsvField(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private void logInvalidZipCode(String zipCode, String reason) {
        try (FileWriter writer = new FileWriter(INVALID_ZIP_FILE, true)) {
            writer.append(zipCode).append(" - ").append(reason).append("\n");
        } catch (IOException e) {
            System.out.println("Error logging invalid ZIP code: " + e.getMessage());
        }
    }
}
