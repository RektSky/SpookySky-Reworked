package ml.rektsky.spookysky.ac;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AgentMain {

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("SpookyAC has been injected! Detecting hacks...");
        File outputFile = new File(System.getProperty("java.io.tmpdir"), "SpookyAC-Log-" + System.currentTimeMillis() + ".txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            String classPath = System.getProperty("java.class.path");
            ArrayList<String> expectedClasses = new ArrayList<>();
            for (String s : classPath.split(":")) {
                fileOutputStream.write(("Classpath: " + s + "\n").getBytes(StandardCharsets.UTF_8));
                System.out.write(("Classpath: " + s + "\n").getBytes(StandardCharsets.UTF_8));
                if (new File(s).isDirectory()) {
                    visit(new File(s), "", expectedClasses);
                } else {
                    ZipInputStream inputStream = new ZipInputStream(new FileInputStream(s));
                    ZipEntry entry = inputStream.getNextEntry();
                    while (entry != null) {
                        if (entry.getName().endsWith(".class")) {
                            String newName = entry.getName().replace("/", ".");
                            expectedClasses.add(newName.substring(0, newName.length() - 6));
                        }
                        inputStream.closeEntry();
                        entry = inputStream.getNextEntry();
                    }
                }
            }
            fileOutputStream.write("\n\n=====================\n\n".getBytes(StandardCharsets.UTF_8));
            System.out.write("\n\n=====================\n\n".getBytes(StandardCharsets.UTF_8));
            Class<?>[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            for (Class<?> loadedClass : allLoadedClasses) {
                if (loadedClass.getName().startsWith("[")) continue;
                if (loadedClass.getName().startsWith("sun.reflect.")) continue;
                if (loadedClass.getName().contains("$")) continue;
                if (!expectedClasses.contains(loadedClass.getName())) {
                    fileOutputStream.write(("WARNING: Hack Detected! Class: " + loadedClass.getName() + "\n").getBytes(StandardCharsets.UTF_8));
                    System.out.write(("WARNING: Hack Detected! Class: " + loadedClass.getName() + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                fileOutputStream.write("Failed\n".getBytes(StandardCharsets.UTF_8));
                e.printStackTrace(new PrintStream(fileOutputStream, true));
                fileOutputStream.close();
                e.printStackTrace();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void visit(File file, String prefix, ArrayList<String> expectedClass) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                visit(f, prefix + f.getName() + ".", expectedClass);
            } else {
                if (f.getName().endsWith(".class")) {
                    expectedClass.add(prefix + f.getName().substring(0, f.getName().length() - 6));
                }
            }
        }
    }

    public static void premain(String args, Instrumentation instrumentation) {

    }

}
