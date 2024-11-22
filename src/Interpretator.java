import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Interpretator {
    private String pathInput, pathOutput;
    FileInputStream reader;
    FileWriter writer;
    ArrayList<Integer> memory;
    public Interpretator(String pathInput, String pathOutput) {
        this.pathInput = pathInput;
        this.pathOutput = pathOutput;
        memory = new ArrayList<>(64);
        for(int i = 0; i<64; i++){
            memory.add(i,0);
        }
    }

    public void start() {
        try {
            reader = new FileInputStream(pathInput);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        executeCommand(getCommand(reader));
        executeCommand(getCommand(reader));
        executeCommand(getCommand(reader));
        executeCommand(getCommand(reader));
        output();
        for(int i : memory){
            System.out.println(i);}
    }
    private String getCommand(FileInputStream reader){
        byte[] command = new byte[8];
        try {
            reader.read(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long result = 0L;
        for (int i = 0; i < 8; i++) {
            result |= ((long) command[i] & 0xFF) << (56 - (i * 8));
        }
        String comm = Long.toBinaryString(result);
        while(comm.length()<64){
            comm = "0"+comm;
        }
        return comm;
    }
    private void executeCommand(String command){
        switch (Integer.parseInt(command.substring(0,8),2)){
            case 90:
                load(command);
                break;
            case 1 :
                read(command);
                break;
            case 62:
                move(command);
                break;
            case 137:
                sqrt(command);
                break;
        }
    }
    private void load(String parameters){
        Integer address = Integer.parseInt(parameters.substring(8,14),2);
        Integer constant =Integer.parseInt(parameters.substring(14,44),2);
        memory.set(address,constant);
    }
    private void read(String parameters){
        Integer addressTo= Integer.parseInt(parameters.substring(8,14),2);
        Integer addressFrom =Integer.parseInt(parameters.substring(14,19),2);
        Integer shift = Integer.parseInt(parameters.substring(20,26),2);
        memory.set(addressTo,memory.get(addressFrom+shift));
    }
    private void move(String parameters){
        Integer addressTo= Integer.parseInt(parameters.substring(8,14),2);
        Integer addressFrom =Integer.parseInt(parameters.substring(14,20),2);
        memory.set(addressTo,memory.get(addressFrom));
    }
    private void sqrt(String parameters){
        Integer addressTo= Integer.parseInt(parameters.substring(8,14),2);
        Integer shift =Integer.parseInt(parameters.substring(14,19),2);
        Integer addressFrom = Integer.parseInt(parameters.substring(20,26),2);
        memory.set(addressTo+shift, (int) Math.sqrt(addressFrom));
    }
    private void output() {
        HashMap<String, Integer> memoryResult = new HashMap<>();
        for (int i = 0; i < memory.size(); i++) {
            String str = Integer.toBinaryString(i);
            while(str.length()<6){str="0"+str;}
            memoryResult.put(str, memory.get(i));
        }
        Map<String, Integer> sortedMemoryResult = new TreeMap<>(memoryResult);

        FileWriter Writer;
        try {
            Writer = new FileWriter(pathOutput);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(sortedMemoryResult, Writer);
            Writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
