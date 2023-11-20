package net.rainbowcreation.extension.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.rainbowcreation.extension.server.config.GenaralConfig;
import net.rainbowcreation.extension.server.event.Handler;
import net.rainbowcreation.extension.server.utils.IString;
import net.rainbowcreation.extension.server.utils.Packet;
import net.rainbowcreation.extension.server.utils.Reference;
import net.rainbowcreation.extension.server.command.LoggedCommand;
import net.rainbowcreation.extension.server.command.LoginCommand;
import net.rainbowcreation.extension.server.command.RegisterCommand;
import net.rainbowcreation.extension.server.config.LoginerConfig;
import net.rainbowcreation.extension.server.guard.datasource.DatabaseSourceStrategy;
import net.rainbowcreation.extension.server.guard.datasource.FileDataSourceStrategy;
import net.rainbowcreation.extension.server.guard.datasource.IDataSourceStrategy;
import net.rainbowcreation.extension.server.guard.datasource.db.ConnectionFactory;
import net.rainbowcreation.extension.server.guard.datasource.db.IConnectionFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.rainbowcreation.extension.server.utils.Time;
import org.apache.logging.log4j.Logger;

import static net.rainbowcreation.extension.server.config.GenaralConfig.settings;

@Mod.EventBusSubscriber
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.12.2]")
public class Main {
  @Mod.Instance
  public static Main instance;
  public static Logger LOGGER = FMLLog.log;
  public static File config;
  private static int staticTime;

  private static int timeRemaining;
  private static int[] timePrevious = new int[3];
  private static List<String> whitelist;
  public static List<String> blacklist;
  private static int Tick = 20;
  private static int tick = Tick;
  private static int[] M_TIME_TO_1;
  private static TextComponentString MAINTENANCE_TEXT = new TextComponentString(TextFormatting.RED + "Daily Maintenance " + TextFormatting.RESET);

  private Handler handler;
  
  private IDataSourceStrategy dataSourceStrategy;
  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) throws Exception {
    LOGGER = event.getModLog();
    for (String txt: Reference.HEADER)
      LOGGER.info(txt);
    LOGGER.info(IString.genHeader(Reference.NAME+":"+Reference.VERSION+":"+settings.MODE));
    switch (LoginerConfig.dataSourceStrategy) {
      case DATABASE:
        this.dataSourceStrategy = (IDataSourceStrategy)new DatabaseSourceStrategy(LoginerConfig.database.table, (IConnectionFactory)new ConnectionFactory(LoginerConfig.database.dialect, LoginerConfig.database.host, LoginerConfig.database.port, LoginerConfig.database.database, LoginerConfig.database.user, LoginerConfig.database.password));
        LOGGER.info("Now using DatabaseSourceStrategy.");
        return;
      case FILE:
        this
          
          .dataSourceStrategy = (IDataSourceStrategy)new FileDataSourceStrategy(Paths.get(event.getModConfigurationDirectory().getAbsolutePath(), new String[] { Reference.NAME+".csv" }).toFile());
        LOGGER.info("Now using FileDataSourceStrategy.");
        return;
    } 
    this.dataSourceStrategy = null;
    LOGGER.info("Unknown guard strategy selected. Nothing will happen.");
  }
  
  @EventHandler
  public void serverStarting(FMLServerStartingEvent event) {
    if (LoginerConfig.dataSourceStrategy != null) {
      this.handler = new Handler();
      if (LoginerConfig.enableAuthentication) {
        LOGGER.info("Registering Main event handler");
        MinecraftForge.EVENT_BUS.register(this.handler);
        LOGGER.info("Registering Main /login command");
        event.registerServerCommand((ICommand)new LoginCommand(this.handler, this.dataSourceStrategy, LoginerConfig.emailRequired));
        LOGGER.info("Registering Main /logged command");
        event.registerServerCommand((ICommand)new LoggedCommand(this.handler));
      } 
      if (LoginerConfig.enableRegistration) {
        LOGGER.info("Registering Main /register command");
        event.registerServerCommand((ICommand)new RegisterCommand(this.handler, this.dataSourceStrategy, LoginerConfig.emailRequired));
      } 
    }
    Time.TIME = Time.getTimeInSecond(settings.TIME);
    staticTime = Time.TIME;
    timeRemaining = staticTime;
    whitelist = Arrays.asList(GenaralConfig.whitelist.ITEM_WHITELIST);
    blacklist = Arrays.asList(GenaralConfig.blacklist.DO_NOT_BROADCAST_TITLE_TO);
    Time.WARNING_TIME = Time.getTimeInSecond(settings.WARNING_TIME);
    int i = Time.WARNING_TIME;
    while (i > 10) {
      Time.WARNING_TIME_LIST.add(i);
      i/=2;
    }
    for (int j = 1; j <= 10; j++)
      Time.WARNING_TIME_LIST.add(j);
    Time.prefix = settings.TIME_ZONE_PREFIX;
    Time.offset = settings.TIME_ZONE_OFFSET;
    timePrevious = Time.getCurrentTime();
    String[] TIME_FROM = Time.timeToString(settings.M_TIME_FROM);
    String[] TIME_TO = Time.timeToString(settings.M_TIME_TO);
    M_TIME_TO_1 = Time.getSubstract(settings.M_TIME_TO, new int[]{0,0,1});
    MAINTENANCE_TEXT.appendSibling(new TextComponentString(TIME_FROM[0] + ":" + TIME_FROM[1] + TextFormatting.RED + "-" + TextFormatting.RESET + TIME_TO[0] + ":" + TIME_TO[1] + TextFormatting.RED));
  }


  @SubscribeEvent
  public static void worldTick(TickEvent.WorldTickEvent event) {
    if (tick > 0) {
      tick--;
      return;
    }
    else
      tick = Tick;
    int[] time = Time.getCurrentTime();
    if (time[2] == timePrevious[2])
      return;
    World world = event.world;
    PlayerList playerList = world.getMinecraftServer().getPlayerList();
    List<EntityPlayerMP> plist = playerList.getPlayers();
    if (timeRemaining == 0) {
      if (settings.clearItems) {
        int amount = 0;
        for (Entity entity : world.loadedEntityList) {
          if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem) entity;
            if (!whitelist.contains(((ResourceLocation) Item.REGISTRY.getNameForObject(item.getItem().getItem())).toString())) {
              item.setDead();
              amount++;
            }
          }
        }
        if (settings.MODE.equals("server"))
          playerList.sendMessage((ITextComponent) new TextComponentString(TextFormatting.BOLD + "[Clear Lag] " + TextFormatting.RESET + "Cleared " + TextFormatting.RED + amount + TextFormatting.RESET + " items!"));
      }
      timeRemaining = staticTime;
      return;
    }
    switch (settings.MODE) {
      case ("server"): {
        if (!settings.MAINTENANCE)
          break;
        if (time[0] != timePrevious[0]) {
          playerList.sendMessage(new TextComponentString(TextFormatting.BOLD + "[Maintenance Scheduler] " + TextFormatting.RESET).appendSibling(MAINTENANCE_TEXT));
        }
        Time.alert(settings.M_TIME_FROM, "Maintenance Scheduler", "Server close in", playerList, true);
        Time.alert(timeRemaining, "Clear Lag", "Items will be cleared in", playerList);
        if (Time.getSubstractInSecond(settings.M_TIME_FROM, Time.getCurrentTime()) == 0) {
          world.getMinecraftServer().initiateShutdown();
          return;
        }
        break;
      }
      case ("lobby"): {
        if (!settings.MAINTENANCE)
          break;
        if (time[0] >= settings.M_TIME_FROM[0] && time[0] < settings.M_TIME_TO[0]) {
          TextComponentString text = new TextComponentString("Time remaining ");
          if (time[0] < M_TIME_TO_1[0])
            text.appendSibling(new TextComponentString(TextFormatting.RED + String.valueOf(M_TIME_TO_1[0] - time[0]) + TextFormatting.RESET + ":"));
          String minute = String.valueOf(M_TIME_TO_1[1] - time[1]);
          String second = String.valueOf(M_TIME_TO_1[2] - time[2]);
          if (minute.length() == 1)
            minute = "0" + minute;
          if (second.length() == 1)
            second = "0" + second;
          text.appendSibling(new TextComponentString(TextFormatting.RED + minute + TextFormatting.RESET + ":" + TextFormatting.RED + second));
          for (EntityPlayerMP playerMP : plist) {
            Packet.sent(playerMP, MAINTENANCE_TEXT, SPacketTitle.Type.TITLE, 0, 100, 0);
            Packet.sent(playerMP, text, SPacketTitle.Type.SUBTITLE, 0, 100, 0);
          }
        }
        else if (time[0] == settings.M_TIME_TO[0] && time[1] == settings.M_TIME_TO[1] && time[2] == settings.M_TIME_TO[2]) {
          for (EntityPlayerMP playerMP : plist)
            playerMP.connection.sendPacket(Reference.PACKET_MAINTENANCE_COMPLETE);
          playerList.sendMessage(new TextComponentString(TextFormatting.BOLD + "[Maintenance Completed]" ));
        }
        break;
      }
    }
    timeRemaining-= Time.getSubstractInSecond(time, timePrevious);
    timePrevious = time;
  }
}