function getAvailableGraphs(){
	$.get("http://localhost:8080/graph/graphs", function(data){
		for (var i = 0; i < data.length; i++) {
			var x = document.getElementById("graphs");
		    var option = document.createElement("option");
		    option.text = data[i];
		    x.add(option);
		}
	});
	
}