function ht_lg_cluster_htcat() {
	
var svg = d3.select("#ht-lg-cluster-htcat").append("svg"),
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
	.paddingInner(0.1);

var y = d3.scaleLinear()
    .rangeRound([height, 0]);

d3.tsv("./data/cluster-ht-cat.tsv", type, function(error, data) {
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
      .text("# of ads/total ads per cluster");
	  
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
      .attr("width", x.range()[1]/6)
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

ht_lg_cluster_htcat();






/*(function() {

var margin = {top: 20, right: 20, bottom: 30, left: 65},
    width = 550 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var x = d3.scale.ordinal()
    .rangeRoundBands([0, width], .1);

var y = d3.scale.linear()
    .range([height, 0]);
	
var color = d3.scale.ordinal()
	.range(["#6b486b", "#ff8c00"]);

var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .ticks(10);

var svg = d3.select("#ht-lg-cluster-htcat").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");*/
