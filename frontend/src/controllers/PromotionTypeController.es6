import PromotionTypes from 'PromotionTypes'
export default class {
      /* @ngInject */
    constructor($scope) {
      console.log(PromotionTypes);
        this.types = [
            PromotionTypes.INCENTIVE,
            PromotionTypes.TRIAL,
            PromotionTypes.DISCOUNT,
            PromotionTypes.TRACKING
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
