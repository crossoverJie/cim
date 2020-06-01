import os
from time import sleep
from tqdm import trange, tqdm

import click
import subprocess

pbar = tqdm(total=100)
pbar.set_description('building')


@click.command()
@click.option("--model", prompt="build model", help="build model[s:server,r:route]")
def hello(model):
    if model == 's':
        __build_server()


def __build_server():
    click.echo('build cim server.....')

    pbar.update(10)
    FNULL = open(os.devnull, 'w')
    subprocess.call(['mvn', '-Dmaven.test.skip=true', 'clean', 'package'], stdout=FNULL, stderr=subprocess.STDOUT)
    pbar.update(30)
    subprocess.call(['cp', 'cim-server/target/cim-server-1.0.0-SNAPSHOT.jar', '/data/work/cim/server'])
    subprocess.call(['sh', 'script/server-startup.sh'])

    pbar.update(60)

    click.echo('build cim server success!!!')
    pbar.close()


def progress():
    pbar.update(10)



if __name__ == '__main__':
    hello()
