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

import java.util.HashSet;
import java.util.Set;

import static net.rainbowcreation.extension.server.config.RedstoneBlockConfig.redstoneBlock;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class RedStone {
    // event handler for disable redstone events

    private static final Set<Block> redstoneRelatedBlocks = new HashSet<>();

    static {
        // Add redstone-related blocks to the set during initialization
        redstoneRelatedBlocks.add(Blocks.REDSTONE_BLOCK);
        redstoneRelatedBlocks.add(Blocks.REDSTONE_LAMP);
        redstoneRelatedBlocks.add(Blocks.REDSTONE_TORCH);
        redstoneRelatedBlocks.add(Blocks.REDSTONE_WIRE);
        redstoneRelatedBlocks.add(Blocks.OBSERVER);
        redstoneRelatedBlocks.add(Blocks.POWERED_COMPARATOR);
        redstoneRelatedBlocks.add(Blocks.UNPOWERED_COMPARATOR);
        redstoneRelatedBlocks.add(Blocks.POWERED_REPEATER);
        redstoneRelatedBlocks.add(Blocks.UNPOWERED_REPEATER);
        // Add more blocks if needed
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRedstoneActivation(BlockEvent.NeighborNotifyEvent event) {
        if (!redstoneBlock.DESTROY_BLOCK_WHEN_POWERED)
            return;
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (world.isBlockPowered(pos)) {
            IBlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (redstoneRelatedBlocks.contains(block))
                world.destroyBlock(pos, false);
        }
    }

    @SubscribeEvent
    public static void onPlayerPlaceBlock(BlockEvent.PlaceEvent event) {
        if (!redstoneBlock.ENABLE)
            return;
        EntityPlayer player = event.getPlayer();
        IBlockState blockState = event.getPlacedBlock();
        // Check if the player is right-clicking to place a block
        // Check if the block being placed is redstone-related
        if (redstoneRelatedBlocks.contains(blockState.getBlock())) {
            player.sendMessage(new TextComponentString(TextFormatting.BOLD  + "[Redstone Block] " + TextFormatting.RED + "Redstone disabled on this server"));
            event.setCanceled(true);
        }
    }
}