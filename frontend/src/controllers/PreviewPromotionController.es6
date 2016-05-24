export default class {

    /* @ngInject */
    constructor($scope, environmentService, $timeout, promotionService) {
        this.environmentService = environmentService;
        this.promotionService = promotionService;
        this.$timeout = $timeout;
        this.$scope = $scope;
    }

    promotionUpdated(promotion) {
        if (!promotion) {
            return;
        }

        if (this.currentTimeout) {
            this.$timeout.cancel(this.currentTimeout);
        }
        this.currentTimeout = this.$timeout(() => {
            this.promotionService.validate(promotion).then(() => {
                this.$scope.src = 'https://' + this.environmentService.getProductDomain() + '/q?json=' + JSON.stringify(promotion)
                this.$scope.invalid = false;
            }, () => {
                this.$scope.invalid = true;
            })
        }, 500)
    }
}
