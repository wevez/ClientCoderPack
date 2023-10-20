package tech.tenamen.ide;

import java.util.ArrayList;
import java.util.List;

public abstract class IDE {

    private final List<String> LIBRARY_NAMES = new ArrayList<>();
    private final List<String> NATIVE_NAMES = new ArrayList<>();

    public final List<String> getLibraryNames() { return this.LIBRARY_NAMES; }
    public final List<String> getNativeNames() { return this.NATIVE_NAMES; }

    public abstract void createProperties();

}
