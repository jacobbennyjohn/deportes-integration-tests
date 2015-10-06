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

    $scope.hoverIn = function(){
        var delayTime = this.record.delayTime;
        $http.get('rest/information/?id=' + this.record.id + '&type=' + this.record.fixture).success(function(data) {
            $scope.information = data.infoList[0];
            $scope.delayTime = delayTime;
            $scope.peak = true;
        })
    };

    $scope.hoverOut = function(){
        $scope.peak = false;
    };

    $scope.getData = function() {
        $http.get('rest/resource/').success(function(data) {
            $scope.actities = data.records;
            $scope.ttl = data.ttl;
            $scope.peak = false;
        })
    }

    $scope.getData();

    setInterval($scope.getData, 10000);
});

