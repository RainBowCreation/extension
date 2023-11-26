package net.rainbowcreation.extension.server.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.rainbowcreation.extension.server.utils.ITime;
import net.rainbowcreation.extension.server.utils.Reference;

@EventBusSubscriber(modid = Reference.MODID)
@Config(modid = Reference.MODID, name = Reference.NAME, category = "clearlag")
public class ClearLagConfig {
    public static ClearLag clearLag = new ClearLag();
    public static class ClearLag {
        @Config.Comment({"ITime between each clear [H, M, S]"})
        public int[] TIME = ITime.secondToTime(1800);

        @Comment({"ITime to start Announced to chat before clear, [H, M, S]"})
        public int[] WARNING_TIME = ITime.secondToTime(900);

        @Comment({"Disable xp drops xp will give to players instead of dropping to the ground"})
        public boolean DISABLE_XP_DROP = true;

        @Comment({"Set to false to disable item clearing"})
        public boolean CLEAR_ITEM = true;

        @Comment({"Amount of monster with same type limit per chunk default is 5 set it to 0 to disable"})
        public int MONSTER_LIMIT = 5;

        @Comment({"Limit tiles entity can be cleared per chunk default is 100 set it to 0 to disable"})
        public int TILE_LIMIT = 100;
    }
}