import java.io.RandomAccessFile;

public class CsvReader {

  private RandomAccessFile csv;

  /**
   * Construtor que carrega um arquivo CSV existente e o associa ao ponteiro RAF.
   * @param path
   * @throws Exception
   */
  public CsvReader(String path) throws Exception {
    csv = new RandomAccessFile(path, "r");
    csv.readLine();
  }

  /**
   * Recebe arquivo de bytes (parâmetro), lê os registros e os escreve em um arquivo em formato .dat.
   * @return Produto - registro lido
   * @throws Exception
   */
  public void loadFromCsv(FileManager fm) throws Exception {
    Produto p;
    int id = 1;
    while (csv.getFilePointer() < csv.length()) {
      p = loadProduto();
      p.setId(id);
      fm.writeElement(p);
      id++;
    }
  }

  /**
   * Cria novo produto e designa atributos corretamente com base nas funções de leitura.
   * @return Produto - registro lido
   * @throws Exception
   */
  public Produto loadProduto() throws Exception {
    Produto p = new Produto();
    p.setAlive(true);
    p.setUrl(readString());
    p.setSku(readString());
    p.setName(readString());
    p.setDescription(readString());
    p.setPrice(Float.parseFloat(readString()));
    p.setCurrency(readString());
    p.setImages(readStringList());
    p.setDate(Util.getUTC(readString()));
    p.setTerms(readString());
    p.setSection(readString().equals("MAN"));
    p.setImageDownloads(readStringList());

    return p;
  }

  /**
   * Lê string demarcada por vírgulas ou aspas e vírgulas (colunas dos atributos).
   * @return String - atributo do registro lido
   * @throws Exception
   */
  public String readString() throws Exception {
    byte b = csv.readByte();
    byte endMark = (byte) ',';
    if (b == (byte) '"') {
      endMark = (byte) '"';
      b = (byte) csv.readByte();
    }
    String buffer = "";
    while (b != endMark && csv.getFilePointer() < csv.length()) {
      if (b != '\n' && b != 13) buffer += (char) b;
      b = (byte) csv.readByte();
    }

    if (endMark == (byte) '"') csv.readByte();

    return buffer;
  }

  /**
   * Lê lista de strings demarcada por colchetes e vírgulas.
   * @return String[] - lista de imagens do Produto lido
   * @throws Exception
   */
  public String[] readStringList() throws Exception {
    csv.readByte();
    String buffer = "";
    byte b = (byte) csv.readByte();
    while (b != (byte) ']') {
      buffer += (char) b;
      b = (byte) csv.readByte();
    }

    String[] separated = buffer.split(",");

    for (int i = 0; i < separated.length; i++) {
      separated[i] = separated[i].substring(1);
    }

    for (int i = 0; i < separated.length; i++) {
      separated[i] = separated[i].substring(1, separated[i].length() - 1);
    }

    csv.readByte();
    if (csv.getFilePointer() < csv.length()) csv.readByte();

    return separated;
  }

  /**
   * Fecha o arquivo CSV.
   * @throws Exception
   */
  public void close() throws Exception {
    csv.close();
  }
}
