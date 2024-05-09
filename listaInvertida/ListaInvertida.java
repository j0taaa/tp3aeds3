package listaInvertida;

import java.io.RandomAccessFile;

public class ListaInvertida {
    RandomAccessFile raf;
    int maxChunkSize = 30;
    
    public ListaInvertida(){
        this.raf = null;
    }

    public ListaInvertida(String path, int maxChunkSize) throws Exception{
        this.loadFile(path);
        this.maxChunkSize = maxChunkSize;
    }

    public void start(String path, int maxChunkSize) throws Exception{
        this.raf = new RandomAccessFile(path, "rw");
        this.maxChunkSize = maxChunkSize;
        raf.setLength(0);
        raf.setLength((6 + 4*maxChunkSize)*65536);
        raf.seek(0);
    }
    
    public void loadFile(String path) throws Exception{
        this.raf = new RandomAccessFile(path, "rw");
    }

    public static int fnv1a_32(byte[] data) {
        int hash = 0x811c9dc5;
        for (byte b : data) {
            hash ^= (b & 0xff);
            hash *= 0x01000193;
        }
        return hash;
    }

    public static int hashString(String text) {
        final byte[] bytes = text.getBytes();
        int hash32 = fnv1a_32(bytes);
        return hash32 & 0xFFFF;
    }

    public int getListStartAddress(String text){
        int hash = hashString(text);
        return hash * (6 + 4*maxChunkSize);
    }

    public void inserir(int id, String text) throws Exception{
        int address = getListStartAddress(text);
        raf.seek(address);
        short chunkSize = raf.readShort();
        if(chunkSize < maxChunkSize){
            raf.seek(address + 2 + chunkSize*4);
            raf.writeInt(id);
            raf.seek(address);
            raf.writeShort(chunkSize + 1);
            System.out.println("Inserido");
            return;
        }
        raf.seek(address + 2 + maxChunkSize*4);
        int next = raf.readInt();
        if(next == 0){
            raf.seek(raf.getFilePointer() - 4);
            raf.writeInt((int)raf.length());
            raf.seek(raf.length());
            raf.writeShort(1);
            raf.writeInt(id);
            for(int i = 0; i < maxChunkSize; i++){
                raf.writeInt(0);
            }
        }else{
            inserirR(id, text, next);
        }
    }
    public void inserirR(int id, String text, int address) throws Exception{
        raf.seek(address);
        short chunkSize = raf.readShort();
        if(chunkSize < maxChunkSize){
            raf.seek(address + 2 + chunkSize*4);
            raf.writeInt(id);
            raf.seek(address);
            raf.writeShort(chunkSize + 1);
            return;
        }
        raf.seek(address + 2 + maxChunkSize*4);
        int next = raf.readInt();
        if(next == 0){
            raf.seek(raf.getFilePointer() - 4);
            raf.writeInt((int)raf.length());
            raf.seek(raf.length());
            raf.writeShort(1);
            raf.writeInt(id);
            for(int i = 0; i < maxChunkSize; i++){
                raf.writeInt(0);
            }
        }else{
            inserirR(id, text, next);
        }
    }

    public int[] buscar(String text) throws Exception{
        int amount = 0;
        int startAddress = getListStartAddress(text);
        int address = startAddress;
        while(address != 0){
            raf.seek(address);
            short chunkSize = raf.readShort();
            amount += chunkSize;
            raf.seek(address + 2 + maxChunkSize*4);
            address = raf.readInt();
        }

        int[] res = new int[amount];
        address = startAddress;
        int i = 0;
        while(address != 0){
            raf.seek(address);
            int chunkSize = raf.readShort();
            for(int j = 0; j < chunkSize; j++){
                int value = raf.readInt();
                if(value != 0){
                    res[i] = value;
                    i++;
                }
            }
            raf.seek(address + 2 + maxChunkSize*4);
            address = raf.readInt();
        }
        return res;
    }

    public void inserirProduto(int id, String text) throws Exception{
        String[] words = text.split(" ");
        for(int i=0;i<words.length;i++){
            inserir(id, words[i].toLowerCase());
        }
    }

    public void remover(int id, String text) throws Exception{
        int startAddress = getListStartAddress(text);
        int address = startAddress;    
        while(address != 0){
            raf.seek(address);
            short chunkSize = raf.readShort();
            int[] values = new int[maxChunkSize];
            for(int i = 0; i < maxChunkSize; i++){
                values[i] = raf.readInt();
            }
            for(int i = 0; i < maxChunkSize; i++){
                if(values[i] == id){
                    values[i] = -1;
                    chunkSize--;
                }
            }
            raf.seek(address);
            raf.writeShort(chunkSize);
            
            int k = 0;
            while(k < maxChunkSize){
                if(values[k] != -1){
                    raf.writeInt(values[k]);
                }
                k++;
            }

            for(int i=k;i<maxChunkSize;i++){
                raf.writeInt(0);
            }
        
            raf.seek(address + 2 + maxChunkSize*4);
            address = raf.readInt();
        }
    }

    public void removerProduto(int id, String text) throws Exception{
        String[] words = text.split(" ");
        for(int i=0;i<words.length;i++){
            remover(id, words[i].toLowerCase());
        }
    }
}
