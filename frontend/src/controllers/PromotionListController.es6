export default class {

    /* @ngInject */
    constructor($stateParams, $scope, campaignService, promotionService) {
        this.promotionService = promotionService;
        this.campaignService = campaignService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.fetchPromotions($stateParams.code);
    }

    fetchPromotions(code) {
        let promos = code ? this.promotionService.byCampaign(code) : this.promotionService.all();
        promos.then(res => this.$scope.promotions = res);

        this.campaignService.all().then(campaigns => this.$scope.campaigns = campaigns)
    }
}
