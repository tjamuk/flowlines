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
//            "\u001B[41m",
//            "\u001B[44m",
//            "\u001B[42m",
//            "\u001B[103m",
//            "\u001B[45m",
//            "\u001B[43m",
//            "\u001B[47m",
//            "\u001B[40m",
//            "\u001B[100m",
//            "\u001B[0m"
            "\033[48;5;0m",
            "\033[48;5;1m",
            "\033[48;5;2m",
            "\033[48;5;3m",
            "\033[48;5;4m",
            "\033[48;5;5m",
            "\033[48;5;6m",
            "\033[48;5;7m",
            "\033[48;5;8m",
            "\033[48;5;9m",
            "\033[48;5;10m",
            "\033[48;5;11m",
            "\033[48;5;12m",
            "\033[48;5;13m",
            "\033[48;5;14m",
            "\033[48;5;15m",
            "\033[48;5;47m",
            "\033[48;5;208m",
            "\033[48;5;181m",
            "\033[48;5;228m",
            "\033[48;5;218m",




    };

    public static Colour getEnumFromOrdinal(int ordinal)
    {
        return colours[ordinal];
    }

    public static String getBackgroundFromOrdinal(int ordinal)
    {
        return colourToBackground[ordinal];
    }

    public static int getColourCount()
    {
        return colourToBackground.length;
    }

    public static char getCharFromColour(int ordinal)
    {
        return colourToChar[ordinal];
    }
}
