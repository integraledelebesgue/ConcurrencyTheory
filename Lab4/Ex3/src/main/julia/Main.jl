using Plots
using Plots.PlotMeasures
using Query
using StatsPlots
using DataFrames
using CSV
using Pipe

const name = Dict(
    0 => "Producer",
    1 => "Consumer"
)


function main(src::String, dest::String)
    data = CSV.read(src, DataFrame)

    plt = scatter(
        size = (1600, 1200),
        title = "Amount vs. Average wait time",
        left_margin=[30px 0px],
        bottom_margin = 15px
    )

    xlabel!(plt, "Amount")
    ylabel!(plt, "log10(avgTime)")

    data |>
        @groupby(_.type) .|>
        @df(scatter!(
            plt, 
            :amount, 
            log10.(:avgTime), 
            label=name[first(:type)], 
            markersize = 6, 
            markerstrokewidth = 0.5
        ))
    
    @pipe plt |> png(_, dest)
end

main(ARGS[1], ARGS[2])
