var app = angular.module('deportes', []);
var eventApp = angular.module('event', []);

app.controller('home', function($scope, $http) {

    $scope.getData = function() {
        $http.get('/rest/resource/').success(function(data) {
            $scope.greeting = data;
        })
    }

    $scope.getData();
});

activityList = function($scope, $http) {

    var eventId = 0;
    try {
        if (document.getElementById("eventId") != null) {
            eventId = document.getElementById("eventId").value;
        }
    } catch(err) {

    }

    $scope.hoverIn = function(){
        var delayTime = this.record.delayTime;
        $http.get('/rest/information/?id=' + this.record.id + '&type=' + this.record.fixture).success(function(data) {
            $scope.information = data.infoList[0];
            $scope.delayTime = delayTime;
            $scope.peak = true;
        })
    };

    $scope.hoverOut = function(){
        $scope.peak = false;
    };

    $scope.getData = function() {
        if (eventId != 0) {
            $http.get('/rest/event/' + eventId).success(function(data) {
                $scope.actities = data.records;
                $scope.ttl = data.ttl;
                $scope.peak = false;
            })
        } else {
            $http.get('/rest/resource/').success(function(data) {
                $scope.actities = data.records;
                $scope.ttl = data.ttl;
                $scope.peak = false;
            })
        }
    }

    $scope.getData();

    setInterval($scope.getData, 10000);
};

app.controller('activityList', activityList);
eventApp.controller('activityList', activityList);