var app = angular.module('deportes', []);

app.controller('home', function($scope, $http) {
    $scope.getData = function() {
        $http.get('rest/resource/').success(function(data) {
            $scope.greeting = data;
        })
    }
    $scope.getData();
});

app.controller('activityList', function($scope, $http) {
    $scope.getData = function() {
        $http.get('rest/resource/').success(function(data) {
            $scope.actities = data.records;
        })
    }
    $scope.getData();
    setInterval($scope.getData, 10000);
});