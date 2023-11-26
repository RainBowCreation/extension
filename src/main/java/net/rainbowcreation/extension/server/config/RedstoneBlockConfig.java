package net.rainbowcreation.extension.server.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.rainbowcreation.extension.server.utils.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
@Config(modid = Reference.MODID, name = Reference.NAME, category = "redstoneblock")
public class RedstoneBlockConfig {
    public static RedstoneBlock redstoneBlock = new RedstoneBlock();
    public static class RedstoneBlock {
        @Config.Comment({"Enabled?"})
        public boolean ENABLE = true;

        @Config.Comment({"Should destroy block that powered by redstone? default true"})
        public boolean DESTROY_BLOCK_WHEN_POWERED = true;
    }

    public static BlockedList blockedList = new BlockedList();
    public static class BlockedList {
        public boolean DISPENSER = true;
        public boolean NOTE_BLOCK = false;
        public boolean STICKY_PISTON = true;
        public boolean PISTON = true;
        public boolean TNT = false;
        public boolean LEVER = false;
        public boolean PRESSURE_PLATE = false;
        public boolean REDSTONE_TORCH = true;
        public boolean BUTTON = false;
        public boolean TRAP_DOOR = false;
        public boolean FENCE_GATE = false;
        public boolean REDSTONE_LAMP = true;
        public boolean TRIPWIRE_HOOK = false;
        public boolean TRAP_CHEST = false;
        public boolean DAYLIGHT_SENSOR = true;
        public boolean REDSTONE_BLOCK = true;
        public boolean HOPPER = false;
        public boolean DROPPER = true;
        public boolean OBSERVER = true;
        public boolean DOOR = false;
        public boolean REDSTONE_DUST = true;
        public boolean REPEATER = true;
        public boolean COMPARATOR = true;
    }
}
