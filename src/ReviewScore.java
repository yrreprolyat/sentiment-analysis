package sentiment_analysis;

/**
 * Enum that allows for easy of extension of Movie Review categories
 *
 * @author tperry
 */
public enum ReviewScore {
    NEGATIVE {
        @Override
        public String toString() {
            return "Negative";
        }
    },
    POSITIVE {
        @Override
        public String toString() {
            return "Positive";
        }
    },
    UNKNOWN {
        @Override
        public String toString() {
            return "Unknown";
        }
    };

    /**
     * Returns an enum value for a given int value (0, 1, 2)
     *
     * @param x given int value
     * @return ReviewScore object
     */
    public static ReviewScore fromInteger(int x) {
        switch (x) {
            case 0:
                return NEGATIVE;
            case 1:
                return POSITIVE;
            case 2:
                return UNKNOWN;
        }
        return null;
    }

    /**
     * Returns an enum value for a given String value ("Negative", "Positive", "Unknown")
     *
     * @param s String value
     * @return ReviewScore enum value
     */
    public static ReviewScore fromString(String s) {
        switch (s) {
            case "Negative":
                return NEGATIVE;
            case "Positive":
                return POSITIVE;
            case "Unknown":
                return UNKNOWN;
        }
        return null;
    }
}
