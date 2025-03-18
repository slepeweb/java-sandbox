<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/angular-spa.tag --></gen:debug>

<div class="row">
<div data-ng-app="f1App" data-ng-controller="f1Ctrl" class="9u">
	<table>
		<tr>
			<th>Position</th>
			<th>Driver</th>
			<th>Constructor</th>
		</tr>
		<tr data-ng-repeat="d in driverStandings">
			<td>{{d.positionText}}</td>
			<td>{{d.Driver.givenName + " " + d.Driver.familyName}}</td>
			<td>{{d.Constructors[0].name}}</td>
		</tr>
	</table>
</div>
</div>

<script>
var app = angular.module("f1App", []);
app.controller("f1Ctrl", function($scope, $http) {
	$http.get("http://ergast.com/api/f1/current/driverStandings.json").success(function (response) {
		$scope.driverStandings = response.MRData.StandingsTable.StandingsLists[0].DriverStandings;
	});
});
</script>