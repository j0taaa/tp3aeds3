package huffman;

/**
 * Classe que representa um nó da árvore de Huffman.
 */
public class No {

  No esq;
  No dir;
  int freq;
  byte c;
  int altura;

  /**
   * Construtor da classe No.
   * @param esq
   * @param dir
   * @param freq
   * @param c
   * @param a
   */
  public No(No esq, No dir, int freq, byte c, int a) {
    this.esq = esq;
    this.dir = dir;
    this.freq = freq;
    this.c = c;
    this.altura = a;
  }

  /**
   * Construtor da classe No.
   * @param esq
   * @param dir
   * @param freq
   * @param c
   */
  public No(No esq, No dir, int freq, byte c) {
    this.esq = esq;
    this.dir = dir;
    this.freq = freq;
    this.c = c;
    this.altura =
      Math.max(esq != null ? esq.altura : -1, dir != null ? dir.altura : -1) +
      1;
  }

  /**
   * Construtor da classe No.
   * @param freq
   * @param c
   */
  public No(int freq, byte c) {
    this(null, null, freq, c);
  }

  /**
   * Construtor da classe No.
   * @param freq
   */
  public boolean isFolha() {
    return esq == null && dir == null;
  }

  /**
   * Método que retorna a representação do nó em String.
   * @return
   */
  public String toString() {
    return c + " : " + freq + " : " + altura;
  }
}
