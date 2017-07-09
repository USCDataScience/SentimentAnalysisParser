(function() {
	
var margin = {top: 20, right: 20, bottom: 30, left: 50},
    width = 800 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var svg = d3.select("#ht-lg-roc").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  	.append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
var x = d3.scale.linear()
    .range([0, width]);

var y = d3.scale.linear()
    .range([height, 0]);

var line = d3.svg.line()
    .x(function(d) { return x(d.fpr); })
    .y(function(d) { return y(d.tpr); });
	
var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")

d3.tsv("./data/data.tsv", function(d) {
  d.fpr = +d.fpr;
  d.tpr = +d.tpr;
  return d;
}, function(error, data) {
  if (error) throw error;

  x.domain(d3.extent(data, function(d) { return d.fpr; }));
  y.domain(d3.extent(data, function(d) { return d.tpr; }));

  svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis)
    .select(".domain")
      .remove();
	  
  svg.append("text")      // text label for the x axis
      .attr("x", 400)
      .attr("y", 450)
      .attr("text-anchor", "end")
        //.style("text-anchor", "middle")
      .text("False Positive Rate");

  svg.append("g")
      .call(yAxis)
    .append("text")
      .attr("fill", "#000")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", "0.71em")
      .attr("text-anchor", "end")
      .text("True Positive Rate");

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