import java.io.File;
import huffman.Huffman;
import lzw.LZW;

public class BackupManager {
    static final String backupDir = "./backups/";
    static final String[] backupFiles = {"data.dat", "li1.dat", "li2.dat", "arvore.dat", "hash.dat"};

    public int getBackupCount() {
        File backupDir = new File(this.backupDir);
        return backupDir.list().length;
    }

    public long compressHuffman(String resultFolder) throws Exception { //retorna tamanho total final
        Huffman huffman = new Huffman();

        long totalFinalSize = 0;

        for(String file : backupFiles) {
            huffman.compressFile(file, resultFolder + "/" + file);
            totalFinalSize += new File(resultFolder + "/" + file).length();
        }

        return totalFinalSize;
    }

    public long compressLZW(String resultFolder) throws Exception {
        LZW lzw = new LZW();

        long totalFinalSize = 0;

        for(String file : backupFiles) {
            lzw.compress(file, resultFolder + "/" + file);
            totalFinalSize += new File(resultFolder + "/" + file).length();
        }

        return totalFinalSize;
    }

    public void backup() throws Exception{
        File backupDir = new File(this.backupDir);
        if (!backupDir.exists()) {
            backupDir.mkdir();
        }

        int backupCount = getBackupCount();

        File backupFolder = new File(this.backupDir + "backup" + backupCount);
        backupFolder.mkdir();

        long totalFileSizeInitial = 0;

        for(String file : backupFiles) {
            totalFileSizeInitial += new File(file).length();
        }

        File huffmanFolder = new File(backupFolder + "/huffman");
        huffmanFolder.mkdir();

        File lzwFolder = new File(backupFolder + "/lzw");
        lzwFolder.mkdir();

        long startHuffman = System.currentTimeMillis();
        
        long totalFinalSizeHuffman = compressHuffman(huffmanFolder.toString());
        
        long endHuffman = System.currentTimeMillis();

        long startLZW = System.currentTimeMillis();

        long totalFinalSizeLZW = compressLZW(lzwFolder.toString());

        long endLZW = System.currentTimeMillis();

        System.out.println("Número do backup realizado: " + backupCount);

        System.out.println("\n\nPorcentam de perda Huffman: " + (totalFinalSizeHuffman * 100 / totalFileSizeInitial) + "%");
        System.out.println("Tempo de execução Huffman: " + (endHuffman - startHuffman) + "ms");

        System.out.println("\nPorcentam de perda LZW: " + (totalFinalSizeLZW * 100 / totalFileSizeInitial) + "%");
        System.out.println("Tempo de execução LZW: " + (endLZW - startLZW) + "ms");

    }

    public void retoreBackup(String algoritmo, int backupNumber) throws Exception{
        File backupFolder = new File(this.backupDir + "backup" + backupNumber);

        long start = System.currentTimeMillis();

        if(algoritmo.equals("huffman")) {
            File huffmanFolder = new File(backupFolder + "/huffman");
            for(String file : backupFiles) {
                new Huffman().decompressFile(huffmanFolder + "/" + file, file);
            }

            long end = System.currentTimeMillis();

            System.out.println("\nDescompressão Huffman realizada com sucesso!");
            System.out.println("Tempo de execução Huffman: " + (end - start) + "ms");
        } else {
            File lzwFolder = new File(backupFolder + "/lzw");
            for(String file : backupFiles) {
                new LZW().decompress(lzwFolder + "/" + file, file);
            }

            long end = System.currentTimeMillis();

            System.out.println("\nDescompressão LZW realizada com sucesso!");
            System.out.println("Tempo de execução LZW: " + (end - start) + "ms");
        }
    }
    

    public static void main(String[] args) throws Exception{
        BackupManager bm = new BackupManager();
        System.out.println("Backup count: " + bm.getBackupCount());
        bm.backup();
    }
}
