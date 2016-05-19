const PRODUCT = 'product';
const STAGE = 'stage';

export default class {

    /* @ngInject */
    constructor($state, $rootScope, $scope, environmentService) {
        this.$state = $state;
        $rootScope.environment = {stage: environmentService.getStage(), product: environmentService.getProduct()};
        this.environmentService = environmentService;
        this.$scope = $scope;
    }

    setStage(stage) {
        this.$scope.environment.stage = stage;
        this.environmentService.setStage(stage);
        this.$state.go('allPromotions.chooseCampaign', {}, {reload: true});
    }
    
    setProductFamily(pf) {
        this.$scope.environment.product = pf;
        this.environmentService.setProduct(pf);
        this.$state.go('allPromotions.chooseCampaign', {}, {reload: true});
    }
}
