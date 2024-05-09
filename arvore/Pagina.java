package arvore;

//ordem 8
public class Pagina{
    byte nElementos;
    boolean isFolha;
    int[] chaves;
    long[] posicoes;
    long[] filhos;

    public Pagina(){
        nElementos = 0;
        isFolha = true;
        chaves = new int[7];
        posicoes = new long[7];
        filhos = new long[8];
    }

    public void addNumber(byte[] arr, int n, int pos){
        arr[pos] = (byte)(n);
        arr[pos+1] = (byte)(n >> 8);
        arr[pos+2] = (byte)(n >> 16);
        arr[pos+3] = (byte)(n >> 24);
    }

    public void addNumber(byte[] arr, long n, int pos){
        arr[pos] = (byte)(n);
        arr[pos+1] = (byte)(n >> 8);
        arr[pos+2] = (byte)(n >> 16);
        arr[pos+3] = (byte)(n >> 24);
        arr[pos+4] = (byte)(n >> 32);
        arr[pos+5] = (byte)(n >> 40);
        arr[pos+6] = (byte)(n >> 48);
        arr[pos+7] = (byte)(n >> 56);
    }

    public byte[] toByteArray(){
        int i = 0;
        byte[] b = new byte[150];
        b[i++] = nElementos;
        b[i++] = (byte)(isFolha ? 1 : 0);
        addNumber(b, filhos[0], i);
        i += 8;

        for(int j = 0; j < 7; j++){
            addNumber(b, chaves[j], i);
            i += 4;
            addNumber(b, posicoes[j], i);
            i += 8;
            addNumber(b, filhos[j+1], i);
            i += 8;
        }
        
        return b;
    }

    public boolean isCheia(){
        return nElementos == 7;
    }

    public int readInt(byte[] b, int pos){
        return (b[pos] & 0xFF) | ((b[pos+1] & 0xFF) << 8) | ((b[pos+2] & 0xFF) << 16) | ((b[pos+3] & 0xFF) << 24);
    }

    public long readLong(byte[] b, int pos){
        return (b[pos] & 0xFF) | ((b[pos+1] & 0xFF) << 8) | ((b[pos+2] & 0xFF) << 16) | ((b[pos+3] & 0xFF) << 24) | ((b[pos+4] & 0xFF) << 32) | ((b[pos+5] & 0xFF) << 40) | ((b[pos+6] & 0xFF) << 48) | ((b[pos+7] & 0xFF) << 56);
    }

    public static Pagina fromByteArray(byte[] b){
        Pagina p = new Pagina();
        int i = 0;
        p.nElementos = b[i++];
        p.isFolha = b[i++] == 1;
        
        p.filhos[0] = p.readLong(b, i);
        i += 8;

        for(int j = 0; j < p.nElementos; j++){
            p.chaves[j] = p.readInt(b, i);
            i += 4;
            p.posicoes[j] = p.readLong(b, i);
            i += 8;
            p.filhos[j+1] = p.readLong(b, i);
            i += 8;
        }

        return p;
    }

    public int inserirElemento(int chave, long posicao){
        if(isCheia()){
            System.out.println("Pagina cheia");
            return -1;
        }
        int i = nElementos;
        while(i > 0 && chaves[i-1] > chave){
            chaves[i] = chaves[i-1];
            posicoes[i] = posicoes[i-1];
            filhos[i+1] = filhos[i];
            i--;
        }
        chaves[i] = chave;
        posicoes[i] = posicao;
        nElementos++;

        return i;
    }

    public void removerElemento(int chave){
        int i = 0;
        while(i < nElementos && chaves[i] != chave){
            i++;
        }
        if(i == nElementos){
            System.out.println("Elemento nao encontrado");
            return;
        }
        while(i < nElementos-1){
            chaves[i] = chaves[i+1];
            posicoes[i] = posicoes[i+1];
            filhos[i+1] = filhos[i+2];
            i++;
        }
        nElementos--;
    }

    public boolean contains(int chave){
        for(int i = 0; i < nElementos; i++){
            if(chaves[i] == chave){
                return true;
            }
        }
        return false;
    }

    public void print(){
        System.out.println("nElementos: "+nElementos);
        System.out.println("isFolha: "+isFolha);
        System.out.println("filhos[0]: "+filhos[0]);

        for(int i = 0; i < 7; i++){
            System.out.println("chave: "+chaves[i]);
            System.out.println("posicao: "+posicoes[i]);
            System.out.println("filhos["+(i+1)+"]: "+filhos[i+1]);
        }
    }

    public static void main(String[] args) {
        Pagina p = new Pagina();
        for(int i = 0; i < 7; i++){
            p.chaves[i] = i;
            p.posicoes[i] = i*100;
            p.filhos[i] = i;
            p.nElementos++;
        }
        System.out.println("nelementos: "+p.nElementos);
        byte[] b = p.toByteArray();
        Pagina p2 = Pagina.fromByteArray(b);
        System.out.println("nelementos: "+p2.nElementos);
        for(int i = 0; i < 7; i++){
            System.out.println(p2.chaves[i]);
            System.out.println(p2.posicoes[i]);
            System.out.println(p2.filhos[i]);
        }
    }
}