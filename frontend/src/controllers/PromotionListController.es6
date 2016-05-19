export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService, environmentService) {
        this.promotionService = promotionService;
        this.environmentService = environmentService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.fetchPromotions($stateParams.code);
        this.$scope.code = $stateParams.code;
    }

    fetchPromotions(code) {
        this.$scope.productDomain = this.environmentService.getProductDomain();
        let promos = code ? this.promotionService.byCampaign(code) : this.promotionService.all();
        promos.then(res => this.$scope.promotions = res);
    }
}
