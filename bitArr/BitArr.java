package bitArr;

import java.io.RandomAccessFile;
import java.util.Random;

public class BitArr {

  byte[] arr;
  public int size = 0;

  public BitArr(int byteAmount) {
    arr = new byte[byteAmount];
    size = 0;
  }

  public void pushBit(boolean b) {
    if (size % 8 == 0) {
      arr[size / 8] = 0;
    }
    if (b) {
      arr[size / 8] |= 1 << (size % 8);
    }
    size++;
  }

  public boolean popBit() {
    if (size % 8 == 0) {
      size--;
    }
    boolean b = (arr[size / 8] & (1 << (size % 8))) != 0;
    size--;
    return b;
  }

  public boolean getBit(int i) {
    return (arr[i / 8] & (1 << (i % 8))) != 0;
  }

  public void pushChar(char c) {
    int j = 1 << 15;
    for (int i = 0; i < 16; i++) {
      pushBit((c & j) != 0);
      j >>= 1;
    }
  }

  public char popChar() {
    char c = 0;
    int j = 1 << 15;
    for (int i = 0; i < 16; i++) {
      if (popBit()) {
        c |= j;
      }
      j >>= 1;
    }
    return c;
  }

  public char getChar(int pos) {
    char c = 0;
    int j = 1 << 15;
    for (int i = 0; i < 16; i++) {
      if (getBit(pos)) {
        c |= j;
      }
      j >>= 1;
      pos++;
    }
    return c;
  }

  public void pushByte(byte b) {
    int j = 1 << 7;
    for (int i = 0; i < 8; i++) {
      pushBit((b & j) != 0);
      j >>= 1;
    }
  }

  public byte popByte() {
    byte b = 0;
    int j = 1 << 7;
    for (int i = 0; i < 8; i++) {
      if (popBit()) {
        b |= j;
      }
      j >>= 1;
    }
    return b;
  }

  public byte getByte(int pos) {
    byte b = 0;
    int j = 1 << 7;
    for (int i = 0; i < 8; i++) {
      if (getBit(pos)) {
        b |= j;
      }
      j >>= 1;
      pos++;
    }
    return b;
  }

  public void pushInt(int n) {
    pushShort((short) (n >> 16));
    pushShort((short) n);
  }

  public int popInt() {
    int n = 0;
    n |= popShort() << 16;
    n |= popShort();
    return n;
  }

  public int getInt(int pos) {
    int n = 0;
    n |= getShort(pos) << 16;
    int n2 = (int) getShort(pos + 16);
    if (n2 < 0) {
      n2 += 65536;
    }
    n |= n2;
    return n;
  }

  public void pushShort(short n) {
    int j = 1 << 15;
    for (int i = 0; i < 16; i++) {
      pushBit((n & j) != 0);
      j >>= 1;
    }
  }

  public short popShort() {
    short n = 0;
    int j = 1 << 15;
    for (int i = 0; i < 16; i++) {
      if (popBit()) {
        n |= j;
      }
      j >>= 1;
    }
    return n;
  }

  public short getShort(int pos) {
    short n = 0;
    int j = 1 << 15;
    for (int i = 0; i < 16; i++) {
      if (getBit(pos)) {
        n |= j;
      }
      j >>= 1;
      pos++;
    }
    return n;
  }

  public void setBit(int i, boolean b) {
    if (b) {
      arr[i / 8] |= 1 << (i % 8);
    } else {
      arr[i / 8] &= ~(1 << (i % 8));
    }
  }

  public void pushHalfByte(byte b) {
    int j = 1 << 3;
    for (int i = 0; i < 4; i++) {
      pushBit((b & j) != 0);
      j >>= 1;
    }
  }

  public byte popHalfByte() {
    byte b = 0;
    int j = 1 << 3;
    for (int i = 0; i < 4; i++) {
      if (popBit()) {
        b |= j;
      }
      j >>= 1;
    }
    return b;
  }

  public byte getHalfByte(int pos) {
    byte b = 0;
    int j = 1 << 3;
    for (int i = 0; i < 4; i++) {
      if (getBit(pos)) {
        b |= j;
      }
      j >>= 1;
      pos++;
    }
    return b;
  }

  public void pushTwoThirdsByte(byte b) {
    int j = 1 << 5;
    for (int i = 0; i < 6; i++) {
      pushBit((b & j) != 0);
      j >>= 1;
    }
  }

  public byte popTwoThirdsByte() {
    byte b = 0;
    int j = 1 << 5;
    for (int i = 0; i < 6; i++) {
      if (popBit()) {
        b |= j;
      }
      j >>= 1;
    }
    return b;
  }

  public byte getTwoThirdsByte(int pos) {
    byte b = 0;
    int j = 1 << 5;
    for (int i = 0; i < 6; i++) {
      if (getBit(pos)) {
        b |= j;
      }
      j >>= 1;
      pos++;
    }
    return b;
  }

  public void pushXBits(int n, int x) {
    int j = 1 << (x - 1);
    for (int i = 0; i < x; i++) {
      pushBit((n & j) != 0);
      j >>= 1;
    }
  }

  public int popXBits(int x) {
    int n = 0;
    int j = 1 << (x - 1);
    for (int i = 0; i < x; i++) {
      if (popBit()) {
        n |= j;
      }
      j >>= 1;
    }
    return n;
  }

  public int getXBits(int pos, int x) {
    int n = 0;
    int j = 1 << (x - 1);
    for (int i = 0; i < x; i++) {
      if (getBit(pos)) {
        n |= j;
      }
      j >>= 1;
      pos++;
    }
    return n;
  }

  public void print() {
    for (int i = 0; i < size; i++) {
      System.out.print(getBit(i) ? "1" : "0");
    }
    System.out.println();
  }

  public byte[] getTrimmed() {
    int amountOfBytes = size / 8 + (size % 8 == 0 ? 0 : 1);
    byte[] trimmed = new byte[amountOfBytes];
    for (int i = 0; i < amountOfBytes; i++) {
      trimmed[i] = getByte(i * 8);
    }
    return trimmed;
  }

  public void combine(BitArr b) {
    for (int i = 0; i < b.size; i++) {
      pushBit(b.getBit(i));
    }
  }

  public String toString() {
    String s = "";
    for (int i = 0; i < size; i++) {
      s += getBit(i) ? "1" : "0";
    }
    return s;
  }

  public void writeToFile(RandomAccessFile raf) throws Exception {
    raf.write(getTrimmed());
  }

  public static void main(String[] args) {
    BitArr b = new BitArr(32);
    b.pushXBits(298, 9);
    /* b.pushBit(true);
        b.pushBit(false);
        b.pushBit(true);
        b.pushByte((byte)3);
        b.pushChar('a');
        b.pushTwoThirdsByte((byte)5); */
    b.print();

    System.out.println(b.getXBits(0, 9));
    byte[] arr = b.getTrimmed();
    for (int i = 0; i < arr.length; i++) {
      System.out.println(arr[i]);
    }
  }
}
