package tech.tenamen;

import tech.tenamen.client.Client;
import tech.tenamen.ide.IDE;
import tech.tenamen.ide.InteliJIDE;
import tech.tenamen.property.OS;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static IDE IDE = new InteliJIDE();
    public static Client client;

    public static File WORKING_DIR = new File(System.getProperty("user.dir")),
    ASSETS_DIR = new File(WORKING_DIR, "assets"),
    LIBRARIES_DIR = new File(WORKING_DIR, "libraries"),
    OBJECTS_DIR = new File(ASSETS_DIR, "objects"),
    INDEXES_DIR = new File(ASSETS_DIR, "indexes"),
    SRC_DIR = new File(WORKING_DIR, "src"),
    NATIVES_DIR = new File(LIBRARIES_DIR, "natives");

    public static void main(String[] main) throws Exception {
        final Scanner scanner = new Scanner(System.in);
        System.out.println("ClientCoderPack");
        System.out.print("Version<<");
        final File versionFile = new File(String.format("%s\\.minecraft\\versions\\%s", System.getenv("APPDATA"), scanner.next()));
        if (!versionFile.exists()) {
            System.out.printf("File %s not found%n", versionFile.getAbsolutePath());
            return;
        }
        ASSETS_DIR.mkdirs();
        LIBRARIES_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        INDEXES_DIR.mkdirs();
        SRC_DIR.mkdirs();
        NATIVES_DIR.mkdirs();
        client = new Client(versionFile);
        System.out.println("Downloading dependencies");
        client.parseDependencies();
        client.download(getOs());
        System.out.println("Making IDE property file");
        IDE.createProperties();
        System.out.println("Process has finished!");

    }

    private static OS getOs() {
        final String OS_NAME = System.getProperty("os.name").toLowerCase();
        if (OS_NAME.startsWith("linux")) return OS.LINUX;
        if (OS_NAME.startsWith("mac")) return OS.MAC;
        return OS.WINDOWS;
    }

}
