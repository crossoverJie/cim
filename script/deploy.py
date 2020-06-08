import os
from time import sleep
from tqdm import trange, tqdm

import click
import subprocess

pbar = tqdm(total=100)
pbar.set_description('building')

# pip install tqdm
# pip install click

@click.command()
@click.option("--model", prompt="build model", help="build model[s:server,r:route]")
def run(model):

    __package()

    if model == 's':
        __build_server()
    elif model == 'r':
        __build_route()


def __build_server():
    click.echo('build cim server.....')
    pbar.update(10)
    subprocess.call(['cp', 'cim-server/target/cim-server-1.0.0-SNAPSHOT.jar', '/data/work/cim/server'])
    subprocess.call(['sh', 'script/server-startup.sh'])

    pbar.update(60)

    click.echo('build cim server success!!!')
    pbar.close()


def __package():
    pbar.update(30)
    FNULL = open(os.devnull, 'w')
    subprocess.call(['mvn', '-Dmaven.test.skip=true', 'clean', 'package'], stdout=FNULL, stderr=subprocess.STDOUT)


def __build_route():
    click.echo('build cim route.....')
    pbar.update(10)
    subprocess.call(['cp', 'cim-forward-route/target/cim-forward-route-1.0.0-SNAPSHOT.jar', '/data/work/cim/route'])
    subprocess.call(['sh', 'script/route-startup.sh'])

    pbar.update(60)

    click.echo('build cim route success!!!')


def progress():
    pbar.update(10)


if __name__ == '__main__':
    run()
