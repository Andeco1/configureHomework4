import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
/* LOAD MOVE READ SQRT*/
public class Assembler {
    private class Command{
        public int A,B,C,D;
        public String hex;
        public Command() {
        }

        @Override
        public String toString() {
            return "Command{" +
                    "A=" + A +
                    ", B=" + B +
                    ", C=" + C +
                    ", D=" + D +
                    ", hex='" + hex + '\'' +
                    '}';
        }
    }
    private String pathInput, pathOutput, pathLog;
    FileReader reader;
    FileWriter writer;
    private ArrayList<Command> logs;
    private HashMap<String,Integer> memory;
    private Integer freeMemoryIndex;

    public Assembler(String pathInput, String pathOutput, String pathLog) {
        this.pathInput = pathInput;
        this.pathOutput = pathOutput;
        this.pathLog = pathLog;
        this.logs = new ArrayList<>();
        this.memory = new HashMap<>();
        this.freeMemoryIndex =0 ;
    }

    public void start(){
        FileReader inputReader;
        try {
            inputReader =  new FileReader(pathInput);
            FileOutputStream writer = new FileOutputStream(pathOutput);
            String[] command;
            while ((command = getCommand(inputReader)) != null) {
                if (command== null) {
                    break;
                }
                executeCommand(command, writer);
            }
            FileWriter logWriter = new FileWriter(pathLog);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            gson.toJson(logs, logWriter);
            logWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private String[] getCommand(FileReader inputReader) throws IOException {
        String[] command = new String[4];
        for(int i = 0; i<4;i++){
            StringBuilder str = new StringBuilder();
            char characher;
            do{
                characher = (char) inputReader.read();
                str.append(characher);
                if(str.toString().contains("END")){return null;}
            }while(characher != ' ' && characher !='\n');
            command[i] = str.toString().strip();
            if (characher == '\n'){
                break;
            }
        }
        return command;
    }

    private void executeCommand(String[] command, FileOutputStream writer){
        switch (command[0]){
            case "LOAD":
                load(command[1],command[2], writer);
                break;
            case "READ":
                read(command[1],command[2],command[3],writer);
                break;
            case "MOVE":
                move(command[1], command[2],writer);
                break;
            case "SQRT":
                sqrt(command[1],command[2],command[3],writer);
                break;
        }
    }
    private void load(String address, String constant, FileOutputStream writer) {
 //       variables.put(address,Integer.valueOf(constant));
        memory.put(address, freeMemoryIndex);
        Command newCommand = new Command();
        newCommand.A=90;
        newCommand.B=memory.get(address);
        newCommand.C= Integer.parseInt(constant);
        Long x = 0L;
        Long a = 90L;
        Long b = Long.valueOf(freeMemoryIndex++);
        Long c = Long.valueOf(constant);
        x = x | (a << 56);
        x = x | (b << 50);
        x = x | (c << 20);
        String binary = Long.toBinaryString(x);
        while (binary.length() < 64){
            binary = "0" + binary;
        }
        String bytes = "";
        StringBuilder newLog = new StringBuilder();
        for(int i = 0; i < 8; i++){
            bytes = binary.substring(i*8,(i+1)*8);
            newLog.append("0x");
            try {
                writer.write((byte) Integer.parseInt(bytes,2));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String hex = Integer.toHexString(Integer.parseInt(bytes,2));
            if(hex.length()==1){
                newLog.append("0");
            }
            newLog.append(hex);
            if(i!=7) {
                newLog.append(", ");
            }
        }
        newCommand.hex = newLog.toString();
        logs.add(newCommand);
    }
    private void read(String addressTo, String addressFrom, String shift, FileOutputStream writer){
        if(!memory.keySet().contains(addressTo)){
            memory.put(addressTo,freeMemoryIndex++);
        }
        Command newCommand = new Command();
        newCommand.A=1;
        newCommand.B=memory.get(addressTo);
        newCommand.C=memory.get(addressFrom);
        newCommand.D=Integer.parseInt(shift);
        Long x = 0L;
        Long a = 1L;
        Long b = Long.valueOf(memory.get(addressTo));
        Long c = Long.valueOf(memory.get(addressFrom));
        Long d = Long.valueOf(shift);
        x = x | (a << 56);
        x = x | (b << 50);
        x = x | (c << 44);
        x = x | (d << 38);
        String binary = Long.toBinaryString(x);
        while (binary.length() < 64){
            binary = "0" + binary;
        }
        String bytes = "";
        StringBuilder newLog = new StringBuilder();
        for(int i = 0; i < 8; i++){
            bytes = binary.substring(i*8,(i+1)*8);
            newLog.append("0x");
            try {
                writer.write((byte) Integer.parseInt(bytes,2));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String hex = Integer.toHexString(Integer.parseInt(bytes,2));
            if(hex.length()==1){
                newLog.append("0");
            }
            newLog.append(hex);
            if(i!=7) {
                newLog.append(", ");
            }
        }
        newCommand.hex = newLog.toString();
        logs.add(newCommand);
        memory.put(addressTo, memory.get(addressFrom)+Integer.valueOf(shift));
    }
    private void move(String addressTo, String addressFrom, FileOutputStream writer){
        if(!memory.keySet().contains(addressTo)){
            memory.put(addressTo,freeMemoryIndex++);
        }
        Command newCommand = new Command();
        newCommand.A=62;
        newCommand.B=memory.get(addressTo);
        newCommand.C=memory.get(addressFrom);

        Long x = 0L;
        Long a = 62L;
        Long b = Long.valueOf(memory.get(addressTo));
        Long c = Long.valueOf(memory.get(addressFrom));
        x = x | (a << 56);
        x = x | (b << 50);
        x = x | (c << 44);
        String binary = Long.toBinaryString(x);
        while (binary.length() < 64){
            binary = "0" + binary;
        }
        String bytes = "";
        StringBuilder newLog = new StringBuilder();
        for(int i = 0; i < 8; i++){
            bytes = binary.substring(i*8,(i+1)*8);
            newLog.append("0x");
            try {
                writer.write((byte) Integer.parseInt(bytes,2));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String hex = Integer.toHexString(Integer.parseInt(bytes,2));
            if(hex.length()==1){
                newLog.append("0");
            }
            newLog.append(hex);
            if(i!=7) {
                newLog.append(", ");
            }
        }
        newCommand.hex = newLog.toString();
        logs.add(newCommand);
        memory.put(addressTo,memory.get(addressFrom));
    }
    private void sqrt(String addressTo,String shift, String addressFrom, FileOutputStream writer) {
        if (!memory.keySet().contains(addressTo)) {
            memory.put(addressTo, freeMemoryIndex++);
        }
        Integer str = (memory.get(addressTo));
        memory.put(addressTo,memory.get(addressFrom));
        Command newCommand = new Command();
        newCommand.A=137;
        newCommand.B=memory.get(addressTo);
        newCommand.C=Integer.parseInt(shift);
        newCommand.D=memory.get(addressFrom);
        Long x = 0L;
        Long a = 137L;
        Long b = Long.valueOf(str);
        Long c = Long.valueOf(Integer.parseInt(shift));
        Long d = Long.valueOf(memory.get(addressFrom));
        x = x | (a << 56);
        x = x | (b << 50);
        x = x | (c << 44);
        x = x | (d << 40);
        String binary = Long.toBinaryString(x);
        while (binary.length() < 64){
            binary = "0" + binary;
        }
        String bytes = "";
        StringBuilder newLog = new StringBuilder();
        for(int i = 0; i < 8; i++){
            bytes = binary.substring(i*8,(i+1)*8);
            newLog.append("0x");
            try {
                writer.write((byte) Integer.parseInt(bytes,2));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String hex = Integer.toHexString(Integer.parseInt(bytes,2));
            if(hex.length()==1){
                newLog.append("0");
            }
            newLog.append(hex);
            if(i!=7) {
                newLog.append(", ");
            }
        }
        newCommand.hex=newLog.toString();
        logs.add(newCommand);
    }
}
