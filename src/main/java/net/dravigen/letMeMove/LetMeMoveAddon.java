package net.dravigen.letMeMove;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.world.util.data.DataEntry;
import btw.world.util.data.DataProvider;
import net.dravigen.letMeMove.render.AnimationRegistry;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;

import static net.dravigen.letMeMove.render.AnimationRegistry.STANDING_ID;

public class LetMeMoveAddon extends BTWAddon {
    public LetMeMoveAddon() {
        super();
    }
    private static final String CURRENT_ANIMATION_NAME = "CurrentAnimation";
    public static final DataEntry.PlayerDataEntry<String> CURRENT_ANIMATION = DataProvider.getBuilder(String.class)
            .name(CURRENT_ANIMATION_NAME)
            .defaultSupplier(() -> String.valueOf(STANDING_ID))
            .readNBT(NBTTagCompound::getString)
            .writeNBT(NBTTagCompound::setString)
            .player()
            .syncPlayer()
            .buildPlayer();

    public static ResourceLocation getDataID(EntityPlayer player, DataEntry.PlayerDataEntry<String> animation) {
        String[] s = player.getData(animation).split(":");
        return new ResourceLocation(s[0].toUpperCase(), s[1]);
    }

    @Override
    public void initialize() {
        AnimationRegistry.registerAllAnimation();
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }


    @Override
    public void preInitialize() {
        CURRENT_ANIMATION.register();
    }
}