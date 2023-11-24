package net.rainbowcreation.extension.server.command;

import net.rainbowcreation.extension.server.Main;
import net.rainbowcreation.extension.server.config.LoginerConfig;
import net.rainbowcreation.extension.server.event.loginer.Handler;
import net.rainbowcreation.extension.server.exception.InvalidEmailException;
import net.rainbowcreation.extension.server.exception.InvalidPasswordException;
import net.rainbowcreation.extension.server.exception.PlayerAlreadyExistException;
import net.rainbowcreation.extension.server.exception.RegistrationException;
import net.rainbowcreation.extension.server.exception.WrongPasswordConfirmationException;
import net.rainbowcreation.extension.server.guard.datasource.IDataSourceStrategy;
import net.rainbowcreation.extension.server.guard.payload.IPayload;
import net.rainbowcreation.extension.server.guard.payload.RegistrationPayload;
import net.rainbowcreation.extension.server.guard.registration.Registrator;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.Logger;

public class RegisterCommand implements ICommand {
  private static final Logger LOGGER = Main.LOGGER;
  
  private final List<String> aliases;
  
  private final Registrator registrator;
  
  private final Handler handler;
  
  private final boolean emailRequired;
  
  public RegisterCommand(Handler handler, IDataSourceStrategy strategy, boolean emailRequired) {
    this.handler = handler;
    this.aliases = new ArrayList<>();
    this.aliases.add("reg");
    this.registrator = new Registrator(strategy);
    this.emailRequired = emailRequired;
  }
  
  public RegisterCommand(Handler handler, IDataSourceStrategy strategy) {
    this(handler, strategy, false);
  }

  @Override
  public String getName() {
    return "register";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return this.emailRequired ? LoginerConfig.i18n.registerUsage : LoginerConfig.i18n.registerAlternativeUsage;
  }

  @Override
  public List<String> getAliases() {
    return this.aliases;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    EntityPlayer player = (EntityPlayer)sender;
    String playerName = player.getDisplayNameString();
    LOGGER.info(String.format("%s is using /register", new Object[] { playerName }));
    if (args.length == (this.emailRequired ? 3 : 2)) {
      if (!this.handler.isLogged(player)) {
        try {
          LOGGER.info(String.format("Forging payload for player %s", new Object[] { playerName }));
          RegistrationPayload payload = createPayload(player, args);
          LOGGER.info(String.format("Registering payload for player %s", new Object[] { playerName }));
          this.registrator.register(payload);
          LOGGER.info(String.format("Authorizing player %s", new Object[] { playerName }));
          this.handler.authorizePlayer(player);
          sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.registerSuccess));
        } catch (ArrayIndexOutOfBoundsException e) {
          sender.sendMessage((ITextComponent)new TextComponentString(getUsage(sender)));
        } catch (InvalidEmailException e) {
          sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.loginInvalidEmail));
        } catch (PlayerAlreadyExistException e) {
          sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.registerExist));
        } catch (WrongPasswordConfirmationException e) {
          sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.registerWrongPasswordConfirmation));
        } catch (InvalidPasswordException e) {
          sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.registerPasswordTooShort));
        } catch (RegistrationException e) {
          LOGGER.error(e.getMessage());
          sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.error));
        } 
      } else {
        sender.sendMessage((ITextComponent)new TextComponentString(LoginerConfig.i18n.registerAlreadyLogged));
      } 
    } else {
      sender.sendMessage((ITextComponent)new TextComponentString(getUsage(sender)));
    } 
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return true;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
    return new ArrayList<>();
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    return true;
  }
  
  public int compareTo(ICommand iCommand) {
    return getName().compareTo(iCommand.getName());
  }
  
  private RegistrationPayload createPayload(EntityPlayer player, String[] args) {
    return new RegistrationPayload((IPayload)LoginCommand.createPayload(this.emailRequired, player, args), this.emailRequired ? args[2] : args[1]);
  }
}