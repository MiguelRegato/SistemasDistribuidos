// Load google charts
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

var action;
var adventure;
var animation;
var comedy;
var drama;
var horror;
var scFiction;

// Draw the chart and set the chart values
function drawChart() {
  $.ajax({
	url: '/api/films/comments/number'
  }).done(function(counters) {
	action = counters[0];
	adventure = counters[1];
	animation = counters[2];
	comedy = counters[3];
	drama = counters[4];
	horror = counters[5];
	scFiction = counters[6];
	
	var data = google.visualization.arrayToDataTable([
		['Genre', 'Comments Number'],
		['Action', action],
		['Adventure', adventure],
		['Animation', animation],
		['Comedy', comedy],
		['Drama', drama],
		['Horror', horror],
		['Science Fiction', scFiction]
	]);

	var options = {
		'width': 460,
		'height': 305,
		'backgroundColor': 'transparent',
		'chartArea': { left: 20, top: 0, width: '100%', height: '100%' },
		'legend': { position: 'right', alignment: 'center', textStyle: { color: 'white', fontSize: 16 } }
	};

	// Display the chart inside the <div> element with id="piechart"
	var chart = new google.visualization.PieChart(document.getElementById('piechart'));
	chart.draw(data, options);
  });
}