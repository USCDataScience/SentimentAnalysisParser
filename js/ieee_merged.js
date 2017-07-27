function ieee_merged() {
	
var svg = d3.select("#ieee-on-ieee-merged").append("svg"),
    margin = {top: 20, right: 20, bottom: 90, left: 65},
    width = 960,
    height = 500,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var x = d3.scaleBand()
	.rangeRound([0, width])
	.padding(0.2)
	.paddingInner(0.01);

var y = d3.scaleLinear()
    .rangeRound([height, 0]);

d3.tsv("./data/ieee_on_ieee_merged.tsv", type, function(error, data) {
  if (error) throw error;

  x.domain(data.map(function(d) { return d.sentiment; }));
  y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

  g.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x))
.selectAll("text")
      .attr("y", 0)
      .attr("x", 9)
      .attr("dy", ".35em")
      .attr("transform", "rotate(45)")
      .style("text-anchor", "start");

  g.append("g")
      .attr("class", "y axis")
      .call(d3.axisLeft(y).ticks(null, "s"))
    .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("# of ads/total ads per cluster");

  g.selectAll(".bar")
      .data(data)
    .enter().append("rect")
      .attr("class", "bar")
      .attr("x", function(d) { return x(d.sentiment); })
      .attr("width", x.range()[1]/15)
      .attr("y", function(d) { return y(d.frequency); })
      .attr("height", function(d) { return height - y(d.frequency); });
});

function type(d) {
  d.frequency = +d.frequency;
  return d;
}
}

ieee_merged();
