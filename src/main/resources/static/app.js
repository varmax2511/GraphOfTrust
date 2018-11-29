// initial setup
var flag = false;

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
	
	// clear existing
	hideElement("small_simple1")
	hideElement("small_dense1")
	$("#gable > tr").remove();
	
	
	if(graph == "SMALL_SIMPLE1"){
		$.get("http://localhost:8080/graph/configure/" + graph);
		showElement("small_simple1")
	}else if(graph == "SMALL_DENSE1"){
		$.get("http://localhost:8080/graph/configure/" + graph);
		showElement("small_dense1")
	}
	
	showElement("config")
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
	/*var shuffled = $('#shuffled').checked();*/
	var participants = parseInt($('#size').val());
	
	console.log(id);
	console.log(ratio);
	console.log(participants);
	
	// clear table
	$("#gable > tr").remove();
	
	
	$.get("http://localhost:8080/graph/execute/" + id + "?ratio=" + ratio + "&shuffled=" + false
			+"&participants=" + participants, function(data){
		
		console.log(data)
		/*data = JSON.parse(data);*/
		if(data == null){
			return;
		}
		
		var table = document.getElementById('gable');
		data.forEach(function(object) {
            var tr = document.createElement('tr');
            tr.innerHTML = '<td>' + object.result + '</td>' +
            '<td>' + object.confidence + '</td>' +
            '<td>' + object.heuristic + '</td>' +
            '<td>' + object.trustDecayType + '</td>';
            table.appendChild(tr);
        });
	});
}