package com.github.git24j.core;

public class ObjectType {

    /*
    // Basic type (loose or packed) of any Git object.
    typedef enum {
        GIT_OBJECT_ANY =      -2, // Object can be any of the following
        GIT_OBJECT_INVALID =  -1, // Object is invalid.
        GIT_OBJECT_COMMIT =    1, // A commit object.
        GIT_OBJECT_TREE =      2, // A tree (directory listing) object.
        GIT_OBJECT_BLOB =      3, // A file revision object.
        GIT_OBJECT_TAG =       4, // An annotated tag object.
        GIT_OBJECT_OFS_DELTA = 6, // A delta, base is given by an offset.
        GIT_OBJECT_REF_DELTA = 7  // A delta, base is given by object id.
    } git_object_t;

     */
    public static final ObjectType any = new ObjectType(-2);
    public static final ObjectType invalid = new ObjectType(-1);
    public static final ObjectType commit = new ObjectType(1);
    public static final ObjectType tree = new ObjectType(2);
    public static final ObjectType blob = new ObjectType(3);
    public static final ObjectType tag = new ObjectType(4);
    public static final ObjectType ofsDelta = new ObjectType(6);
    public static final ObjectType refDelta = new ObjectType(7);

    private final int value;
    private ObjectType(int value) {
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
