export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService) {
        this.promotionService = promotionService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.fetchPromotions($stateParams.code);
        this.$scope.code = $stateParams.code;
    }

    fetchPromotions(code) {
        let promos = code ? this.promotionService.byCampaign(code) : this.promotionService.all();
        promos.then(res => this.$scope.promotions = res);
    }
}
