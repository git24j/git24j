
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


def get_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog='scrap')
    parser.add_argument('-m', '--module', help='which module to scrap')
    parser.add_argument(
        '-o', '--outtype', default='all',
        help='choose output type', choices=['modules', 'functions'])
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
        print('\n'.join(functions))


if __name__ == "__main__":
    import sys
    main(sys.argv[1:])
