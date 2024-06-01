import java.util.Scanner;
import java.util.function.Predicate;

public class JQL {

  /**
   * Recebe (parâmetro) uma query e retorna um array de strings. Separa a query em um array de strings com base nos espaços, mas mantendo as strings entre aspas como um único elemento.
   * @param query
   * @return Retorna um array de strings que representa a query separada.
   */
  public static String[] separateQuery(String query) {
    String[] res = query.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    return res;
  }

  /**
   * Recebe (parâmetro) duas expressões e retorna uma expressão que representa a conjunção das duas.
   * @param p1
   * @param p2
   * @return Retorna uma expressão que representa a conjunção das duas expressões.
   */
  public static Predicate<Produto> and(
    Predicate<Produto> p1,
    Predicate<Produto> p2
  ) {
    return p -> p1.test(p) && p2.test(p);
  }

  /**
   * Recebe (parâmetro) duas expressões e retorna uma expressão que representa a disjunção das duas.
   * @param p1
   * @param p2
   * @return Retorna uma expressão que representa a disjunção das duas expressões.
   */
  public static Predicate<Produto> or(
    Predicate<Produto> p1,
    Predicate<Produto> p2
  ) {
    return p -> p1.test(p) || p2.test(p);
  }

  /**
   * Recebe (parâmetro) uma propriedade, operador e valor e retorna uma expressão que representa a condição.
   * @param property
   * @param operator
   * @param value
   * @return Retorna uma expressão que representa a condição.
   */
  public static Predicate<Produto> getCondition(
    String property,
    String operator,
    String value
  ) throws Exception {
    switch (property) {
      case "id":
        return p ->
          Util.compareNumber(p.getId(), operator, Integer.parseInt(value));
      case "price":
        return p ->
          Util.compareNumber(p.getPrice(), operator, Double.parseDouble(value));
      case "date":
        return p ->
          Util.compareNumber(p.getDate(), operator, Util.getUTC(value));
      case "url":
        return p -> p.compareString(property, operator, value);
      case "sku":
        return p -> p.compareString(property, operator, value);
      case "name":
        return p -> p.compareString(property, operator, value);
      case "description":
        return p -> p.compareString(property, operator, value);
      case "currency":
        return p -> p.compareString(property, operator, value);
      case "terms":
        return p -> p.compareString(property, operator, value);
      case "section":
        return p ->
          Util.compareBoolean(
            p.getSection(),
            operator,
            Boolean.parseBoolean(value)
          );
      case "images":
        return p -> p.compareStringArray(property, value);
      case "image_downloads":
        return p -> p.compareStringArray(property, value);
      default:
        throw new Exception("Invalid property");
    }
  }

  /**
   * Recebe (parâmetro) uma query e retorna uma expressão que representa a condição.
   * @param query
   * @return Retorna uma expressão que representa a condição.
   */
  public static Predicate<Produto> getConditionList(String[] query)
    throws Exception {
    int i = 0;
    String property;
    String operator;
    String value;

    Predicate<Produto> expression = null;
    Predicate<Produto> newExpression;
    int logic = 0; // 0 = nao tem, 1 = and, 2 = or

    while (i < query.length) {
      property = query[i];
      operator = query[i + 1];
      value = query[i + 2];

      newExpression = getCondition(property, operator, value);

      switch (logic) {
        case 0:
          expression = newExpression;
          break;
        case 1:
          expression = and(expression, newExpression);
          break;
        case 2:
          expression = or(expression, newExpression);
          break;
        default:
          break;
      }

      if (i < query.length - 3) {
        if (query[i + 3].equals("AND")) {
          i += 4;
          logic = 1;
        } else if (query[i + 3].equals("OR")) {
          i += 4;
          logic = 2;
        } else {
          i += 3;
        }
      } else {
        i += 3;
      }
    }

    return expression;
  }

  /**
   * Recebe (parâmetro) um arquivo e uma query. Retorna um array de produtos que satisfazem a query.
   * @param fm
   * @param query
   * @return Retorna um array de produtos que satisfazem a query.
   */
  public static Produto[] query(FileManager fm, String query) throws Exception {
    String[] separated = separateQuery(query);
    int max = 100;
    int conditionLength = separated.length;
    if (separated[separated.length - 2].equals("MAX")) {
      max = Integer.parseInt(separated[separated.length - 1]);
      conditionLength -= 2;
    }

    String[] conditionStr = new String[conditionLength];
    for (int i = 0; i < conditionLength; i++) {
      conditionStr[i] = separated[i];
    }

    Predicate<Produto> condition = getConditionList(conditionStr);

    Produto[] res = fm.conditionalSearch(condition, max);

    return res;
  }

  /**
   * Recebe (parâmetro) um arquivo e uma query. Retorna um array de produtos que satisfazem a query.
   * @throws Exception
   */
  public static void startJQL() throws Exception {
    Scanner sc = new Scanner(System.in);
    System.out.println(
      "Qual ação deseja realizar:\n" +
      "1 - Começar novo arquivo\n" +
      "2 - Continuar arquivo existente"
    );

    int action = Integer.parseInt(sc.nextLine());

    FileManager fm = new FileManager();

    if (action == 1) {
      System.out.println("Digite o nome do arquivo que deseja criar");
      String filename = sc.nextLine();
      fm.start(filename);
      System.out.println("Digite o nome do arquivo csv que deseja abrir");
      String csv = sc.nextLine();
      CsvReader c = new CsvReader(csv);
      c.loadFromCsv(fm);
    } else {
      System.out.println("Digite o nome do arquivo que deseja abrir");
      String filename = sc.nextLine();
      fm.loadFile(filename);
    }

    int nextAction = 0;

    while (nextAction != 6) {
      System.out.println(
        "Qual será a próxima ação?\n" +
        "1 - Pesquisa avançada\n" +
        "2 - Adicionar produtos\n" +
        "3 - Ordenar produtos\n" +
        "4 - Excluir produto\n" +
        "5 - Atualizar produto\n" +
        "6 - Pesquisar produtos\n" +
        "7 - Buscar elemento utilizando Arvore B\n" +
        "8 - Realizar Backup\n" +
        "9 - Restaurar Backup\n" +
        "10 - Parar"
      );

      nextAction = Integer.parseInt(sc.nextLine());

      switch (nextAction) {
        case 1:
          System.out.println("Digite a query que deseja realizar");
          String query = sc.nextLine();
          Produto[] res = query(fm, query);

          boolean foundOne = false;

          System.out.println("\nResultados:\n\n");

          for (Produto p : res) {
            if (p != null) {
              System.out.println(p.toString() + "\n\n");
              foundOne = true;
            }
          }

          if (!foundOne) {
            System.out.println("Nenhum produto encontrado\n\n");
          }
          break;
        case 2:
          Produto p = new Produto();
          System.out.println("Digite o nome do produto");
          p.setName(sc.nextLine());
          System.out.println("Digite a descrição do produto");
          p.setDescription(sc.nextLine());
          System.out.println("Digite o preço do produto");
          p.setPrice(Float.parseFloat(sc.nextLine()));
          System.out.println("Digite a moeda do produto");
          p.setCurrency(sc.nextLine());
          System.out.println("Digite a url do produto");
          p.setUrl(sc.nextLine());
          System.out.println("Digite o sku do produto");
          p.setSku(sc.nextLine());
          System.out.println("Digite a data do produto (em milisegundos)");
          p.setDate(Long.parseLong(sc.nextLine()));
          System.out.println("Digite os termos do produto");
          p.setTerms(sc.nextLine());
          System.out.println("Digite a seção do produto (M/F)");
          p.setSection(sc.nextLine().equals("M"));
          System.out.println(
            "Digite as imagens do produto (separadas por vírgula)"
          );
          p.setImages(sc.nextLine().split(","));
          System.out.println(
            "Digite os downloads das imagens do produto (separados por vírgula)"
          );
          p.setImageDownloads(sc.nextLine().split(","));
          p.setAlive(true);
          fm.writeElement(p);

          System.out.println(
            "\n\nProduto com id " + p.getId() + " adicionado com sucesso\n"
          );

          break;
        case 3:
          System.out.println(
            "Escolha a propriedade pela qual deseja ordenar os produtos\n" +
            "1- id\n" +
            "2- url\n" +
            "3- sku\n" +
            "4- name\n" +
            "5- description\n" +
            "6- price\n" +
            "7- currency\n" +
            "8- date\n" +
            "9- terms\n"
          );

          int choice = Integer.parseInt(sc.nextLine());
          String property = "";
          switch (choice) {
            case 1:
              property = "id";
              break;
            case 2:
              property = "url";
              break;
            case 3:
              property = "sku";
              break;
            case 4:
              property = "name";
              break;
            case 5:
              property = "description";
              break;
            case 6:
              property = "price";
              break;
            case 7:
              property = "currency";
              break;
            case 8:
              property = "date";
              break;
            case 9:
              property = "terms";
              break;
            default:
              break;
          }

          String endFile = "";
          System.out.println("Qual será o arquivo de saída?\n");
          endFile = sc.nextLine();
          Sorter.intercalacaoBalanceada(fm, property, endFile);
          System.out.println("\nOrdenação realizada com sucesso!\n");

          break;
        case 4:
          System.out.println("Digite o id do produto que deseja excluir");
          int id = Integer.parseInt(sc.nextLine());
          fm.deleteElement(id);
          System.out.println("\nProduto excluído com sucesso!\n");
          break;
        case 5:
          System.out.println("Digite o id do produto que deseja atualizar");
          int id2 = Integer.parseInt(sc.nextLine());

          Produto produto = fm.readElement(id2);

          System.out.println(
            "Qual propriedade deseja atualizar?\n" +
            "1- url\n" +
            "2- sku\n" +
            "3- name\n" +
            "4- description\n" +
            "5- price\n" +
            "6- currency\n" +
            "7- date\n" +
            "8- terms\n"
          );

          int choice2 = Integer.parseInt(sc.nextLine());
          String property2 = "";

          System.out.println("Digite o novo valor");
          String value = sc.nextLine();

          switch (choice2) {
            case 1:
              property2 = "url";
              produto.setUrl(value);
              break;
            case 2:
              property2 = "sku";
              produto.setSku(value);
              break;
            case 3:
              property2 = "name";
              produto.setName(value);
              break;
            case 4:
              property2 = "description";
              produto.setDescription(value);
              break;
            case 5:
              property2 = "price";
              produto.setPrice(Float.parseFloat(value));
              break;
            case 6:
              property2 = "currency";
              produto.setCurrency(value);
              break;
            case 7:
              property2 = "date";
              produto.setDate(Long.parseLong(value));
              break;
            case 8:
              property2 = "terms";
              produto.setTerms(value);
              break;
            default:
              break;
          }

          fm.updateElement(produto);

          System.out.println("Produto atualizado com sucesso!\n\n");

          break;
        case 6:
          //se inclusivo, retorna a união dos dois arrays, se exclusivo, retorna a interseção
          //se produtos1 for null, retorna produtos2
          //se produtos2 for null, retorna produtos1
          //se ambos forem null, retorna null
          //remover produtos duplicados em todos os casos ao final
          System.out.println("Quais termos devem estar no titulo?");
          String titulo = sc.nextLine();
          System.out.println("Qual termo deve estar na categoria do produto?");
          String category = sc.nextLine();
          System.out.println("Busca deve ser inclusiva ou exclusiva? (1/0)");
          boolean inclusivo = sc.nextLine().equals("1");
          Produto[] produtos1 = fm.findProdutosByTitle(titulo, inclusivo);

          Produto[] produtos2 = fm.findProdutosByCategory(category);

          Produto[] res2 = Util.mergeArrays(produtos1, produtos2, inclusivo);

          boolean foundOne2 = false;

          System.out.println("\nResultados:\n\n");

          for (Produto p1 : res2) {
            if (p1 != null) {
              System.out.println(p1.toString() + "\n\n");
              foundOne2 = true;
            }
          }

          if (!foundOne2) {
            System.out.println("Nenhum produto encontrado\n\n");
          }

          break;
        case 7:
          System.out.println("Digite o id do produto que deseja buscar");
          int id3 = Integer.parseInt(sc.nextLine());
          Produto p3 = fm.getProdutoComArvore(id3);
          if (p3 != null) {
            System.out.println(p3.toString());
          } else {
            System.out.println("Produto não encontrado");
          }
          break;
        case 8:
          BackupManager bm = new BackupManager();
          bm.backup();
          break;
        case 9:
          System.out.println("Digite o número do backup que deseja restaurar");
          int backupNumber = Integer.parseInt(sc.nextLine());
          System.out.println("Qual algoritmo deseja utilizar? (huffman/lzw)");
          String algoritmo = sc.nextLine();
          BackupManager bm2 = new BackupManager();
          bm2.restoreBackup(algoritmo, backupNumber);
          break;
        case 10:
          fm.close();
          sc.close();
          return;
        default:
          break;
      }
    }
    sc.close();
  }
}
