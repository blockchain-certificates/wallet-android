import argparse
import contextlib
import os
import shutil
import tempfile
import textwrap
from typing import Optional

CV_REPO = "cert-verifier-js"
CV_GIT = f"https://github.com/blockchain-certificates/{CV_REPO}.git"
WALLET_VERIFIER = "LearningMachine/app/src/main/assets/www/verifier.js"
CV_VERIFIER = "dist/verifier-iife.js"


@contextlib.contextmanager
def chdir(path):
    cwd = os.getcwd()
    try:
        os.chdir(path)
        yield None
    finally:
        os.chdir(cwd)


def main() -> None:
    args = _parse()
    wallet_path = os.path.abspath(args.walletPath)
    with tempfile.TemporaryDirectory() as tmp:
        with chdir(tmp):
            _clone_repo()
            _export_token(args.npmToken)
            _install_and_build()
            _copy_verifier(wallet_path)
            _clean_up(args.uninstallYarn)
        print("SUCCESS!")


def _parse() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        formatter_class=argparse.RawDescriptionHelpFormatter,
        description=textwrap.dedent(
            """
            Adds the verifier from the cert-verifier-js package. This 
            process is also outlined in the repo README,
            
            https://github.com/blockchain-certificates/wallet-android
             
            Note: this script requires runas  access to install yarn (instead 
            of npm). Checksum integrity errors result when attempting to use 
            npm, as described in the cert-verifier-js README.
            """))
    parser.add_argument(
        "walletPath",
        help="relative path to the top-level wallet-android directory")
    parser.add_argument(
        "-t",
        "--npmToken",
        metavar="token",
        default=None,
        help="required to install and build. If unset, the environment "
             "variable NPM_TOKEN should be set.")
    parser.add_argument(
        "-u",
        "--uninstallYarn",
        action="store_true",
        help="automatically uninstall yarn before completion")
    return parser.parse_args()


def _clone_repo() -> None:
    print(f"CLONING {CV_REPO}")
    os.system(f"git clone {CV_GIT}")
    os.chdir(CV_REPO)


def _export_token(npm_token: Optional[str]) -> None:
    if npm_token:
        print(f"EXPORTING NPM TOKEN {npm_token}")
        os.environ["NPM_TOKEN"] = npm_token


def _install_and_build() -> None:
    print(f"INSTALLING YARN AND BUILDING {CV_REPO}")
    os.system("runas  npm install --global yarn")
    os.system("yarn cache clean")
    os.system("yarn --update-checksums")
    os.system("yarn build")


def _copy_verifier(wallet_path: str) -> None:
    src = os.path.join(CV_REPO, CV_VERIFIER)
    dest = os.path.join(wallet_path, WALLET_VERIFIER)
    print(f"COPYING {src} TO {dest}")
    shutil.copy(CV_VERIFIER, dest)


def _clean_up(uninstall_yarn: bool) -> None:
    if uninstall_yarn:
        print("UNINSTALLING YARN")
        os.system("runas  npm uninstall --global yarn")


if __name__ == '__main__':
    main()
