using CSV
using DataFrames
using Plots
using StatsPlots
using Query

function main(src::String, dest::String)
    data = CSV.read(src, DataFrame)

    plt = scatter(
        size=(800, 600),
        title="Amount vs Average wait time"
    )

    data |>
    @df scatter!(plt, :amount, :avgTime) |>
    display
end


main(ARGS[1], ARGS[2])

