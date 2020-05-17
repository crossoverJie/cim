from time import sleep
from tqdm import trange

import click


@click.command()
@click.option("--count", default=1, help="Number of greetings.")
@click.option("--name", prompt="Your name", help="The person to greet.")
def hello(count, name):
    """Simple program that greets NAME for a total of COUNT times."""
    for _ in range(count):
        click.echo(u"Hello".format(name))

def progress():
    for i in trange(100):
        sleep(0.01)

if __name__ == '__main__':
    # hello()
    progress()