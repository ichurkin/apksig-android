package android.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("WeakerAccess")
public class Base64OutputStream extends FilterOutputStream {

  private static byte[] EMPTY = new byte[0];

  private final Base64.Coder coder;
  private final int flags;
  private byte[] buffer = null;
  private int bpos = 0;

  public Base64OutputStream(final OutputStream out, final int flags) {
    this(out, flags, true);
  }

  public Base64OutputStream(final OutputStream out, final int flags, final boolean encode) {
    super(out);
    this.flags = flags;
    coder = encode ? new Base64.Encoder(flags, null) : new Base64.Decoder(flags, null);
  }

  @Override public void write(final int b) throws IOException {
    if (buffer == null) {
      buffer = new byte[1024];
    }
    if (bpos >= buffer.length) {
      internalWrite(buffer, 0, bpos, false);
      bpos = 0;
    }
    buffer[bpos++] = (byte)b;
  }

  private void flushBuffer() throws IOException {
    if (bpos > 0) {
      internalWrite(buffer, 0, bpos, false);
      bpos = 0;
    }
  }

  @Override public void write(final byte[] b, final int off, final int len) throws IOException {
    if (len <= 0) return;
    flushBuffer();
    internalWrite(b, off, len, false);
  }

  @Override public void close() throws IOException {
    IOException thrown = null;
    try {
      flushBuffer();
      internalWrite(EMPTY, 0, 0, true);
    }
    catch (final IOException e) {
      thrown = e;
    }
    try {
      if ((flags & Base64.NO_CLOSE) == 0) {
        out.close();
      }
      else {
        out.flush();
      }
    }
    catch (final IOException e) {
      if (thrown == null) {
        throw e;
      }
    }
    if (thrown != null) {
      throw thrown;
    }
  }

  private void internalWrite(final byte[] b, final int off, final int len,
                             final boolean finish) throws IOException {
    coder.output = embiggen(coder.output, coder.maxOutputSize(len));
    if (!coder.process(b, off, len, finish)) {
      throw new Base64DataException("bad base-64");
    }
    out.write(coder.output, 0, coder.op);
  }

  private byte[] embiggen(final byte[] b, final int len) {
    if (b == null || b.length < len) {
      return new byte[len];
    }
    else {
      return b;
    }
  }

}
