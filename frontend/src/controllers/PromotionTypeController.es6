export default class {

    /* @ngInject */
    constructor($scope) {
        this.types = [
            'incentive',
            'free_trial',
            'percent_discount',
            'retention',
            'tracking'
        ];
        this.$scope = $scope;
    }

    updateSelectedTab(promotionTypeName) {
        this.$scope.selectedTab = this.types.indexOf(promotionTypeName)
    }
    
    setPromotionType(newName) {
        this.$scope.promotionType = {name: newName};
    }
}