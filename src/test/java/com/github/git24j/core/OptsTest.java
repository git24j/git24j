package com.github.git24j.core;

import org.junit.Test;

public class OptsTest extends TestBase {

    @Test
    public void testLibgit2Opts() {

        Libgit2.optsGitOptSetMwindowSize(1020L);
        System.out.println("Libgit2.optsGitOptGetMwindowSize():::"+Libgit2.optsGitOptGetMwindowSize());
        assert(Libgit2.optsGitOptGetMwindowSize() ==1020L);

        Libgit2.optsGitOptSetMWindowMappedLimit(1234L);
        System.out.println("Libgit2.optsGitOptGetMWindowMappedLimit():::"+Libgit2.optsGitOptGetMWindowMappedLimit());
        assert(Libgit2.optsGitOptGetMWindowMappedLimit() == 1234L);

        Libgit2.optsGitOptSetMWindowFileLimit(1234L);
        assert(1234L==Libgit2.optsGitOptGetMWindowFileLimit());

        Libgit2.optsGitOptSetSearchPath(Libgit2.OptsCons.ConfigLevel.system,"/abc");
        assert(Libgit2.optsGitOptGetSearchPath(Libgit2.OptsCons.ConfigLevel.system).equals("/abc"));

        Libgit2.optsGitOptSetCacheObjectLimit(Libgit2.OptsCons.ObjectType.commit,1010);

        Libgit2.optsGitOptSetCacheMaxSize(11);

        Libgit2.optsGitOptEnableCaching(true);

        GitCachedMemorySaver cachedMemory = Libgit2.optsGitOptGetCachedMemory();
        System.out.println("cachedMemory:::"+cachedMemory.getMaxStorage()+","+cachedMemory.getCurrentStorageValue());

        String tmpPath = Libgit2.optsGitOptGetTemplatePath();
        System.out.println("templatePath:::"+tmpPath);
        Libgit2.optsGitOptSetTemplatePath("/tmp/libgit2");
        assert(Libgit2.optsGitOptGetTemplatePath().equals("/tmp/libgit2"));

        Libgit2.optsGitOptSetSslCertLocations(null,null);
        //only file nonexist will occur exception, only path nonexist is ok
//    Libgit2.optsGitOptSetSslCertLocations("/tmp/nonexistfile",null)  //occur GitException, msg is no such file or directory
        Libgit2.optsGitOptSetSslCertLocations(null,"/tmp/nonexistpath");

        Libgit2.optsGitOptSetUserAgent("chromua");
        assert(Libgit2.optsGitOptGetUserAgent().equals("chromua"));

        Libgit2.optsGitOptEnableStrictObjectCreation(true);

        Libgit2.optsGitOptEnableStrictSymbolicRefCreation(true);

        Libgit2.optsGitOptSetSslCiphers("rsa");
        Libgit2.optsGitOptSetSslCiphers("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");

        Libgit2.optsGitOptEnableOfsDelta(false);
        Libgit2.optsGitOptEnableFsyncGitdir(false);

        //sharemode opts only for Windows, so , in android set it is nonsense, will always get 0 sharemode
        Libgit2.optsGitOptSetWindowsSharemode(1001);
        System.out.println("Libgit2.optsGitOptGetWindowsSharemode():::"+Libgit2.optsGitOptGetWindowsSharemode());  //make sense in windows only, other system will get 0

        Libgit2.optsGitOptEnableStrictHashVerification(true);
        Libgit2.optsGitOptEnableUnsavedIndexSafety(true);

        Libgit2.optsGitOptSetPackMaxObjects(1024);
        assert(Libgit2.optsGitOptGetPackMaxObjects()==1024L);

        Libgit2.optsGitOptDisablePackKeepFileChecks(true);

        Libgit2.optsGitOptEnableHttpExpectContinue(true);

        Libgit2.optsGitOptSetOdbPackedPriority(1234);
        Libgit2.optsGitOptSetOdbLoosePriority(1234);

        Libgit2.optsGitOptSetExtensions(new String[]{"abc","def"});
        System.out.println("Libgit2.optsGitOptGetExtensions()[0]:::"+Libgit2.optsGitOptGetExtensions()[0]);
        assert(Libgit2.optsGitOptGetExtensions()[0].equals("abc"));
        assert(Libgit2.optsGitOptGetExtensions()[1].equals("def"));

        Libgit2.optsGitOptSetOwnerValidation(false);
        assert(Libgit2.optsGitOptGetOwnerValidation()==false);

        System.out.println("Libgit2.optsGitOptGetHomedir():::"+Libgit2.optsGitOptGetHomedir());  // default is empty
        Libgit2.optsGitOptSetHomedir("/tmp/git2home");
        assert(Libgit2.optsGitOptGetHomedir().equals("/tmp/git2home"));

        System.out.println("Libgit2.optsGitOptGetServerConnectTimeout():::"+Libgit2.optsGitOptGetServerConnectTimeout());  //default is 0
        Libgit2.optsGitOptSetServerConnectTimeout(36000);
        assert(Libgit2.optsGitOptGetServerConnectTimeout() == 36000L);

        System.out.println("Libgit2.optsGitOptGetServerTimeout():::"+Libgit2.optsGitOptGetServerTimeout());  // default is 0
        Libgit2.optsGitOptSetServerTimeout(30000);
        assert(Libgit2.optsGitOptGetServerTimeout()==30000L);
    }
}
