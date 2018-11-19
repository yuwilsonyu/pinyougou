/** 定义首页控制器层 */
app.controller("indexController", function($scope, baseService){

    $scope.findContentByCategoryId = function (categoryId) {
        baseService.sendGet("/content/findContentByCategoryId?categoryId="+categoryId).then(function (response) {
            $scope.contentList = response.data;
        });
    }

    $scope.search = function (keywords) {
        var keywords = $scope.keywords ? $scope.keywords : '';
        location.href="http://search.pinyougou.com?keywords=" + keywords;
    };

});