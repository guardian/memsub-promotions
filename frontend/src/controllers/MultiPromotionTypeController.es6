export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
    }

    makeMulti(existingPromotionType) {
        let newType = existingPromotionType.name !== 'incentive' ? 'incentive' : 'free_trial';
        this.$scope.promotion.promotionType = {name: "double", a: existingPromotionType, b: {name: newType}};
    }

    makeSingular(existingPromotionType) {
        this.$scope.promotion.promotionType = existingPromotionType.a;
    }

}