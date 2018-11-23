app.controller("indexController",function ($scope,baseService) {

    /** 获取用户登录名*/
    $scope.showName =  function () {
        baseService.sendGet("/user/showName").then(function (response) {
            $scope.loginName =  response.data.loginName;
        })
    }
});