export default class {

    /* @ngInject */
    constructor(campaignService, $stateParams, $scope) {
        this.service = campaignService;
        this.$scope = $scope;
        this.fetchCampaign($stateParams.code);
    }

    fetchCampaign(code) {
        let campaign = this.service.all();
        campaign.then(r => r.filter(v => v.code == code)[0]).then(r => {
            this.$scope.campaign = r ? r : {name: "", code: ""}
        });
    }

    save(campaign) {
        this.service.save(campaign);
    }
}
