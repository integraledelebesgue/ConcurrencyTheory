import { waterfall } from "async"

function make_task(n) {
    return (callback) => {
        console.log(n)
        callback()
    }
}

function final() {
    console.log("Done!")
}

waterfall([
    make_task(1),
    make_task(2),
    make_task(3)
], final);

