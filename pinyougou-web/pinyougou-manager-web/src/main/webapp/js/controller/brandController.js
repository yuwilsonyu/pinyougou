/** 定义品牌控制器层 */
app.controller('brandController', function($scope, baseService){
    /** 定义分页配置信息对象 */
    $scope.paginationConf = {
        currentPage : 1, // 当前页码
        totalItems : 0, // 总记录数
        itemsPerPage : 10, // 每页显示的记录数
        perPageOptions : [10,15,20], // 页码下拉列表
        onChange : function() { // 改变事件
            $scope.reload();
        }
    };
    /** 当下拉列表页码发生改变，重新加载数据 */
    $scope.reload = function(){
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    };
    /** 定义搜索对象 */
    $scope.searchEntity = {};
    /** 分页查询品牌 */
    $scope.search = function(page, rows){
        baseService.findByPage("/brand/findByPage", page,
            rows, $scope.searchEntity)
            .then(function(response){
                $scope.dataList = response.data.rows;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };
    /** 添加或修改品牌 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        if ($scope.entity.id){
            url = "update";
        }
        /** 发送post请求添加品牌 */
        baseService.sendPost("/brand/" + url, $scope.entity)
            .then(function(response){
                if (response.data){
                    /** 重新加载品牌数据 */
                    $scope.reload();
                }else{
                    alert("操作失败！");
                }
            });
    };
    /** 显示修改 */
    $scope.show = function(entity){
        // 把entity的json对象转化成一个新的json对象
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 定义ids数组封装删除的id */
    $scope.ids = [];
    /** 定义checkbox点击事件函数 */
    $scope.updateSelection = function($event, id){
        /** 判断checkbox是否选中 */
        if ($event.target.checked){
            $scope.ids.push(id);
        }else{
            /** 从数组中移除 */
            var idx = $scope.ids.indexOf(id);
            $scope.ids.splice(idx, 1);
        }
    };
    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/brand/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }
    };
});
