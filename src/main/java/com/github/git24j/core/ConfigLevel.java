package com.github.git24j.core;

public enum ConfigLevel {
    /** System-wide on Windows, for compatibility with portable git */
    PROGRAMDATA(1),

    /** System-wide configuration file; /etc/gitconfig on Linux systems */
    SYSTEM(2),

    /** XDG compatible configuration file; typically ~/.config/git/config */
    XDG(3),

    /**
     * User-specific configuration file (also called Global configuration file); typically
     * ~/.gitconfig
     */
    GLOBAL(4),

    /** Repository specific configuration file; $WORK_DIR/.git/config on non-bare repos */
    LOCAL(5),

    /** Application specific configuration file; freely defined by applications */
    APP(6),

    /**
     * Represents the highest level available config file (i.e. the most specific config file
     * available that actually is loaded)
     */
    HIGHEST(-1),
    ;
    private final int _bit;

    ConfigLevel(int bit) {
        _bit = bit;
    }

    public int getBit() {
        return _bit;
    }
}