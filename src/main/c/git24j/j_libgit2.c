
#include "j_libgit2.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

JavaVM *globalJvm;

jclass j_find_and_hold_clz(JNIEnv *env, const char *descriptor)
{
    jclass clz = (*env)->FindClass(env, descriptor);
    assert(clz && "class not found");
    jclass gClz = (jclass)(*env)->NewGlobalRef(env, clz);
    (*env)->DeleteLocalRef(env, clz);
    return gClz;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    globalJvm = vm;
    return JNI_VERSION_1_6;
}

void git24j_init(JNIEnv *env)
{
    assert(env && "cannot initiate git24j without jvm");
    jniConstants = (j_constants_t *)malloc(sizeof(j_constants_t));
    jniConstants->clzAtomicInt = j_find_and_hold_clz(env, "java/util/concurrent/atomic/AtomicInteger");
    jniConstants->clzAtomicLong = j_find_and_hold_clz(env, "java/util/concurrent/atomic/AtomicLong");
    jniConstants->clzAtomicReference = j_find_and_hold_clz(env, "java/util/concurrent/atomic/AtomicReference");
    jniConstants->clzList = j_find_and_hold_clz(env, "java/util/List");
    assert(jniConstants->clzAtomicInt && "AtomicInteger class not found");
    assert(jniConstants->clzAtomicLong && "AtomicLong class not found");
    assert(jniConstants->clzAtomicReference && "AtomicReference class not found");
    assert(jniConstants->clzList && "List class not found");
    jniConstants->midAtomicIntSet = (*env)->GetMethodID(env, jniConstants->clzAtomicInt, "set", "(I)V");
    jniConstants->midAtomicLongSet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "set", "(J)V");
    jniConstants->midAtomicLongGet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "get", "()J");
    jniConstants->midAtomicLongInit = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "<init>", "()V");
    jniConstants->midAtomicReferenceSet = (*env)->GetMethodID(env, jniConstants->clzAtomicReference, "set", "(Ljava/lang/Object;)V");
    jniConstants->midListGetI = (*env)->GetMethodID(env, jniConstants->clzList, "get", "(I)Ljava/lang/Object;");
    /* Remote constants */
    jclass clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Remote$Callbacks");
    assert(clz && "Remote.Callbacks class not found");
    jniConstants->remote.clzCallbacks = clz;
    jniConstants->remote.midAcquireCred = (*env)->GetMethodID(env, clz, "acquireCred", "(Ljava/lang/String;Ljava/lang/String;I)J");
    jniConstants->remote.midCompletion = (*env)->GetMethodID(env, clz, "complete", "(I)I");
    jniConstants->remote.midTransportMessage = (*env)->GetMethodID(env, clz, "transportMessage", "(Ljava/lang/String;)I");
    jniConstants->remote.midTransportCertificateCheck = (*env)->GetMethodID(env, clz, "transportMessageCheck", "(JILjava/lang/String;)I");
    jniConstants->remote.midTransferProgress = (*env)->GetMethodID(env, clz, "transferProgress", "(J)I");
    jniConstants->remote.midUpdateTips = (*env)->GetMethodID(env, clz, "updateTips", "(Ljava/lang/String;[B[B)I");
    jniConstants->remote.midPackProgress = (*env)->GetMethodID(env, clz, "packProgress", "(IJJ)I");
    jniConstants->remote.midPushTransferProgress = (*env)->GetMethodID(env, clz, "pushTransferProgress", "(JJI)I");
    jniConstants->remote.midPushUpdateReference = (*env)->GetMethodID(env, clz, "pushUpdateReference", "(Ljava/lang/String;Ljava/lang/String;)I");
    jniConstants->remote.midPushNegotiation = (*env)->GetMethodID(env, clz, "pushNegotiation", "([J)I");
    jniConstants->remote.midTransport = (*env)->GetMethodID(env, clz, "transport", "(J)J");
    jniConstants->remote.midResolveUrl = (*env)->GetMethodID(env, clz, "resolveUrl", "(Ljava/lang/String;Ljava/lang/String;I)I");
    /* java/util/List<?> */

    /* clz = j_find_and_hold_clz(env, "java/util/List");
    assert(clz && "List class not found");
    jniConstants->list.midAdd = (*env)->GetMethodID(env, clz, "add", "(Ljava/lang/Object;)z");
    jniConstants->list.midGet = (*env)->GetMethodID(env, clz, "get", "(I)Ljava/lang/Object;");
    jniConstants->list.midSize = (*env)->GetMethodID(env, clz, "size", "()I"); */

    /* oid class and methods */
    clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Oid");
    assert(clz && "Oid class not found");
    jniConstants->oid.clzOid = clz;
    jniConstants->oid.midSetId = (*env)->GetMethodID(env, clz, "setId", "([B)V");
    jniConstants->oid.midGetId = (*env)->GetMethodID(env, clz, "getId", "()[B");
}

void git24j_shutdown(JNIEnv *env)
{
    assert(jniConstants && env && "jvm was shutdown unexpected");
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicInt);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicLong);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicReference);
    (*env)->DeleteGlobalRef(env, jniConstants->clzList);
    (*env)->DeleteGlobalRef(env, jniConstants->remote.clzCallbacks);
    free(jniConstants);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *env, jclass obj)
{
    git_libgit2_init();
    git24j_init(env);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *env, jclass obj)
{
    git24j_shutdown(env);
    git_libgit2_shutdown();
}

JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *env, jclass obj)
{
    int major, minor, patch;
    git_libgit2_version(&major, &minor, &patch);

    jclass cls = (*env)->FindClass(env, J_CLZ_PREFIX "Version");
    if (cls == NULL)
    {
        return NULL;
    }

    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(III)V");
    if (ctor == NULL)
    {
        return NULL;
    }

    jobject version = (*env)->NewObject(env, cls, ctor, major, minor, patch);
    return version;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *env, jclass obj)
{
    return git_libgit2_features();
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_jniShadowFree)(JNIEnv *env, jclass obj, long ptr)
{
    free((void *)ptr);
}

//TODO outParam处理：在c里创建变量，然后调用git_libgit2_opts(),然后拿到返回结果，设置到out里，不行，太麻烦了，干脆把所有可能的变量
//  都加到参数列表里，然后按需存储？或者为每个key都写一个对应的方法实现？那样要写几十个方法.........
JNIEXPORT int JNICALL J_MAKE_METHOD(Libgit2_opts)(JNIEnv *env, jclass obj, jobject out, jlong key,jobjectArray params) {

    int size = env->GetArrayLength(params);
    if(size<1 || key<0) {
        return 0;
    }

	int error = 0;

	switch (key) {
	case 0:
	//TODO NEXT
	//找到Long类，找到longValue()方法，调用longValue()方法，取出long值，转换成size_t，调用git_ligit2_opts
	    env->GetObjectArrayElement(params, 0);

	    error = git_libgit2_opts(GIT_OPT_SET_MWINDOW_SIZE, )
//		git_mwindow__window_size = va_arg(ap, size_t);
		break;

	case GIT_OPT_GET_MWINDOW_SIZE:
		*(va_arg(ap, size_t *)) = git_mwindow__window_size;
		break;

	case GIT_OPT_SET_MWINDOW_MAPPED_LIMIT:
		git_mwindow__mapped_limit = va_arg(ap, size_t);
		break;

	case GIT_OPT_GET_MWINDOW_MAPPED_LIMIT:
		*(va_arg(ap, size_t *)) = git_mwindow__mapped_limit;
		break;

	case GIT_OPT_SET_MWINDOW_FILE_LIMIT:
		git_mwindow__file_limit = va_arg(ap, size_t);
		break;

	case GIT_OPT_GET_MWINDOW_FILE_LIMIT:
		*(va_arg(ap, size_t *)) = git_mwindow__file_limit;
		break;

	case GIT_OPT_GET_SEARCH_PATH:
		{
			int sysdir = va_arg(ap, int);
			git_buf *out = va_arg(ap, git_buf *);
			git_str str = GIT_STR_INIT;
			const git_str *tmp;
			int level;

			if ((error = git_buf_tostr(&str, out)) < 0 ||
			    (error = config_level_to_sysdir(&level, sysdir)) < 0 ||
			    (error = git_sysdir_get(&tmp, level)) < 0 ||
			    (error = git_str_put(&str, tmp->ptr, tmp->size)) < 0)
				break;

			error = git_buf_fromstr(out, &str);
		}
		break;

	case GIT_OPT_SET_SEARCH_PATH:
		{
			int level;

			if ((error = config_level_to_sysdir(&level, va_arg(ap, int))) >= 0)
				error = git_sysdir_set(level, va_arg(ap, const char *));
		}
		break;

	case GIT_OPT_SET_CACHE_OBJECT_LIMIT:
		{
			git_object_t type = (git_object_t)va_arg(ap, int);
			size_t size = va_arg(ap, size_t);
			error = git_cache_set_max_object_size(type, size);
			break;
		}

	case GIT_OPT_SET_CACHE_MAX_SIZE:
		git_cache__max_storage = va_arg(ap, ssize_t);
		break;

	case GIT_OPT_ENABLE_CACHING:
		git_cache__enabled = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_GET_CACHED_MEMORY:
		*(va_arg(ap, ssize_t *)) = git_cache__current_storage.val;
		*(va_arg(ap, ssize_t *)) = git_cache__max_storage;
		break;

	case GIT_OPT_GET_TEMPLATE_PATH:
		{
			git_buf *out = va_arg(ap, git_buf *);
			git_str str = GIT_STR_INIT;
			const git_str *tmp;

			if ((error = git_buf_tostr(&str, out)) < 0 ||
			    (error = git_sysdir_get(&tmp, GIT_SYSDIR_TEMPLATE)) < 0 ||
			    (error = git_str_put(&str, tmp->ptr, tmp->size)) < 0)
				break;

			error = git_buf_fromstr(out, &str);
		}
		break;

	case GIT_OPT_SET_TEMPLATE_PATH:
		error = git_sysdir_set(GIT_SYSDIR_TEMPLATE, va_arg(ap, const char *));
		break;

	case GIT_OPT_SET_SSL_CERT_LOCATIONS:
#ifdef GIT_OPENSSL
		{
			const char *file = va_arg(ap, const char *);
			const char *path = va_arg(ap, const char *);
			error = git_openssl__set_cert_location(file, path);
		}
#elif defined(GIT_MBEDTLS)
		{
			const char *file = va_arg(ap, const char *);
			const char *path = va_arg(ap, const char *);
			error = git_mbedtls__set_cert_location(file, path);
		}
#else
		git_error_set(GIT_ERROR_SSL, "TLS backend doesn't support certificate locations");
		error = -1;
#endif
		break;
	case GIT_OPT_SET_USER_AGENT:
		git__free(git__user_agent);
		git__user_agent = git__strdup(va_arg(ap, const char *));
		if (!git__user_agent) {
			git_error_set_oom();
			error = -1;
		}

		break;

	case GIT_OPT_ENABLE_STRICT_OBJECT_CREATION:
		git_object__strict_input_validation = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION:
		git_reference__enable_symbolic_ref_target_validation = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_SET_SSL_CIPHERS:
#if (GIT_OPENSSL || GIT_MBEDTLS)
		{
			git__free(git__ssl_ciphers);
			git__ssl_ciphers = git__strdup(va_arg(ap, const char *));
			if (!git__ssl_ciphers) {
				git_error_set_oom();
				error = -1;
			}
		}
#else
		git_error_set(GIT_ERROR_SSL, "TLS backend doesn't support custom ciphers");
		error = -1;
#endif
		break;

	case GIT_OPT_GET_USER_AGENT:
		{
			git_buf *out = va_arg(ap, git_buf *);
			git_str str = GIT_STR_INIT;

			if ((error = git_buf_tostr(&str, out)) < 0 ||
			    (error = git_str_puts(&str, git__user_agent)) < 0)
				break;

			error = git_buf_fromstr(out, &str);
		}
		break;

	case GIT_OPT_ENABLE_OFS_DELTA:
		git_smart__ofs_delta_enabled = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_ENABLE_FSYNC_GITDIR:
		git_repository__fsync_gitdir = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_GET_WINDOWS_SHAREMODE:
#ifdef GIT_WIN32
		*(va_arg(ap, unsigned long *)) = git_win32__createfile_sharemode;
#endif
		break;

	case GIT_OPT_SET_WINDOWS_SHAREMODE:
#ifdef GIT_WIN32
		git_win32__createfile_sharemode = va_arg(ap, unsigned long);
#endif
		break;

	case GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION:
		git_odb__strict_hash_verification = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_SET_ALLOCATOR:
		error = git_allocator_setup(va_arg(ap, git_allocator *));
		break;

	case GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY:
		git_index__enforce_unsaved_safety = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_SET_PACK_MAX_OBJECTS:
		git_indexer__max_objects = va_arg(ap, size_t);
		break;

	case GIT_OPT_GET_PACK_MAX_OBJECTS:
		*(va_arg(ap, size_t *)) = git_indexer__max_objects;
		break;

	case GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS:
		git_disable_pack_keep_file_checks = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE:
		git_http__expect_continue = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_SET_ODB_PACKED_PRIORITY:
		git_odb__packed_priority = va_arg(ap, int);
		break;

	case GIT_OPT_SET_ODB_LOOSE_PRIORITY:
		git_odb__loose_priority = va_arg(ap, int);
		break;

	case GIT_OPT_SET_EXTENSIONS:
		{
			const char **extensions = va_arg(ap, const char **);
			size_t len = va_arg(ap, size_t);
			error = git_repository__set_extensions(extensions, len);
		}
		break;

	case GIT_OPT_GET_EXTENSIONS:
		{
			git_strarray *out = va_arg(ap, git_strarray *);
			char **extensions;
			size_t len;

			if ((error = git_repository__extensions(&extensions, &len)) < 0)
				break;

			out->strings = extensions;
			out->count = len;
		}
		break;

	case GIT_OPT_GET_OWNER_VALIDATION:
		*(va_arg(ap, int *)) = git_repository__validate_ownership;
		break;

	case GIT_OPT_SET_OWNER_VALIDATION:
		git_repository__validate_ownership = (va_arg(ap, int) != 0);
		break;

	case GIT_OPT_GET_HOMEDIR:
		{
			git_buf *out = va_arg(ap, git_buf *);
			git_str str = GIT_STR_INIT;
			const git_str *tmp;

			if ((error = git_buf_tostr(&str, out)) < 0 ||
			    (error = git_sysdir_get(&tmp, GIT_SYSDIR_HOME)) < 0 ||
			    (error = git_str_put(&str, tmp->ptr, tmp->size)) < 0)
				break;

			error = git_buf_fromstr(out, &str);
		}
		break;

	case GIT_OPT_SET_HOMEDIR:
		error = git_sysdir_set(GIT_SYSDIR_HOME, va_arg(ap, const char *));
		break;

	case GIT_OPT_GET_SERVER_CONNECT_TIMEOUT:
		*(va_arg(ap, int *)) = git_socket_stream__connect_timeout;
		break;

	case GIT_OPT_SET_SERVER_CONNECT_TIMEOUT:
		{
			int timeout = va_arg(ap, int);

			if (timeout < 0) {
				git_error_set(GIT_ERROR_INVALID, "invalid connect timeout");
				error = -1;
			} else {
				git_socket_stream__connect_timeout = timeout;
			}
		}
		break;

	case GIT_OPT_GET_SERVER_TIMEOUT:
		*(va_arg(ap, int *)) = git_socket_stream__timeout;
		break;

	case GIT_OPT_SET_SERVER_TIMEOUT:
		{
			int timeout = va_arg(ap, int);

			if (timeout < 0) {
				git_error_set(GIT_ERROR_INVALID, "invalid timeout");
				error = -1;
			} else {
				git_socket_stream__timeout = timeout;
			}
		}
		break;

	default:
		git_error_set(GIT_ERROR_INVALID, "invalid option key");
		error = -1;
	}

	va_end(ap);

	return error;
}
