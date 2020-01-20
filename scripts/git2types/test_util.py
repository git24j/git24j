from .utils import (
    Git2TypeConstIndex,
    get_git2_param,
    get_c_wrapper_param_list,
    get_c_param_list,
    get_c_wrapper_before_list,
)

import unittest


class TestUtils(unittest.TestCase):
    C_PARAMS = "git_oid *out, git_index *index, git_repository *repo"

    def test_match(self):
        pt = Git2TypeConstIndex.parse('git_index *index')
        self.assertIsNotNone(pt)

    def test_get_param(self):
        pt = get_git2_param('git_repository *repo')
        self.assertEqual('jlong repoPtr', pt.c_header_param)

    def test_get_c_wrapper_param_list(self):
        pt_list = [get_git2_param(s.strip()) for s in self.C_PARAMS.split(',')]
        s = get_c_wrapper_param_list(pt_list)
        self.assertEqual('jobject out, jlong indexPtr, jlong repoPtr', s)

    def test_get_c_param_list(self):
        pt_list = [get_git2_param(s.strip()) for s in self.C_PARAMS.split(',')]
        s = get_c_param_list(pt_list)
        self.assertEqual(
            '&c_oid, (git_index *)indexPtr, (git_repository *)repoPtr', s)

    def test_get_c_wrapper_before_list(self):
        pt_list = [get_git2_param(s.strip()) for s in self.C_PARAMS.split(',')]
        s = get_c_wrapper_before_list(pt_list)
        self.assertEqual('\t git_oid c_out;', s)


if __name__ == '__main__':
    unittest.main()
