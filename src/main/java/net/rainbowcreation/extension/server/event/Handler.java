package net.rainbowcreation.extension.server.event;

import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextFormatting;
import net.rainbowcreation.extension.server.config.GenaralConfig;
import net.rainbowcreation.extension.server.config.LoginerConfig;
import net.rainbowcreation.extension.server.model.PlayerDescriptor;
import net.rainbowcreation.extension.server.model.PlayerPos;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.rainbowcreation.extension.server.utils.Reference;

@EventBusSubscriber
public class Handler {
  private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
  
  private static final Map<EntityPlayer, PlayerDescriptor> descriptors = new HashMap<>();
  
  private static final Map<EntityPlayer, Boolean> logged = new HashMap<>();
  
  private static final String WELCOME = LoginerConfig.i18n.welcome;
  
  private static final String WAKE_UP = String.format(LoginerConfig.i18n.delay, new Object[] { Integer.toString(LoginerConfig.delay) });
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
    EntityPlayer player = event.player;

    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        double x, y, z;
        x = -23.5;
        y = 63.5;
        z = 0.5;
        player.setPositionAndUpdate(x, y, z);
        BlockPos pos = player.getPosition();
        float yaw = player.rotationYaw, pitch = player.rotationPitch;
        PlayerPos pp = new PlayerPos(pos, yaw, pitch);
        PlayerDescriptor dc = new PlayerDescriptor(player, pp);
        descriptors.put(player, dc);
        scheduler.schedule(() -> {
          if (descriptors.containsKey(player)) {
            descriptors.remove(player);
            logged.remove(player);
            ((EntityPlayerMP)player).connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString(WAKE_UP)));
          }
        }, LoginerConfig.delay, TimeUnit.SECONDS);
        player.sendMessage(new TextComponentString(TextFormatting.BOLD + "[Queue] " + TextFormatting.RESET + "enter the portal to join queue"));
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        SPacketTitle packetActionBar = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentString(player.getName()), 20,120,20);
        playerMP.connection.sendPacket(Reference.PACKET_TITLE);
        playerMP.connection.sendPacket(Reference.PACKET_SUB_TITLE);
        playerMP.connection.sendPacket(packetActionBar);
        player.sendMessage(new TextComponentString(TextFormatting.BOLD + "[Auth] " + TextFormatting.RESET + "please login or register\n/login <password>\nor\n/register <password> <password>"));
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLeave(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        logged.remove(event.player);
        break;
      }
    }
  }
  
  @SubscribeEvent
  public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
      switch (GenaralConfig.settings.MODE) {
        case ("lobby"): {
          if (descriptors.containsKey(event.player)) {
            PlayerPos pp = ((PlayerDescriptor) descriptors.get(event.player)).getPosition();
            BlockPos pos = pp.getPosition();
            ((EntityPlayerMP) event.player).connection.setPlayerLocation(pos.getX(), pos.getY(), pos.getZ(), pp.getYaw(), pp.getPitch());
          }
          break;
        }
      }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onPlayerEvent(PlayerEvent event) {
    EntityPlayer entity = event.getEntityPlayer();
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        if (descriptors.containsKey(entity) && event.isCancelable()) {
          event.setCanceled(true);
          entity.sendMessage((ITextComponent) new TextComponentString(WELCOME));
        }
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onCommand(CommandEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        String name = event.getCommand().getName();
        if (descriptors.containsKey(event.getSender()) &&
                !name.equals("register") && !name.equals("login") && !name.equals("logged") && event
                .getSender() instanceof EntityPlayer && event
                .isCancelable()) {
          event.setCanceled(true);
          event.getSender().sendMessage((ITextComponent) new TextComponentString(WELCOME));
        }
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onChatEvent(ServerChatEvent event) {
    EntityPlayerMP entity = event.getPlayer();
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        if (event.isCancelable() && descriptors.containsKey(entity)) {
          event.setCanceled(true);
          event.getPlayer().sendMessage((ITextComponent) new TextComponentString(WELCOME));
        }
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onTossEvent(ItemTossEvent event) {
    EntityPlayer entity = event.getPlayer();
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        if (event.isCancelable() && descriptors.containsKey(entity)) {
          event.setCanceled(true);
          entity.inventory.addItemStackToInventory(event.getEntityItem().getItem());
          event.getPlayer().sendMessage((ITextComponent) new TextComponentString(WELCOME));
        }
        break;
      }
    }
  }
  
  private static void handleLivingEvents(LivingEvent event, Entity entity) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        if (event.getEntity() instanceof EntityPlayer && event.isCancelable() && descriptors.containsKey(entity)) {
          event.setCanceled(true);
          entity.sendMessage((ITextComponent) new TextComponentString(WELCOME));
        }
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLivingAttackEvent(LivingAttackEvent event) {
      switch (GenaralConfig.settings.MODE) {
        case ("lobby"): {
          handleLivingEvents((LivingEvent) event, event.getEntity());
          break;
        }
      }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLivingDeathEvent(LivingDeathEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        handleLivingEvents((LivingEvent) event, event.getEntity());
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLivingEntityUseItemEvent(LivingEntityUseItemEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        handleLivingEvents((LivingEvent) event, event.getEntity());
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLivingHealEvent(LivingHealEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        handleLivingEvents((LivingEvent) event, event.getEntity());
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLivingHurtEvent(LivingHurtEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        handleLivingEvents((LivingEvent) event, event.getEntity());
        break;
      }
    }
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onLivingSetTargetAttackEvent(LivingSetAttackTargetEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        if (event.getTarget() instanceof EntityPlayer && descriptors.containsKey(event.getTarget()))
          ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
        break;
      }
    }
  }
  
  @SubscribeEvent
  public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
    switch (GenaralConfig.settings.MODE) {
      case ("lobby"): {
        if (event.getModID().equals(Reference.MODID))
          ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
        break;
      }
    }
  }
  
  public void authorizePlayer(EntityPlayer player) {
    logged.put(player, Boolean.valueOf(true));
    descriptors.remove(player);
  }
  
  public boolean isLogged(EntityPlayer player) {
    return ((Boolean)logged.getOrDefault(player, Boolean.valueOf(false))).booleanValue();
  }
}