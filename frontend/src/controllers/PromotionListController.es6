export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService) {
        this.service = promotionService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.fetchPromotions($stateParams.code);
    }

    fetchPromotions(name) {
        let promos = this.service.all().then(res => res.data);
        promos.then(r => this.$scope.campaigns = r.map(i => i.campaignName).filter((v, i, self) => self.indexOf(v) === i));

        let byCampaign = name ? promos.then(p => p.filter(v => v.campaignName == name)) : promotions;
        byCampaign.then(res => this.$scope.promotions = res)
    }
}
