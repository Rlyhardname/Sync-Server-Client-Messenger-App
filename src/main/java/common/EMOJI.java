package common;

public enum EMOJI {
    CHECK_MARK,
    COOL_FACE,
    X_SYMBOL;

    public static String getEmoji(EMOJI emoji) {
        return switch (emoji) {
            case CHECK_MARK -> Character.toString(0x00002705);
            case X_SYMBOL -> Character.toString(0x0001F5D9);
            case COOL_FACE -> Character.toString(0x1F60D);
            default -> "null";
        };

    }

}
