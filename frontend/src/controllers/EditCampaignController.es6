export default class {

    /* @ngInject */
    constructor(campaignService, $stateParams, $scope, $state) {
        this.service = campaignService;
        this.$scope = $scope;
        this.$state = $state;
        this.fetchCampaign($stateParams.code);
    }

    fetchCampaign(code) {
        if (!code) {
            this.$scope.campaign = {name: "", code: ""};
            return;
        }
        let campaign = this.service.all();
        campaign.then(r => r.filter(v => v.code == code)[0]).then(r => {
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
