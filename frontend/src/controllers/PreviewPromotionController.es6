export default class {

    /* @ngInject */
    constructor($scope, environmentService, $timeout) {
        this.environmentService = environmentService;
        this.$timeout = $timeout;
        this.$scope = $scope;
    }

    promotionUpdated(promotion) {
        if (this.currentTimeout) {
            this.$timeout.cancel(this.currentTimeout);
        }
        this.currentTimeout = this.$timeout(() => {
            this.$scope.src = 'https://' + this.environmentService.getProductDomain() + '/q?json=' + JSON.stringify(promotion)
        }, 500)
    }
}
