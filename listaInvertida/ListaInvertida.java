package listaInvertida;

import java.io.RandomAccessFile;

/**
 * Implementa uma lista invertida para busca de produtos.
 */
public class ListaInvertida {

  RandomAccessFile raf;
  int maxChunkSize = 30;

  /**
   * Construtor da classe ListaInvertida.
   */
  public ListaInvertida() {
    this.raf = null;
  }

  /**
   * Construtor da classe ListaInvertida.
   * @param path String - Caminho do arquivo de lista invertida
   * @param maxChunkSize int - Tamanho máximo de um chunk
   * @throws Exception
   */
  public ListaInvertida(String path, int maxChunkSize) throws Exception {
    this.loadFile(path);
    this.maxChunkSize = maxChunkSize;
  }

  /**
   * Inicializa a lista invertida.
   * @param path String - Caminho do arquivo de lista invertida
   * @param maxChunkSize int - Tamanho máximo de um chunk
   * @throws Exception
   */
  public void start(String path, int maxChunkSize) throws Exception {
    this.raf = new RandomAccessFile(path, "rw");
    this.maxChunkSize = maxChunkSize;
    raf.setLength(0);
    raf.setLength((6 + 4 * maxChunkSize) * 65536);
    raf.seek(0);
  }

  /**
   * Carrega um arquivo de lista invertida.
   * @param path String - Caminho do arquivo de lista invertida
   * @throws Exception
   */
  public void loadFile(String path) throws Exception {
    this.raf = new RandomAccessFile(path, "rw");
  }

  /**
   * Calcula o hash de uma string.
   * @param text String - Texto a ser calculado o hash
   * @return int - Hash da string
   */
  public static int fnv1a_32(byte[] data) {
    int hash = 0x811c9dc5;
    for (byte b : data) {
      hash ^= (b & 0xff);
      hash *= 0x01000193;
    }
    return hash;
  }

  /**
   * Calcula o hash de uma string.
   * @param text String - Texto a ser calculado o hash
   * @return int - Hash da string
   */
  public static int hashString(String text) {
    final byte[] bytes = text.getBytes();
    int hash32 = fnv1a_32(bytes);
    return hash32 & 0xFFFF;
  }

  /**
   * Retorna o endereço de início de uma lista.
   * @param text String - Texto a ser calculado o endereço
   * @return int - Endereço de início da lista
   */
  public int getListStartAddress(String text) {
    int hash = hashString(text);
    return hash * (6 + 4 * maxChunkSize);
  }

  /**
   * Insere um id em uma lista.
   * @param id int - Id a ser inserido
   * @param text String - Texto da lista
   * @throws Exception
   */
  public void inserir(int id, String text) throws Exception {
    int address = getListStartAddress(text);
    raf.seek(address);
    short chunkSize = raf.readShort();
    if (chunkSize < maxChunkSize) {
      raf.seek(address + 2 + chunkSize * 4);
      raf.writeInt(id);
      raf.seek(address);
      raf.writeShort(chunkSize + 1);
      System.out.println("Inserido");
      return;
    }
    raf.seek(address + 2 + maxChunkSize * 4);
    int next = raf.readInt();
    if (next == 0) {
      raf.seek(raf.getFilePointer() - 4);
      raf.writeInt((int) raf.length());
      raf.seek(raf.length());
      raf.writeShort(1);
      raf.writeInt(id);
      for (int i = 0; i < maxChunkSize; i++) {
        raf.writeInt(0);
      }
    } else {
      inserirR(id, text, next);
    }
  }

  /**
   * Insere um id em uma lista recursivamente.
   * @param id int - Id a ser inserido
   * @param text String - Texto da lista
   * @param address int - Endereço da lista
   * @throws Exception
   */
  public void inserirR(int id, String text, int address) throws Exception {
    raf.seek(address);
    short chunkSize = raf.readShort();
    if (chunkSize < maxChunkSize) {
      raf.seek(address + 2 + chunkSize * 4);
      raf.writeInt(id);
      raf.seek(address);
      raf.writeShort(chunkSize + 1);
      return;
    }
    raf.seek(address + 2 + maxChunkSize * 4);
    int next = raf.readInt();
    if (next == 0) {
      raf.seek(raf.getFilePointer() - 4);
      raf.writeInt((int) raf.length());
      raf.seek(raf.length());
      raf.writeShort(1);
      raf.writeInt(id);
      for (int i = 0; i < maxChunkSize; i++) {
        raf.writeInt(0);
      }
    } else {
      inserirR(id, text, next);
    }
  }

  /**
   * Busca um texto na lista invertida.
   * @param text String - Texto a ser buscado
   * @return int[] - Lista de ids
   * @throws Exception
   */
  public int[] buscar(String text) throws Exception {
    int amount = 0;
    int startAddress = getListStartAddress(text);
    int address = startAddress;
    while (address != 0) {
      raf.seek(address);
      short chunkSize = raf.readShort();
      amount += chunkSize;
      raf.seek(address + 2 + maxChunkSize * 4);
      address = raf.readInt();
    }

    int[] res = new int[amount];
    address = startAddress;
    int i = 0;
    while (address != 0) {
      raf.seek(address);
      int chunkSize = raf.readShort();
      for (int j = 0; j < chunkSize; j++) {
        int value = raf.readInt();
        if (value != 0) {
          res[i] = value;
          i++;
        }
      }
      raf.seek(address + 2 + maxChunkSize * 4);
      address = raf.readInt();
    }
    return res;
  }

  /**
   * Insere um produto na lista invertida.
   * @param id
   * @param text
   * @throws Exception
   */
  public void inserirProduto(int id, String text) throws Exception {
    String[] words = text.split(" ");
    for (int i = 0; i < words.length; i++) {
      inserir(id, words[i].toLowerCase());
    }
  }

  /**
   * Remove um id de uma lista.
   * @param id int - Id a ser removido
   * @param text String - Texto da lista
   * @throws Exception
   */
  public void remover(int id, String text) throws Exception {
    int startAddress = getListStartAddress(text);
    int address = startAddress;
    while (address != 0) {
      raf.seek(address);
      short chunkSize = raf.readShort();
      int[] values = new int[maxChunkSize];
      for (int i = 0; i < maxChunkSize; i++) {
        values[i] = raf.readInt();
      }
      for (int i = 0; i < maxChunkSize; i++) {
        if (values[i] == id) {
          values[i] = -1;
          chunkSize--;
        }
      }
      raf.seek(address);
      raf.writeShort(chunkSize);

      int k = 0;
      while (k < maxChunkSize) {
        if (values[k] != -1) {
          raf.writeInt(values[k]);
        }
        k++;
      }

      for (int i = k; i < maxChunkSize; i++) {
        raf.writeInt(0);
      }

      raf.seek(address + 2 + maxChunkSize * 4);
      address = raf.readInt();
    }
  }

  /**
   * Remove um produto da lista invertida.
   * @param id int - Id do produto a ser removido
   * @param text String - Texto do produto
   * @throws Exception
   */
  public void removerProduto(int id, String text) throws Exception {
    String[] words = text.split(" ");
    for (int i = 0; i < words.length; i++) {
      remover(id, words[i].toLowerCase());
    }
  }
}
