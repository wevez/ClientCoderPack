package tech.tenamen.client;

import tech.tenamen.Main;
import tech.tenamen.property.OS;
import tech.tenamen.util.NetUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NativeLibrary implements Downloadable {

    private final String NAME;
    private final String PATH;
    private final String SHA1;
    private final long SIZE;
    private final String URL;

    public NativeLibrary(final String NAME, final String PATH, final String SHA1, final long SIZE, final String URL) {
        this.NAME = NAME;
        this.PATH = PATH;
        this.SHA1 = SHA1;
        this.SIZE = SIZE;
        this.URL = URL;
    }

    @Override
    public void download(OS os) {
        if (!this.NAME.equalsIgnoreCase(os.toNativeOs())) return;
        final String[] SP_PATH = this.PATH.split("/");
        final File desFile = new File(Main.LIBRARIES_DIR, SP_PATH[SP_PATH.length - 1]);
        if (!NetUtil.checkExist(desFile, this.SIZE)) {
            NetUtil.download(this.URL, desFile);
        }
        unzip(desFile, Main.NATIVES_DIR);
    }

    private static void unzip(final File zip, final File output) {
        try {
            final JarFile jar = new JarFile(zip.getAbsoluteFile());
            final Enumeration<JarEntry> enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                final JarEntry file = (JarEntry) enumEntries.nextElement();
                if (!file.getName().endsWith(".dll")) continue;
                Main.IDE.getNativeNames().add(file.getName());
                final File f = new File(String.format("%s%s%s", output, File.separator, file.getName()));
                final InputStream is = jar.getInputStream(file);
                final OutputStream os = Files.newOutputStream(f.toPath());
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
                is.close();
                os.close();
            }
            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
