package tech.tenamen.property;

public enum OS {

    WINDOWS("windows", "natives-windows"),
    MAC("osx", "natives-macos"),
    LINUX("linux", "natives-linux");

    private final String OS_RULE_NAME, NATIVE_OS_NAME;

    OS(final String OS_RULE_NAME, final String NATIVE_OS_NAME) {
        this.OS_RULE_NAME = OS_RULE_NAME;
        this.NATIVE_OS_NAME = NATIVE_OS_NAME;
    }

    public String toOsRule() { return OS_RULE_NAME; }
    public String toNativeOs() { return NATIVE_OS_NAME; }
}
