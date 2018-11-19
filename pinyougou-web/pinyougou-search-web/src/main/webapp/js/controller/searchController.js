/** 定义搜索控制器 */
app.controller("searchController", function ($scope, $sce, $location,baseService) {

    $scope.searchParams = {keywords: '', category: '', price: '',
        brand: '', spec: {}, page: 1, rows: 20,sortField:'',sort:''};

    // 页面跳转初始化方法
    $scope.getKeywords = function () {
        $scope.searchParams.keywords = $location.search().keywords;
        $scope.search();
    };

    $scope.search = function () {
        baseService.sendPost("/Search", $scope.searchParams).then(function (response) {
            $scope.resultMap = response.data;
            initPageNum();
        })
    };

    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    $scope.addSearchItem = function (key, value) {
        if (key == 'brand' || key == 'category' || key == 'price') {
            $scope.searchParams[key] = value;
        } else {
            $scope.searchParams.spec[key] = value;
        }
        $scope.search();
    };

    $scope.removeSearchParam = function (key) {
        if (key == 'brand' || key == 'category' || key == 'price') {
            $scope.searchParams[key] = '';
        } else {
            delete $scope.searchParams.spec[key];
        }
        $scope.search();
    };

    // 分页方法
    var initPageNum = function () {
        $scope.pageNum = [];
        // 获取总页数
        var totalPages = $scope.resultMap.totalPages;
        var firstPage = 1;
        var lastPage = totalPages;
        $scope.firstDot = true;
        $scope.lastDot = true;
        if (totalPages > 5 ) {
            if ($scope.searchParams.page <= 3) {
                lastPage = 5;
                $scope.firstDot = false;
            } else if ($scope.searchParams.page > totalPages - 3) {
                firstPage = totalPages - 4;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.searchParams.page - 2;
                lastPage = $scope.searchParams.page + 2;
            }
            for (var i = firstPage; i <= lastPage; i++) {
                $scope.pageNum.push(i);
            }
        }else{
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
    };

    //跳页方法
    $scope.pageSearch = function (page) {
        if (page > 0 && page <= $scope.resultMap.totalPages && page != $scope.searchParams.page){
            $scope.searchParams.page = page;
            $scope.search();
        }
    };
    //排序搜索方法
    $scope.sortSearch=function (sortFied, sort) {
        $scope.searchParams.sortField=sortFied;
        $scope.searchParams.sort=sort;
        $scope.search();
    }
});
