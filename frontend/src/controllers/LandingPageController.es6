export default class {

    /* @ngInject */
    constructor($scope, environmentService) {
        this.environmentService = environmentService;
        this.$scope = $scope;
        this.$scope.backup = this.blankBackup();
        this.$scope[this.environmentService.getCampaignGroup()] = true;
    }

    blankBackup() {
        return { type: this.environmentService.getCampaignGroup() }
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
        if (currentAlignment === undefined) {
            this.$scope.promotion.landingPage.heroImage.alignment = "centre";
        }
    }
}
