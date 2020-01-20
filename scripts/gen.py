from git2types.git2_function import Git2Function
from typing import Dict, List

import argparse


def get_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog='gen')
    parser.add_argument('-s', '--string', help='read a string and generate')
    parser.add_argument('-f', '--file', help='read a file and generate line by line')
    parser.add_argument(
        '-o', '--outtype', default='all',
        help='choose output type', choices=['header', 'body', 'jni', 'all'])
    # parser.add_argument('-f', '--file', help='read a file and generate')
    return parser


def generate(input: List[str]) -> Dict[str, str]:
    res = {
        'header': [],
        'body': [],
        'jni': [],
    }
    for s in input:
        git2f = Git2Function()
        git2f.parse(s)
        res['header'].append(git2f.header_sig)
        res['body'].append(git2f.wrapper)
        res['jni'].append(git2f.jni_sig)
    return res


if __name__ == "__main__":
    parser = get_parser()
    ns = parser.parse_args()
    input = []
    if ns.string:
        input = [ns.string.strip()]
    elif ns.file:
        input = [s.strip() for s in open(ns.file).readlines()]

    output = generate(input)
    if ns.outtype == 'header' or ns.outtype == 'all':
        print('-------- Signature of the header ----------')
        print('\n'.join(output['header']))
    if ns.outtype == 'all' or ns.outtype == 'body':
        print('-------- Wrapper Body ----------')
        print('\n'.join(output['body']))
    if ns.outtype == 'all' or ns.outtype == 'jni':
        print('-------- Jni Signature ----------')
        print('\n'.join(output['jni']))

