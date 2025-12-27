package mta.patmal.enigma.console;

import mta.patmal.enigma.engine.Engine;


public class ConsoleUI {
    private final MenuDisplay display;
    private final InputCollector input;
    private final EngineOperations operations;

    public ConsoleUI(Engine engine) {
        this.display = new MenuDisplay();
        this.input = new InputCollector();
        this.operations = new EngineOperations(engine, display, input);
    }

    public void run() {
        while (true) {
            display.showMainMenu();
            int choice = input.getMenuChoice();
            processUserChoice(choice);
        }
    }

    private void processUserChoice(int choice) {
        switch (choice) {
            case 1: // Load XML
                operations.handleLoadXml();
                break;

            case 2: // Show Machine Data
                operations.handleShowMachineData();
                break;

            case 3: // Code Manual
                operations.handleManualConfiguration();
                break;

            case 4: // Code Automatic
                operations.handleAutomaticConfiguration();
                break;

            case 5: // Process
                operations.handleProcess();
                break;

            case 6: // Reset Current Code
                operations.handleResetCode();
                break;

            case 7: // Statistics
                operations.handleStatistics();
                break;

            case 8: // Exit
                display.displayExiting();
                input.close();
                System.exit(0);
                break;

            default:
                display.displayError("Invalid option. Please enter a number between 1 and 8.");
        }
    }
}
