export default class {

    /* @ngInject */
    constructor($scope, environmentService) {
        this.environmentService = environmentService;
        this.$scope = $scope;
    }

    promotionUpdated(promotion) {
        this.$scope.src = 'https://' + this.environmentService.getProductDomain() + '/q?json=' + JSON.stringify(promotion)
    }
}
