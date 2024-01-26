package com.github.git24j.core;

public class ConfigLevel {
    /*

typedef enum {
    //System-wide on Windows, for compatibility with portable git
    GIT_CONFIG_LEVEL_PROGRAMDATA = 1,

    // System-wide configuration file; /etc/gitconfig on Linux systems
    GIT_CONFIG_LEVEL_SYSTEM = 2,

    //XDG compatible configuration file; typically ~/.config/git/config
    GIT_CONFIG_LEVEL_XDG = 3,

    // User-specific configuration file (also called Global configuration
    // file); typically ~/.gitconfig
    GIT_CONFIG_LEVEL_GLOBAL = 4,

    // Repository specific configuration file; $WORK_DIR/.git/config on
    // non-bare repos
    GIT_CONFIG_LEVEL_LOCAL = 5,

    // Application specific configuration file; freely defined by applications
    GIT_CONFIG_LEVEL_APP = 6,

    // Represents the highest level available config file (i.e. the most
    // specific config file available that actually is loaded)
    GIT_CONFIG_HIGHEST_LEVEL = -1
} git_config_level_t;

 */

    public final static ConfigLevel programData = new ConfigLevel(1);
    public final static ConfigLevel system = new ConfigLevel(2);
    public final static ConfigLevel xdg = new ConfigLevel(3);
    public final static ConfigLevel global = new ConfigLevel(4);
    public final static ConfigLevel local = new ConfigLevel(5);
    public final static ConfigLevel app = new ConfigLevel(6);
    public final static ConfigLevel highest = new ConfigLevel(-1);

    private final int value;
    private ConfigLevel(int value) {
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
