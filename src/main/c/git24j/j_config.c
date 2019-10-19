#include "j_config.h"
#include "j_common.h"
#include "j_mappers.h"

/** void git_config_free(git_config *cfg); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniFree)(JNIEnv *env, jclass obj, jlong cfgPtr)
{
    git_config_free((git_config *)cfgPtr);
}

/** int git_config_find_global(git_buf *out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindGlobal)(JNIEnv *env, jclass obj, jobject buf)
{
    git_buf c_buf = {0};
    int e = git_config_find_global(&c_buf);
    j_git_buf_to_java(env, &c_buf, buf);
    return e;
}

/** int git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetStringBuf)(JNIEnv *env, jclass obj, jobject buf, jlong cfgPtr, jstring name)
{
    git_buf c_buf = {0};
    char *c_name = j_copy_of_jstring(env, name, false);
    int e = git_config_get_string_buf(&c_buf, (git_config *)cfgPtr, c_name);
    j_git_buf_to_java(env, &c_buf, buf);
    free(c_name);
    return e;
}
