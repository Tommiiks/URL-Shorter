package com.urlshorter.urlshorter.exception;

public class UrlExpiredException extends RuntimeException {
  public UrlExpiredException(String shortCode) {
    super("URL scaduto: " + shortCode);
  }

}
