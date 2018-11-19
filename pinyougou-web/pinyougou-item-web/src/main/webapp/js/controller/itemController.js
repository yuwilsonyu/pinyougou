app.controller('itemController', function ($scope) {

    $scope.specItems = {};
    // 监控num变量
    $scope.$watch('num', function (newVal, oldVal) {
        if (newVal){
            if (newVal < 1 || !/^\d$/.test(newVal)){
                $scope.num = 1;
            }
        }
    });
    //购买数量加减方法
    $scope.addNum = function (num) {
        $scope.num += num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };
    //判断规格是否选中样式
    $scope.isSelected = function (name, value) {
        return $scope.specItems[name] == value;
    };
    //保存选中规格
    $scope.selectSpec = function (name, value) {
        $scope.specItems[name] = value;
        searchSku();
    };

    //读取默认sku商品规格
    $scope.loadSku = function () {
        $scope.sku = itemList[0];
        $scope.specItems = JSON.parse($scope.sku.spec);
    };
    //查找选中的商品sku
    var searchSku = function () {
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i].spec == JSON.stringify($scope.specItems)) {
                $scope.sku = itemList[i];
                return;
            }
        }
    };
    //添加购物车
    $scope.addToCart = function () {
        alert($scope.sku.id);
    };
});