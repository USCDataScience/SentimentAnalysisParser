(function() {
	
var margin = {top: 20, right: 20, bottom: 30, left: 50},
    width = 800 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var svg = d3.select("#accuracy").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  	.append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
var x = d3.scale.linear()
    .range([0, width]);

var y = d3.scale.linear()
    .range([height, 0]);
	
var z = d3.scale.ordinal(d3.schemeCategory10);

var line = d3.svg.line()
    .x(function(d) { return x(d.iteration); })
    .y(function(d) { return y(d.accuracy); });
	
var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")

d3.tsv("./data/accuracy.tsv", function(d, _, columns) {
	  d.iteration = +d.iteration;
	  //d.accuracy = +d.accuracy;
	  for (var i = 1, n = columns.length, c; i < n; ++i) d[c = columns[i]] = +d[c];
	  return d;
	}, function(error, data) {
	if (error) throw error;

	  var models = data.columns.slice(1).map(function(id) {
	    return {
	      id: id,
	      values: data.map(function(d) {
	        return {iteration: d.iteration, accuracy: d[id]};
	      })
	    };
	  });

	  x.domain(d3.extent(data, function(d) { return d.iteration; }));

	  y.domain([
	    d3.min(models, function(c) { return d3.min(c.values, function(d) { return d.accuracy; }); }),
	    d3.max(models, function(c) { return d3.max(c.values, function(d) { return d.accuracy; }); })
	  ]);

	  z.domain(models.map(function(c) { return c.id; }));

	  g.append("g")
	      .attr("class", "axis axis--x")
	      .attr("transform", "translate(0," + height + ")")
	      .call(d3.axisBottom(x));

	  g.append("g")
	      .attr("class", "axis axis--y")
	      .call(d3.axisLeft(y))
	    .append("text")
	      .attr("transform", "rotate(-90)")
	      .attr("y", 6)
	      .attr("dy", "0.71em")
	      .attr("fill", "#000")
	      .text("Temperature, ºF");

	  var model = g.selectAll(".model")
	    .data(models)
	    .enter().append("g")
	      .attr("class", "model");

	  model.append("path")
	      .attr("class", "line")
	      .attr("d", function(d) { return line(d.values); })
	      .style("stroke", function(d) { return z(d.id); });

	  model.append("text")
	      .datum(function(d) { return {id: d.id, value: d.values[d.values.length - 1]}; })
	      .attr("transform", function(d) { return "translate(" + x(d.value.iteration) + "," + y(d.value.accuracy) + ")"; })
	      .attr("x", 3)
	      .attr("dy", "0.35em")
	      .style("font", "10px sans-serif")
	      .text(function(d) { return d.id; });
});

	
}())