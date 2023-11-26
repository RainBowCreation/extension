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
}
