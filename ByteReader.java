public class ByteReader {

  byte[] byteArr;
  int index;

  /**
   * Construtor da classe ByteReader
   * @param byteArr
   */
  public ByteReader(byte[] byteArr) {
    this.byteArr = byteArr;
    this.index = 0;
  }

  /**
   * Lê um array de bytes do array de bytes
   * @param len
   * @return byte[]
   */
  public byte[] readBytes(int len) {
    byte[] res = new byte[len];
    for (int i = 0; i < len; i++) {
      res[i] = this.byteArr[this.index++];
    }

    return res;
  }

  /**
   * Lê um byte do array de bytes
   * @return byte
   */
  public byte readByte() {
    return this.byteArr[this.index++];
  }

  /**
   * Lê um inteiro do array de bytes
   * @return int
   */
  public int readInt() {
    int res = 0;
    for (int i = 3; i >= 0; i--) {
      res = res << 8;
      res = res | (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  /**
   * Lê um long do array de bytes
   * @return long
   */
  public long readLong() {
    long res = 0;
    for (int i = 7; i >= 0; i--) {
      res = res << 8;
      res = res | (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  /**
   * Lê uma string longa do array de bytes
   * @return String
   */
  public String readLongString() {
    int len = this.readInt();
    String res = "";
    for (int i = 0; i < len; i++) {
      res += (char) (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  /**
   * Lê uma string do array de bytes
   * @return String
   */
  public String readString() {
    int len = this.readShort();
    String res = "";
    for (int i = 0; i < len; i++) {
      res += (char) (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  /**
   * Lê um short do array de bytes
   * @return short
   */
  public short readShort() {
    short res = 0;
    res = (short) (res | (this.byteArr[this.index++] & 0xFF) << 8);
    res = (short) (res | (this.byteArr[this.index++] & 0xFF));

    return res;
  }

  /**
   * Lê um boolean do array de bytes
   * @param option1
   * @param option2
   * @return boolean
   */
  public boolean readBoolean(byte option1, byte option2) {
    return this.readByte() == option1;
  }

  /**
   * Lê uma string do array de bytes
   * @param len int
   * @return String
   */
  public String readString(int len) {
    String res = "";
    for (int i = 0; i < len; i++) {
      res += (char) (this.byteArr[this.index++] & 0xFF);
    }

    return res;
  }

  /**
   * Lê um float do array de bytes
   * @return float
   */
  public float readFloat() {
    int res = 0;
    for (int i = 3; i >= 0; i--) {
      res = res << 8;
      res = res | (this.byteArr[this.index++] & 0xFF);
    }

    return Float.intBitsToFloat(res);
  }

  /**
   * Lê um double do array de bytes
   * @return double
   */
  public String[] readStringArray() {
    String fullStr = this.readLongString();
    String[] res = fullStr.split(";");

    return res;
  }
}
