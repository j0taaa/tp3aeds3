import huffman.Huffman;
import java.io.File;
import lzw.LZW;

/**
 * Gerencia backups e compressões
 * Possui os métodos para realizar backup e restaurar backup
 */
public class BackupManager {

  // Diretório onde os backups serão salvos
  static final String backupDir = "./backups/";
  static final String[] backupFiles = {
    "data.dat",
    "li1.dat",
    "li2.dat",
    "arvore.dat",
    "hash.dat",
  };

  /**
   * Retorna o número de backups realizados
   * @return int - Número de backups realizados
   */
  public int getBackupCount() {
    File backupDir = new File(this.backupDir);
    return backupDir.list().length;
  }

  /**
   * Comprime os arquivos de backup usando o algoritmo de Huffman
   * @param resultFolder String - Pasta onde os arquivos comprimidos serão salvos
   * @return long - Tamanho total dos arquivos comprimidos
   * @throws Exception
   */
  public long compressHuffman(String resultFolder) throws Exception { //retorna tamanho total final
    Huffman huffman = new Huffman();

    long totalFinalSize = 0;

    for (String file : backupFiles) {
      huffman.compressFile(file, resultFolder + "/" + file);
      totalFinalSize += new File(resultFolder + "/" + file).length();
    }

    return totalFinalSize;
  }

  /**
   * Comprime os arquivos de backup usando o algoritmo LZW
   * @param resultFolder String - Pasta onde os arquivos comprimidos serão salvos
   * @return long - Tamanho total dos arquivos comprimidos
   * @throws Exception
   */
  public long compressLZW(String resultFolder) throws Exception {
    LZW lzw = new LZW();

    long totalFinalSize = 0;

    for (String file : backupFiles) {
      lzw.compress(file, resultFolder + "/" + file);
      totalFinalSize += new File(resultFolder + "/" + file).length();
    }

    return totalFinalSize;
  }

  /**
   * Realiza um backup dos arquivos de backup e comprime os arquivos usando os algoritmos de Huffman e LZW
   * @throws Exception
   */
  public void backup() throws Exception {
    File backupDir = new File(this.backupDir);
    if (!backupDir.exists()) {
      backupDir.mkdir();
    }

    int backupCount = getBackupCount();

    File backupFolder = new File(this.backupDir + "backup" + backupCount);
    backupFolder.mkdir();

    long totalFileSizeInitial = 0;

    for (String file : backupFiles) {
      totalFileSizeInitial += new File(file).length();
    }

    File huffmanFolder = new File(backupFolder + "/huffman");
    huffmanFolder.mkdir();

    File lzwFolder = new File(backupFolder + "/lzw");
    lzwFolder.mkdir();

    long startHuffman = System.currentTimeMillis();

    long totalFinalSizeHuffman = compressHuffman(huffmanFolder.toString());

    long endHuffman = System.currentTimeMillis();

    long startLZW = System.currentTimeMillis();

    long totalFinalSizeLZW = compressLZW(lzwFolder.toString());

    long endLZW = System.currentTimeMillis();

    System.out.println("Número do backup realizado: " + backupCount);

    System.out.println(
      "\n\nPorcentam de perda Huffman: " +
      (totalFinalSizeHuffman * 100 / totalFileSizeInitial) +
      "%"
    );
    System.out.println(
      "Tempo de execução Huffman: " + (endHuffman - startHuffman) + "ms"
    );

    System.out.println(
      "\nPorcentam de perda LZW: " +
      (totalFinalSizeLZW * 100 / totalFileSizeInitial) +
      "%"
    );
    System.out.println("Tempo de execução LZW: " + (endLZW - startLZW) + "ms");
  }

  /**
   * Restaura um backup de acordo com o algoritmo de compressão
   * @param algoritmo String - Algoritmo de compressão a ser utilizado
   * @param backupNumber int - Número do backup a ser restaurado
   * @throws Exception
   */
  public void restoreBackup(String algoritmo, int backupNumber)
    throws Exception {
    File backupFolder = new File(this.backupDir + "backup" + backupNumber);

    long start = System.currentTimeMillis();

    if (algoritmo.equals("huffman")) {
      File huffmanFolder = new File(backupFolder + "/huffman");
      for (String file : backupFiles) {
        new Huffman().decompressFile(huffmanFolder + "/" + file, file);
      }

      long end = System.currentTimeMillis();

      System.out.println("\nDescompressão Huffman realizada com sucesso!");
      System.out.println("Tempo de execução Huffman: " + (end - start) + "ms");
    } else {
      File lzwFolder = new File(backupFolder + "/lzw");
      for (String file : backupFiles) {
        new LZW().decompress(lzwFolder + "/" + file, file);
      }

      long end = System.currentTimeMillis();

      System.out.println("\nDescompressão LZW realizada com sucesso!");
      System.out.println("Tempo de execução LZW: " + (end - start) + "ms");
    }
  }

  /**
   * Método main (teste da classe BackupManager)
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    BackupManager bm = new BackupManager();
    System.out.println("Backup count: " + bm.getBackupCount());
    bm.backup();
  }
}
