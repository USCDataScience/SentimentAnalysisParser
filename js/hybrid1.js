function hybrid() {

var svg = d3.select("#hybrid-1").append("svg"),
    margin = {top: 20, right: 20, bottom: 30, left: 65},
    width = 800,
    height = 500,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	function fill(d) {
		var name = d.name || d;
		return 'url(#diagonalHatch)';
	}

var x = d3.scaleBand()
	.rangeRound([0, width])
	.padding(0.3)
	.paddingInner(0.1);

var y = d3.scaleLinear()
    .rangeRound([height, 0]);


d3.tsv("./data/hybrid1.tsv", type, function(error, data) {
  if (error) throw error;

  x.domain(data.map(function(d) { return d.sentiment; }));
  y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

g.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + height + ")")
    .call(d3.axisBottom(x));

g.append("g")
    .attr("class", "y axis")
    .call(d3.axisLeft(y).ticks(null, "s"))
  .append("text")
    .attr("transform", "rotate(-90)")
    .attr("y", 6)
    .attr("dy", ".71em")
    .style("text-anchor", "end")
    .text("# of ads");
	
g.append('defs')
 .append('pattern')
  	.attr('id', 'diagonalHatch')
  	.attr('patternUnits', 'userSpaceOnUse')
  	.attr('width', 4)
  	.attr('height', 4)
 .append('path')
  	.attr('d', 'M-1,1 l2,-2 M0,4 l4,-4 M3,5 l2,-2')
  	.attr('stroke', '#000000')
  	.attr('stroke-width', 1);

g.selectAll(".bar")
    .data(data)
  .enter().append("rect")
    .attr("class", "bar")
    .attr("x", function(d) { return x(d.sentiment); })
    .attr("width", x.range()[1]/3.5)
    .attr("y", function(d) { return y(d.frequency); })
    .attr("height", function(d) { return height - y(d.frequency); })
	.attr('fill', fill);
	  //.style("fill", function(d) { return d.color; });
	  //.style("fill", function(d) { return color(d.name); });
	  //.style("fill", color);
});

function type(d) {
  d.frequency = +d.frequency;
  return d;
}
}

hybrid();