/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    $scope.saveOrUpdate = function(){
        // 获取富文本编辑器中的内容
        $scope.goods.goodsDesc.introduction = editor.html();

        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function(response){
                if (response.data){
                    // 清空表单
                    $scope.goods = {};
                    // 清空富文本编辑器中的内容
                    editor.html('');
                }else{
                    alert("添加失败！");
                }
            });
    };

    // 定义数据存储格式
    $scope.goods = {goodsDesc : { itemImages : [], specificationItems : []}};

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
            baseService.findOne("/typeTemplate/findSpecByTemplateId",newVal).then(function (response) {
                $scope.specList = response.data;
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

    /** 定义数据存储结构(修改以前定义的) */
    $scope.goods = {goodsDesc : {itemImages : [], specificationItems : []}};
    /** 定义修改规格选项方法 */
    $scope.updateSpecAttr = function($event, name, value){
        /** 根据json对象的key到json数组中搜索该key值对应的对象 */
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc
            .specificationItems,'attributeName', name);
        /** 判断对象是否为空 */
        if(obj){
            /** 判断checkbox是否选中 */
            if($event.target.checked){
                /** 添加该规格选项到数组中 */
                obj.attributeValue.push(value);
            }else{
                /** 取消勾选，从数组中删除该规格选项 */
                obj.attributeValue.splice(obj.attributeValue
                    .indexOf(value), 1);
                /** 如果选项都取消了，将此条记录删除 */
                if(obj.attributeValue.length == 0){
                    $scope.goods.goodsDesc.specificationItems.splice(
                        $scope.goods.goodsDesc
                            .specificationItems.indexOf(obj),1);
                }
            }
        }else{
            /** 如果为空，则新增数组元素 */
            $scope.goods.goodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]});
        }
    };

    /** 从json数组中根据key查询指定的json对象 */
    $scope.searchJsonByKey = function(jsonArr, key, value){
        /** 迭代json数组 */
        for(var i = 0; i < jsonArr.length; i++){
            if(jsonArr[i][key] == value){
                return jsonArr[i];
            }
        }
    };

    /** 创建SKU商品方法 */
    $scope.createItems = function(){
        /** 定义SKU数组，并初始化 */
        $scope.goods.items = [{spec:{}, price:0, num:9999,
            status:'0', isDefault:'0' }];
        /** 定义选中的规格选项数组 */
        var specItems = $scope.goods.goodsDesc.specificationItems;
        /** 循环选中的规格选项数组 */
        for(var i = 0; i < specItems.length; i++){
            /** 扩充原SKU数组方法 */
            $scope.goods.items = swapItems($scope.goods.items,
                specItems[i].attributeName,
                specItems[i].attributeValue);
        }
    };
    /** 扩充SKU数组方法 */
    var swapItems = function(items, attributeName, attributeValue){
        /** 创建新的SKU数组 */
        var newItems = new Array();
        /** 迭代旧的SKU数组，循环扩充 */
        for(var i = 0; i < items.length; i++){
            /** 获取一个SKU商品 */
            var item = items[i];
            /** 迭代规格选项值数组 */
            for(var j = 0; j < attributeValue.length; j++){
                /** 克隆旧的SKU商品，产生新的SKU商品 */
                var newItem = JSON.parse(JSON.stringify(item));
                /** 增加新的key与value */
                newItem.spec[attributeName] = attributeValue[j];
                /** 添加到新的SKU数组 */
                newItems.push(newItem);
            }
        }
        return newItems;
    };


});