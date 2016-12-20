import PromotionTypes from 'PromotionTypes'

export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
    }

    makeMulti(existingPromotionType) {
        let newType = existingPromotionType.name !==   PromotionTypes.INCENTIVE ?   PromotionTypes.INCENTIVE :   PromotionTypes.TRIAL;
        this.$scope.promotion.promotionType = {name: PromotionTypes.DOUBLE, a: existingPromotionType, b: {name: newType}};
    }

    makeSingular(existingPromotionType) {
        this.$scope.promotion.promotionType = existingPromotionType.a;
    }

}
