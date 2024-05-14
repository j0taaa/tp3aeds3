package huffman;

public class No {
    No esq;
    No dir;
    int freq;
    byte c;
    int altura;

    public No(No esq, No dir, int freq, byte c, int a){
        this.esq = esq;
        this.dir = dir;
        this.freq = freq;
        this.c = c;
        this.altura = a;
    }

    public No(No esq, No dir, int freq, byte c){
        this.esq = esq;
        this.dir = dir;
        this.freq = freq;
        this.c = c;
        this.altura = Math.max(esq != null ? esq.altura : -1, dir != null ? dir.altura : -1) + 1;
    }

    public No(int freq, byte c){
        this(null, null, freq, c);
    }

    public boolean isFolha(){
        return esq == null && dir == null;
    }

    public String toString(){
        return c + " : " + freq + " : " + altura;
    }
}
