package mta.patmal.enigma.dto;

public class CodeConfigurationResultDTO {
    private final boolean success;
    private final String formattedCode;
    private final String message;
    
    public CodeConfigurationResultDTO(boolean success, String formattedCode, String message) {
        this.success = success;
        this.formattedCode = formattedCode;
        this.message = message;
    }
    
    public static CodeConfigurationResultDTO success(String formattedCode) {
        return new CodeConfigurationResultDTO(true, formattedCode, "Code configuration set successfully!");
    }
    
    public static CodeConfigurationResultDTO failure(String message) {
        return new CodeConfigurationResultDTO(false, null, message);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getFormattedCode() {
        return formattedCode;
    }
    
    public String getMessage() {
        return message;
    }
}
