/**
 * Classe que representa um Produto, com atributos como id, url, sku, name, description, price, currency, images, date, terms, section e image_downloads.
 * Possui métodos para converter os atributos em arrays de bytes e vice-versa, além de métodos para comparar atributos do Produto com valores passados.
 * @see ByteReader
 */
public class Produto {

  private boolean alive;
  private int id;
  private String url;
  private String sku;
  private String name;
  private String description;
  private float price;
  private String currency;
  private String[] images;
  private long date;
  private String terms;
  private boolean section;
  private String[] image_downloads;

  /**
   * Construtor de Produto
   */
  public Produto() {
    this.alive = false;
    this.id = 0;
    this.url = "";
    this.sku = "";
    this.name = "";
    this.description = "";
    this.price = 0.0f;
    this.currency = "";
    this.images = new String[0];
    this.date = 0;
    this.terms = "";
    this.section = false;
    this.image_downloads = new String[0];
  }

  public Produto(byte[] byteArr) {
    this.fromByteArray(byteArr);
  }

  /**
   * Construtor de Produto
   * @param alive
   * @param id
   * @param url
   * @param sku
   * @param name
   * @param description
   * @param price
   * @param currency
   * @param images
   * @param date
   * @param terms
   * @param section
   * @param image_downloads
   */
  public Produto(
    boolean alive,
    int id,
    String url,
    String sku,
    String name,
    String description,
    float price,
    String currency,
    String[] images,
    long date,
    String terms,
    boolean section,
    String[] image_downloads
  ) {
    this.alive = alive;
    this.id = id;
    this.url = url;
    this.sku = sku;
    this.name = name;
    this.description = description;
    this.price = price;
    this.currency = currency;
    this.images = images;
    this.date = date;
    this.terms = terms;
    this.section = section;
    this.image_downloads = image_downloads;
  }

  public boolean getAlive() {
    return this.alive;
  }

  public void setAlive(boolean a) {
    this.alive = a;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getSku() {
    return this.sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public float getPrice() {
    return this.price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public String getCurrency() {
    return this.currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String[] getImages() {
    return this.images;
  }

  public void setImages(String[] images) {
    this.images = images;
  }

  public long getDate() {
    return this.date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public String getTerms() {
    return this.terms;
  }

  public void setTerms(String terms) {
    this.terms = terms;
  }

  public boolean getSection() {
    return this.section;
  }

  public void setSection(boolean section) {
    this.section = section;
  }

  public String[] getImageDownloads() {
    return this.image_downloads;
  }

  public void setImageDownloads(String[] imageDownloads) {
    this.image_downloads = imageDownloads;
  }

  /**
   * Lê dados e os converte em arrays de bytes, que então são combinadas ao final da função.
   * @return byte[] - Registro completo dos atributos do produto.
   */
  public byte[] toByteArray() {
    byte[] aliveArr = Util.getByteArray(this.alive, (byte) ' ', (byte) '*');

    byte[] idArr = Util.getByteArray(this.id);

    byte[] urlArr = Util.getByteArray(this.url);

    byte[] skuArr = Util.getByteArray(this.sku);

    byte[] nameArr = Util.getByteArray(this.name);

    byte[] descriptionArr = Util.getLongByteArray(this.description);

    byte[] priceArr = Util.getByteArray(this.price);

    byte[] currentArr = Util.getByteArray(this.currency, 3);

    byte[] imagesArr = Util.getLongByteArray(Util.combineStrings(this.images));

    byte[] dateArr = Util.getByteArray(this.date);

    byte[] termsArr = Util.getByteArray(this.terms);

    byte[] sectionArr = Util.getByteArray(this.section, (byte) 'M', (byte) 'F');

    byte[] image_downloadsArr = Util.getLongByteArray(
      Util.combineStrings(this.image_downloads)
    );

    int length =
      idArr.length +
      urlArr.length +
      skuArr.length +
      nameArr.length +
      descriptionArr.length +
      priceArr.length +
      currentArr.length +
      imagesArr.length +
      dateArr.length +
      termsArr.length +
      sectionArr.length +
      image_downloadsArr.length;

    byte[] lengthArr = Util.getByteArray(length);

    byte[] res = Util.combineByteArrays(
      aliveArr,
      lengthArr,
      idArr,
      urlArr,
      skuArr,
      nameArr,
      descriptionArr,
      priceArr,
      currentArr,
      imagesArr,
      dateArr,
      termsArr,
      sectionArr,
      image_downloadsArr
    );

    return res;
  }

  /**
   * Recebe arrays de bytes equivalentes a cada um dos atributos e os designa corretamente às propriedades do Produto.
   * @param byteArr
   */
  public void fromByteArray(byte[] byteArr) {
    ByteReader br = new ByteReader(byteArr);
    this.alive = br.readBoolean((byte) ' ', (byte) '*');
    br.readInt();
    this.id = br.readInt();
    this.url = br.readString();
    this.sku = br.readString();
    this.name = br.readString();
    this.description = br.readLongString();
    this.price = br.readFloat();
    this.currency = br.readString(3);
    this.images = br.readStringArray();
    this.date = br.readLong();
    this.terms = br.readString();
    this.section = br.readBoolean((byte) 'M', (byte) 'F');
    this.image_downloads = br.readStringArray();
  }

  public String toString() {
    return (
      "Alive: " +
      this.alive +
      "\n" +
      "Id: " +
      this.id +
      "\n" +
      "Url: " +
      this.url +
      "\n" +
      "Sku: " +
      this.sku +
      "\n" +
      "Name: " +
      this.name +
      "\n" +
      "Description: " +
      this.description +
      "\n" +
      "Price: " +
      this.price +
      "\n" +
      "Currency: " +
      this.currency +
      "\n" +
      "Images: " +
      Util.combineStrings(this.images) +
      "\n" +
      "Date: " +
      this.date +
      "\n" +
      "Terms: " +
      this.terms +
      "\n" +
      "Section: " +
      this.section +
      "\n" +
      "Image Downloads: " +
      Util.combineStrings(this.image_downloads)
    );
  }

  /**
   * Combina parâmetro property (e.g id, price ou date) + operador (maior que, menor que, etc)
   * e número para retornar verdadeiro ou falso de acordo com o critério estabelecido
   * @param property
   * @param operator
   * @param n
   * @return true (se condição for atendida) OU false (condição não é atendida)
   */
  public boolean compareNumber(String property, String operator, double n) {
    switch (property) {
      case "id":
        return Util.compareNumber(this.id, operator, n);
      case "price":
        return Util.compareNumber(this.price, operator, n);
      case "date":
        return Util.compareNumber(this.date, operator, n);
      default:
        return false;
    }
  }

  /**
   * Combina parâmetro property (e.g url, name, etc) + operador (igual a, contém)
   * e um termo para retornar verdadeiro ou falso caso a string seja igual ou contenha o termo passada.
   * @param property
   * @param operator
   * @param str
   * @return true (termo passado está contido na propriedade do produto) OU false (termo não se encontra na propriedade especificada)
   */
  public boolean compareString(String property, String operator, String str) {
    switch (property) {
      case "url":
        if (operator.equals("=")) return this.url.equals(str);
        if (operator.equals("contains")) return this.url.contains(str);
      case "sku":
        if (operator.equals("=")) return this.sku.equals(str);
        if (operator.equals("contains")) return this.sku.contains(str);
      case "name":
        if (operator.equals("=")) return this.name.equals(str);
        if (operator.equals("contains")) return this.name.contains(str);
      case "description":
        if (operator.equals("=")) return this.description.equals(str);
        if (operator.equals("contains")) return this.description.contains(str);
      case "currency":
        if (operator.equals("=")) return this.currency.equals(str);
        if (operator.equals("contains")) return this.currency.contains(str);
      case "terms":
        if (operator.equals("=")) return this.terms.equals(str);
        if (operator.equals("contains")) return this.terms.contains(str);
      default:
        return false;
    }
  }

  /**
   * Confere se o link de uma imagem está contido dentro de uma lista de imagens associada a um determinado Produto.
   * @param property
   * @param str
   * @return true (a imagem está na lista de imagens do produto) OU false (imagem não se encontra nessa lista)
   */
  public boolean compareStringArray(String property, String str) {
    switch (property) {
      case "images":
        for (String s : this.images) {
          if (s.equals(str)) {
            return true;
          }
        }
        return false;
      case "image_downloads":
        for (String s : this.image_downloads) {
          if (s.equals(str)) {
            return true;
          }
        }
        return false;
      default:
        return false;
    }
  }
}
