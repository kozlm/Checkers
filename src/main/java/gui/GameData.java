package gui;

import gamelogic.Colour;

public class GameData {

    private static final GameData instance = new GameData();
    public static GameData getInstance(){
        return instance;
    }

    private static int mode;
    private static String whiteName, blackName;
    private static Colour whichColour;

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        GameData.mode = mode;
    }

    public static String getWhiteName() {
        return whiteName;
    }

    public static void setWhiteName(String whiteName) {
        GameData.whiteName = whiteName;
    }

    public static String getBlackName() {
        return blackName;
    }

    public static void setBlackName(String blackName) {
        GameData.blackName = blackName;
    }

    public static Colour whichColour() {
        return whichColour;
    }

    public static void setWhichColour(Colour whichColour) {
        GameData.whichColour = whichColour;
    }

}
