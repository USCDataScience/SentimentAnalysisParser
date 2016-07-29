(function() {

var margin = {top: 20, right: 20, bottom: 30, left: 50},
    width = 1000 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var x0 = d3.scale.ordinal()
    .rangeRoundBands([0, width], .1);

var x1 = d3.scale.ordinal();

var y = d3.scale.linear()
    .range([height, 0]);

var color = d3.scale.ordinal()
	.range(["#6b486b", "#ff8c00"]);
    //.range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);

var xAxis = d3.svg.axis()
    .scale(x0)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .tickFormat(d3.format(".2s"));

var svg = d3.select("#gun-ad2").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

d3.json("./data/countries.json", function(error, dataObj) {
  if (error) throw error;
  
  var data = [];
  for (var p in dataObj) {
	  var country = dataObj[p];
	  country.name = p;
	  //var positive =  p.prediction.positive;
	  //var negative = p.prediction.negative;
	  //var sentiment = [positive, negative];
	  country.sentiment = [];
	  country.sentiment.push({ name: "positive", value: country.prediction.positive || 0});
	  country.sentiment.push({ name: "negative", value: country.prediction.negative || 0});
	  //country.prediction = sentiment;
	  data.push(country);
  }

  //var ageNames = d3.keys(data[0]).filter(function(key) { return key !== "State"; });

  //data.forEach(function(d) {
  //  d.ages = ageNames.map(function(name) { return {name: name, value: +d[name]}; });
  //});
  var sentNames = ["positive", "negative"];

  x0.domain(data.map(function(d) { return d.name; }));
  x1.domain(sentNames).rangeRoundBands([0, x0.rangeBand()]);
  y.domain([0, d3.max(data, function(d) { return d3.max(d.sentiment, function(d) { return d.value; }); })]);

  svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis);

  svg.append("g")
      .attr("class", "y axis")
	  .attr("transform", "translate(" + (-15) + ",0)")
      .call(yAxis)
    .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("# of ads");
	  
  var countryData = svg.selectAll(".country")
      .data(data)
    .enter().append("g")
      .attr("class", "country")
      .attr("transform", function(d) { return "translate(" + x0(d.name) + ",0)"; });

  countryData.selectAll("rect")
      .data(function(d) { return d.sentiment; })
    .enter().append("rect")
      .attr("width", x1.rangeBand())
      .attr("x", function(d) { return x1(d.name); })
      .attr("y", function(d) { return y(d.value); })
      .attr("height", function(d) { return height - y(d.value); })
      .style("fill", function(d) { return color(d.name); });

  var legend = svg.selectAll(".legend")
      .data(sentNames.slice().reverse())
    .enter().append("g")
      .attr("class", "legend")
      .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

  legend.append("rect")
      .attr("x", width - 18)
      .attr("width", 18)
      .attr("height", 18)
      .style("fill", color);

  legend.append("text")
      .attr("x", width - 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "end")
      .text(function(d) { return d; });

});

}())
