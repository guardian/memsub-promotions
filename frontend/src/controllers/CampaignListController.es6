export default class {

    /* @ngInject */
    constructor($scope, campaignService, $state) {
        this.campaignService = campaignService;
        $scope.campaigns = [];
        this.$scope = $scope;
        this.$state = $state;
        this.fetchCampaigns();
    }

    fetchCampaigns() {
        this.campaignService.all()
            .then(campaigns => {this.$scope.campaigns = campaigns; return campaigns})
            .then(campaigns => {
                if (campaigns && campaigns.length) {
                    this.$state.go('allPromotions.singleCampaign', {code: campaigns[0].code})
                }
            })
    }
}
