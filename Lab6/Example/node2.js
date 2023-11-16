
function printAsync(s, cb) {
    var delay = Math.floor((Math.random() * 1000) + 500);

    setTimeout(() => {
        console.log(s);
        if (cb) cb();
    }, delay);
}

// Napisz funkcje (bez korzytania z biblioteki async) wykonujaca 
// rownolegle funkcje znajdujace sie w tablicy 
// parallel_functions. Po zakonczeniu wszystkich funkcji
// uruchamia sie funkcja final_function. Wskazowka:  zastosowc 
// licznik zliczajacy wywolania funkcji rownoleglych 

function inparallel(parallel_functions, final_function) {
    var completed = 0

    let complete = () => {
        completed += 1

        if (completed == parallel_functions.length) 
            final_function() 
    }

    parallel_functions.forEach(element => {
        element(complete)
    });
}

let A = (cb) => { printAsync("A", cb) }
let B = (cb) => { printAsync("B", cb) }
let C = (cb) => { printAsync("C", cb) }
let D = (cb) => { printAsync("Done", cb) }

let tasks = [A, B, C]

inparallel([A, B, C], D)

