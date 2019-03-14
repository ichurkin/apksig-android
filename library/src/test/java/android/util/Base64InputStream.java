package android.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public class Base64InputStream extends FilterInputStream {

  private final Base64.Coder coder;
  private static byte[] EMPTY = new byte[0];
  private static final int BUFFER_SIZE = 2048;
  private boolean eof = false;
  private byte[] inputBuffer = new byte[BUFFER_SIZE];
  private int outputStart = 0;
  private int outputEnd = 0;

  public Base64InputStream(final InputStream in, final int flags) {
    this(in, flags, false);
  }

  public Base64InputStream(final InputStream in, final int flags, final boolean encode) {
    super(in);
    coder = encode ? new Base64.Encoder(flags, null) : new Base64.Decoder(flags, null);
    coder.output = new byte[coder.maxOutputSize(BUFFER_SIZE)];
  }

  @Override public boolean markSupported() {
    return false;
  }

  @Override public void mark(final int readlimit) {
    throw new UnsupportedOperationException();
  }

  @Override public void reset() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override public void close() throws IOException {
    in.close();
    inputBuffer = null;
  }

  @Override public int available() throws IOException {
    return outputEnd - outputStart;
  }

  @Override public long skip(final long n) throws IOException {
    if (outputStart >= outputEnd) {
      refill();
    }
    if (outputStart >= outputEnd) {
      return 0;
    }
    final long bytes = Math.min(n, outputEnd - outputStart);
    outputStart += bytes;
    return bytes;
  }

  @Override public int read() throws IOException {
    if (outputStart >= outputEnd) {
      refill();
    }
    if (outputStart >= outputEnd) {
      return -1;
    }
    else {
      return coder.output[outputStart++] & 0xff;
    }
  }

  @Override public int read(final byte[] b, final int off, final int len) throws IOException {
    if (outputStart >= outputEnd) {
      refill();
    }
    if (outputStart >= outputEnd) {
      return -1;
    }
    final int bytes = Math.min(len, outputEnd - outputStart);
    System.arraycopy(coder.output, outputStart, b, off, bytes);
    outputStart += bytes;
    return bytes;
  }

  private void refill() throws IOException {
    if (eof) {
      return;
    }
    final int bytesRead = in.read(inputBuffer);
    final boolean success;
    if (bytesRead == -1) {
      eof = true;
      success = coder.process(EMPTY, 0, 0, true);
    }
    else {
      success = coder.process(inputBuffer, 0, bytesRead, false);
    }
    if (!success) {
      throw new Base64DataException("bad base-64");
    }
    outputEnd = coder.op;
    outputStart = 0;
  }

}
