import re

# int git_tag_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message, int force);
from typing import List

PAT_GIT2_FUNCTION_TEMPLATE = r'\S+ git_{}_\S+\((.*)\);'





def j_header_parse_git2f(input: str, git2obj_name: str) -> List:
    pass

def j_header_gen():
    pass
