package loader;

public class RomanNumeralUtils {

    public static int romanToInt(String roman) {
        if (roman == null || roman.trim().isEmpty()) {
            throw new IllegalArgumentException("Roman numeral cannot be null or empty");
        }

        switch (roman.trim()) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            default:
                throw new IllegalArgumentException("Invalid roman numeral: " + roman + ". Expected I, II, III, IV, or V");
        }
    }

    public static String intToRoman(int value) {
        switch (value) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return Integer.toString(value);
        }
    }
}

