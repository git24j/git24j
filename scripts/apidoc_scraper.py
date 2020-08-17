
import requests, argparse, stringcase
from typing import List, Set


def get_api_json(major: int = 1, minor: int = 0, patch: int = 0) -> dict:
    """Download html content from https://libgit2.org/libgit2/#v{x}.{y}.{z}"""
    url = f"https://libgit2.org/libgit2/v{major}.{minor}.{patch}.json"
    return requests.get(url).json()


def get_all_modules(api_json: dict) -> List[str]:
    return [stringcase.pascalcase(x[0]) for x in api_json['groups']]


def get_all_functions(api_json: dict, module_name: str) -> List[str]:
    for x in api_json['groups']:
        if x[0] == module_name:
            return x[1]


def get_non_documented_function(scrapped: List[str], api_doc_path: str):
    c = open(api_doc_path).read()
    not_documented = []
    for x in scrapped:
        if x not in c:
            not_documented.append(x)
    if not_documented:
        print("----------- following functions are not documented ------------ ")
        print("\n".join(not_documented))


def get_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog='scrap')
    parser.add_argument('-m', '--module', help='which module to scrap')
    parser.add_argument(
        '-o', '--outtype', default='all',
        help='choose output type', choices=['modules', 'functions'])
    parser.add_argument('-d', '--diff-doc', 
        help='find out functions that are not documented (likely not implemented) in the _git2_apis')
    return parser


def main(args):
    parser = get_parser()
    ns = parser.parse_args(args)

    api_json = get_api_json()
    if ns.outtype == 'modules':
        module_names = get_all_modules(api_json)
        print('\n'.join(module_names))
    elif ns.outtype == 'functions':
        if not ns.module:
            print("ERROR: module name must be provided to scrap functions")
        functions = get_all_functions(api_json, ns.module)
        if ns.diff_doc:
            get_non_documented_function(functions, ns.diff_doc)
        else:
            print('\n'.join(functions))


if __name__ == "__main__":
    import sys
    main(sys.argv[1:])
