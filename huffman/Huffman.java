package huffman;

import java.util.LinkedList;
import bitArr.BitArr;

import java.util.BitSet;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class Huffman{
    public int[] getFrequencies(String text){
        int[] freq = new int[256];
        for(int i = 0; i < text.length(); i++){
            freq[text.charAt(i)]++;
        }
        return freq;
    }

    public LinkedList<No> createFirstNos(int[] freq){
        LinkedList<No> nos = new LinkedList<No>();
        for(int i = 0; i < freq.length; i++){
            if(freq[i] > 0){
                nos.add(new No(freq[i], (byte)i));
            }
        }
        return nos;
    }

    public No getLowestNo(LinkedList<No> nos){
        No lowest = nos.getFirst();
        for(No n : nos){
            if(n.freq < lowest.freq || (n.freq == lowest.freq && n.altura < lowest.altura)){
                lowest = n;
            }
        }
        nos.remove(lowest);
        return lowest;
    }

    public boolean createParent(LinkedList<No> nos){
        if(nos.size() == 1){
            return false;
        }
        No esq = getLowestNo(nos);
        No dir = getLowestNo(nos);
        No parent = new No(esq, dir, esq.freq + dir.freq, (byte)'\0');
        nos.add(parent);
        return true;
    }

    public No createTree(LinkedList<No> nos){
        while(createParent(nos));
        return nos.getFirst();
    }

    public void printTree(No n){
        if(n.isFolha()){
            System.out.println((char)n.c + " : " + n.freq + " : " + n.altura);
        }else{
            System.out.println((char)n.c + " : " + n.freq + " : " + n.altura);
            printTree(n.esq);
            printTree(n.dir);
        }
    }

    public HashMap<Byte, String> getDict(No n){
        HashMap<Byte, String> dict = new HashMap<Byte, String>();
        getDict(n, "", dict);
        return dict;
    }

    public void getDict(No n, String currentPath, HashMap<Byte, String> dict){
        if(n.isFolha()){
            dict.put(n.c, currentPath);
        }else{
            getDict(n.esq, currentPath + "0", dict);
            getDict(n.dir, currentPath + "1", dict);
        }
    }

    public void printDict(HashMap<Byte, String> dict){
        for(Byte b : dict.keySet()){
            System.out.println(b + " : " + dict.get(b));
        }
    }

    public BitArr dictToBitArr(HashMap <Byte, String> dict){
        int amount = dict.size();
        BitArr b = new BitArr(amount * 4 + 4);  // 8 bits for the char and x for the bits 6 for the amount of bits (+ 4 bytes for the amount of chars)
        b.pushInt(amount);
        for(Byte c : dict.keySet()){
            System.out.println(toBinaryString(c.byteValue()) + " : " + dict.get(c));
            b.pushByte(c);
            String s = dict.get(c);
            byte size = (byte) s.length();
            b.pushTwoThirdsByte(size);
            for(int i = 0; i < s.length(); i++){
                b.pushBit(s.charAt(i) == '1');
            }
        }
        return b;
    }

    public HashMap<Byte, String> bitArrToDict(BitArr b){
        HashMap<Byte, String> dict = new HashMap<Byte, String>();
        int amount = b.getInt(0);
        int pos = 32;
        for(int i = 0; i < amount; i++){
            byte c = b.getByte(pos);
            pos += 8;
            byte size = b.getTwoThirdsByte(pos);
            pos += 6;
            String s = "";
            for(int j = 0; j < size; j++){
                s += b.getBit(pos + j) ? "1" : "0";
            }
            pos += size;
            dict.put(c, s);
        }

        return dict;
    }
    
    public String decompressInverted(BitArr b, HashMap<String, Byte> dict) {
        int size = b.size;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1000000000);
        char[] current = new char[30];
        int j = 0;

        System.out.println(dict);

        for (int i = 0; i < size; i++) {
            current[j] = b.getBit(i) ? '1' : '0';
            j++;

            String s = new String(current, 0, j);
            if (dict.containsKey(s)) {
                byteStream.write(dict.get(s));
                j = 0;
            }

            if (i % 1000000 == 0) {
                System.out.println(i + " : " + size);
            }
        }

        return byteStream.toString();
    }


    public HashMap<String, Byte> invertDict(HashMap<Byte, String> dict){
        HashMap<String, Byte> inverted = new HashMap<String, Byte>();
        for(Byte c : dict.keySet()){
            inverted.put(dict.get(c), c);
        }
        return inverted;
    }

    public BitArr compress(String text, HashMap<Byte, String> dict){
        BitArr b = new BitArr(text.length() * 23);
        for(int i = 0; i < text.length(); i++){
            String s = dict.get((byte)text.charAt(i));
            for(int j = 0; j < s.length(); j++){
                b.pushBit(s.charAt(j) == '1');
            }
        }
        return b;
    }

    public String decompress(BitArr b, HashMap<Byte, String> dict){
        String text = "";
        String current = "";
        for(int i = 0; i < b.size; i++){
            current += b.getBit(i) ? "1" : "0";
            for(Byte c : dict.keySet()){
                if(dict.get(c).equals(current)){
                    text += (char) c.byteValue();
                    current = "";
                    break;
                }
            }

            if(i % 100000 == 0){
                System.out.println(i + " : " + b.size);
            }
        }
        return text;
    }

    public int[] getFrequenciesFromFile(RandomAccessFile raf) throws Exception{
        int[] freq = new int[256];
        while(raf.getFilePointer() < raf.length()){
            byte b =  raf.readByte();
            short index = (short) b;
            if(b < 0){
                index += 256;
            }

            freq[index]++;
        }
        return freq;
    }

    public void compressFile(String file, String compressedFile) throws Exception{
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        int[] freq = getFrequenciesFromFile(raf);
        LinkedList<No> nos = createFirstNos(freq);
        No root = createTree(nos);
        HashMap<Byte, String> dict = getDict(root);
        for(Byte b : dict.keySet()){
            short s = (short) b;
            if(b < 0){
                s += 256;
            }
            System.out.println(s + " : " + dict.get(b));   
        }
        System.out.println("DICT: " + dict);
        BitArr bitsDict = dictToBitArr(dict);
        BitArr compressedBits = new BitArr((int)raf.length() * 23);
        raf.seek(0);
        while(raf.getFilePointer() < raf.length()){
            byte b = raf.readByte();
            short index = (short) b;
            if(b < 0){
                index += 256;
            }
            String s = dict.get((byte)index);
            for(int i = 0; i < s.length(); i++){
                compressedBits.pushBit(s.charAt(i) == '1');
            }
        }
        raf.close();

        System.out.println("PEINFEINEI");

        RandomAccessFile rafCompressed = new RandomAccessFile(compressedFile, "rw");
        rafCompressed.setLength(0);
        BitArr allBits = new BitArr(bitsDict.size + compressedBits.size + 32);
        allBits.combine(bitsDict);
        allBits.pushInt(compressedBits.size); // 4 bytes for the size of the compressed bits (in bits)
        allBits.combine(compressedBits);
        int j = 0;
        for(int i = 0; i < allBits.size; i+=8){
            rafCompressed.writeByte(allBits.getByte(i));
            j+=8;
        }

        
        if(allBits.size % 8 != 0){
            BitArr tmp = new BitArr(1);
            
            for(int i = 0; i < allBits.size % 8; i++){
                tmp.pushBit(allBits.getBit(j + i));
            }
    
            for(int i = 0; i < 8 - allBits.size % 8; i++){
                tmp.pushBit(false);
            }

            rafCompressed.writeByte(tmp.getByte(0));
        }


        rafCompressed.close();
    }

    /* public void decompressFile(String compressedFile, String decompressedFile) throws Exception{
        RandomAccessFile raf = new RandomAccessFile(compressedFile, "r");
        BitArr allBits = new BitArr((int)raf.length() * 23);
        while(raf.getFilePointer() < raf.length()){
            allBits.pushByte(raf.readByte());
        }
        raf.close();
        int pos = 0;
        int amount = allBits.getInt(pos);
        pos += 32;
        HashMap<Byte, String> dict = new HashMap<Byte, String>();
        for(int i = 0; i < amount; i++){
            byte b = allBits.getByte(pos);
            pos += 8;
            byte size = allBits.getTwoThirdsByte(pos);
            pos += 6;
            String s = "";
            for(int j = 0; j < size; j++){
                s += allBits.getBit(pos + j) ? "1" : "0";
            }
            pos += size;
            dict.put(b, s);
        }
        int compressedSize = allBits.getInt(pos);
        pos += 32;
        BitArr compressedBits = new BitArr(compressedSize);
        System.out.println("compressedSize: " + compressedSize);
        for(int i = 0; i < compressedSize; i++){
            compressedBits.pushBit(allBits.getBit(pos + i));
        }
        System.out.println("eondsf");
        //String decompressed = decompress(compressedBits, dict);
        HashMap<String, Byte> invertedDict = invertDict(dict);
        String decompressed = decompressInverted(compressedBits, invertedDict);
        System.out.println("cxzcxcvdf");
        RandomAccessFile rafDecompressed = new RandomAccessFile(decompressedFile, "rw");
        rafDecompressed.setLength(0);
        rafDecompressed.writeBytes(decompressed);
        rafDecompressed.close();
    } */

    public static String toBinaryString(byte b){
        String s = "";
        for(int i = 0; i < 8; i++){
            s = (b & 1) + s;
            b >>= 1;
        }
        return s;
    }

    public void decompressFile(String compressedFile, String decompressedFile) throws Exception{
        RandomAccessFile raf = new RandomAccessFile(compressedFile, "r");
        BitArr allBits = new BitArr((int)raf.length() * 23);
        while(raf.getFilePointer() < raf.length()){
            allBits.pushByte(raf.readByte());
        }
        raf.close();
        int pos = 0;
        int amount = allBits.getInt(pos);
        System.out.println("amount: " + amount);
        pos += 32;
        HashMap<Byte, String> dict = new HashMap<Byte, String>();
        for(int i = 0; i < amount; i++){
            byte b = allBits.getByte(pos);
            pos += 8;
            byte size = allBits.getTwoThirdsByte(pos);
            pos += 6;
            String s = "";
            for(int j = 0; j < size; j++){
                s += allBits.getBit(pos + j) ? "1" : "0";
            }
            pos += size;
            System.out.println("b: " + toBinaryString(b) + " : " + size + " : " + s);
            dict.put(b, s);
        }
        int compressedSize = allBits.getInt(pos);
        pos += 32;
        System.out.println("Dict size: " + dict.size());
        System.out.println("compressedSize: " + compressedSize);
        BitArr compressedBits = new BitArr(compressedSize);
        for(int i = 0; i < compressedSize; i++){
            compressedBits.pushBit(allBits.getBit(pos + i));
        }
        System.out.println("eondsf");
        //String decompressed = decompress(compressedBits, dict);
        System.out.println("sexo");
        System.out.println(dict);
        for(Byte b : dict.keySet()){
            System.out.println(b + " : " + toBinaryString(b) + " : " + dict.get(b));
        }
        HashMap<String, Byte> invertedDict = invertDict(dict);
        String decompressed = decompressInverted(compressedBits, invertedDict);
        System.out.println("decompressed: " + decompressed);
        System.out.println("cxzcxcvdf");
        RandomAccessFile rafDecompressed = new RandomAccessFile(decompressedFile, "rw");
        rafDecompressed.setLength(0);
        //rafDecompressed.writeBytes(decompressed);
        System.out.println("JIJCIWIC");
        rafDecompressed.write(decompressed.getBytes(StandardCharsets.UTF_8));
        rafDecompressed.close();
    }

    public static void main(String[] args){
        Huffman h = new Huffman();

        try{
            h.compressFile("data2.dat", "compressed.txt");
            h.decompressFile("compressed.txt", "decompressed.txt");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}