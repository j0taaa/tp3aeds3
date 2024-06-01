package lzw;

import bitArr.BitArr;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementa o algoritmo de compressão LZW
 */
public class LZW {

  /**
   * Cria um dicionário inicial com todos os caracteres ASCII de -128 a 127
   * @return HashMap<String, Integer> - Dicionário inicial
   */
  public HashMap<String, Integer> createInitialDictionary() {
    HashMap<String, Integer> dict = new HashMap<>();
    for (int i = -128; i < 128; i++) {
      if (i < 0) {
        dict.put("" + (char) i, 127 - i);
      } else {
        dict.put("" + (char) i, i);
      }
    }

    return dict;
  }

  /**
   * Inverte um dicionário
   * @param dict HashMap<String, Integer> - Dicionário a ser invertido
   * @return HashMap<Integer, String> - Dicionário invertido
   */
  public HashMap<Integer, String> invertDict(HashMap<String, Integer> dict) {
    HashMap<Integer, String> reverse = new HashMap<>();
    for (String key : dict.keySet()) {
      reverse.put(dict.get(key), key);
    }

    return reverse;
  }

  /**
   * Comprime um arquivo de entrada e salva o resultado em um arquivo de saída usando o algoritmo LZW e BitArr
   * @param fileIn String - Arquivo de entrada
   * @param fileOut String - Arquivo de saída
   * @throws Exception
   */
  public void compress(String fileIn, String fileOut) throws Exception {
    RandomAccessFile raf = new RandomAccessFile(fileIn, "r");
    ArrayList<Integer> compressed = new ArrayList<>();
    HashMap<String, Integer> dict = createInitialDictionary();
    int dictSize = 256;
    String current = "";

    while (raf.getFilePointer() < raf.length()) {
      char c = (char) raf.readByte();
      //System.out.println((byte)c);
      String tmp = current + c;
      if (dict.containsKey(tmp) && dict.get(tmp) < dictSize - 1) {
        current = tmp;
      } else {
        //System.out.println("dict.get(current) = " + dict.get(current));
        compressed.add(dict.get(current));
        dict.put(tmp, (dictSize++));
        current = "" + c;
      }
    }

    for (String key : dict.keySet()) {
      //System.out.println(key + " " + dict.get(key));
    }

    for (int i = 0; i < current.length(); i++) {
      compressed.add(dict.get("" + current.charAt(i)));
    }
    ////////////
    HashMap<Integer, String> tmp = invertDict(dict);

    //System.out.println("\n\n\n");

    for (int i = 0; i < dictSize; i++) {
      //System.out.println(i + " " + tmp.get(i));
    }

    for (int i = 0; i < compressed.size(); i++) {
      //System.out.println(i + " - " + compressed.get(i) + " " + tmp.get(compressed.get(i)));
    }
    ////////////
    int bitAmount = (int) Math.ceil(Math.log(dictSize) / Math.log(2));
    //System.out.println("bitAmount: " + bitAmount);
    BitArr bitArr = new BitArr((int) raf.length() * 2 + 5);
    bitArr.pushByte((byte) bitAmount);
    bitArr.pushInt(compressed.size());
    for (int i = 0; i < compressed.size(); i++) {
      bitArr.pushXBits(compressed.get(i), bitAmount);
    }

    RandomAccessFile rafOut = new RandomAccessFile(fileOut, "rw");
    rafOut.setLength(0);
    rafOut.write(bitArr.getTrimmed());

    raf.close();
    rafOut.close();
  }

  /**
   * Descomprime um arquivo de entrada e salva o resultado em um arquivo de saída por meio do algoritmo LZW e BitArr
   * @param fileIn String - Arquivo de entrada
   * @param fileOut String - Arquivo de saída
   * @throws Exception
   */
  public void decompress(String fileIn, String fileOut) throws Exception {
    RandomAccessFile raf = new RandomAccessFile(fileIn, "r");
    RandomAccessFile rafOut = new RandomAccessFile(fileOut, "rw");
    byte bitAmount = raf.readByte();
    int compressedSize = raf.readInt();

    BitArr bitArr = new BitArr((int) raf.length());
    while (raf.getFilePointer() < raf.length()) {
      bitArr.pushByte(raf.readByte());
    }

    ArrayList<Integer> compressed = new ArrayList<>();
    for (int i = 0; i < compressedSize; i++) {
      compressed.add(bitArr.getXBits(i * bitAmount, bitAmount));
    }

    for (int i = 0; i < compressed.size(); i++) {
      //System.out.println(i + " " + compressed.get(i));
    }

    HashMap<String, Integer> dict = createInitialDictionary();
    int dictSize = 256;
    HashMap<Integer, String> invertedDict = invertDict(dict);
    //System.out.println("\n\n\n");
    String current = invertedDict.get(compressed.get(0));
    //System.out.println((byte)current.charAt(0));
    rafOut.setLength(0);
    rafOut.writeBytes(invertedDict.get(compressed.get(0)));
    for (int i = 1; i < compressed.size(); i++) {
      String tmp = current;
      //System.out.println(current + " i " + i);
      tmp += invertedDict.get(compressed.get(i)).charAt(0);
      invertedDict.put(dictSize++, tmp);
      //System.out.println(invertedDict);
      current = invertedDict.get(compressed.get(i));
      //System.out.print("\ncurrent:");
      for (int j = 0; j < current.length(); j++) {
        //System.out.print((byte)current.charAt(j) + " ");
      }
      rafOut.writeBytes(current);
      //System.out.println((byte)current.charAt(0));
    }

    raf.close();
    rafOut.close();
  }

  /**
   * Método principal (teste da classe LZW)
   * @param args
   */
  public static void main(String[] args) {
    try {
      LZW lzw = new LZW();
      lzw.compress("data2.dat", "lorem.lzw");
      lzw.decompress("lorem.lzw", "lorem2.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
