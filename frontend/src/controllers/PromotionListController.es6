export default class {

    /* @ngInject */
    constructor($scope, promotionService) {
        this.service = promotionService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.refresh()
    }

    refresh() {
        this.service.all().then(res => this.$scope.promotions = res.data)
    }

}