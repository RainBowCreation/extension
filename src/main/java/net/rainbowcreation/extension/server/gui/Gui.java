package net.rainbowcreation.extension.server.gui;

public class Gui {
    public static Main MAIN;
    public void register() {
        MAIN = new Main();
    }

    public Object get(String key) {
        switch (key) {
            case ("gui_main") : {
                return MAIN;
            }
        }
        return null;
    }
}
