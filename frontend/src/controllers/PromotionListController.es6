export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService, environmentService) {
        this.promotionService = promotionService;
        this.environmentService = environmentService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.fetchPromotions($stateParams.code);
        this.$scope.code = $stateParams.code;
        this.$scope.campaignGroupDomain = this.environmentService.getCampaignGroupDomain();
    }

    fetchPromotions(code) {
        let promos = code ? this.promotionService.byCampaign(code) : this.promotionService.all();
        promos.then(res => this.$scope.promotions = res);
    }
}
