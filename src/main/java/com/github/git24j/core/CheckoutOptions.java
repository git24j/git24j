package com.github.git24j.core;

import static com.github.git24j.core.Checkout.NotifyCb;
import static com.github.git24j.core.Checkout.PerfdataCb;
import static com.github.git24j.core.Checkout.ProcessCb;
import static com.github.git24j.core.Checkout.jniOptionsGetDirMode;
import static com.github.git24j.core.Checkout.jniOptionsGetDisableFilters;
import static com.github.git24j.core.Checkout.jniOptionsGetFileMode;
import static com.github.git24j.core.Checkout.jniOptionsGetFileOpenFlags;
import static com.github.git24j.core.Checkout.jniOptionsGetNotifyFlags;
import static com.github.git24j.core.Checkout.jniOptionsGetStrategy;
import static com.github.git24j.core.Checkout.jniOptionsGetVersion;
import static com.github.git24j.core.Checkout.jniOptionsNew;
import static com.github.git24j.core.Checkout.jniOptionsSetAncestorLabel;
import static com.github.git24j.core.Checkout.jniOptionsSetBaseline;
import static com.github.git24j.core.Checkout.jniOptionsSetBaselineIndex;
import static com.github.git24j.core.Checkout.jniOptionsSetDirMode;
import static com.github.git24j.core.Checkout.jniOptionsSetDisableFilters;
import static com.github.git24j.core.Checkout.jniOptionsSetFileMode;
import static com.github.git24j.core.Checkout.jniOptionsSetFileOpenFlags;
import static com.github.git24j.core.Checkout.jniOptionsSetNotifyCb;
import static com.github.git24j.core.Checkout.jniOptionsSetNotifyFlags;
import static com.github.git24j.core.Checkout.jniOptionsSetOurLabel;
import static com.github.git24j.core.Checkout.jniOptionsSetPaths;
import static com.github.git24j.core.Checkout.jniOptionsSetPerfdataCb;
import static com.github.git24j.core.Checkout.jniOptionsSetProcessCb;
import static com.github.git24j.core.Checkout.jniOptionsSetStrategy;
import static com.github.git24j.core.Checkout.jniOptionsSetTargetDirectory;
import static com.github.git24j.core.Checkout.jniOptionsSetTheirLable;
import static com.github.git24j.core.Checkout.jniOptionsSetVersion;

import java.util.EnumSet;
import javax.annotation.Nonnull;

/**
 * Checkout options structure
 *
 * <p>Initialize with `GIT_CHECKOUT_OPTIONS_INIT`. Alternatively, you can use
 * `git_checkout_init_options`.
 */
public class CheckoutOptions extends CAutoReleasable {
    protected CheckoutOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }

    @Nonnull
    public CheckoutOptions create(int version) {
        CheckoutOptions opts = new CheckoutOptions(false, 0);
        Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
        return opts;
    }

    public int getVersion() {
        return jniOptionsGetVersion(getRawPointer());
    }

    public void setVersion(int ver) {
        jniOptionsSetVersion(getRawPointer(), ver);
    }

    /** @return checkout strategy, default is SAFE */
    @Nonnull
    public EnumSet<CheckoutStrategyT> getStrategy() {
        return IBitEnum.parse(jniOptionsGetStrategy(getRawPointer()), CheckoutStrategyT.class);
    }

    public void setStrategy(@Nonnull EnumSet<CheckoutStrategyT> strategies) {
        jniOptionsSetStrategy(getRawPointer(), IBitEnum.bitOrAll(strategies));
    }

    public boolean getDisableFilter() {
        return jniOptionsGetDisableFilters(getRawPointer()) == 1;
    }

    /** don't apply filters like CRLF conversion */
    public void setDisableFilter(boolean disableFilter) {
        jniOptionsSetDisableFilters(getRawPointer(), disableFilter ? 1 : 0);
    }

    /** default is 0755 */
    public int getDirMode() {
        return jniOptionsGetDirMode(getRawPointer());
    }

    public void setDirMode(int mode) {
        jniOptionsSetDirMode(getRawPointer(), mode);
    }

    /** default is 0644 or 0755 as dictated by blob */
    public int getFileMode() {
        return jniOptionsGetFileMode(getRawPointer());
    }

    public void setFileMode(int mode) {
        jniOptionsSetFileMode(getRawPointer(), mode);
    }

    /** default is O_CREAT | O_TRUNC | O_WRONLY */
    public int getOpenFlags() {
        return jniOptionsGetFileOpenFlags(getRawPointer());
    }

    public void setOpenFlags(int flags) {
        jniOptionsSetFileOpenFlags(getRawPointer(), flags);
    }

    /** see `git_checkout_notify_t` above */
    public EnumSet<CheckoutNotifyT> getNotifyFlags() {
        return IBitEnum.parse(jniOptionsGetNotifyFlags(getRawPointer()), CheckoutNotifyT.class);
    }

    public void setNotifyFlags(EnumSet<CheckoutNotifyT> flags) {
        jniOptionsSetNotifyFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
    }

    public void setNotifyCb(@Nonnull NotifyCb callback) {
        jniOptionsSetNotifyCb(
                getRawPointer(),
                (why, s, basePtr, targePtr, workdirPtr) ->
                        callback.accept(
                                IBitEnum.valueOf(why, CheckoutNotifyT.class),
                                s,
                                Diff.File.ofWeak(basePtr),
                                Diff.File.ofWeak(targePtr),
                                Diff.File.ofWeak(workdirPtr)));
    }

    public void setProcessCb(@Nonnull ProcessCb callback) {
        jniOptionsSetProcessCb(getRawPointer(), callback);
    }

    public void setPerfdataCb(@Nonnull PerfdataCb callback) {
        jniOptionsSetPerfdataCb(getRawPointer(), callback);
    }

    public void setPaths(@Nonnull String[] paths) {
        jniOptionsSetPaths(getRawPointer(), paths);
    }

    public void setBaseline(@Nonnull Tree baseline) {
        jniOptionsSetBaseline(getRawPointer(), baseline.getRawPointer());
    }

    public void setBaselineIndex(@Nonnull Index baselineIndex) {
        jniOptionsSetBaselineIndex(getRawPointer(), baselineIndex.getRawPointer());
    }

    public void setTargetDirectory(@Nonnull String targetDirectory) {
        jniOptionsSetTargetDirectory(getRawPointer(), targetDirectory);
    }

    public void setAncestorLabel(@Nonnull String ancestorLabel) {
        jniOptionsSetAncestorLabel(getRawPointer(), ancestorLabel);
    }

    public void setOurLabel(@Nonnull String ourLabel) {
        jniOptionsSetOurLabel(getRawPointer(), ourLabel);
    }

    public void setTheirLabel(@Nonnull String theirLabel) {
        jniOptionsSetTheirLable(getRawPointer(), theirLabel);
    }
}
