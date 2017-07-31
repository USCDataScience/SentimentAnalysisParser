function two_hybrid() {

var svg = d3.select("#two-hybrid-acc").append("svg"),
    margin = {top: 20, right: 80, bottom: 30, left: 50},
    width = 960,
    height = 500,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var x = d3.scaleLinear().range([0, width]),
    y = d3.scaleLinear().range([height, 0]),
    z = d3.scaleOrdinal(d3.schemeCategory10);

var line = d3.line()
    .curve(d3.curveBasis)
    .x(function(d) { return x(d.iteration); })
    .y(function(d) { return y(d.accuracy); });


var drawLine = function(error, data) {
  if (error) throw error;

  var cities = data.columns.slice(1).map(function(id) {
    return {
      id: id,
      values: data.map(function(d) {
        return {iteration: d.iteration, accuracy: d[id]};
      })
    };
  });

  x.domain(d3.extent(data, function(d) { return d.iteration; }));

  y.domain([
    d3.min(cities, function(c) { return d3.min(c.values, function(d) { return d.accuracy; }); }),
    d3.max(cities, function(c) { return d3.max(c.values, function(d) { return d.accuracy; }); })
  ]);

  z.domain(cities.map(function(c) { return c.id; }));

  g.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x))
      .append("text")
      .attr("y", -10)
      .attr("x", width - 25)
      .attr("dx", "0.71em")
      .attr("fill", "#000")
      .text("Iterations");

  g.append("g")
      .attr("class", "axis axis--y")
      .call(d3.axisLeft(y))
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", "0.71em")
      .attr("fill", "#000")
      .text("Accuracy");

  var city = g.selectAll(".city")
    .data(cities)
    .enter().append("g")
      .attr("class", "city");

   city.append("path")
      .attr("class", "line")
      .attr("d", function(d) { return line(d.values); })
      .style("stroke", function(d) { return z(d.id); });

  city.append("text")
      .datum(function(d) { return {id: d.id, value: d.values[d.values.length - 1]}; })
      .attr("transform", function(d) { return "translate(" + x(d.value.iteration) + "," + y(d.value.accuracy) + ")"; })
      .attr("x", 3)
      .attr("dy", "0.35em")
      .style("font", "10px sans-serif")
      .text(function(d) { return d.id; });

};

d3.csv("./data/twohybrid_data.csv", type, drawLine);

function type(d, _, columns) {
  d.iteration = parseInt(d.iteration);
  for (var i = 1, n = columns.length, c; i < n; ++i) {
    d[c = columns[i]] = d[c];
  }
  return d;
}
	
}

two_hybrid();