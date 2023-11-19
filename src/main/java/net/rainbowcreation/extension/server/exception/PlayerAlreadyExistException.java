package net.rainbowcreation.extension.server.exception;

public class PlayerAlreadyExistException extends RegistrationException {
  private static final long serialVersionUID = 1L;
  
  public PlayerAlreadyExistException() {
    super("This player is already registered");
  }
}