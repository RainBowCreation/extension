package net.rainbowcreation.extension.server.event.clearlag;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.rainbowcreation.extension.server.Main;
import net.rainbowcreation.extension.server.utils.Reference;

import static net.rainbowcreation.extension.server.config.RedstoneBlockConfig.redstoneBlock;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class RedStone {
    // event handler for disable redstone events

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRedstoneActivation(BlockEvent.NeighborNotifyEvent event) {
        if (!redstoneBlock.DESTROY_BLOCK_WHEN_POWERED)
            return;
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (world.isBlockPowered(pos)) {
            IBlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            Main.LOGGER.info(block);
            if (block == Blocks.TRAPPED_CHEST || block == Blocks.LEVER) {
                return;
            }
            if (block != Blocks.REDSTONE_BLOCK || block != Blocks.REDSTONE_LAMP || block != Blocks.REDSTONE_TORCH || block != Blocks.REDSTONE_WIRE)
            if (block.getBlockHardness(blockState, world, pos) > 0) {
                world.destroyBlock(pos, false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerPlaceBlock(BlockEvent.PlaceEvent event) {
        if (!redstoneBlock.ENABLE)
            return;
        EntityPlayer player = event.getPlayer();
        IBlockState blockState = event.getPlacedBlock();
        // Check if the player is right-clicking to place a block
        Material material = blockState.getMaterial();
        if (blockState.getBlock() == Blocks.LEVER)
            return;
        // Check if the block being placed is redstone-related
        if (material == Material.CIRCUITS|| blockState.getBlock() instanceof BlockObserver || material == Material.PISTON) {
            player.sendMessage(new TextComponentString(TextFormatting.BOLD  + "[Redstone Block] " + TextFormatting.RED + "Redstone disabled on this server"));
            event.setCanceled(true);
        }
    }
}