export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
        this.$scope.isMulti = false;
    }

    makeMulti(existingPromotionType) {
        this.$scope.promotion.promotionType = {name: "double", a: existingPromotionType, b: {name: "incentive"}}
    }

}