package tech.tenamen.client;

import tech.tenamen.property.OS;

public class OSRule {

    private boolean allow;
    private String osName;

    public void setAllow(boolean allow) { this.allow = allow; }
    public void setOSName(final String osName) { this.osName = osName; }

    public boolean check(final OS myOS) {
        if (this.osName == null) return true;
        if (myOS.toOsRule().equalsIgnoreCase(this.osName)) return allow;
        return !allow;
    }

}
