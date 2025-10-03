package net.dravigen.letMeMove;

import btw.AddonHandler;
import btw.BTWAddon;

public class LetMeMoveAddon extends BTWAddon {
    private static LetMeMoveAddon instance;

    public static LetMeMoveAddon getInstance() {
        return instance == null ? (new LetMeMoveAddon()) : instance;
    }

    public LetMeMoveAddon() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
}