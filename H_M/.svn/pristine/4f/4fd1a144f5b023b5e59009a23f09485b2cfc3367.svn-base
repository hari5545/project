$(document).ready( function() {
  	$(".tips").tip_cards();
  	getOrgList("orgList");  	
});

function loadTeamStatistics(teamId, teamName) {
	$.ajax({
        type: "GET",
        url: '/teamStats',
        data: {
            'teamId': teamId, 'teamName': teamName
        },
        success: function(data, textStatus, jqXHR) {
            if (data) {
            	//console.log(data);
            	var dataList = data.split("###");

            	/*
	            	1. TEAM_STAT
	            	2. TEAM_POST_COUNT_DAY
					3. TEAM_POST_COUNT_USER
					4. TEAM_USER_LAST_ACTIVITY_AT
					5. TEAM_USER_CREATE_AT
            	*/

            	var team_stats_obj = JSON.parse(dataList[0]);
            	var team_post_count_day = JSON.parse(dataList[1]);
            	var team_post_count_user = JSON.parse(dataList[2]);
            	var team_user_last_activity_at = JSON.parse(dataList[3]);
            	var team_user_create_at = JSON.parse(dataList[4]);
            	var broadcastCount = dataList[5];
            	var channelCounts = dataList[6];

            	updateStats(team_stats_obj, channelCounts, broadcastCount);

            	drawGraph1("chart1", team_post_count_day);
            	drawGraph1("chart2", team_post_count_user);

            	renderTable1("table1", team_user_last_activity_at);
            	renderTable2("table2", team_user_create_at);


            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
        	console.log(errorThrown);
        }
    });
}

function updateStats(statsObj, channelCounts, broadcastCount) {
	console.log(channelCounts);

	var totalActiveUsers = statsObj.unique_user_count;
	var groupCount = statsObj.channel_open_count;
	var onetooneCount = statsObj.channel_private_count;
	var postCount = statsObj.post_count;

	var dailyActiveUsers;
	var monthlyAcvtiveUsers;

	var channelCnts = channelCounts.split(":::");

	for (let i=0; i<statsObj.length; i++) {
		var key = statsObj[i].name;
		var value = statsObj[i].value;
		if(key == 'unique_user_count') {
			totalActiveUsers = value;
		}
		if(key == 'channel_private_count') {
			groupCount = value;
		}
		/*if(key == 'channel_private_count') {
			onetooneCount = value;
		}*/
		if(key == 'post_count') {
			postCount = value;
		}
		if(key == 'daily_active_users') {
			dailyActiveUsers = value;
		}
		if(key == 'monthly_active_users') {
			monthlyAcvtiveUsers = value;
		}
	}

	$("#chatCount").html(channelCnts[0]);
	$("#surveyCount").html(channelCnts[1]);
	$("#broadcastCount").html(broadcastCount);
	$("#newsCount").html(channelCnts[2]);

	let count = 0;

	for (var i = 0; i < channelCnts.length; i++) {
		count += parseInt(channelCnts[i]);
	}

	$("#totalActiveUsers").html(totalActiveUsers);
	$("#groupCount").html(count);
	//$("#onetooneCount").html(onetooneCount);
	$("#postCount").html(postCount);

	$("#dailyActiveUsers").html(dailyActiveUsers);
	$("#monthlyAcvtiveUsers").html(monthlyAcvtiveUsers);
}

function changeOrg(sel) {
	loadTeamStatistics(sel.value, $(sel).find("option:selected").text());
	$("#selTeam").text($(sel).find("option:selected").text());
}

function getOrgList(selectID) {
	$.ajax({
      type: "GET",
      url: '/getTeams', 
      success: function(data, textStatus, jqXHR) {
          if (data) {
          	//console.log(data);

          	$('#'+selectID).empty();

          	var orgs = data.split("###"); 
          	//console.log(orgs);

          	var firstOrg = orgs[0].split(":::")[1];
          	var firstOrgName = orgs[0].split(":::")[0];
          	$("#selTeam").text(orgs[0].split(":::")[0]);

			$.each(orgs, function(key, value) {  
				var orgStr = value.split(":::");
				$('#'+selectID)
						.append($("<option></option>")
				          .attr("value",orgStr[1])
				          .text(orgStr[0])); 
			});

			loadTeamStatistics(firstOrg, firstOrgName);
          }
      },
      error: function(jqXHR, textStatus, errorThrown) {
      	console.log(errorThrown);
      }
  });
}

function renderTable1(tableId, tableData) {
	document.getElementById(tableId).innerHTML = '';

	var finalData = "";
	for (var i = 0; i < tableData.length; i++) {
		var tr = "<tr>";
		var username = tableData[i].username;
		var timestamp = timeConverter(tableData[i].last_activity_at);
		//console.log(username);
		//console.log(timestamp);
		tr += "<td>"+username+"</td>" + "<td>"+timestamp+"</td>";
		tr += "</td>";
		finalData += tr;
	}
	document.getElementById(tableId).innerHTML += finalData;
}

function renderTable2(tableId, tableData) {
	document.getElementById(tableId).innerHTML = '';

	var finalData = "";
	for (var i = 0; i < tableData.length; i++) {
		var tr = "<tr>";
		var username = tableData[i].username;
		var timestamp = timeConverter(tableData[i].create_at);
		//console.log(username);
		//console.log(timestamp);
		tr += "<td>"+username+"</td>" + "<td>"+timestamp+"</td>";
		tr += "</td>";
		finalData += tr;
	}
	document.getElementById(tableId).innerHTML += finalData;
}

function timeConverter(timestamp){
	var a = new Date((timestamp / 1000)*1000);
	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	var year = a.getFullYear();
	var month = months[a.getMonth()];
	var date = a.getDate();
	var hour = a.getHours();
	var min = a.getMinutes();
	var sec = a.getSeconds();
	//var time = date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec ;
	var time = month + ' ' + date + ', ' + year + ', ' + hour + ':' + min + ':' + sec ;
	return time;
}

function drawGraph1(divId, graphData) {
	//console.log(graphData);

	document.getElementById(divId).innerHTML = '';

	document.querySelector("#chart-"+divId).innerHTML = '<canvas id="'+divId+'" height="180" style="height: 180px;"></canvas>';

	let chartLabelList=[];
	let chartValueList=[];

	for (let i=0; i<graphData.length; i++) {
		chartLabelList.push(graphData[i].name);
		chartValueList.push(graphData[i].value);
	}

	var bedAreaChartCanvas = $('#'+divId).get(0).getContext('2d')

    var bedAreaChartData = {
      labels  : chartLabelList,
      datasets: [
        {
          backgroundColor     : 'rgb(142, 219, 246)',
          borderColor         : 'rgb(93, 164, 201)',
		  borderDash          : [7, 7],
          pointRadius         : true,
          pointColor          : '#3b8bba',
          pointStrokeColor    : 'rgb(142, 219, 246)',
          pointHighlightFill  : '#fff',
          pointHighlightStroke: 'rgb(142, 219, 246)',
          data                : chartValueList
        },
      ]
    }

    var bedAreaChartOptions = {
      maintainAspectRatio : false,
      responsive : true,
      legend: {
        display: false
      },
	  tooltips: {
		mode: 'index',
		intersect: false,
					},
	hover: {			
	   mode: 'nearest',
	   intersect: true
					},
      scales: {
        xAxes: [{
          gridLines : {
            display : true,
          }
        }],
        yAxes: [{
          gridLines : {
            display : true,
          }
        }]
      }
    }

    // This will get the first returned node in the jQuery collection.
    var bedAreaChart = new Chart(bedAreaChartCanvas, { 
      type: 'line',
      data: bedAreaChartData, 
      options: bedAreaChartOptions
    });

    bedAreaChart.destroy();

    bedAreaChart = new Chart(bedAreaChartCanvas, { 
      type: 'line',
      data: bedAreaChartData, 
      options: bedAreaChartOptions
    });

}