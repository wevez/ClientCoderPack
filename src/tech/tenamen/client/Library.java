package tech.tenamen.client;

import tech.tenamen.Main;
import tech.tenamen.property.OS;
import tech.tenamen.util.NetUtil;

import java.io.File;
import java.util.List;

public class Library implements Downloadable {

    private final String NAME;
    private final String PATH;
    private final String SHA1;
    private final long SIZE;
    private final String URL;
    private final OSRule OS_RULE;
    private final List<NativeLibrary> NATIVE_LIBRARIES;

    public Library(final String NAME, final String PATH, final String SHA1, final long SIZE, final String URL, final OSRule OS_RULE, final List<NativeLibrary> nativeLibraries) {
        this.PATH = PATH;
        this.NAME = NAME;
        this.SHA1 = SHA1;
        this.SIZE = SIZE;
        this.URL = URL;
        this.OS_RULE = OS_RULE;
        this.NATIVE_LIBRARIES = nativeLibraries;
    }

    @Override
    public void download(OS os) {
        if (!this.OS_RULE.check(os)) return;
        this.NATIVE_LIBRARIES.forEach(n -> n.download(os));
        final String[] SP_PATH = this.PATH.split("/");
        final File desFile = new File(Main.LIBRARIES_DIR, SP_PATH[SP_PATH.length - 1]);
        if (!NetUtil.checkExist(desFile, this.SIZE)) {
            NetUtil.download(this.URL, desFile);
        }
        Main.IDE.getLibraryNames().add(SP_PATH[SP_PATH.length - 1]);
    }

}
