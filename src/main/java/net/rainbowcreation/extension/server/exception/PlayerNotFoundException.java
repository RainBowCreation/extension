package net.rainbowcreation.extension.server.exception;

public class PlayerNotFoundException extends LoginException {
  private static final long serialVersionUID = 1L;
  
  public PlayerNotFoundException() {
    super("You're not registered as a player.");
  }
}