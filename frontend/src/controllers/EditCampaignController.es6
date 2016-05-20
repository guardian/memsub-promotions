export default class {

    /* @ngInject */
    constructor(campaignService, $stateParams, $scope, $state, environmentService) {
        this.campaignService = campaignService;
        this.environmentService = environmentService;
        this.service = campaignService;
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
        this.service.save(campaign).then(
            this.$state.go('allPromotions.singleCampaign', {code: campaign.code})
        );
    }
}
