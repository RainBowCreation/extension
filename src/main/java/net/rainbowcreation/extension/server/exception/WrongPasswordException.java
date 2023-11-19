package net.rainbowcreation.extension.server.exception;

public class WrongPasswordException extends LoginException {
  private static final long serialVersionUID = 1L;
  
  public WrongPasswordException() {
    super("Your password is incorrect. Please retry.");
  }
}