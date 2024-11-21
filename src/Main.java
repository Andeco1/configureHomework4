import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String inputPath = "src/input.txt";
        String outputPath = "src/output.bin";
        String logPath = "src/logs.json";
        Assembler assembler = new Assembler(inputPath,outputPath,logPath);
        assembler.start();

    }
}