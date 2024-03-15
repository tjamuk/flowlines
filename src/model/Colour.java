package model;

public enum Colour {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE,
    ORANGE,
    WHITE,
    BLACK,
    GREY,
    NONE;

    public static final Colour[] colours = Colour.values();

    private static final char[] colourToChar = {
            'R',
            'B',
            'G',
            'Y',
            'P',
            'O',
            '#'
    };

    private static final String[] colourToBackground = {
            "\u001B[41m",
            "\u001B[44m",
            "\u001B[42m",
            "\u001B[103m",
            "\u001B[45m",
            "\u001B[43m",
            "\u001B[47m",
            "\u001B[40m",
            "\u001B[100m",
            "\u001B[0m"
    };

    public static Colour getEnumFromOrdinal(int ordinal)
    {
        return colours[ordinal];
    }

    public static String getBackgroundFromOrdinal(int ordinal)
    {
        return colourToBackground[ordinal];
    }

    public static char getCharFromColour(int ordinal)
    {
        return colourToChar[ordinal];
    }
}
