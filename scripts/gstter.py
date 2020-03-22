#!/usr/bin/env python3.7

import argparse
import stringcase

from typing import NamedTuple, List
from git2types.git2_structure import Git2Structure


def get_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog='gen')
    parser.add_argument('-c', '--class', dest='clazz', help='set class, e.g, Commit, Commit$Options', required=True)
    parser.add_argument('-s', '--string', help='read a string and generate')
    parser.add_argument('-f', '--file', help='read a file and generate line by line')
    parser.add_argument(
        '-o', '--outtype', default='all',
        help='choose output type', choices=['header', 'body', 'jni', 'all'])
    return parser


class GenRes(NamedTuple):
    header_getter: List[str]
    header_setter: List[str]
    body_getter: List[str]
    body_setter: List[str]
    jni_getter: List[str]
    jni_setter: List[str]


def generate(clazz: str, lines: List[str]) -> GenRes:    
    res = GenRes(
        header_getter=[], 
        header_setter=[], 
        body_getter=[], 
        body_setter=[], 
        jni_getter=[],
        jni_setter=[],
    )
    for sig_str in lines:
        gen = Git2Structure(sig_str, clazz)
        res.header_getter.append(gen.header_getter())
        res.header_setter.append(gen.header_setter())
        res.body_getter.append(gen.body_getter())
        res.body_setter.append(gen.body_setter())
        res.jni_getter.append(gen.jni_getter())
        res.jni_setter.append(gen.jni_setter())
    return res


if __name__ == "__main__":
    parser = get_parser()
    ns = parser.parse_args()
    lines = []
    ex_chars = set('#{}')
    if ns.string:
        lines = [ns.string.strip("\n\t ;")]
    elif ns.file:
        lines = [s.strip("\n\t ;") for s in open(ns.file).readlines() if not ex_chars.intersection(s)]

    output = generate(ns.clazz, lines)
    if ns.outtype == 'header' or ns.outtype == 'all':
        print('/** -------- Signature of the header ---------- */')
        print('\n'.join(output.header_getter))
        print('\n'.join(output.header_setter))
    if ns.outtype == 'all' or ns.outtype == 'body':
        print('/** -------- Wrapper Body ---------- */')
        print('\n'.join(output.body_getter))
        print('\n'.join(output.body_setter))
    if ns.outtype == 'all' or ns.outtype == 'jni':
        print('/** -------- Jni Signature ---------- */')
        print('\n'.join(output.jni_getter))
        print('\n'.join(output.jni_setter))

