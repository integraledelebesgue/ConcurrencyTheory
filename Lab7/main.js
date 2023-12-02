class Semaphore {
    constructor(capacity) {
        this.state = capacity;
    }

    acquire(cb, timeout = 1) {
        if (this.state == 0) {
            setTimeout(
                () => this.acquire(cb, timeout * 2),
                timeout
            );

            return;
        }

        this.state -= 1;

        cb();
    }

    release() {
        this.state += 1;
    }
}

const State = {
    free: 0,
    occupied: 1
};

class Fork {
    constructor() {
        this.state = State.free;
        return this;
    }

    acquire(cb, timeout = 1) {
        // zaimplementuj funkcje acquire, tak by korzystala z algorytmu BEB
        // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
        // 1. przed pierwsza proba podniesienia widelca Filozof odczekuje 1ms
        // 2. gdy proba jest nieudana, zwieksza czas oczekiwania dwukrotnie
        //    i ponawia probe itd.
        
        if (this.state == State.occupied) {
            setTimeout(
                () => this.acquire(cb, timeout * 2), 
                timeout
            );

            return;
        }

        this.state = State.occupied;

        cb();
    }

    release() {
        this.state = State.free;
    }
}

const Side = {
    1: 'left',
    2: 'right'
};

class Philosopher {
    constructor(id, forks, waiter) {
        this.id = id;
        this.forks = forks;
        this.f1 = forks[id % forks.length];
        this.f2 = forks[(id + 1) % forks.length];
        this.waiter = waiter;
        
        return this;
    }

    startNaive(count) {
        // zaimplementuj rozwiazanie naiwne
        // kazdy filozof powinien 'count' razy wykonywac cykl
        // podnoszenia widelcow -- jedzenia -- zwalniania widelcow

        if (count == 0) return;

        let f1 = this.f1;
        let f2 = this.f2;

        let release_and_repeat = () => {
            f1.release();
            f2.release();
            this.startNaive(count - 1);

            console.log(`Philosopher ${this.id} sleeps...`);

            var delay = Math.floor((Math.random() * 500) + 500);
            setTimeout(() => this.startNaive(count - 1), delay);
        };
        
        let on_acquirement = () => {
            console.log(`Philosopher ${this.id} picks up his ${Side[2]} fork and eats...`);
            var delay = Math.floor((Math.random() * 500) + 500);
            setTimeout(release_and_repeat, delay);
        };

        f1.acquire(() => {
            console.log(`Philosopher ${this.id} picks up his ${Side[1]} fork`);
            setTimeout(() => f2.acquire(on_acquirement));
        });
    }

    startAsym(count) {
        // zaimplementuj rozwiazanie asymetryczne
        // kazdy filozof powinien 'count' razy wykonywac cykl
        // podnoszenia widelcow -- jedzenia -- zwalniania widelcow

        if (count == 0) return;

        let f1 = this.f1;
        let f2 = this.f2;

        let side1 = Side[1];
        let side2 = Side[2];

        if (this.id % 2 == 1) {
            [f1, f2] = [f2, f1];
            [side1, side2] = [side2, side1];
        }

        let releaseAndRepeat = () => {
            f1.release();
            f2.release();

            console.log(`Philosopher ${this.id} sleeps...`);

            var delay = Math.floor((Math.random() * 500) + 500);
            setTimeout(() => this.startAsym(count - 1), delay);
        };
        
        let eat = () => {
            var delay = Math.floor((Math.random() * 500) + 500);
            setTimeout(releaseAndRepeat, delay);
        };

        let acquireSecond = () => {
            f2.acquire(() => {
                console.log(`Philosopher ${this.id} picks up his ${side2} fork and eats...`);
                setTimeout(eat);
            })
        }

        f1.acquire(() => {
            console.log(`Philosopher ${this.id} picks up his ${side1} fork`);
            setTimeout(acquireSecond);
        });
    }

    startWaiter(count) {
        // zaimplementuj rozwiazanie z kelnerem
        // kazdy filozof powinien 'count' razy wykonywac cykl
        // podnoszenia widelcow -- jedzenia -- zwalniania widelcow

        if (count == 0) return;

        let f1 = this.f1;
        let f2 = this.f2;
        let waiter = this.waiter;

        let releaseAndRepeat = () => {
            f1.release();
            f2.release();
            waiter.release();

            console.log(`Philosopher ${this.id} leaves and sleeps...`);

            var delay = Math.floor((Math.random() * 500) + 500);
            setTimeout(() => this.startWaiter(count - 1), delay);
        }

        let eat = () => {
            var delay = Math.floor((Math.random() * 500) + 500);
            setTimeout(releaseAndRepeat, delay);
        };

        let acquireSecond = () => {
            f2.acquire(() => {
                console.log(`Philosopher ${this.id} picks up his ${Side[2]} fork and eats...`);
                setTimeout(eat);
            });
        };

        let acquireFirst = () => {
            f1.acquire(() => {
                console.log(`Philosopher ${this.id} picks up his ${Side[1]} fork`);
                setTimeout(acquireSecond);
            });
        };

        waiter.acquire(() => {
            console.log(`Philosopher ${this.id} sits`);
            setTimeout(acquireFirst);
        });
    }
}

const n = 5;

const Variant = {
    naive: 'naive',
    asymmetrical: 'asym',
    waiter: 'waiter'
};

function main(variant) {
    let forks = [];
    let philosophers = [];

    let waiter = variant == Variant.waiter
        ? new Semaphore(n - 1)
        : null;

    for (var i = 0; i < n; i++) {
        forks.push(new Fork());
    }

    for (var i = 0; i < n; i++) {
        philosophers.push(new Philosopher(i, forks, waiter));
    }

    switch (variant) {
        case Variant.naive:
            for (var i = 0; i < n; i++)
                philosophers[i].startNaive(10);

            break;

        case Variant.asymmetrical:
            for (var i = 0; i < n; i++)
                philosophers[i].startAsym(10);

            break;

        case Variant.waiter:
            for (var i = 0; i < n; i++)
                philosophers[i].startWaiter(10);

            break;
    };
}

if (!(Object.values(Variant).includes(process.argv[2]))) {
    console.error('Usage: main <variant: naive, asym, waiter>');
    process.exit();
}

main(process.argv[2]);
