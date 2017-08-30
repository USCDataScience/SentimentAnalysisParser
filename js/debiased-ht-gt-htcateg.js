function ht_gt_stanford() {
	
var svg = d3.select("#debiased-ht-gt-htcateg").append("svg"),
    margin = {top: 20, right: 20, bottom: 30, left: 65},
    width = 800,
    height = 500,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

/*var x0 = d3.scaleOrdinal()
    .rangeRoundBands([0, width], .1);

var x1 = d3.scaleOrdinal();

var y = d3.scaleLinear()
    .range([height, 0]);*/

	function fill(d) {
		var name = d.name || d;
		if(name === 'neutral') {
			return 'url(#diagonalHatch)';
		} else if(name === 'like') {
			return 'url(#crosshatch)';
		} else if(name === 'sad') {
			return 'url(#dots)';
		} else if(name === 'angry') {
			return 'url(#check)';
		} else if(name === 'love') {
			return 'url(#boo)';
		}
		return null;
	}
	
var x0 = d3.scaleBand()
	.rangeRound([0, width])
	.paddingInner(0.1);
	/*d3.scaleOrdinal()
    .range([0, width], .1);*/

var x1 = d3.scaleBand()
	.padding(0.05);//d3.scaleOrdinal();

var y = d3.scaleLinear()
    .rangeRound([height, 0]);

var color = d3.scaleOrdinal()
	.range(["#6b486b", "#ff8c00", "#ed2998", "#29a8ed", "#c40f0f"]);
    //.range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);


d3.json("./data/debiased-ht-gt-htcat.json", function(error, dataObj) {
  if (error) throw error;
  
  var data = [];
  for (var p in dataObj) {
	  var truth = dataObj[p];
	  truth.name = p;
	  //var positive =  p.prediction.positive;
	  //var negative = p.prediction.negative;
	  //var sentiment = [positive, negative];
	  truth.sentiment = [];
	  truth.sentiment.push({ name: "angry", value: truth.prediction.angry || 0});
	  truth.sentiment.push({ name: "sad", value: truth.prediction.sad || 0});
	  truth.sentiment.push({ name: "neutral", value: truth.prediction.neutral || 0});
	  truth.sentiment.push({ name: "like", value: truth.prediction.like || 0});
	  truth.sentiment.push({ name: "love", value: truth.prediction.love || 0});
	  //country.prediction = sentiment;
	  data.push(truth);
  }

  //var ageNames = d3.keys(data[0]).filter(function(key) { return key !== "State"; });

  //data.forEach(function(d) {
  //  d.ages = ageNames.map(function(name) { return {name: name, value: +d[name]}; });
  //});
  var sentNames = ["angry", "sad", "neutral", "like", "love"];
  
  x0.domain(data.map(function(d) { return d.name; }));
  x1.domain(sentNames).rangeRound([0, x0.bandwidth()]);
  y.domain([0, d3.max(data, function(d) { return d3.max(d.sentiment, function(d) { return d.value; }); })]);

  g.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x0));

  g.append("g")
      .attr("class", "y axis")
	  .attr("transform", "translate(" + (-15) + ",0)")
      .call(d3.axisLeft(y).ticks(null, "s"))//d3.format(5)))
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
	
  g.append('defs')
   .append('pattern')
    	.attr('id', 'crosshatch')
    	.attr('patternUnits', 'userSpaceOnUse')
    	.attr('width', 8)
    	.attr('height', 8)
   .append('path')
    	//.attr('d', 'M-1,1 l2,-2 M0,4 l4,-4 M3,5 l2,-2')
    	.attr('d', 'M2,2 l5,5')
    	.attr('stroke', '#000000')
    	.attr('stroke-width', 1);
	
  g.append('defs')
   .append('pattern')
    	.attr('id', 'dots')
    	.attr('patternUnits', 'userSpaceOnUse')
    	.attr('width', 10)
    	.attr('height', 10)
   .append('path')
    	//.attr('d', 'M-1,1 l2,-2 M0,4 l4,-4 M3,5 l2,-2')
    	.attr('d', 'M2,2 l5,-4 M3,5')
    	.attr('stroke', '#000000')
    	.attr('stroke-width', 1);
	
  g.append('defs')
   .append('pattern')
    	.attr('id', 'check')
    	.attr('patternUnits', 'userSpaceOnUse')
    	.attr('width', 2)
    	.attr('height', 2)
   .append('path')
    	.attr('d', 'M-1,1 M0,4 l4,-4 M3,15')
    	.attr('stroke', '#000000')
    	.attr('stroke-width', 2);
	
  g.append('defs')
   .append('pattern')
    	.attr('id', 'boo')
    	.attr('patternUnits', 'userSpaceOnUse')
    	.attr('width', 8)
    	.attr('height', 8)
   .append('path')
    	//.attr('d', 'M-1,1 l2,-2 M0,4 l4,-4 M3,5 l2,-2')
    	.attr('d', 'M2,2 l5,5 l4, -10')
    	.attr('stroke', '#000000')
    	.attr('stroke-width', 1);
	  
  //var truthData = g.append("g")//.selectAll(".truth")
  var truthData = g.append("g")
	  .selectAll("g")
      .data(data)
    .enter().append("g")
      .attr("class", "truth")
      .attr("transform", function(d) { return "translate(" + x0(d.name) + ",0)"; });
  
	  //console.log(x1.range());
  
  truthData.selectAll("rect")
      .data(function(d) { return d.sentiment; })
    .enter().append("rect")
      .attr("width", x1.range()[1]/5.7)
      .attr("x", function(d) { return x1(d.name); })
      .attr("y", function(d) { return y(d.value); })
      .attr("height", function(d) { return height - y(d.value); })
	  .attr('fill', fill);
      //.style("fill", function(d) { return color(d.name); });

  var legend = g.append("g")//.selectAll(".legend")
	  .selectAll("g")
      .data(sentNames.slice().reverse())
    .enter().append("g")
      .attr("class", "legend")
      .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

  legend.append("rect")
      .attr("x", width - 18)
      .attr("width", 18)
      .attr("height", 18)
	  .attr('fill', fill);
      //style("fill", color);

  legend.append("text")
      .attr("x", width - 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "end")
      .text(function(d) { return d; });

  /*x0.domain(data.map(function(d) { return d.name; }));
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
	  
  var truthData = svg.selectAll(".truth")
      .data(data)
    .enter().append("g")
      .attr("class", "truth")
      .attr("transform", function(d) { return "translate(" + x0(d.name) + ",0)"; });

  truthData.selectAll("rect")
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
      .text(function(d) { return d; });*/

});

}

ht_gt_stanford();
