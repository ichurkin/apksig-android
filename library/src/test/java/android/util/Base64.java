package android.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

@SuppressWarnings("WeakerAccess")
public final class Base64 {

  private Base64() {}

  public static final int DEFAULT = 0;
  public static final int NO_PADDING = 1;
  public static final int NO_WRAP = 2;
  public static final int CRLF = 4;
  public static final int URL_SAFE = 8;
  public static final int NO_CLOSE = 16;

  private static final Charset US_ASCII;
  static {
    try {
      US_ASCII = Charset.forName("US-ASCII");
    }
    catch (final UnsupportedCharsetException e) {
      throw new AssertionError(e);
    }
  }

  static abstract class Coder {

    public byte[] output;
    public int op;

    public abstract boolean process(final byte[] input, final int offset,
                                    final int len, final boolean finish);

    public abstract int maxOutputSize(final int len);

  }

  public static byte[] decode(final String str, final int flags) {
    return decode(str.getBytes(), flags);
  }

  public static byte[] decode(final byte[] input, final int flags) {
    return decode(input, 0, input.length, flags);
  }

  public static byte[] decode(final byte[] input, final int offset, final int len, final int flags) {
    final Decoder decoder = new Decoder(flags, new byte[len * 3 / 4]);
    if (!decoder.process(input, offset, len, true)) {
      throw new IllegalArgumentException("bad base-64");
    }
    if (decoder.op == decoder.output.length) {
      return decoder.output;
    }
    final byte[] temp = new byte[decoder.op];
    System.arraycopy(decoder.output, 0, temp, 0, decoder.op);
    return temp;
  }

  static class Decoder extends Coder {

    private static final int DECODE[] = {
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
      52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1,
      -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
      15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
      -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
      41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      };

    private static final int DECODE_WEBSAFE[] = {
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1,
      52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1,
      -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
      15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,
      -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
      41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      };

    private static final int SKIP = -1;
    private static final int EQUALS = -2;

    private int state;
    private int value;

    private final int[] alphabet;

    public Decoder(final int flags, final byte[] output) {
      this.output = output;
      alphabet = ((flags & URL_SAFE) == 0) ? DECODE : DECODE_WEBSAFE;
      state = 0;
      value = 0;
    }

    @Override public int maxOutputSize(final int len) {
      return len * 3/4 + 10;
    }

    @Override public boolean process(final byte[] input, final int offset, final int length,
                                     final boolean finish) {
      if (this.state == 6) {
        return false;
      }
      int len = length;
      int p = offset;
      len += offset;
      int state = this.state;
      int value = this.value;
      int op = 0;
      final byte[] output = this.output;
      final int[] alphabet = this.alphabet;
      while (p < len) {
        if (state == 0) {
          while (p + 4 <= len &&
                 (value = ((alphabet[input[p] & 0xff] << 18) |
                           (alphabet[input[p + 1] & 0xff] << 12) |
                           (alphabet[input[p + 2] & 0xff] << 6) |
                           (alphabet[input[p + 3] & 0xff]))) >= 0) {
            output[op + 2] = (byte)value;
            output[op + 1] = (byte)(value >> 8);
            output[op] = (byte)(value >> 16);
            op += 3;
            p += 4;
          }
          if (p >= len) {
            break;
          }
        }
        int d = alphabet[input[p++] & 0xff];
        switch (state) {
          case 0:
            if (d >= 0) {
              value = d;
              ++state;
            }
            else if (d != SKIP) {
              this.state = 6;
              return false;
            }
            break;

          case 1:
            if (d >= 0) {
              value = (value << 6) | d;
              ++state;
            }
            else if (d != SKIP) {
              this.state = 6;
              return false;
            }
            break;

          case 2:
            if (d >= 0) {
              value = (value << 6) | d;
              ++state;
            }
            else if (d == EQUALS) {
              output[op++] = (byte)(value >> 4);
              state = 4;
            }
            else if (d != SKIP) {
              this.state = 6;
              return false;
            }
            break;

          case 3:
            if (d >= 0) {
              value = (value << 6) | d;
              output[op + 2] = (byte)value;
              output[op + 1] = (byte)(value >> 8);
              output[op] = (byte)(value >> 16);
              op += 3;
              state = 0;
            }
            else if (d == EQUALS) {
              output[op + 1] = (byte)(value >> 2);
              output[op] = (byte)(value >> 10);
              op += 2;
              state = 5;
            }
            else if (d != SKIP) {
              this.state = 6;
              return false;
            }
            break;

          case 4:
            if (d == EQUALS) {
              ++state;
            }
            else if (d != SKIP) {
              this.state = 6;
              return false;
            }
            break;

          case 5:
            if (d != SKIP) {
              this.state = 6;
              return false;
            }
            break;
        }
      }
      if (!finish) {
        this.state = state;
        this.value = value;
        this.op = op;
        return true;
      }
      switch (state) {
        case 0:
          break;
        case 1:
          this.state = 6;
          return false;
        case 2:
          output[op++] = (byte)(value >> 4);
          break;
        case 3:
          output[op++] = (byte)(value >> 10);
          output[op++] = (byte)(value >> 2);
          break;
        case 4:
          this.state = 6;
          return false;
        case 5:
          break;
      }
      this.state = state;
      this.op = op;
      return true;
    }

  }

  @SuppressWarnings("unused")
  public static String encodeToString(final byte[] input, final int flags) {
    return new String(encode(input, 0, input.length, flags), US_ASCII);
  }

  public static String encodeToString(final byte[] input, final int offset, final int length,
                                      final int flags) {
    return new String(encode(input, offset, length, flags), US_ASCII);
  }

  public static byte[] encode(final byte[] input, final int flags) {
    return encode(input, 0, input.length, flags);
  }

  public static byte[] encode(final byte[] input, final int offset, final int len, final int flags) {
    final Encoder encoder = new Encoder(flags, null);
    int output_len = len / 3 * 4;
    if (encoder.do_padding) {
      if (len % 3 > 0) {
        output_len += 4;
      }
    }
    else {
      switch (len % 3) {
        case 0:
          break;
        case 1:
          output_len += 2;
          break;
        case 2:
          output_len += 3;
          break;
      }
    }
    if (encoder.do_newline && len > 0) {
      output_len += (((len - 1) / (3 * Encoder.LINE_GROUPS)) + 1) *
                    (encoder.do_cr ? 2 : 1);
    }
    encoder.output = new byte[output_len];
    encoder.process(input, offset, len, true);
    return encoder.output;
  }

  static class Encoder extends Coder {

    public static final int LINE_GROUPS = 19;

    private static final byte ENCODE[] = {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/',
      };

    private static final byte ENCODE_WEBSAFE[] = {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_',
      };

    private final byte[] tail;
    int tailLen;
    private int count;

    final public boolean do_padding;
    final public boolean do_newline;
    final public boolean do_cr;
    final private byte[] alphabet;

    public Encoder(final int flags, final byte[] output) {
      this.output = output;

      do_padding = (flags & NO_PADDING) == 0;
      do_newline = (flags & NO_WRAP) == 0;
      do_cr = (flags & CRLF) != 0;
      alphabet = ((flags & URL_SAFE) == 0) ? ENCODE : ENCODE_WEBSAFE;

      tail = new byte[2];
      tailLen = 0;

      count = do_newline ? LINE_GROUPS : -1;
    }

    @Override public int maxOutputSize(final int len) {
      return len * 8/5 + 10;
    }

    @Override public boolean process(final byte[] input, final int offset, final int length,
                                     final boolean finish) {
      final byte[] alphabet = this.alphabet;
      final byte[] output = this.output;
      int op = 0;
      int count = this.count;
      int len = length;
      int p = offset;
      len += offset;
      int v = -1;
      switch (tailLen) {
        case 0:
          break;
        case 1:
          if (p + 2 <= len) {
            v = ((tail[0] & 0xff) << 16) |
                ((input[p++] & 0xff) << 8) |
                (input[p++] & 0xff);
            tailLen = 0;
          }
          break;
        case 2:
          if (p + 1 <= len) {
            v = ((tail[0] & 0xff) << 16) |
                ((tail[1] & 0xff) << 8) |
                (input[p++] & 0xff);
            tailLen = 0;
          }
          break;
      }
      if (v != -1) {
        output[op++] = alphabet[(v >> 18) & 0x3f];
        output[op++] = alphabet[(v >> 12) & 0x3f];
        output[op++] = alphabet[(v >> 6) & 0x3f];
        output[op++] = alphabet[v & 0x3f];
        if (--count == 0) {
          if (do_cr) {
            output[op++] = '\r';
          }
          output[op++] = '\n';
          count = LINE_GROUPS;
        }
      }
      while (p + 3 <= len) {
        v = ((input[p] & 0xff) << 16) |
            ((input[p + 1] & 0xff) << 8) |
            (input[p + 2] & 0xff);
        output[op] = alphabet[(v >> 18) & 0x3f];
        output[op + 1] = alphabet[(v >> 12) & 0x3f];
        output[op + 2] = alphabet[(v >> 6) & 0x3f];
        output[op + 3] = alphabet[v & 0x3f];
        p += 3;
        op += 4;
        if (--count == 0) {
          if (do_cr) {
            output[op++] = '\r';
          }
          output[op++] = '\n';
          count = LINE_GROUPS;
        }
      }
      if (finish) {
        if (p - tailLen == len - 1) {
          int t = 0;
          v = ((tailLen > 0 ? tail[t++] : input[p++]) & 0xff) << 4;
          tailLen -= t;
          output[op++] = alphabet[(v >> 6) & 0x3f];
          output[op++] = alphabet[v & 0x3f];
          if (do_padding) {
            output[op++] = '=';
            output[op++] = '=';
          }
          if (do_newline) {
            if (do_cr) {
              output[op++] = '\r';
            }
            output[op++] = '\n';
          }
        }
        else if (p - tailLen == len - 2) {
          int t = 0;
          v = (((tailLen > 1 ? tail[t++] : input[p++]) & 0xff) << 10) |
              (((tailLen > 0 ? tail[t++] : input[p++]) & 0xff) << 2);
          tailLen -= t;
          output[op++] = alphabet[(v >> 12) & 0x3f];
          output[op++] = alphabet[(v >> 6) & 0x3f];
          output[op++] = alphabet[v & 0x3f];
          if (do_padding) {
            output[op++] = '=';
          }
          if (do_newline) {
            if (do_cr) {
              output[op++] = '\r';
            }
            output[op++] = '\n';
          }
        }
        else if (do_newline && op > 0 && count != LINE_GROUPS) {
          if (do_cr) {
            output[op++] = '\r';
          }
          output[op++] = '\n';
        }

        assert tailLen == 0;
        assert p == len;
      }
      else {
        if (p == len - 1) {
          tail[tailLen++] = input[p];
        }
        else if (p == len - 2) {
          tail[tailLen++] = input[p];
          tail[tailLen++] = input[p + 1];
        }
      }
      this.op = op;
      this.count = count;
      return true;
    }
  }

}
