package net.rainbowcreation.extension.server.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.rainbowcreation.extension.server.utils.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
@Config(modid = Reference.MODID, name = Reference.NAME, category = "redstoneblock")
public class RequiemConfig {
    public static Requiem requiem = new Requiem();
    public static class Requiem {
        @Config.Comment({"Enabled?"})
        public boolean ENABLE = true;

        @Config.Comment({"Requiem Threshold in % default 33%"})
        public int threshold = 33;
    }
}
