import java.io.RandomAccessFile;
import java.util.Random;

public class Sorter {

  /**
   * Recebe (parâmetro) dois produtos e atributo a ser comparado. Compara ambos os produtos com base no atributo fornecido utilizando funções da classe Util.
   * @param a
   * @param b
   * @param property
   * @return Retorna 1 se a > b, -1 se a < b e 0 se a == b.
   */
  private static int compare(Produto a, Produto b, String property) {
    switch (property) {
      case "id":
        if (a.getId() > b.getId()) return 1;
        if (a.getId() < b.getId()) return -1;
        return 0;
      case "url":
        return Util.cmpStrings(a.getUrl(), b.getUrl());
      case "sku":
        return Util.cmpStrings(a.getSku(), b.getSku());
      case "name":
        return Util.cmpStrings(a.getName(), b.getName());
      case "description":
        return Util.cmpStrings(a.getDescription(), b.getDescription());
      case "price":
        if (a.getPrice() > b.getPrice()) return 1;
        if (a.getPrice() < b.getPrice()) return -1;
        return 0;
      case "currency":
        return Util.cmpStrings(a.getCurrency(), b.getCurrency());
      case "date":
        if (a.getDate() > b.getDate()) return 1;
        if (a.getDate() < b.getDate()) return -1;
        return 0;
      case "terms":
        return Util.cmpStrings(a.getTerms(), b.getTerms());
      case "section":
        if (a.getSection() == b.getSection()) return 0;
        return 1;
      default:
        break;
    }

    return 0;
  }

  /**
   * Recebe (parâmetro) um array de produtos e atributo a ser comparado. Ordena o array com base no atributo fornecido utilizando o algoritmo de ordenação quicksort.
   * @param arr
   * @param property
   * @return Produto[] - array de produtos ordenado
   */
  public static Produto[] quicksort(Produto[] arr, String property) {
    quicksort(arr, 0, arr.length - 1, property);
    return arr;
  }

  /**
   * Recebe (parâmetro) um array de produtos, índices esquerdo e direito e atributo a ser comparado. Ordena o array com base no atributo fornecido utilizando o algoritmo de ordenação quicksort.
   * @param arr
   * @param esq
   * @param dir
   * @param property
   */
  private static void quicksort(
    Produto[] arr,
    int esq,
    int dir,
    String property
  ) {
    Produto pivot = arr[(esq + dir) / 2];
    int i = esq;
    int j = dir;

    while (i <= j) {
      while (compare(arr[i], pivot, property) < 0) {
        i++;
      }
      while (compare(arr[j], pivot, property) > 0) {
        j--;
      }
      if (i <= j) {
        Produto temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        i++;
        j--;
      }
    }

    if (esq < j) {
      quicksort(arr, esq, j, property);
    }
    if (i < dir) {
      quicksort(arr, i, dir, property);
    }
  }

  /**
   * Recebe (parâmetro) um arquivo e um array de produtos (já ordenados). Escreve array de produtos no arquivo fornecido.
   * @param f
   * @param pArr
   * @throws Exception
   */
  public static void writeProdutoArray(RandomAccessFile f, Produto[] pArr)
    throws Exception {
    f.seek(f.length());
    for (Produto p : pArr) {
      if (p != null) f.write(p.toByteArray());
    }
  }

  /**
   * Recebe (parâmetro) um FileManager, dois arquivos, um tamanho de chunk e um atributo. Lê os produtos do FileManager, ordena-os e escreve-os nos arquivos fornecidos, de maneira intercalada com base no tamanho da chunk.
   * @param fm
   * @param f1
   * @param f2
   * @param chunkSize
   * @param property
   * @throws Exception
   */
  public static void startIBFiles(
    FileManager fm,
    RandomAccessFile f1,
    RandomAccessFile f2,
    int chunkSize,
    String property
  ) throws Exception {
    fm.resetPosition();

    boolean fileSelected = true;

    while (fm.hasNext()) {
      Produto[] pArr = new Produto[chunkSize];
      pArr = fm.readNext(chunkSize);
      pArr = Util.removeEndingNulls(pArr);
      pArr = quicksort(pArr, property);
      if (fileSelected) {
        writeProdutoArray(f1, pArr);
      } else {
        writeProdutoArray(f2, pArr);
      }

      fileSelected = !fileSelected;
    }
  }

  /**
   * Recebe (parâmetro) quatro arquivos, um tamanho de chunk e um atributo. Move ponteiros RAF para início dos dois primeiros arquivos e inicializa outros dois. Compara os produtos dos dois primeiros arquivos e insere o menor em arquivo novo (f3 / f4), de maneira intercalada com base no tamanho da chunk.
   * @param f1
   * @param f2
   * @param f3
   * @param f4
   * @param chunkSize
   * @param property
   * @throws Exception
   */
  public static void mergeFiles(
    RandomAccessFile f1,
    RandomAccessFile f2,
    RandomAccessFile f3,
    RandomAccessFile f4,
    int chunkSize,
    String property
  ) throws Exception {
    f1.seek(0);
    f2.seek(0);
    f3.setLength(0);
    f4.setLength(0);

    int i = 0;
    int j = 0;

    RandomAccessFile current = f3;

    while (
      f1.getFilePointer() < f1.length() && f2.getFilePointer() < f2.length()
    ) {
      i = 0;
      j = 0;

      while (
        i < chunkSize &&
        j < chunkSize &&
        f1.getFilePointer() < f1.length() &&
        f2.getFilePointer() < f2.length()
      ) {
        Produto p1 = Util.rawReadProduto(f1);
        Produto p2 = Util.rawReadProduto(f2);

        if (compare(p1, p2, property) < 0) {
          current.write(p1.toByteArray());
          i++;
        } else {
          current.write(p2.toByteArray());
          j++;
        }
      }

      while (i < chunkSize && f1.getFilePointer() < f1.length()) {
        Produto p1 = Util.rawReadProduto(f1);
        current.write(p1.toByteArray());
        i++;
      }

      while (j < chunkSize && f2.getFilePointer() < f2.length()) {
        Produto p2 = Util.rawReadProduto(f2);
        current.write(p2.toByteArray());
        j++;
      }

      if (current == f3) {
        current = f4;
      } else {
        current = f3;
      }
    }
  }

  /**
   * Recebe (parâmetro) um FileManager (arquivo que será feita a ordenção), um atributo e um nome de arquivo (arquivoFinal). Cria quatro arquivos temporários, ordena os produtos do FileManager e escreve-os no arquivoFinal fornecido, de maneira intercalada.
   * @param fm
   * @param property
   * @param endFile
   * @throws Exception
   */
  public static void intercalacaoBalanceada(
    FileManager fm,
    String property,
    String endFile
  ) throws Exception {
    RandomAccessFile f1 = new RandomAccessFile("f1.dat", "rw");
    RandomAccessFile f2 = new RandomAccessFile("f2.dat", "rw");
    RandomAccessFile f3 = new RandomAccessFile("f3.dat", "rw");
    RandomAccessFile f4 = new RandomAccessFile("f4.dat", "rw");
    RandomAccessFile temp = null;
    f1.setLength(0);
    f2.setLength(0);
    f3.setLength(0);
    f4.setLength(0);

    int chunkSize = 100;

    startIBFiles(fm, f1, f2, chunkSize, property);

    while (chunkSize < fm.getProductAmount()) {
      mergeFiles(f1, f2, f3, f4, chunkSize, property);
      chunkSize *= 2;
      startIBFiles(fm, f3, f4, chunkSize, property);

      temp = f1;
      f1 = f3;
      f3 = temp;
      temp = f2;
      f2 = f4;
      f4 = temp;
    }

    RandomAccessFile finalFile = new RandomAccessFile(endFile, "rw");
    finalFile.setLength(0);
    mergeFiles(f3, f4, finalFile, finalFile, chunkSize, property);
  }
}
