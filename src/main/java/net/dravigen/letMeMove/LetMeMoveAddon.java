package net.dravigen.letMeMove;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.world.util.data.DataEntry;
import btw.world.util.data.DataProvider;
import net.dravigen.letMeMove.render.AnimationRegistry;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;

import static net.dravigen.letMeMove.render.AnimationRegistry.STANDING_ID;

public class LetMeMoveAddon extends BTWAddon {

    private static final String CURRENT_ANIMATION_NAME = "CurrentAnimation";
    public static final DataEntry.PlayerDataEntry<String> CURRENT_ANIMATION = DataProvider.getBuilder(String.class)
            .name(CURRENT_ANIMATION_NAME)
            .defaultSupplier(() -> String.valueOf(STANDING_ID))
            .readNBT(NBTTagCompound::getString)
            .writeNBT(NBTTagCompound::setString)
            .player()
            .syncPlayer()
            .buildPlayer();

    public static KeyBinding crawl_key;
    public static KeyBinding roll_key;

    public static KeyBinding[] addonKeys;

    public LetMeMoveAddon() {
        super();
    }

    public static ResourceLocation getDataID(EntityPlayer player, DataEntry.PlayerDataEntry<String> animation) {
        String[] s = player.getData(animation).split(":");
        return new ResourceLocation(s[0].toUpperCase(), s[1]);
    }

    @Override
    public void initialize() {
        initKeybind();
        AnimationRegistry.registerAllAnimation();
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }

    public void initKeybind(){
        crawl_key = new KeyBinding(StatCollector.translateToLocal("Crawl"), Keyboard.KEY_C);
        roll_key = new KeyBinding(StatCollector.translateToLocal("Roll"), Keyboard.KEY_V);

        addonKeys = new KeyBinding[]{
                crawl_key,
                roll_key
        };
    }

    @Override
    public void preInitialize() {
        CURRENT_ANIMATION.register();
    }
}