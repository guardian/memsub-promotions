export default class {

    /* @ngInject */
    constructor(campaignService, $stateParams, $scope, $state, environmentService) {
        this.environmentService = environmentService;
        this.service = campaignService;
        this.$scope = $scope;
        this.$state = $state;
        this.fetchCampaign($stateParams.code);
    }

    fetchCampaign(code) {
        if (!code) {
            this.$scope.campaign = {name: "", code: "", productFamily: this.environmentService.getProduct()};
            return;
        }
        this.service.get(code).then(r => {
            this.$scope.editing = true;
            this.$scope.campaign = r;
        });
    }

    save(campaign) {
        this.service.save(campaign).then(
            this.$state.go('allPromotions.singleCampaign', {code: campaign.code})
        );
    }
}
