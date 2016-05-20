export default class {

    /* @ngInject */
    constructor(campaignService, $stateParams, $scope, $state, environmentService) {
        this.environmentService = environmentService;
        this.campaignService = campaignService;
        this.$scope = $scope;
        this.$state = $state;
        this.fetchCampaign($stateParams.code);
    }

    generateSuggestedCampaignCode() {
        return 'C_' + new Date().getTime().toString(36).toUpperCase();
    }

    fetchCampaign(code) {
        if (!code) {
            this.$scope.campaign = { name: "", code: this.generateSuggestedCampaignCode(), productFamily: this.environmentService.getProduct()} ;
            return;
        }
        this.campaignService.get(code).then(r => {
            this.$scope.editing = true;
            this.$scope.campaign = r;
        });
    }

    save(campaign) {
        this.campaignService.save(campaign).then(
            this.$state.go('allPromotions.singleCampaign', {code: campaign.code})
        );
    }
}
