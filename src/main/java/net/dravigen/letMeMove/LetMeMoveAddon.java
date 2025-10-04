package net.dravigen.letMeMove;

import btw.AddonHandler;
import btw.BTWAddon;
import net.dravigen.letMeMove.render.AnimationRegistry;

public class LetMeMoveAddon extends BTWAddon {
    public LetMeMoveAddon() {
        super();
    }

    @Override
    public void initialize() {
        AnimationRegistry.registerAllAnimation();
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
}