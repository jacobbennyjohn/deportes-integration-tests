angular.module('deportes', []).controller('home', function($scope, $http) {
    $scope.getData = function() {
        $http.get('rest/resource/').success(function(data) {
            $scope.greeting = data;
        })
    }
    $scope.getData();
    setInterval($scope.getData, 10000);
});