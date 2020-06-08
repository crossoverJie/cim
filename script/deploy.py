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
@click.option("--model", prompt="build model", help="build model[s:server, r:route, c:client]")
def run(model):
    __package()

    if model == 's':
        __build_server()
    elif model == 'r':
        __build_route()
    else:
        __build_client(model.split(' ')[1])


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


def __build_client(count):
    count = int(count)
    process = 30
    click.echo('build cim {} client.....'.format(count))
    subprocess.call(['cp', 'cim-client/target/cim-client-1.0.0-SNAPSHOT.jar', '/data/work/cim/client'])
    port = 8084
    for i in range(count):
        port = port + 1
        process = process + count
        command = ['java', '-jar', '-Xmx128M', 'Xms128M', '/data/work/cim/client/cim-client-1.0.0-SNAPSHOT.jar',
                   '--server.port='.format(port), '--cim.user.id=1',
                   '--cim.user.userName=1', '--cim.route.url=http://47.98.194.60:8083/']
        click.echo(' '.join(command))
        subprocess.call(command)
        pbar.update(process)

    click.echo('build cim {} client success!!!'.format(count))


def progress():
    pbar.update(10)


if __name__ == '__main__':
    run()
