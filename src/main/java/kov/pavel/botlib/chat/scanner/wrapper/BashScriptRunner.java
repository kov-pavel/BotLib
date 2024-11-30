//package kov.pavel.botlib.chat.scanner.wrapper;
//
//import jdk.jshell.spi.ExecutionControl.NotImplementedException;
//import lombok.AllArgsConstructor;
//
//import java.io.*;
//
//@AllArgsConstructor
//public abstract class BashScriptRunner {
//
//    protected final File script;
//
//    public void execute() {
//        try {
//            var processBuilder = new ProcessBuilder("bash", pathToScript)
//                    .directory(script)
//                    .redirectInput(File);
//            var process = processBuilder.start();
//
//            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//
//            executeInner(reader, writer, process);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected void executeInner(BufferedReader reader, BufferedWriter writer, Process process) throws Exception {
//        throw new NotImplementedException("Implement me!");
//    }
//}
