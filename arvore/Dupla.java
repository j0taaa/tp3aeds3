package arvore;

/**
 * Representa uma dupla de chave e posição
 */
public class Dupla {

  int chave;
  long posicao;

  public Dupla(int chave, long posicao) {
    this.chave = chave;
    this.posicao = posicao;
  }

  /**
   * Converte a dupla para uma string
   * @return String - Representação da dupla
   */
  public static void sort(Dupla[] arr) {
    for (int i = 0; i < arr.length; i++) {
      for (int j = i + 1; j < arr.length; j++) {
        if (arr[i].chave > arr[j].chave) {
          Dupla aux = arr[i];
          arr[i] = arr[j];
          arr[j] = aux;
        }
      }
    }
  }
}
