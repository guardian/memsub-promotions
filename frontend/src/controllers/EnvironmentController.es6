export default class {

    /* @ngInject */
    constructor($state, $rootScope, $scope, environmentService) {
        this.$state = $state;
        $rootScope.environment = {stage: environmentService.getStage(), campaignGroup: environmentService.getCampaignGroup()};
        this.environmentService = environmentService;
        this.$scope = $scope;
    }

    setStage(stage) {
        this.$scope.environment.stage = stage;
        this.environmentService.setStage(stage);
        this.$state.go('allPromotions.chooseCampaign', {}, {reload: true});
    }
    
    setCampaignGroup(campaignGroup) {
        this.environmentService.setCampaignGroup(campaignGroup);
        this.$scope.environment.campaignGroup = campaignGroup;
        this.$state.go('allPromotions.chooseCampaign', {}, {reload: true});
    }
}
