export default class {

    /* @ngInject */
    constructor($scope, environmentService, $timeout, promotionService) {
        this.environmentService = environmentService;
        this.promotionService = promotionService;
        this.$timeout = $timeout;
        $scope.mode = 'desktop';
        this.$scope = $scope;
        
        this.$scope.src = 'https://' + this.environmentService.getProductDomain() + '/q';
        
    }

    promotionUpdated(promotion, isValid) {
        if (!promotion || !isValid) {
            return;
        }

        if (this.currentTimeout) {
            this.$timeout.cancel(this.currentTimeout);
        }
        
        this.currentTimeout = this.$timeout(() => {
            this.$scope.promotionString = JSON.stringify(promotion);
        }, 500)
    }
}
