package mta.patmal.enigma.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class InputCollector {
    private final Scanner scanner;

    public InputCollector() {
        this.scanner = new Scanner(System.in);
    }

    public int getMenuChoice() {
        System.out.print("\nEnter your choice: ");
        
        while (true) {
            try {
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    return choice;
                } else {
                    scanner.nextLine();
                    System.out.println("Invalid input. Please enter a number between 1 and 8.");
                    System.out.print("Enter your choice: ");
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number between 1 and 8.");
                System.out.print("Enter your choice: ");
            }
        }
    }

    public String getFilePath() {
        System.out.print("Enter XML file path: ");
        return scanner.nextLine().trim();
    }

    public String getText() {
        System.out.print("Enter text to process: ");
        return scanner.nextLine().trim();
    }

    public List<Integer> getRotorIds(int totalRotors, int required) {
        while (true) {
            System.out.print("Please enter " + required + " rotor IDs (separated by commas): ");
            String input = scanner.nextLine().trim();
            
            if (shouldReturnToMenu(input)) {
                return null;
            }
            
            try {
                return parseRotorIds(input, required);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                if (!confirmRetry()) {
                    return null;
                }
            }
        }
    }

    public String getPositions(String abc, int required) {
        while (true) {
            System.out.print("Please enter " + required + " initial position characters: ");
            String input = scanner.nextLine().trim();
            
            if (shouldReturnToMenu(input)) {
                return null;
            }
            
            if (input.isEmpty()) {
                System.out.println("Error: Initial positions cannot be empty.");
                if (!confirmRetry()) {
                    return null;
                }
                continue;
            }
            
            if (input.length() != required) {
                System.out.println("Error: Expected exactly " + required + " characters, but got " + input.length() + ".");
                if (!confirmRetry()) {
                    return null;
                }
                continue;
            }
            
            return input;
        }
    }

    public int getReflectorChoice(int totalReflectors) {
        while (true) {
            System.out.print("Please select a reflector (1-" + totalReflectors + "): ");
            String input = scanner.nextLine().trim();
            
            if (shouldReturnToMenu(input)) {
                return -1;
            }
            
            try {
                int reflectorId = Integer.parseInt(input);
                if (reflectorId < 1 || reflectorId > totalReflectors) {
                    System.out.println("Error: Reflector ID must be between 1 and " + totalReflectors + ".");
                    if (!confirmRetry()) {
                        return -1;
                    }
                    continue;
                }
                return reflectorId;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format. Please enter a number between 1 and " + totalReflectors + ".");
                if (!confirmRetry()) {
                    return -1;
                }
            }
        }
    }

    public boolean confirmRetry() {
        System.out.print("Would you like to try again? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    private boolean shouldReturnToMenu(String input) {
        return input.equalsIgnoreCase("exit") || 
               input.equalsIgnoreCase("menu") || 
               input.equalsIgnoreCase("back") || 
               input.equalsIgnoreCase("cancel");
    }

    private List<Integer> parseRotorIds(String input, int required) {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Rotor IDs cannot be empty. Please enter " + required + " rotor IDs separated by commas.");
        }

        String[] parts = input.split(",");
        if (parts.length != required) {
            throw new IllegalArgumentException("Expected exactly " + required + " rotor IDs, but got " + parts.length + ".");
        }

        List<Integer> rotorIds = new ArrayList<>();
        for (String part : parts) {
            try {
                int rotorId = Integer.parseInt(part.trim());
                rotorIds.add(rotorId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid rotor ID format: '" + part.trim() + "'. Rotor IDs must be decimal numbers.");
            }
        }

        return rotorIds;
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
