// initial setup
var flag = false;
var graphJson;



function getAvailableGraphs(){
	if(flag){
		return;
	}
	
	$.get("http://localhost:8080/graph/graphs", function(data){
		for (var i = 0; i < data.length; i++) {
			var x = document.getElementById("graphs");
		    var option = document.createElement("option");
		    option.text = data[i];
		    x.add(option);
		}// for
		
		flag = true;
	});
	
}

function configureGraph(){
	
	var graph = $('#graphs').children("option").filter(":selected").text()
	console.log(graph)	
	
	// remove earlier entries and hide table
	hideElement("gable")
	
	$.get("http://localhost:8080/graph/configure/" + graph, function(data){
		graphJson = data;
		
		// null check
		if(data == null){
			return;
		}
		loadD3Graph(graphJson);
	});
	
	showElement("config")
	
	$.get("http://localhost:8080/graph/questions/", function(data){
		for (var i = 0; i < data.length; i++) {
			var x = document.getElementById("questions");
		    var option = document.createElement("option");
		    option.text = data[i].question;
		    option.value = data[i].answer; 
		    x.add(option);
		}// for
	});
	
	showElement("qdisplay");
}

function hideElement(elementId){
	var hideElement = document.getElementById(elementId);
	hideElement.style.display = 'none'
}

function showElement(elementId){
	var showElement = document.getElementById(elementId);
	showElement.style.display = 'block'
}


function loadTrustOutput(){
	var id = parseInt($('#userid').val());
	var ratio = parseFloat($('#ratio').val());
	var shuffled = false; 
    var yes = $('#yesids').val();
    var no = $('#noids').val();
	
	if($('#shuffled').is(':checked')){
       shuffled = true;
    }
 
	var participants = parseInt($('#size').val());
	var answer = $('#questions').children("option").filter(":selected").val();
	
	console.log(answer);
	
	// clear table
	$("#gable > tr").remove();
	showElement("gable");

	
	var mode = $('#modeSelect').children("option").filter(":selected").text();
	var url;
	if(mode == "Ratio"){
		url = "http://localhost:8080/graph/execute/" + id + "?ratio=" + ratio + "&shuffled=" + shuffled
		+"&participants=" + participants + "&answer=" + answer;
	}else{
		url = "http://localhost:8080/graph/executeIds/" + id + "?yesIds=" + yes + "&noIds=" + no
		+"&participants=" + participants + "&answer=" + answer
	}
	
	
	
	$.get(url, function(data){
	
		if(data == null){
			return;
		}
		
		var table = document.getElementById('gable');
		data.forEach(function(object) {
            var tr = document.createElement('tr');
            tr.innerHTML = '<td>' + object.result + '</td>' +
            '<td>' + object.confidence + '</td>' +
            '<td>' + object.heuristic + '</td>' +
            '<td>' + object.trustDecayType + '</td>' +
            '<td>' + object.answer + '</td>' +
            '<td>' + object.yesIds.toString() + '</td>' +
            '<td>' + object.noIds.toString() + '</td>' + 
            '<td>' + '<input type="button" value = "Accept" class="feebutton" />' +
            '&nbsp;' +
            '<input type="button" value="Reject" class="feebutton" />' +
            '</td>';
            table.appendChild(tr);
        });
	});
	
}

function loadD3Graph(graphJson){

	$("#d3graph").empty();
	
	var colors = d3.scaleOrdinal(d3.schemeCategory10);

    var svg = d3.select("svg"),
        width = +svg.attr("width"),
        height = +svg.attr("height"),
        node,
        link;

    
    svg.append('defs').append('marker')
        .attrs({'id':'arrowhead',
            'viewBox':'-0 -5 10 10',
            'refX':13,
            'refY':0,
            'orient':'auto',
            'markerWidth':13,
            'markerHeight':13,
            'xoverflow':'visible'})
        .append('svg:path')
        .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
        .attr('fill', '#999')
        .style('stroke','none');

    var simulation = d3.forceSimulation()
        .force("link", d3.forceLink().id(function (d) {return d.id;}).distance(100).strength(1))
        .force("charge", d3.forceManyBody())
        .force("center", d3.forceCenter(width / 2, height / 2));

    
    /*
	 * d3.json("graph.json", function (error, graph) { if (error) throw error;
	 * console.log("file"); console.log(graph.links); console.log(graph.nodes);
	 * console.log("object"); console.log(graphJson.links);
	 * console.log(graphJson.nodes); update(graph.links, graph.nodes); })
	 */
    
    update(graphJson.links, graphJson.nodes);
    

    function update(links, nodes) {
        link = svg.selectAll(".link")
            .data(links)
            .enter()
            .append("line")
            .attr("class", "link")
            .attr("stroke", "black")
            .attr('marker-end','url(#arrowhead)')
				/* console.log(link); */
        link.append("title")
            .text(function (d) {return d.weight;});

        edgepaths = svg.selectAll(".edgepath")
            .data(links)
            .enter()
            .append('path')
            .attrs({
                'class': 'edgepath',
                'fill-opacity': 0,
                'stroke-opacity': 0,
                'id': function (d, i) {return 'edgepath' + i}
            })
            .style("pointer-events", "none");

        edgelabels = svg.selectAll(".edgelabel")
            .data(links)
            .enter()
            .append('text')
            .style("pointer-events", "none")
            .attrs({
                'class': 'edgelabel',
                'id': function (d, i) {return 'edgelabel' + i},
                'font-size': 10,
                'fill': '#aaa'
            });

        edgelabels.append('textPath')
            .attr('xlink:href', function (d, i) {return '#edgepath' + i})
            .style("text-anchor", "middle")
            .style("pointer-events", "none")
            .attr("startOffset", "50%")
            .text(function (d) {return d.weight});

        node = svg.selectAll(".node")
            .data(nodes)
            .enter()
            .append("g")
            .attr("class", "node")
            .call(d3.drag()
                    .on("start", dragstarted)
                    .on("drag", dragged)
                    // .on("end", dragended)
            );

        node.append("circle")
            .attr("r", 5)
            .style("fill", function (d, i) {return colors(i);})

        node.append("title")
            .text(function (d) {return d.id;});

        node.append("text")
            .attr("dy", -3)
            .text(function (d) {return d.id;});

        simulation
            .nodes(nodes)
            .on("tick", ticked);

        simulation.force("link")
            .links(links);
    }

    function ticked() {
        link
            .attr("x1", function (d) {return d.source.x;})
            .attr("y1", function (d) {return d.source.y;})
            .attr("x2", function (d) {return d.target.x;})
            .attr("y2", function (d) {return d.target.y;});

        node
            .attr("transform", function (d) {return "translate(" + d.x + ", " + d.y + ")";});

        edgepaths.attr('d', function (d) {
            return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y;
        });

        edgelabels.attr('transform', function (d) {
            if (d.target.x < d.source.x) {
                var bbox = this.getBBox();
								/* console.log(bbox); */
                rx = bbox.x + bbox.width / 2;
                ry = bbox.y + bbox.height / 2;
                return 'rotate(180 ' + rx + ' ' + ry + ')';
            }
            else {
                return 'rotate(0)';
            }
        });
    }

    function dragstarted(d) {
        if (!d3.event.active) simulation.alphaTarget(0.3).restart()
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }
}// loadD3

function submitFeedback(feedback, answer){
	console.log("efqef");
	// $.get("http://localhost:8080/graph/feedback/" + feedback + "?answer=" +
	// answer)
}

function showOpt(){
	var mode = $('#modeSelect').children("option").filter(":selected").text();
	if(mode == "Ratio"){
		showElement("fraction");
		hideElement("list");
	}else{
		hideElement("fraction");
		showElement("list");
	}
	
}