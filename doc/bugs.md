potential bug:
    - In C code, some pointer variables coerce cast to `long`,
        it may occur bugs when trans the value to java code,
        coerce cast ptr type to `jlong` or `size_t` may resolve
        it.
