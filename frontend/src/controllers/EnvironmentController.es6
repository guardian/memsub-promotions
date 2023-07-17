export default class {

    /* @ngInject */
    constructor($state, $rootScope, $scope, environmentService) {
        this.$state = $state;
        $rootScope.environment = {campaignGroup: environmentService.getCampaignGroup()};
        this.environmentService = environmentService;
        this.$scope = $scope;
    }
    
    setCampaignGroup(campaignGroup) {
        this.environmentService.setCampaignGroup(campaignGroup);
        this.$scope.environment.campaignGroup = campaignGroup;
        this.$state.go('allPromotions.chooseCampaign', {}, {reload: true});
    }
}
