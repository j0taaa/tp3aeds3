import java.io.RandomAccessFile;
import java.time.format.DateTimeFormatter;

public class Util {

  /**
   * Recebe (parâmetro) dois produtos e atributo a ser comparado. Compara ambos os produtos com base no atributo fornecido utilizando funções da classe Util.
   * @param f
   * @return Retorna um array de bytes que representa o float f.
   */
  public static byte[] getByteArray(float f) {
    int bits = Float.floatToIntBits(f);
    byte[] bytes = new byte[] {
      (byte) (bits >> 24),
      (byte) (bits >> 16),
      (byte) (bits >> 8),
      (byte) bits,
    };

    return bytes;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] getByteArray(String str) {
    byte[] bArr = str.getBytes();
    short len = (short) bArr.length;

    byte[] res = new byte[bArr.length + 2];
    res[0] = (byte) (len >> 8);
    res[1] = (byte) len;

    for (int i = 0; i < bArr.length; i++) {
      res[i + 2] = bArr[i];
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] getLongByteArray(String str) {
    byte[] bArr = str.getBytes();
    int len = bArr.length;

    byte[] res = new byte[bArr.length + 4];
    res[0] = (byte) (len >> 24);
    res[1] = (byte) (len >> 16);
    res[2] = (byte) (len >> 8);
    res[3] = (byte) len;

    for (int i = 0; i < bArr.length; i++) {
      res[i + 4] = bArr[i];
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] getByteArray(String str, int len) {
    byte[] bArr = str.getBytes();
    byte[] res = new byte[len];
    for (int i = 0; i < bArr.length; i++) {
      if (i >= len) break;
      res[i] = bArr[i];
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] getByteArray(long l) {
    byte[] res = new byte[8];
    for (int i = 7; i >= 0; i--) {
      res[i] = (byte) l;
      l = l >> 8;
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] getByteArray(int n) {
    byte[] res = new byte[4];
    for (int i = 3; i >= 0; i--) {
      res[i] = (byte) n;
      n = n >> 8;
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] getByteArray(boolean b, byte option1, byte option2) {
    byte[] res = new byte[1];
    res[0] = b ? option1 : option2;

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static String combineStrings(String[] strs) {
    String res = "";
    for (int i = 0; i < strs.length; i++) {
      res += strs[i];
      if (i < strs.length - 1) res += ';';
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de bytes e retorna um float.
   * @param bArr
   * @return Retorna um float que representa o array de bytes bArr.
   */
  public static byte[] combineByteArrays(byte[]... byteArrs) {
    int len = 0;
    for (byte[] ba : byteArrs) {
      len += ba.length;
    }

    byte[] res = new byte[len];

    int k = 0;
    for (int i = 0; i < byteArrs.length; i++) {
      for (int j = 0; j < byteArrs[i].length; j++) {
        res[k] = byteArrs[i][j];
        k++;
      }
    }

    return res;
  }

  public static float getFloat(byte[] bArr) {
    int bits = 0;
    for (int i = 0; i < 4; i++) {
      bits = bits << 8;
      bits = bits | (bArr[i] & 0xFF);
    }

    return Float.intBitsToFloat(bits);
  }

  public static long getUTC(String str) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );
    return java.time.LocalDateTime
      .parse(str, formatter)
      .toEpochSecond(java.time.ZoneOffset.UTC);
  }

  /**
   * Recebe (parâmetro) dois produtos e atributo a ser comparado. Compara ambos os produtos com base no atributo fornecido utilizando funções da classe Util.
   * @param n
   * @param operator
   * @param n2
   * @return Retorna true se a comparação for verdadeira e false caso contrário.
   */
  public static boolean compareNumber(double n, String operator, double n2) {
    switch (operator) {
      case "=":
        return n == n2;
      case "<":
        return n < n2;
      case ">":
        return n > n2;
      case "<=":
        return n <= n2;
      case ">=":
        return n >= n2;
      case "!=":
        return n != n2;
      default:
        return false;
    }
  }

  /**
   * Recebe (parâmetro) dois produtos e atributo a ser comparado. Compara ambos os produtos com base no atributo fornecido utilizando funções da classe Util.
   * @param b
   * @param operator
   * @param b2
   * @return Retorna true se a comparação for verdadeira e false caso contrário.
   */
  public static boolean compareBoolean(boolean b, String operator, boolean b2) {
    switch (operator) {
      case "=":
        return b == b2;
      case "!=":
        return b != b2;
      default:
        return false;
    }
  }

  /**
   * Recebe (parâmetro) dois produtos e atributo a ser comparado. Compara ambos os produtos com base no atributo fornecido utilizando funções da classe Util.
   * @param str1
   * @param str2
   * @return Retorna -1 se str1 < str2, 1 se str1 > str2 e 0 se str1 == str2.
   */
  public static int cmpStrings(String str1, String str2) {
    int i = 0;
    while (i < str1.length() && i < str2.length()) {
      if (str1.charAt(i) < str2.charAt(i)) {
        return -1;
      }
      if (str1.charAt(i) > str2.charAt(i)) {
        return 1;
      }
      i++;
    }

    if (str1.length() < str2.length()) {
      return -1;
    }
    if (str1.length() > str2.length()) {
      return 1;
    }

    return 0;
  }

  /**
   * Recebe (parâmetro) um array de produtos, índices esquerdo e direito e atributo a ser comparado. Ordena o array com base no atributo fornecido utilizando o algoritmo de ordenação quicksort.
   * @param arr
   * @param esq
   * @param dir
   * @param property
   */
  public static Produto[] removeEndingNulls(Produto[] arr) {
    int i = arr.length - 1;
    while (i >= 0 && arr[i] == null) {
      i--;
    }

    Produto[] res = new Produto[i + 1];
    for (int j = 0; j <= i; j++) {
      res[j] = arr[j];
    }

    return res;
  }

  /**
   * Recebe (parâmetro) um array de produtos, índices esquerdo e direito e atributo a ser comparado. Ordena o array com base no atributo fornecido utilizando o algoritmo de ordenação quicksort.
   * @param raf
   * @return Produto - produto lido do arquivo.
   * @throws Exception
   */
  public static Produto rawReadProduto(RandomAccessFile raf) throws Exception {
    raf.readByte();
    int len = raf.readInt();
    raf.seek(raf.getFilePointer() - 5);
    byte[] bArr = new byte[len + 5];
    raf.read(bArr);
    Produto p = new Produto(bArr);
    return p;
  }

  public static Produto[] quicksort(Produto[] arr) {
    quicksort(arr, 0, arr.length - 1);
    return arr;
  }

  public static void quicksort(Produto[] arr, int esq, int dir) {
    if (esq < dir) {
      int p = partition(arr, esq, dir);
      quicksort(arr, esq, p - 1);
      quicksort(arr, p + 1, dir);
    }
  }

  public static int partition(Produto[] arr, int esq, int dir) {
    Produto pivot = arr[dir];
    int i = esq - 1;
    for (int j = esq; j < dir; j++) {
      if (arr[j].getId() < pivot.getId()) {
        i++;
        Produto temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
      }
    }
    Produto temp = arr[i + 1];
    arr[i + 1] = arr[dir];
    arr[dir] = temp;
    return i + 1;
  }

  public static Produto[] mergeArrays(
    Produto[] arr1,
    Produto[] arr2,
    boolean inclusive
  ) {
    //retorna um array de produtos que é a união dos arrays arr1 e arr2 caso inclusive seja true
    //ou a interseção dos arrays arr1 e arr2 caso inclusive seja false
    //remover duplicatas
    //retornar array ordenado
    Produto[] res = new Produto[arr1.length + arr2.length];
    arr1 = quicksort(arr1);
    arr2 = quicksort(arr2);

    if (inclusive) {
      int i = 0;
      int j = 0;
      int k = 0;
      while (i < arr1.length && j < arr2.length) {
        if (arr1[i].getId() < arr2[j].getId()) {
          res[k] = arr1[i];
          i++;
        } else if (arr1[i].getId() > arr2[j].getId()) {
          res[k] = arr2[j];
          j++;
        } else {
          res[k] = arr1[i];
          i++;
          j++;
        }
        k++;
      }
      while (i < arr1.length) {
        res[k] = arr1[i];
        i++;
        k++;
      }
      while (j < arr2.length) {
        res[k] = arr2[j];
        j++;
        k++;
      }
      return removeEndingNulls(res);
    } else {
      int i = 0;
      int j = 0;
      int k = 0;
      while (i < arr1.length && j < arr2.length) {
        if (arr1[i].getId() < arr2[j].getId()) {
          i++;
        } else if (arr1[i].getId() > arr2[j].getId()) {
          j++;
        } else {
          res[k] = arr1[i];
          i++;
          j++;
          k++;
        }
      }
      return removeEndingNulls(res);
    }
  }
}
