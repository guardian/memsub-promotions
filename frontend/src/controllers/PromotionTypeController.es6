export default class {

    /* @ngInject */
    constructor($scope) {
        this.types = [
            'incentive',
            'free_trial',
            'percent_discount',
            'tracking'
        ];
        this.$scope = $scope;
    }

    updateSelectedTab(promotionTypeName) {
        this.$scope.selectedTab = this.types.indexOf(promotionTypeName)
    }
    
    setPromotionType(oldName, newName) {
        if (oldName != newName) {
            this.$scope.promotion.promotionType = {name: newName}
        }
    }
}