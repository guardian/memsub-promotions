export default class {

    /* @ngInject */
    constructor($scope, environmentService) {
        this.environmentService = environmentService;
        this.$scope = $scope;
        this.$scope.backup = this.blankBackup();
        this.$scope[this.environmentService.getProduct()] = true;
    }

    blankBackup() {
        return {productFamily: this.environmentService.getProduct()}
    }

    toggleBackup(promo, backup) {
        if(promo.landingPage) {
            this.$scope.backup = promo.landingPage;
            delete this.$scope.promotion.landingPage;
        } else {
            this.$scope.promotion.landingPage = backup;
            this.$scope.backup =  this.blankBackup();
        }
    }

    heroImageChange(currentAlignment) {
        if (currentAlignment === undefined && this.$scope.promotion !== undefined && this.$scope.promotion.landingPage !== undefined) {
            this.$scope.promotion.landingPage.heroImage = Object.assign(this.$scope.promotion.landingPage.heroImage, {alignment: "centre"});
        }
    }
}
