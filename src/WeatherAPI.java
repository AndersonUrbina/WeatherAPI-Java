import java.util.Scanner;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherAPI {
    public static void main(String[] args) {
        //Welcome message
        System.out.println("\nWelcome to the Weather API Reader!");

        //Declare a Scanner
        Scanner scanner = new Scanner(System.in);

        //Prompt the user and get their choice:
        System.out.println("\nThe available locations are: ");
        System.out.println("1. West Bend, Wisconsin");
        System.out.println("2. Milwaukee, Wisconsin");
        System.out.println("3. Chicago, Illinois");
        System.out.print("\nChoose an option: ");

        int choice = scanner.nextInt();

        //Assign values based on choice, or assume West Bend
        String locationName = "";
        double latitude = 0;
        double longitude = 0;


        if (choice == 1) {
            locationName = "West Bend, Wisconsin";
            latitude = 43.4253;
            longitude = -88.1834;
        }
        else if (choice == 2)
        {
            locationName = "Milwaukee, Wisconsin";
            latitude = 43.0389;
            longitude = -87.9065;
        }
        else if (choice == 3)
        {
            locationName = "Chicago, Illinois";
            latitude = 41.8781;
            longitude = -87.6298;
        }
        else
        {
            System.out.println("Invalid choice! Please select a valid location.");
        }

        //Build the URL and Notify the user
        String apiUrl = buildWeatherURL(latitude, longitude);

        System.out.println();
        System.out.println("Location: " + locationName);
        System.out.println("Attempting to connect to the weather API...");
        System.out.println();

        //Try sending the API call
        try{
            String response = getApiRequest(apiUrl);
            System.out.println("Raw API response: ");
            System.out.println(response);
            System.out.println();

            //Print the weather report summary
            System.out.println();
            System.out.println("Simple weather summary: ");

            String time = extractTextValue(response, "time");
            String temperature  = extractNumberValue(response, "temperature_2m");
            String windSpeed = extractNumberValue(response, "wind_speed_10m");

            System.out.println("Current time: " + time);
            System.out.println("Temperature: " + temperature);
            System.out.println("Wind Speed: " + windSpeed);

            System.out.println();
            System.out.println("Weather data retrieved successfully!");

        } catch (IOException e) {
            System.out.println("Network or File Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("The request has been interrupted.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("An Error Occurred: " + e.getMessage());
        }

    }

    //String Method to build the API URL
    public static String buildWeatherURL(double latitude, double longitude)
    {
        return "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,wind_speed_10m"
                + "&temperature_unit=fahrenheit"
                + "&wind_speed_unit=mph";
    }

    //Method to GET the Weather API information
    public static String getApiRequest(String apiURL) throws  IOException, InterruptedException
    {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status code : " + response.statusCode());
        }
        return response.body();
    }

    //Helper method
    public static String extractTextValue(String json, String key){
        int currentIndex = json.indexOf("\"current\":{");

        if (currentIndex == -1) {
            return "Not found";
        }

        String searchText = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchText, currentIndex);

        // Get the hour
        startIndex += 11;

        startIndex = startIndex + searchText.length();
        int endIndex = json.indexOf("\"", startIndex);

        if (endIndex == -1){
            return "Not found";
        }

        return json.substring(startIndex, endIndex);
    }

    public static String extractNumberValue(String json, String key){
        int currentIndex = json.indexOf("\"current\":{");

        if (currentIndex == -1) {
            return "Not found";
        }

        String searchText = "\"" + key + "\":";
        int startIndex = json.indexOf(searchText, currentIndex);

        if (startIndex == -1) {
            return "Not found";
        }

        startIndex = startIndex + searchText.length();
        int endIndex = startIndex;

        while (endIndex < json.length()) {
            char currentChar = json.charAt(endIndex);
            if (currentChar >= '0' && currentChar <= '9' || currentChar == '.' || currentChar == '-') {
                endIndex++;
            } else {
                break;
            }
        }

        //Make sure the value is numeric
        if (startIndex == endIndex){
            return "Not found";
        }

        return json.substring(startIndex, endIndex);
    }
}
