package hash;

import java.io.RandomAccessFile;

public class HashExtensivel {
    RandomAccessFile hashRaf;
    RandomAccessFile indiceRaf;
    int p = -1;
    int bucketSize = -1;

    public int getP(){
        return p;
    }

    public int getBucketSize(){
        return bucketSize;
    }

    public HashExtensivel(String hashArq, String indiceArq, int pInicial, int bucketSize) throws Exception{
        this.hashRaf = new RandomAccessFile(hashArq, "rw");
        this.indiceRaf = new RandomAccessFile(indiceArq, "rw");
        this.hashRaf.setLength(0);
        this.indiceRaf.setLength(0);
        this.p = pInicial;
        this.bucketSize = bucketSize;
        if(hashRaf.length() == 0){
            indiceRaf.writeInt(p);
            indiceRaf.writeInt(bucketSize);
        }
        
        for(int i = 0; i < (Math.pow(2, p)); i++){
            indiceRaf.writeInt((int)hashRaf.getFilePointer());

            hashRaf.writeInt(pInicial);
            hashRaf.writeShort(0);
            for(int j = 0; j < bucketSize; j++){
                hashRaf.writeInt(-1);
                hashRaf.writeLong(-1);
            }
            
        }
    }

    public HashExtensivel(String hashArq, String indiceArq) throws Exception{
        this.hashRaf = new RandomAccessFile(hashArq, "rw");
        this.indiceRaf = new RandomAccessFile(indiceArq, "rw");
        this.indiceRaf.seek(0);
        this.p = indiceRaf.readInt();
        this.bucketSize = indiceRaf.readInt();
    }
    
    public int hash(int chave){
        return chave % ((int)Math.pow(2, p));
    }

    public int hashWithLocalP(int chave, int localP){
        return chave % ((int)Math.pow(2, localP));
    }

    public int getIndiceAddress(int chave) throws Exception{
        int hash = hash(chave);
        return 8 + 4*hash;
    }

    public int getIndiceAddressByModulo(int modulo) throws Exception{
        return 8 + 4*modulo;
    }

    public int getBucketAddress(int chave) throws Exception{
        int hash = hash(chave);
        indiceRaf.seek(8 + 4*hash);
        int endereco = indiceRaf.readInt();
        return endereco;
    }

    public void putBucketAddress(int chave, int endereco) throws Exception{
        int hash = hash(chave);
        indiceRaf.seek(8 + 4*hash);
        indiceRaf.writeInt(endereco);
    }

    public int createBucket(int pLocal) throws Exception{
        hashRaf.seek(hashRaf.length());
        int endereco = (int)hashRaf.length();
        hashRaf.writeInt(pLocal);
        hashRaf.writeShort(0);
        for(int i = 0; i < bucketSize; i++){
            hashRaf.writeInt(-1);
            hashRaf.writeLong(-1);
        }
        return endereco;
    }

    public void increaseP() throws Exception{
        p++;
        indiceRaf.seek(0);
        indiceRaf.writeInt(p);
        indiceRaf.seek(8);

        int[] addresses = new int[(int)(indiceRaf.length()-8)/4];
        for(int j = 0; j < addresses.length; j++){
            addresses[j] = indiceRaf.readInt();
        }
        indiceRaf.seek(indiceRaf.length());
        for(int j = 0; j < addresses.length; j++){
            indiceRaf.writeInt(addresses[j]);
        }
        
    }

    /* public void redistribuir(int chave, int localP) throws Exception{
        hashRaf.seek(hashRaf.length());
        int bucketAddress = (int)hashRaf.length();
        hashRaf.writeInt(localP+1);
        hashRaf.writeShort(0);
        for(int i = 0; i < bucketSize; i++){
            hashRaf.writeInt(-1);
            hashRaf.writeLong(-1);
        }
        indiceRaf.seek(getIndiceAddress(chave));
        int previousAddress = indiceRaf.readInt();
        indiceRaf.seek(indiceRaf.getFilePointer() - 4);
        indiceRaf.writeInt(bucketAddress);
        int chaves[] = new int[bucketSize];
        long addresses[] = new long[bucketSize];
        hashRaf.seek(previousAddress);
        int pLocal = hashRaf.readInt();
        int qtd = hashRaf.readShort();
        for(int i = 0; i < qtd; i++){
            chaves[i] = hashRaf.readInt();
            addresses[i] = hashRaf.readLong();
        }
        hashRaf.seek(previousAddress);
        hashRaf.writeInt(pLocal+1);
        hashRaf.writeShort(0);
        for(int i = 0; i < bucketSize; i++){
            hashRaf.writeInt(-1);
            hashRaf.writeLong(-1);
        }
        for(int i = 0; i < qtd; i++){
            inserir(chaves[i], addresses[i]);
        }
    } */

    public void redistribuir(int chave, int localP) throws Exception{
        hashRaf.seek(hashRaf.length());
        int bucketAddress = (int)hashRaf.length();
        hashRaf.writeInt(localP+1);
        hashRaf.writeShort(0);
        for(int i = 0; i < bucketSize; i++){
            hashRaf.writeInt(-1);
            hashRaf.writeLong(-1);
        }

        int localHash = hashWithLocalP(chave, localP);
        indiceRaf.seek(getIndiceAddressByModulo(localHash) + (int)Math.pow(2, localP)*4);
        int previousAddress = indiceRaf.readInt();
        indiceRaf.seek(indiceRaf.getFilePointer() - 4);
        indiceRaf.writeInt(bucketAddress);
        hashRaf.seek(previousAddress);
        hashRaf.writeInt(localP+1);
        int qtd = hashRaf.readShort();
        hashRaf.seek(hashRaf.getFilePointer() - 2);
        hashRaf.writeShort(0);

        int chaves[] = new int[qtd];
        long addresses[] = new long[qtd];

        for(int i = 0; i < qtd; i++){
            chaves[i] = hashRaf.readInt();
            addresses[i] = hashRaf.readLong();
        }      
        
        hashRaf.seek(previousAddress+6);

        for(int i=0;i<qtd;i++){
            hashRaf.writeInt(-1);
            hashRaf.writeLong(-1);
        }

        for(int i = 0; i < qtd; i++){
            inserir(chaves[i], addresses[i]);
        }
    }

    public void inserir(int chave, long address) throws Exception{
        int bucketAddress = getBucketAddress(chave);
        hashRaf.seek(bucketAddress);
        int pLocal = hashRaf.readInt();
        int qtd = hashRaf.readShort();
        if(qtd < bucketSize){
            hashRaf.seek(bucketAddress + 6 + 12*qtd);
            hashRaf.writeInt(chave);
            hashRaf.writeLong(address);
            hashRaf.seek(bucketAddress + 4);
            hashRaf.writeShort(qtd + 1);
        } else {
            if(pLocal == p){
                increaseP();
                redistribuir(chave, pLocal);
                inserir(chave, address);
            } else {
                redistribuir(chave, pLocal);
                inserir(chave, address);
            }
        }
    }

    public int pesquisar(int chave) throws Exception{
        int bucketAddress = getBucketAddress(chave);
        hashRaf.seek(bucketAddress);
        int pLocal = hashRaf.readInt();
        int qtd = hashRaf.readShort();
        for(int i = 0; i < qtd; i++){
            int chaveAtual = hashRaf.readInt();
            int endereco = (int)hashRaf.readLong();
            if(chaveAtual == chave){
                return endereco;
            }
        }
        return -1;
    }

    public void remover(int chave) throws Exception{
        int bucketAddress = getBucketAddress(chave);
        hashRaf.seek(bucketAddress);
        int pLocal = hashRaf.readInt();
        int qtd = hashRaf.readShort();
        int chaves[] = new int[qtd];
        long addresses[] = new long[qtd];
        int i = 0;
        int chaveIndex = -1;
        for(i = 0; i < qtd; i++){
            chaves[i] = hashRaf.readInt();
            addresses[i] = hashRaf.readLong();
            if(chaves[i] == chave){
                chaveIndex = i;
            }
        }
        if(chaveIndex == -1){
            return;
        }
        System.out.println("Chave index: " + chaveIndex);
        hashRaf.seek(bucketAddress + 6 + 12*chaveIndex);
        for(int j = chaveIndex; j < qtd-1; j++){
            hashRaf.writeInt(chaves[j+1]);
            hashRaf.writeLong(addresses[j+1]);
        }
        for(int j = 0; j < bucketSize-chaveIndex-1; j++){
            hashRaf.writeInt(-1);
            hashRaf.writeLong(-1);
        }

        hashRaf.seek(bucketAddress + 4);
        hashRaf.writeShort(qtd - 1);

        if(qtd == 1){
            int previousIndiceAddress = getIndiceAddressByModulo(hashWithLocalP(chave, pLocal-1));
            int indiceAddress = getIndiceAddress(chave);
            System.out.println("Previous indice address: " + previousIndiceAddress);
            System.out.println("Indice address: " + indiceAddress);
            indiceRaf.seek(previousIndiceAddress);   
            int previousBucketAddress = indiceRaf.readInt();
            System.out.println("Previous bucket address: " + previousBucketAddress);
            indiceRaf.seek(indiceAddress);
            indiceRaf.writeInt(previousBucketAddress);
            hashRaf.seek(previousBucketAddress);
            hashRaf.writeInt(pLocal-1);
        }
    }

    public void printIndice(){
        try{
            indiceRaf.seek(0);
            System.out.println("P: " + indiceRaf.readInt());
            System.out.println("Bucket Size: " + indiceRaf.readInt());
            for(int i = 0; i < Math.pow(2, p); i++){
                System.out.println("Bucket " + i + ": " + indiceRaf.readInt());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void printHash(){
        try{
            hashRaf.seek(0);
            while(hashRaf.getFilePointer() < hashRaf.length()){
                System.out.println("P: " + hashRaf.readInt());
                System.out.println("Qtd: " + hashRaf.readShort());
                for(int i = 0; i < bucketSize; i++){
                    System.out.println("Chave: " + hashRaf.readInt());
                    System.out.println("Address: " + hashRaf.readLong());
                }
                System.out.println();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
