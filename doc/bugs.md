potential bug:
    - C pointer cast to `long` may cause jvm error:
        In C code, some pointer variables coerce cast to `long`
        when trans to java code, it may cause bugs,
        coerce cast ptr type to `jlong`(recommend) or `size_t` may resolve
        it. is a weired bug, because most time `jlong` actually is a typedef for 
        c-type `long`, but, it just happened, You can check 
        commit oid "087fac86ff5",file `j_diff.c`, function `j_git_diff_line_cb()`,
        which line include `env->CallIntMethod()`, it was cast params to `long`, 
        then I got jvm fatal error, but when I cast params to `jlong`, it resolved.
        I am not sure how can 100% re-produce this bug in every env. 
        you can try change the function I was mentioned, 
        then print rawPtr in java and in c, if difference(pointer variable in java 
        has an invalid address, but C code is ok), maybe is because this bug.

