(function() {
	
var margin = {top: 20, right: 20, bottom: 30, left: 50},
    width = 800 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var svg = d3.select("#hybrid-accuracy").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  	.append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
var x = d3.scale.linear()
    .range([0, width]);

var y = d3.scale.linear()
    .range([height, 0]);

var line = d3.svg.line()
    .x(function(d) { return x(d.iteration); })
    .y(function(d) { return y(d.accuracy); });
	
var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")

d3.tsv("./data/accuracy_hybrid.tsv", function(d) {
  d.iteration = +d.iteration;
  d.accuracy = +d.accuracy;
  return d;
}, function(error, data) {
  if (error) throw error;

  x.domain(d3.extent(data, function(d) { return d.iteration; }));
  y.domain(d3.extent(data, function(d) { return d.accuracy; }));

  svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis)
    .select(".domain")
      .remove();
	  
  svg.append("text")      // text label for the x axis
   	  .attr("text-anchor", "end")
      .attr("x", width)
      .attr("y", height - 6)
	  .text("Iteration");
	  
      //.attr("x", 500)
      //.attr("y", 500)
      //.attr("text-anchor", "end")
        //.style("text-anchor", "middle")
      //.text("False Positive Rate");

  svg.append("g")
      .call(yAxis)
    .append("text")
      .attr("fill", "#000")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", "0.71em")
      .attr("text-anchor", "end")
      .text("Accuracy");

  svg.append("path")
      .datum(data)
      .attr("fill", "none")
      .attr("stroke", "steelblue")
      .attr("stroke-linejoin", "round")
      .attr("stroke-linecap", "round")
      .attr("stroke-width", 1.5)
      .attr("d", line);
});

}())