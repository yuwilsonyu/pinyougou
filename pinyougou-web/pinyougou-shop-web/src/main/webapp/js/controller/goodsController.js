/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 根据上级id查询下级*/
    $scope.findItemByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemByParentId?parentId=" + parentId).then(function (response) {
            $scope[name] = response.data;
        });
    };

    $scope.$watch('goods.category1Id', function (newVal, oldVal) {
        if (newVal) {
            $scope.findItemByParentId(newVal, 'itemCatList2');
        } else {
            $scope.itemCatList2 = [];
        }
    });

    $scope.$watch('goods.category2Id', function (newVal, oldVal) {
        if (newVal) {
            $scope.findItemByParentId(newVal, 'itemCatList3');
        } else {
            $scope.itemCatList3 = [];
        }
    });

    $scope.$watch('goods.category3Id', function (newVal, oldVal) {
        if (newVal) {
            for (i = 0; i < $scope.itemCatList3.length; i++) {
                var itemCat = $scope.itemCatList3[i];
                if (itemCat.id == newVal) {
                    $scope.goods.typeTemplateId = itemCat.typeId;
                    break;
                }
            }
        }else{
            $scope.goods.typeTemplateId = null;
        }
    });

    $scope.$watch('goods.typeTemplateId', function (newVal, oldVal){
        if(newVal){
            baseService.sendGet("/typeTemplate/findOne?id="+newVal).then(function (response) {
                $scope.brandIds = JSON.parse(response.data.brandIds);
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse(response.data.customAttributeItems);
            });
        }else {
            $scope.brandIds = null;
        }
    });
    /**上传图片 */
    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response) {
            /** 如果上传成功，取出url */
            if (response.data.status == 200) {
                /** 设置图片访问地址 */
                $scope.picEntity.url = response.data.url;
            } else {
                alert("上传失败！");
            }
        });
    };

    $scope.goods = {goodsDesc: {itemImages: []}};
    /** 添加图片到数组 */
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };

    $scope.removePic = function (idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx, 1);
    };

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/goods/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function () {
        /** 发送post请求 */
        $scope.goods.goodsDesc.introduction = editor.html();
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function (response) {
                if (response.data) {
                    alert("添加成功")
                    $scope.goods = {};
                    editor.html('');
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.reload();
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };
});