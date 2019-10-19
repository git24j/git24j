


## Coding style

in .c/.h files, jni associated variables should be named in camel case. C variables are in snake case. 

| java Variables   | jni variables in .h/.c |   native variables |
| ---------------- | :--------------------: | -----------------: |
| addOptions       |       addOptions       |        add_options |
| repo             |          repo          |             c_repo |
| long sourceEntry |   jlong source_entry   | void *source_entry |


## Variable mapping rules:

If possible, use c pointers in jni code. 
