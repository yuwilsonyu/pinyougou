/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope, baseService) {

    $scope.searchParams = {};

    $scope.search = function () {
        baseService.sendPost("/Search",$scope.searchParams).then(function (response) {
            $scope.resultMap = response.data;
        })
    }
   
});
