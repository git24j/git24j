from unittest import TestCase

from .git2_type_oid import Git2TypeOid


class TestGit2TypeConstOid(TestCase):

    def test_match(self):
        t1 = Git2TypeOid('git_oid *oid')
        self.assertEqual("jobject oid", t1.c_header_param)
        self.assertEqual("\t git_oid c_oid; \n\t j_git_oid_from_java(env, oid, &c_oid);\n", t1.c_wrapper_before)
        self.assertEqual("\t j_git_oid_to_java(env, &c_oid, oid);\n", t1.c_wrapper_after)
        self.assertEqual("Oid oid", t1.jni_param)
