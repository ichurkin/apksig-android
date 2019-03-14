package android.util;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class Base64DataException extends IOException {

  public Base64DataException(final String detailMessage) {
    super(detailMessage);
  }

}
