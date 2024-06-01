package arvore;

import java.io.RandomAccessFile;

public class ArvoreB {

  public static final int ORDEM = 8;
  public static final int TAMANHO_PAGINA = 150;
  long raizPos;
  RandomAccessFile raf;

  /**
   * Construtor da classe ArvoreB
   */
  public ArvoreB() {
    raizPos = -1;
    raf = null;
  }

  /**
   * Construtor da classe ArvoreB
   * @param arq String - Nome do arquivo
   * @throws Exception
   */
  public ArvoreB(String arq) throws Exception {
    raf = new RandomAccessFile(arq, "rw");
    raizPos = 8;
    if (raf.length() == 0) {
      raf.writeLong(raizPos);
      raf.write(new Pagina().toByteArray());
    } else {
      raizPos = raf.readLong();
    }
  }

  /**
   * Inicia a árvore B
   * @param arq String - Nome do arquivo
   * @throws Exception
   */
  public void start(String arq) throws Exception {
    raf = new RandomAccessFile(arq, "rw");
    raizPos = 8;
    raf.setLength(0);
    raf.seek(0);
    raf.writeLong(raizPos);
    raf.write(new Pagina().toByteArray());
  }

  /**
   * Recebe um long e retorna um array de bytes com a finalideade de realizar a leitura por meio do método read
   * @throws Exception
   * @see read
   */
  public byte[] readPagina(long pos) throws Exception {
    byte[] b = new byte[TAMANHO_PAGINA];
    raf.seek(pos);
    raf.read(b);
    return b;
  }

  /**
   * Recebe um long e uma Pagina e escreve a Pagina no arquivo na posição indicada pelo long
   * @throws Exception
   */
  public void writePagina(long pos, Pagina p) throws Exception {
    raf.seek(pos);
    raf.write(p.toByteArray());
  }

  /**
   * Recebe uma Pagina e escreve a Pagina no final do arquivo
   * @throws Exception
   */
  public void writePaginaEnd(Pagina p) throws Exception {
    raf.seek(raf.length());
    raf.write(p.toByteArray());
  }

  /**
   * Insere um elemento na árvore B
   * @param chave int - Chave do elemento
   * @param posicao long - Posição do elemento
   * @throws Exception
   */
  public Dupla inserir(int chave, long posicao, long posPagina)
    throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(posPagina));
    if (p.isFolha) {
      if (!p.isCheia()) {
        //folha que cabe mais elementos
        p.inserirElemento(chave, posicao);
        raf.seek(posPagina);
        raf.write(p.toByteArray());
        return null;
      }
      //folha que nao cabe mais elementos
      Pagina nova = new Pagina();
      Dupla[] duplas = new Dupla[p.nElementos + 1];
      for (int i = 0; i < p.nElementos; i++) {
        duplas[i] = new Dupla(p.chaves[i], p.posicoes[i]);
      }
      duplas[p.nElementos] = new Dupla(chave, posicao);
      Dupla.sort(duplas);
      for (int i = 0; i < (ORDEM) / 2; i++) {
        p.chaves[i] = duplas[i].chave;
        p.posicoes[i] = duplas[i].posicao;
      }
      p.nElementos = (ORDEM) / 2;
      nova.isFolha = true;
      nova.nElementos = (ORDEM - 1) / 2;
      for (int i = 0; i < (ORDEM - 1) / 2; i++) {
        nova.chaves[i] = duplas[i + (ORDEM) / 2 + 1].chave;
        nova.posicoes[i] = duplas[i + (ORDEM) / 2 + 1].posicao;
      }

      nova.filhos[0] = p.filhos[0];
      for (int i = 0; i < (ORDEM - 1) / 2; i++) {
        nova.filhos[i + 1] = 0;
      }
      writePagina(posPagina, p);
      writePaginaEnd(nova);

      return new Dupla(duplas[(ORDEM) / 2].chave, duplas[(ORDEM) / 2].posicao);
    }

    //nao é folha
    int i = 0;
    while (i < p.nElementos && chave > p.chaves[i]) {
      i++;
    }
    Dupla d = inserir(chave, posicao, p.filhos[i]);
    if (d == null) {
      return null;
    }

    if (!p.isCheia()) {
      //pagina que cabe mais elementos
      int pos = p.inserirElemento(d.chave, d.posicao);
      p.filhos[pos + 1] = raf.length() - TAMANHO_PAGINA;
      raf.seek(posPagina);
      raf.write(p.toByteArray());

      return null;
    }

    //pagina que nao cabe mais elementos
    Dupla[] duplas = new Dupla[p.nElementos + 1];
    for (int j = 0; j < p.nElementos; j++) {
      duplas[j] = new Dupla(p.chaves[j], p.posicoes[j]);
    }
    duplas[p.nElementos] = d;
    Dupla.sort(duplas);
    Pagina nova = new Pagina();
    for (int j = 0; j < (ORDEM) / 2; j++) {
      p.chaves[j] = duplas[j].chave;
      p.posicoes[j] = duplas[j].posicao;
    }
    p.nElementos = (ORDEM) / 2;
    for (int j = 0; j < (ORDEM - 1) / 2; j++) {
      nova.chaves[j] = duplas[j + (ORDEM) / 2 + 1].chave;
      nova.posicoes[j] = duplas[j + (ORDEM) / 2 + 1].posicao;
    }
    nova.nElementos = (ORDEM - 1) / 2;
    nova.isFolha = false;

    long[] filhos = new long[ORDEM + 1];
    for (int j = 0; j < ORDEM; j++) {
      filhos[j] = p.filhos[j];
    }
    long posAInserir = raf.length() - TAMANHO_PAGINA;

    int keyToInsert = Pagina.fromByteArray(readPagina(posAInserir)).chaves[0];

    int j = ORDEM - 1;
    int currentKey = Pagina.fromByteArray(readPagina(filhos[j])).chaves[0];
    while (currentKey > keyToInsert) {
      filhos[j + 1] = filhos[j];
      j--;
      if (j < 0) {
        break;
      }
      currentKey = Pagina.fromByteArray(readPagina(filhos[j])).chaves[0];
    }
    filhos[j + 1] = posAInserir;

    for (int k = 0; k < (ORDEM) / 2 + 1; k++) {
      p.filhos[k] = filhos[k];
    }

    for (int k = 0; k < (ORDEM) / 2; k++) {
      nova.filhos[k] = filhos[k + (ORDEM) / 2 + 1];
    }

    writePagina(posPagina, p);
    writePaginaEnd(nova);

    return new Dupla(duplas[(ORDEM) / 2].chave, duplas[(ORDEM) / 2].posicao);
  }

  /**
   * Insere um elemento na árvore B
   * @param chave int - Chave do elemento
   * @param posicao long - Posição do elemento
   * @throws Exception
   */
  public void inserir(int chave, long posicao) throws Exception {
    Dupla d = inserir(chave, posicao, raizPos);
    if (d == null) {
      return;
    }

    Pagina nova = new Pagina();
    nova.isFolha = false;
    nova.nElementos = 1;
    nova.chaves[0] = d.chave;
    nova.posicoes[0] = d.posicao;
    nova.filhos[0] = raizPos;
    nova.filhos[1] = raf.length() - TAMANHO_PAGINA;
    writePaginaEnd(nova);
    raizPos = raf.length() - TAMANHO_PAGINA;
    raf.seek(0);
    raf.writeLong(raizPos);
  }

  /**
   * Imprime todos os elementos da árvore B
   * @throws Exception
   */
  public void printAll() throws Exception {
    raf.seek(8);
    while (raf.getFilePointer() < raf.length()) {
      System.out.println("Pos: " + raf.getFilePointer());
      Pagina p = Pagina.fromByteArray(readPagina(raf.getFilePointer()));
      p.print();
      System.out.println("----");
      System.out.println();
    }
  }

  /**
   * Busca um elemento na árvore B
   * @param chave int - Chave do elemento
   * @return long - Posição do elemento
   * @throws Exception
   */
  public long buscar(int chave) throws Exception {
    return buscar(chave, raizPos);
  }

  /**
   * Busca um elemento na árvore B
   * @param chave int - Chave do elemento
   * @param pos long - Posição da página
   * @return long - Posição do elemento
   * @throws Exception
   */
  public long buscar(int chave, long pos) throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(pos));
    if (p.isFolha) {
      for (int i = 0; i < p.nElementos; i++) {
        if (p.chaves[i] == chave) {
          return p.posicoes[i];
        }
      }
      return -1;
    }

    int i = 0;
    while (i < p.nElementos && chave > p.chaves[i]) {
      i++;
    }
    if (p.chaves[i] == chave) {
      return p.posicoes[i];
    }

    if (p.filhos[i] == 0) {
      System.out.println("Erro: filho 0");
      return -1;
    }
    return buscar(chave, p.filhos[i]);
  }

  /**
   * Remove um elemento da árvore B
   * @param chave int - Chave do elemento
   * @throws Exception
   */
  public void remover(int chave) throws Exception {
    remover(chave, raizPos);
  }

  /**
   * Remove um elemento da árvore B
   * @param chave int - Chave do elemento
   * @param pos long - Posição da página
   * @throws Exception
   */
  public int remover(int chave, long pos) throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(pos));
    if (p.isFolha) {
      if (p.nElementos > (ORDEM - 1) / 2) {
        //folha com mais elementos que o minimo
        if (p.contains(chave)) {
          p.removerElemento(chave);
          writePagina(pos, p);
          return 1;
        }
        return -1;
      }
      //folha com menos elementos que o minimo
      p.removerElemento(chave);
      writePagina(pos, p);
      return 3;
    } else {
      //nao é folha
      int i = 0;
      while (i < p.nElementos && chave > p.chaves[i]) {
        i++;
      }
      int r;
      if (p.chaves[i] == chave) {
        //chave encontrada
        int chaveAnterior = getElementoAnterior(chave, p.filhos[i]);
        r = remover(chaveAnterior, pos);
        p.chaves[i] = chaveAnterior;
        writePagina(pos, p);
      } else {
        //chave nao encontrada
        r = remover(chave, p.filhos[i]);
      }

      if (r == -1) {
        return -1;
      }

      if (r == 1) {
        return -1;
      }

      if (r == 3) {
        //pagina com elemento removido com menos elementos que o minimo
        //tentar redistribuir
        //se nao conseguir, tentar fusao

        long posIrmaoEsq = -1;
        long posIrmaoDir = -1;

        Pagina irmaoEsq = null;
        Pagina irmaoDir = null;

        int qtdElementosIrmaoEsq = -1;
        int qtdElementosIrmaoDir = -1;

        if (i > 0) {
          posIrmaoEsq = p.filhos[i - 1];
          irmaoEsq = Pagina.fromByteArray(readPagina(posIrmaoEsq));
          qtdElementosIrmaoEsq = irmaoEsq.nElementos;
        }
        if (i < p.nElementos) {
          posIrmaoDir = p.filhos[i + 1];
          irmaoDir = Pagina.fromByteArray(readPagina(posIrmaoDir));
          qtdElementosIrmaoDir = irmaoDir.nElementos;
        }

        if (qtdElementosIrmaoEsq > (ORDEM - 1) / 2) {
          //irmao esquerdo doa um elemento
          Dupla elementoDoado = getDuplaAnterior(p.chaves[i], posIrmaoEsq);
          int elementoPai = p.chaves[i - 1];
          long posicaoPai = p.posicoes[i - 1];
          p.chaves[i - 1] = elementoDoado.chave;
          p.posicoes[i - 1] = elementoDoado.posicao;
          long posPaginaNecessitando = p.filhos[i];
          Pagina paginaNecessitando = Pagina.fromByteArray(
            readPagina(posPaginaNecessitando)
          );
          paginaNecessitando.inserirElemento(elementoPai, posicaoPai);
          paginaNecessitando.filhos[0] = irmaoEsq.filhos[irmaoEsq.nElementos];
          irmaoEsq.nElementos--;
          writePagina(posPaginaNecessitando, paginaNecessitando);
          writePagina(posIrmaoEsq, irmaoEsq);
          writePagina(pos, p);
          return -1;
        }

        if (qtdElementosIrmaoDir > (ORDEM - 1) / 2) {
          //irmao direito doa um elemento
          Dupla elementoDoado = getDuplaPosterior(p.chaves[i], posIrmaoDir);
          int elementoPai = p.chaves[i];
          long posicaoPai = p.posicoes[i];
          p.chaves[i] = elementoDoado.chave;
          p.posicoes[i] = elementoDoado.posicao;
          long posPaginaNecessitando = p.filhos[i];
          Pagina paginaNecessitando = Pagina.fromByteArray(
            readPagina(posPaginaNecessitando)
          );
          paginaNecessitando.inserirElemento(elementoPai, posicaoPai);
          paginaNecessitando.filhos[paginaNecessitando.nElementos] =
            irmaoDir.filhos[0];

          for (int j = 0; j < irmaoDir.nElementos - 1; j++) {
            irmaoDir.chaves[j] = irmaoDir.chaves[j + 1];
            irmaoDir.posicoes[j] = irmaoDir.posicoes[j + 1];
            irmaoDir.filhos[j] = irmaoDir.filhos[j + 1];
          }
          irmaoDir.filhos[irmaoDir.nElementos - 1] =
            irmaoDir.filhos[irmaoDir.nElementos];
          irmaoDir.nElementos--;
          writePagina(posPaginaNecessitando, paginaNecessitando);
          writePagina(posIrmaoDir, irmaoDir);
          writePagina(pos, p);

          return -1;
        }

        //fusao
        if (posIrmaoEsq != -1) {
          //fusao com irmao esquerdo
          Pagina paginaNecessitando = Pagina.fromByteArray(
            readPagina(p.filhos[i])
          );

          irmaoEsq.chaves[irmaoEsq.nElementos] = p.chaves[i - 1];
          irmaoEsq.posicoes[irmaoEsq.nElementos] = p.posicoes[i - 1];

          for (int j = 0; j < paginaNecessitando.nElementos; j++) {
            irmaoEsq.chaves[irmaoEsq.nElementos + j + 1] =
              paginaNecessitando.chaves[j];
            irmaoEsq.posicoes[irmaoEsq.nElementos + j + 1] =
              paginaNecessitando.posicoes[j];
            irmaoEsq.filhos[irmaoEsq.nElementos + j + 1] =
              paginaNecessitando.filhos[j];
          }

          irmaoEsq.filhos[irmaoEsq.nElementos +
            paginaNecessitando.nElementos +
            1] =
            paginaNecessitando.filhos[paginaNecessitando.nElementos];

          irmaoEsq.nElementos += paginaNecessitando.nElementos + 1;

          writePagina(posIrmaoEsq, irmaoEsq);

          for (int j = i - 1; j < p.nElementos - 1; j++) {
            p.chaves[j] = p.chaves[j + 1];
            p.posicoes[j] = p.posicoes[j + 1];
            p.filhos[j + 1] = p.filhos[j + 2];
          }

          p.nElementos--;
          writePagina(pos, p);

          return -1;
        }

        //fusao com irmao direito
        Pagina paginaNecessitando = Pagina.fromByteArray(
          readPagina(p.filhos[i])
        );

        paginaNecessitando.chaves[paginaNecessitando.nElementos] = p.chaves[i];
        paginaNecessitando.posicoes[paginaNecessitando.nElementos] =
          p.posicoes[i];

        for (int j = 0; j < irmaoDir.nElementos; j++) {
          paginaNecessitando.chaves[paginaNecessitando.nElementos + j + 1] =
            irmaoDir.chaves[j];
          paginaNecessitando.posicoes[paginaNecessitando.nElementos + j + 1] =
            irmaoDir.posicoes[j];
          paginaNecessitando.filhos[paginaNecessitando.nElementos + j + 1] =
            irmaoDir.filhos[j];
        }

        paginaNecessitando.filhos[paginaNecessitando.nElementos +
          irmaoDir.nElementos +
          1] =
          irmaoDir.filhos[irmaoDir.nElementos];

        paginaNecessitando.nElementos += irmaoDir.nElementos + 1;

        writePagina(p.filhos[i], paginaNecessitando);

        for (int j = i; j < p.nElementos - 1; j++) {
          p.chaves[j] = p.chaves[j + 1];
          p.posicoes[j] = p.posicoes[j + 1];
          p.filhos[j + 1] = p.filhos[j + 2];
        }

        p.nElementos--;
        writePagina(pos, p);

        return -1;
      }
    }
    return -1;
  }

  /**
   * Retorna o elemento anterior a um elemento na árvore B
   * @param chave int - Chave do elemento
   * @return int - Elemento anterior
   * @throws Exception
   */
  public int getElementoAnterior(int chave, long pos) throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(pos));
    if (p.isFolha) {
      return p.chaves[p.nElementos - 1];
    }

    int i = 0;
    while (i < p.nElementos && chave > p.chaves[i]) {
      i++;
    }
    if (p.chaves[i] == chave) {
      return getElementoAnterior(chave, p.filhos[i]);
    }

    return getElementoAnterior(chave, p.filhos[i]);
  }

  /**
   * Retorna o elemento posterior a um elemento na árvore B
   * @param chave int - Chave do elemento
   * @return int - Elemento posterior
   * @throws Exception
   */
  public int getElementoPosterior(int chave, long pos) throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(pos));
    if (p.isFolha) {
      return p.chaves[0];
    }

    int i = 0;
    while (i < p.nElementos && chave > p.chaves[i]) {
      i++;
    }
    if (p.chaves[i] == chave) {
      return getElementoPosterior(chave, p.filhos[i + 1]);
    }

    return getElementoPosterior(chave, p.filhos[i]);
  }

  /**
   * Retorna a dupla anterior a um elemento na árvore B
   * @param chave int - Chave do elemento
   * @return Dupla - Dupla anterior
   * @throws Exception
   */
  public Dupla getDuplaAnterior(int chave, long pos) throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(pos));
    if (p.isFolha) {
      return new Dupla(
        p.chaves[p.nElementos - 1],
        p.posicoes[p.nElementos - 1]
      );
    }

    int i = 0;
    while (i < p.nElementos && chave > p.chaves[i]) {
      i++;
    }
    if (p.chaves[i] == chave) {
      return getDuplaAnterior(chave, p.filhos[i]);
    }

    return getDuplaAnterior(chave, p.filhos[i]);
  }

  /**
   * Retorna a dupla posterior a um elemento na árvore B
   * @param chave int - Chave do elemento
   * @return Dupla - Dupla posterior
   * @throws Exception
   */
  public Dupla getDuplaPosterior(int chave, long pos) throws Exception {
    Pagina p = Pagina.fromByteArray(readPagina(pos));
    if (p.isFolha) {
      return new Dupla(p.chaves[0], p.posicoes[0]);
    }

    int i = 0;
    while (i < p.nElementos && chave > p.chaves[i]) {
      i++;
    }
    if (p.chaves[i] == chave) {
      return getDuplaPosterior(chave, p.filhos[i + 1]);
    }

    return getDuplaPosterior(chave, p.filhos[i]);
  }
}
