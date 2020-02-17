#include "j_proxy.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_proxy_options *opts = (git_proxy_options *)malloc(sizeof(git_proxy_options));
    int r = git_proxy_init_options(opts, version);
    return r;
}