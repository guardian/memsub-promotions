export default class {

    /* @ngInject */
    constructor($scope, environmentService, $timeout, promotionService) {
        this.environmentService = environmentService;
        this.promotionService = promotionService;
        this.$timeout = $timeout;
        $scope.mode = 'desktop';
        this.$scope = $scope;
    }

    promotionUpdated(promotion, isValid) {
        if (!promotion || !isValid) {
            return;
        }

        if (this.currentTimeout) {
            this.$timeout.cancel(this.currentTimeout);
        }
        this.currentTimeout = this.$timeout(() => {
            this.$scope.src = 'https://' + this.environmentService.getProductDomain() + '/q?json=' + encodeURIComponent(JSON.stringify(promotion));
        }, 500)
    }
}
