import java.io.RandomAccessFile;
import java.util.function.Predicate;

import hash.HashExtensivel;
import listaInvertida.ListaInvertida;
import arvore.ArvoreB;

public class FileManager {
    private RandomAccessFile raf;
    private HashExtensivel he;
    private ListaInvertida li1;
    private ListaInvertida li2;
    private ArvoreB arvore;

    public FileManager(){
        this.raf = null;
        he = null;
        li1 = null;
        li2 = null;
        arvore = null;
    }

    public FileManager(String path) throws Exception{
        this.start(path);
        he = new HashExtensivel("hash.dat", "indice.dat", 1, 60);
        li1 = new ListaInvertida("li1.dat", 30);
        li2 = new ListaInvertida("li2.dat", 30);
        arvore = new ArvoreB("arvore.dat");
    }

    /**
     * Cria/abre um novo arquivo, o esvazia e o inicializa.
     */
    public void start(String path) throws Exception{
        this.raf = new RandomAccessFile(path, "rw");
        this.he = new HashExtensivel("hash.dat", "indice.dat", 1, 60);
        this.li1 = new ListaInvertida();
        this.li2 = new ListaInvertida();
        this.li1.start("li1.dat", 30);
        this.li2.start("li2.dat", 30);
        this.arvore = new ArvoreB();
        this.arvore.start("arvore.dat");
        raf.setLength(0);
        raf.seek(0);
        raf.writeInt(0);
    }

    /**
     * Carrega um arquivo existente e o associa à variável do tipo RAF.
     * @param path
     * @throws Exception
     */
    public void loadFile(String path) throws Exception{
        this.raf = new RandomAccessFile(path, "rw");
        this.he = new HashExtensivel("hash.dat", "indice.dat");
        this.li1 = new ListaInvertida("li1.dat", 30);
        this.li2 = new ListaInvertida("li2.dat", 30);
        this.arvore = new ArvoreB("arvore.dat");
    }

    /**
     * Escreve um array de bytes no arquivo na posição atual onde o ponteiro RAF está.
     * @param bArr
     * @throws Exception
     */
    public void writeBytes(byte[] bArr) throws Exception{
        raf.write(bArr);
    }

    /**
     * Lê um array de bytes do arquivo na posição atual onde o ponteiro RAF está.
     * @param len
     * @return byte[] - array de bytes lido
     * @throws Exception
     */
    public byte[] readBytes(int len) throws Exception{
        byte[] bArr = new byte[len];
        raf.read(bArr);
        return bArr;
    }

    /**
     * Lê um elemento (registro) do arquivo na posição atual onde o ponteiro RAF está.
     * @return Produto - registro lido
     * @throws Exception
     */
    public Produto readElement() throws Exception{
        raf.readByte();
        int len = raf.readInt();
        raf.seek(raf.getFilePointer()-5);
        byte[] bArr = new byte[len+5];
        raf.read(bArr);
        Produto p = new Produto(bArr);
        return p;
    }

    /**
     * Posiciona ponteiro RAF para início do arquivo e lê último id inserido para designar o id de um novo registro corretamente (ultimoInserido + 1). Então, uma array de bytes (novo registro/elemento) é então escrita ao ginal do arquivo.
     * @param p
     * @throws Exception
     */
    public int writeElement(Produto p) throws Exception{
        raf.seek(0);
        int lastId = raf.readInt();
        
        p.setId(lastId+1);

        long pos = raf.length();

        raf.seek(pos);
        raf.write(p.toByteArray());

        raf.seek(0);
        raf.writeInt(lastId+1);


        System.out.println(p.getId());
        he.inserir(p.getId(), pos);

        li1.inserirProduto(p.getId(), p.getName());
        li2.inserirProduto(p.getId(), p.getTerms());

        arvore.inserir(p.getId(), pos);

        return lastId+1;
    }
    
    /**
     * Move ponteiro RAF para início do arquivo, "pula" último id inserido e procura registro passado por meio de id (parâmetro). Se encontrado, retorna registro (Produto), caso contrário, retorna null.
     * @param id
     * @return Produto - registro lido
     * @throws Exception
     */
    public Produto readElement(int id) throws Exception{
        long pos = he.pesquisar(id);
        System.out.println(pos);
        if(pos != -1){
            raf.seek(pos);
            return readElement();
        }

        return null;
    }

    /**
     * Fecha ponteiro RAF.
     * @throws Exception
     */
    public void close() throws Exception{
        raf.close();
    }

    /**
     * Deleta registro (Produto) do arquivo por meio de id (parâmetro). Retorna true se registro foi deletado, caso contrário, retorna false.
     * @param id
     * @return boolean - true se registro foi deletado, caso contrário, retorna false
     * @throws Exception
     */
    public boolean deleteElement(int id) throws Exception{
        raf.seek(0);
        raf.readInt();
        long pos = 0;
        while(raf.getFilePointer() < raf.length()){
            pos = raf.getFilePointer();
            Produto p = readElement();
            if(p.getId() == id){
                p.setAlive(false);
                he.remover(id);
                li1.removerProduto(id, p.getName());
                li2.removerProduto(id, p.getTerms());
                arvore.remover(id);
                raf.seek(pos);
                raf.write(p.toByteArray());
                return true;
            }
        }

        return false;
    }

    /**
     * Atualiza registro (Produto) no arquivo por meio de id (parâmetro). Retorna true se registro foi atualizado, caso contrário, retorna false.
     * @param p
     * @return boolean - true se registro foi atualizado, caso contrário, retorna false
     * @throws Exception
     */
    public boolean updateElement(Produto p) throws Exception{
        raf.seek(0);
        raf.readInt();
        long pos = 0;

        while(raf.getFilePointer() < raf.length()){
            pos = raf.getFilePointer();
            Produto p2 = readElement();
            if(p2.getId() == p.getId() && p2.getAlive()){
                li1.removerProduto(p.getId(), p2.getName());
                li2.removerProduto(p.getId(), p2.getTerms());
                li1.inserirProduto(p.getId(), p.getName());
                li2.inserirProduto(p.getId(), p.getTerms());
                byte[] bArr = p.toByteArray();
                int len = bArr.length;
                raf.seek(pos+1);
                int len2 = raf.readInt()+5;
                if(len <= len2){
                    raf.seek(pos);
                    raf.write(bArr);
                    raf.seek(pos+1);
                    raf.writeInt(len2-5);
                }else{
                    raf.seek(pos);
                    raf.writeByte((byte)'*');
                    raf.seek(raf.length());
                    raf.write(bArr);
                }

                return true;
            }
        }

        return false;
    }

    public Produto[] conditionalSearch(Predicate<Produto> condition, int max) throws Exception{
        raf.seek(0);
        raf.readInt();
        Produto[] res = new Produto[max];
        int count = 0;
        while(raf.getFilePointer() < raf.length() && count < max){
            Produto p = readElement();
            if(condition.test(p)){
                res[count] = p;
                count++;
            }
        }

        return res;
    }

    /**
     * Reposiciona ponteiro RAF para início do arquivo (pula 4 bytes e parte para o primeiro registro).
     * @throws Exception
     */
    public void resetPosition() throws Exception{
        raf.seek(0);
        raf.readInt();
    }

    /**
     * Lê próximo registro (Produto) do arquivo. Se não houver mais registros, retorna null.
     * @param n
     * @return
     */
    public Produto[] readNext(int n){
        Produto[] res = new Produto[n];
        for(int i=0;i<n;i++){
            try{
                res[i] = readElement();
            }catch(Exception e){
                res[i] = null;
            }
        }

        return res;
    }

    /**
     * Retorna true se houver mais registros (Produtos) no arquivo, caso contrário, retorna false.
     * @return boolean - true se houver mais registros, caso contrário, retorna false
     * @throws Exception
     */
    public boolean hasNext() throws Exception{
        return raf.getFilePointer() < raf.length();
    }

    /**
     * Retorna quantidade de registros (Produtos) no arquivo com base na função hasNext().
     * @return int - quantidade de registros
     * @throws Exception
     */
    public int getProductAmount() throws Exception{
        int amount = 0;
        resetPosition();
        while(hasNext()){
            readElement();
            amount++;
        }

        return amount;
    }

    public long getFilePointerPosition() throws Exception{
        return raf.getFilePointer();
    }

    public void fillHashExtensivel() throws Exception{
        raf.seek(4);
        while(raf.getFilePointer() < raf.length()){
            long pos = raf.getFilePointer();
            Produto p = readElement();
            he.inserir(p.getId(), pos);
        }
    }

    public Produto findProdutoUsingHash(HashExtensivel he, int id) throws Exception{
        long pos = he.pesquisar(id);
        if(pos == -1){
            return null;
        }

        raf.seek(pos);
        return readElement();
    }

    public Produto[] findProdutosByTitle(String title, boolean inclusive) throws Exception{
        String[] terms = title.split(" ");
        int[] ids = new int[1000];
        int count = 0;
        if(inclusive){
            for(String term:terms){
                int[] res = li1.buscar(term);
                for(int i=0;i<res.length;i++){
                    if(res[i] != 0){
                        ids[count] = res[i];
                        count++;
                    }
                }
            }
        }else{
            int[] res = li1.buscar(terms[0]);
            for(int i=0;i<res.length;i++){
                if(res[i] == 0){
                    continue;
                }

                boolean found = true;
                for(int j=1;j<terms.length;j++){
                    int[] res2 = li1.buscar(terms[j]);
                    boolean found2 = false;
                    for(int k=0;k<res2.length;k++){
                        if(res2[k] == res[i]){
                            found2 = true;
                            break;
                        }
                    }

                    if(!found2){
                        found = false;
                        break;
                    }
                }

                if(found){
                    ids[count] = res[i];
                    count++;
                }
            }
        }

        Produto[] res = new Produto[count];
        for(int i=0;i<count;i++){
            res[i] = findProdutoUsingHash(he, ids[i]);
        }
        return res;
    }

    public Produto[] findProdutosByCategory(String category) throws Exception{
        int[] ids = li2.buscar(category);
        Produto[] res = new Produto[ids.length];
        for(int i=0;i<ids.length;i++){
            res[i] = findProdutoUsingHash(he, ids[i]);
        }
        return res;
    }

    public Produto getProdutoComArvore(int id) throws Exception{
        long pos = arvore.buscar(id);
        if(pos == -1){
            return null;
        }

        raf.seek(pos);
        return readElement();
    }
}
