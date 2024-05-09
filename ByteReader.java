public class ByteReader {

  byte[] byteArr;
  int index;

  public ByteReader(byte[] byteArr) {
    this.byteArr = byteArr;
    this.index = 0;
  }

  public byte readByte() {
    return this.byteArr[this.index++];
  }

  public int readInt() {
    int res = 0;
    for (int i = 3; i >= 0; i--) {
      res = res << 8;
      res = res | (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  public long readLong() {
    long res = 0;
    for (int i = 7; i >= 0; i--) {
      res = res << 8;
      res = res | (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  public String readLongString() {
    int len = this.readInt();
    String res = "";
    for (int i = 0; i < len; i++) {
      res += (char) (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  public String readString() {
    int len = this.readShort();
    String res = "";
    for (int i = 0; i < len; i++) {
      res += (char) (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  public short readShort() {
    short res = 0;
    res = (short) (res | (this.byteArr[this.index++] & 0xFF) << 8);
    res = (short) (res | (this.byteArr[this.index++] & 0xFF));

    return res;
  }

  public boolean readBoolean(byte option1, byte option2) {
    return this.readByte() == option1;
  }

  public String readString(int len) {
    String res = "";
    for (int i = 0; i < len; i++) {
      res += (char) (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  public float readFloat() {
    int res = 0;
    for (int i = 3; i >= 0; i--) {
      res = res << 8;
      res = res | (this.byteArr[this.index++] & 0xFF);
    }

    return Float.intBitsToFloat(res);
  }

  public String[] readStringArray() {
    String fullStr = this.readLongString();
    String[] res = fullStr.split(";");

    return res;
  }
}
