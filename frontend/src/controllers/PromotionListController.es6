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
        let promos = this.promotionService.all().then(res => res.data);

        let byCampaign = code ? promos.then(p => p.filter(v => v.campaignCode == code)) : promos;
        byCampaign.then(res => this.$scope.promotions = res)

        this.campaignService.all().then(campaigns => this.$scope.campaigns = campaigns)
    }
}
