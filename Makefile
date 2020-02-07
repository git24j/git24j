include Makefile.common

LIBGIT2_SRC=${CURDIR}/src/main/c/libgit2
LIBGIT2_TARGET=$(CURDIR)/target/git2
WRAPPER_SRC=${CURDIR}/src/main/c/git24j
WRAPPER_TARGET=$(CURDIR)/target/git24j

CMAKE_CONFIG=RelWithDebInfo

build_libgit2: $(LIBGIT2_SRC)/src
	echo ${LIBGIT2_SRC}
	echo ${LIBGIT2_TARGET}
	@mkdir -p $(LIBGIT2_TARGET)
	(cd $(LIBGIT2_TARGET) && \
	 cmake $(LIBGIT2_SRC) -DSONAME=OFF -DTHREADSAFE=ON -DBUILD_CLAR=OFF \
	 -DCMAKE_C_FLAGS="$(CMAKE_C_FLAGS) $(ARCH_CFLAGS)")
	cmake --build $(LIBGIT2_TARGET) --config $(CMAKE_CONFIG)

clean_libgit2:
	-rm -rfv $(LIBGIT2_TARGET)

build_wrapper: build_libgit2 $(LIBGIT2_TARGET)/libgit2.$(SO_EXTENSION)
	echo ${WRAPPER_SRC}
	echo ${WRAPPER_TARGET}
	@mkdir -p $(WRAPPER_TARGET)
	(cd $(WRAPPER_TARGET) && \
    	 cmake $(WRAPPER_SRC) \
    	 -DINCLUDE_LIBGIT2="$(LIBGIT2_SRC)/include" \
    	 -DLINK_LIBGIT2="$(LIBGIT2_TARGET)" \
    	 -DCMAKE_C_FLAGS="$(CMAKE_C_FLAGS) $(ARCH_CFLAGS)")
	cmake --build $(WRAPPER_TARGET) --config $(CMAKE_CONFIG)

clean_wrapper:
	-rm -rfv $(WRAPPER_TARGET)

rebuild_wrapper: | clean_wrapper build_wrapper

lint: 
	$(CURDIR)/scripts/lint.sh download
	$(CURDIR)/scripts/lint.sh diff
	
test: | build_wrapper
	mvn test

.DEFAULT_GOAL := rebuild_wrapper