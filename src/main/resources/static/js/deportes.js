angular.module('deportes', []).controller('home', function($scope, $http) {
    $http.get('rest/resource/').success(function(data) {
        $scope.greeting = data;
    })
});